package io.pkts.framer;

import io.pkts.buffer.Buffer;
import io.pkts.packet.TransportPacket;
import io.pkts.packet.vrt.SignalPayloadFormat;
import io.pkts.packet.vrt.VrtPacket;
import io.pkts.packet.vrt.headers.*;
import io.pkts.packet.vrt.impl.VrtPacketImpl;
import io.pkts.packet.vrt.payload.SignalDataPayload;
import io.pkts.protocol.Protocol;

import java.io.IOException;
import java.util.Optional;

public final class VRTFramer implements Framer<TransportPacket, VrtPacket> {

    /* ------------------------------------------------------------------
     * Helpers
     * ---------------------------------------------------------------- */
    private static int ensureReadable(Buffer buf, int bytes) {
        if (buf.getReadableBytes() >= bytes) return bytes;
        buf.markReaderIndex();
        try {
            buf.readBytes(bytes);
        } catch (IndexOutOfBoundsException | IOException e) {
            return buf.getReadableBytes();
        } finally {
            buf.resetReaderIndex();
        }
        return buf.getReadableBytes();
    }

    @Override
    public Protocol getProtocol() {
        return Protocol.VRT;
    }

    /* ------------------------------------------------------------------
     *  1.  Quick “sniff” – is this likely a VRT packet?
     * ---------------------------------------------------------------- */
    @Override
    public boolean accept(final Buffer data) throws IOException {

        /* Need at least one 32‑bit word */
        if (ensureReadable(data, 4) < 4) {
            return false;
        }

        final int hdr = data.getInt(0);

        /* 4‑bit Packet‑Type 0…7 (8…15 are “reserved”, rarely seen)      */
        final int pktType = (hdr >>> 28) & 0x0F;
        if (pktType > 7) {
            return false;
        }

        /* 16‑bit Packet‑Size must be ≥1 and not larger than remaining bytes */
        final int words = hdr & 0xFFFF;
        if (words == 0 || data.getReadableBytes() < words * 4) {
            return false;
        }

        /* Bits 23‑22 (TSI) & 21‑20 (TSF) must each be 0…3               */
        final int tsi = (hdr >>> 22) & 0x03;
        final int tsf = (hdr >>> 20) & 0x03;
        return tsi < 4 && tsf < 4;
    }

    /* ------------------------------------------------------------------
     *  2.  Full parse
     * ---------------------------------------------------------------- */
    @Override
    public VrtPacket frame(final TransportPacket parent, final Buffer buffer) throws IOException {

        if (parent == null) {
            throw new IllegalArgumentException("parent frame cannot be null");
        }
        final int start = buffer.getReaderIndex();

        /* ----------  Header (always present) ------------------------ */
        final VrtPacketHeader header = new VrtPacketHeader(buffer);

        /* Running count of 32‑bit words consumed so far */
        int wordsRead = 1;                       // header itself

        /* ----------  Optional Stream‑ID ----------------------------- */
        Optional<Integer> streamId = Optional.empty();
        final int type = header.getPacketTypeRaw();   // raw 0‑15
        final boolean streamIdMandatory =
                type == 1 || type == 3 ||          // signal/extension w/ SID
                        type == 4 || type == 5;            // context packets
        if (streamIdMandatory) {
            streamId = Optional.of((int) buffer.readUnsignedInt());
            wordsRead++;
        }

        /* ----------  Optional Class‑ID ------------------------------ */
        Optional<VrtClassIdentifier> classId = Optional.empty();
        if (header.hasClassId()) {
            classId = Optional.of(new VrtClassIdentifier(buffer));
            wordsRead += 2;
        }

        /* ----------  Optional Integer‑TS ---------------------------- */
        Optional<Integer> intTs = Optional.empty();
        if (header.getTsiRaw() != 0) {
            intTs = Optional.of((int) buffer.readUnsignedInt());
            wordsRead++;
        }

        /* ----------  Optional Fractional‑TS (64‑bit) ---------------- */
        Optional<Long> fracTs = Optional.empty();
        if (header.getTsfRaw() != 0) {
            long upper = buffer.readUnsignedInt();
            long lower = buffer.readUnsignedInt();
            fracTs = Optional.of((upper << 32) | (lower & 0xFFFF_FFFFL));
            wordsRead += 2;
        }

        /* ----------  Context‑Indicator Fields ----------------------- */
        Optional<VrtCif> cif = Optional.empty();
        if (type == 4 || type == 5) {                      // (Ext)Context packet
            cif = Optional.of(new VrtCif(buffer));
            wordsRead += cif.get().getPresentLevels().size();
        }

        /* ----------  Compute payload length & optional trailer ------ */
        final int totalWords = header.getPacketSize();
        final boolean trailerExpected = (type == 0 || type == 1 || type == 2 || type == 3) &&   // data packets
                ((header.getIndicatorsField() & 0x4) != 0);            // bit 26 == TrailerIncluded
        final int trailerWords = trailerExpected ? 1 : 0;

        final int wordsLeft = totalWords - wordsRead - trailerWords;
        if (wordsLeft < 0) {
            throw new IOException("Malformed VRT packet – size fields inconsistent");
        }

        /* ----------  Slice payload ---------------------------------- */
        final Buffer payload = buffer.readBytes(wordsLeft * 4);
        Optional<SignalDataPayload> sdp = Optional.empty();

        if (type == 0 || type == 1) {  // Signal‑Data pkt
            SignalPayloadFormat fmt = new SignalPayloadFormat(
                    /* ipfBits       = */ 16,                         // 16 bits per item‑packing‑field (i.e. each I or each Q)
                    /* dataItemBits  = */ 16,                         // each sample is 16 bits
                    /* eventTagBits  = */ 0,                          // no event tags
                    /* channelTagBits= */ 0,                          // no channel tags
                    SignalPayloadFormat.PackingMode.PROCESSING_EFFICIENT,                // 2 samples per 32‑bit word
                    SignalPayloadFormat.SampleKind.COMPLEX_CARTESIAN,                    // I then Q
                    SignalPayloadFormat.ItemFormat.S_FIX,                                // signed fixed‑point
                    /* repeatCount   = */ 1,                          // no sample‑component or channel repeating
                    /* vectorWidth   = */ 0                           // no N‑dimensional vectors here
            );
            int padBits = classId.map(VrtClassIdentifier::getPadBitCount).orElse(0);
            sdp = Optional.of(new SignalDataPayload(payload, fmt, padBits));

            // attach to headers or the VrtPacket as you prefer
        }

        wordsRead += wordsLeft;

        /* ----------  Optional trailer -------------------------------- */
        Optional<VrtTrailer> trailer = Optional.empty();
        if (trailerExpected) {
            trailer = Optional.of(new VrtTrailer(buffer));
            wordsRead++;
        }

        /*  Sanity: we must now have consumed exactly totalWords */
        if (wordsRead != totalWords) {
            throw new IOException("VRT framing error – consumed " + wordsRead + " words, header says " + totalWords);
        }

        /* ----------  Wrap everything into the record --------------- */
        VrtHeaders headers = new VrtHeaders(header, streamId, classId, intTs, fracTs, cif, trailer);

        return new VrtPacketImpl(parent, headers, payload, sdp);
    }
}
