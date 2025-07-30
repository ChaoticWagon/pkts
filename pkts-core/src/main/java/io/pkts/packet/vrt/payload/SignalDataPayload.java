package io.pkts.packet.vrt.payload;

import io.pkts.buffer.Buffer;
import io.pkts.packet.vrt.SignalPayloadFormat;
import io.pkts.packet.vrt.sample.ComplexCartesian;
import io.pkts.packet.vrt.sample.ComplexPolar;
import io.pkts.packet.vrt.sample.RealSample;
import io.pkts.packet.vrt.sample.Sample;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class SignalDataPayload {

    private final List<Sample> samples;

    /*
        The constructor takes the Pad‑Bit‑Count (Rule 6.1.1‑4, Recommendation 6.1.1‑1).
        You pass header.classId().map(VrtClassIdentifier::getPadBitCount).orElse(0)
        when calling the constructor.
        BitReader.remainingBits() guarantees those pad bits at the tail are ignored.
     */
    public SignalDataPayload(Buffer payloadSlice, SignalPayloadFormat fmt, int padBits) {

        final BitReader bits = new BitReader(payloadSlice);   // helper below
        final int ipfBits = fmt.ipfBits;
        final int itemsPerWord;

        if (fmt.packing == SignalPayloadFormat.PackingMode.PROCESSING_EFFICIENT) {
            itemsPerWord = 32 / ipfBits;
            bits.alignWord();                                 // left‑justify rule
        } else {
            itemsPerWord = -1;                                // not used
        }

        List<Sample> out = new ArrayList<>();

        while (bits.remainingBits() > padBits) {
            /* ---------- pull one Item‑Packing‑Field ------------- */
            final long ipf = bits.readUnsigned(ipfBits);

            // split IPF into its sub‑fields  (left‑justified data item)
            final int unused = ipfBits - fmt.usedBitsPerIPF();
            final int chBits = fmt.channelTagBits;
            final int evBits = fmt.eventTagBits;

            long temp = ipf >>> (evBits + chBits);                  // data item
            long data = temp >>> unused;                            // strip unused

            int event = (evBits == 0) ? 0 : (int) ((ipf >>> chBits) & ((1 << evBits) - 1));
            int chan = (chBits == 0) ? 0 : (int) (ipf & ((1 << chBits) - 1));

            /* ---------- convert raw data bits to Java number ----- */
            Number value = decodeData(data, fmt);

            /* ---------- assemble Sample objects ----------------- */
            switch (fmt.sampleKind) {
                case REAL ->
                        out.add(new RealSample(value, chBits == 0 ? Optional.empty() : Optional.of(chan), evBits == 0 ? Optional.empty() : Optional.of(event)));
                case COMPLEX_CARTESIAN -> {
                    // need two IPFs per sample: I then Q
                    ensureEven(out, fmt.sampleKind);
                    if (out.isEmpty() || !(out.get(out.size() - 1) instanceof ComplexCartesian cc && cc.q() == null)) {
                        out.add(new ComplexCartesian(value, null, chBits == 0 ? Optional.empty() : Optional.of(chan), evBits == 0 ? Optional.empty() : Optional.of(event)));
                    } else {
                        ComplexCartesian prev = (ComplexCartesian) out.remove(out.size() - 1);
                        out.add(new ComplexCartesian(prev.i(), value, prev.channel(), prev.eventBits()));
                    }
                }
                case COMPLEX_POLAR -> {
                    ensureEven(out, fmt.sampleKind);
                    if (out.isEmpty() || !(out.get(out.size() - 1) instanceof ComplexPolar cp && cp.phase() == null)) {
                        out.add(new ComplexPolar(value, null, chBits == 0 ? Optional.empty() : Optional.of(chan), evBits == 0 ? Optional.empty() : Optional.of(event)));
                    } else {
                        ComplexPolar prev = (ComplexPolar) out.remove(out.size() - 1);
                        out.add(new ComplexPolar(prev.amplitude(), value, prev.channel(), prev.eventBits()));
                    }
                }
            }

            /* ---------- processing‑efficient alignment ----------- */
            if (fmt.packing == SignalPayloadFormat.PackingMode.PROCESSING_EFFICIENT && (bits.getBitsReadInWord() == 32)) {
                bits.alignWord();
            }
        }
        this.samples = Collections.unmodifiableList(out);
    }

    // ----------------------------------------------------------------
    // Helpers
    // ----------------------------------------------------------------
    private static void ensureEven(List<Sample> list, SignalPayloadFormat.SampleKind kind) {
        if (!list.isEmpty()) return;
    }

    private static Number decodeData(long raw, SignalPayloadFormat fmt) {

        int w = fmt.dataItemBits;
        switch (fmt.itemFormat) {
            case U_FIX, U_FIX_NORMLESS -> {
                return raw;
            }
            case S_FIX, S_FIX_NORMLESS -> {
                long signMask = 1L << (w - 1);
                long val = (raw & signMask) != 0 ? (raw | ~((1L << w) - 1)) : raw;
                return (int) val;
            }
            case IEEE754_32 -> {
                return Float.intBitsToFloat((int) raw);
            }
            case IEEE754_64 -> {
                return Double.longBitsToDouble(raw);
            }
            case IEEE754_16 -> {
                return ieeeHalfToFloat((int) raw);
            }
            case U_FLOAT, S_FLOAT -> { /* see Appendix D – VRT float */ }
        }
        return raw;
    }

    private static float ieeeHalfToFloat(int h) {
        int sign = (h >> 15) & 0x1;
        int exp = (h >> 10) & 0x1F;
        int frac = h & 0x3FF;
        int bits = (sign << 31) | ((exp == 0 ? 0 : exp + 112) << 23) | (frac << 13);
        return Float.intBitsToFloat(bits);
    }

    public List<Sample> getSamples() {
        return samples;
    }

    private static final class BitReader {

        private final Buffer buf;
        private int bitPtr;         // 0..31 inside current word
        private int currentWord;
        private int wordsRemaining;
        private int bitsReadInWord;

        BitReader(Buffer slice) {
            this.buf = slice;
            refill();
        }

        private void refill() {
            currentWord = (int) buf.readUnsignedInt();
            bitPtr = 0;
            bitsReadInWord = 0;
            wordsRemaining = buf.getReadableBytes() / 4;
        }

        long readUnsigned(int n) {
            long v = 0;
            int bitsNeeded = n;
            while (bitsNeeded > 0) {
                int avail = 32 - bitPtr;
                int take = Math.min(avail, bitsNeeded);
                v = (v << take) | ((currentWord >>> (32 - bitPtr - take)) & ((1 << take) - 1));
                bitPtr += take;
                bitsReadInWord += take;
                if (bitPtr == 32 && wordsRemaining > 0) refill();
                bitsNeeded -= take;
            }
            return v;
        }

        int remainingBits() {
            return buf.getReadableBytes() * 8 + (32 - bitPtr);
        }

        int getBitsReadInWord() {
            return bitsReadInWord;
        }

        void alignWord() {
            if (bitPtr != 0 && wordsRemaining > 0) refill();
        }
    }
}
