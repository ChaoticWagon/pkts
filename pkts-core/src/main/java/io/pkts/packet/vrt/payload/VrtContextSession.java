package io.pkts.packet.vrt.payload;

import io.pkts.buffer.Buffer;
import io.pkts.packet.vrt.headers.VrtCif;
import io.pkts.packet.vrt.headers.VrtCif.CifField;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Parses the *Context Section* (everything after the CIF words) of a VRT Context packet
 * and exposes three frequently‑needed metadata items:
 * <p>
 * * RF Reference Frequency (Hz)      – CIF0 bit 27, 64‑bit fixed‑point (44.20)
 * * Gain Stage 1 / Stage 2 (dB)      – CIF0 bit 23, 32‑bit fixed‑point (9.7 per sub‑field)
 * * Sample Rate (Hz)                 – CIF0 bit 21, 64‑bit fixed‑point (44.20)
 * <p>
 * All other context fields are simply skipped so that the buffer position remains
 * at the end of the Context Section.
 */
public final class VrtContextSession {

    // --- public accessors --------------------------------------------------

    /**
     * mandatory CIF words (already parsed by the framer)
     */
    private final VrtCif cif;

    /**
     * Present only when CIF0 bit 27 is set – otherwise null
     */
    private final Double rfRefFreqHz;

    /**
     * Present only when CIF0 bit 23 is set – otherwise null
     */
    private final Double gainStage1dB, gainStage2dB;

    /**
     * Present only when CIF0 bit 21 is set – otherwise null
     */
    private final Double sampleRateHz;

    /**
     * @param cif       the already‑parsed CIF words
     * @param ctxFields a Buffer positioned at the first context field **after** the CIF words
     */
    public VrtContextSession(final VrtCif cif, final Buffer ctxFields) throws IOException {

        this.cif = Objects.requireNonNull(cif, "CIF may not be null");

        /* --- little helper to walk the Context‑Field sequence in RFC order ---- */
        final List<CifField> ordered = getBitsInWireOrder(cif);

        Double _rf = null, _sr = null, _g1 = null, _g2 = null;

        for (CifField f : ordered) {

            switch (f) {
                /* ------------------------------------------------ RF Reference Frequency (2 words, 64‑bit 44.20) */
                case RF_REFERENCE_FREQUENCY -> {
                    long upper = ctxFields.readUnsignedInt();
                    long lower = ctxFields.readUnsignedInt();
                    long raw = (upper << 32) | lower;
                    _rf = raw / (double) (1L << 20);                   // 2‑20 fractional scale
                }

                /* ------------------------------------------------ Sample‑Rate (2 words, 64‑bit 44.20) */
                case SAMPLE_RATE -> {
                    long upper = ctxFields.readUnsignedInt();
                    long lower = ctxFields.readUnsignedInt();
                    long raw = (upper << 32) | lower;
                    _sr = raw / (double) (1L << 20);
                }

                /* ------------------------------------------------ Gain (1 word, two 16‑bit 9.7 numbers) */
                case GAIN -> {
                    int word = ctxFields.readInt();
                    short stage1 = (short) (word & 0xFFFF);
                    short stage2 = (short) ((word >>> 16) & 0xFFFF);
                    _g1 = stage1 / 128.0;                             // 2‑7 fractional scale
                    _g2 = stage2 / 128.0;
                }

                /* ------------------------------------------------ everything else – skip properly ----- */
                default -> ctxFields.readBytes(fieldWordCount(f) * 4);
            }
        }

        // Whatever fields weren’t present stay null
        this.rfRefFreqHz = _rf;
        this.sampleRateHz = _sr;
        this.gainStage1dB = _g1;
        this.gainStage2dB = _g2;
    }

    /**
     * Return the CIF‑fields that are set ‑‑ ordered exactly as they appear on the wire.
     */
    private static List<CifField> getBitsInWireOrder(VrtCif cif) {

        List<CifField> ordered = new ArrayList<>(64);

        for (VrtCif.Level lvl : VrtCif.Level.values()) {
            int word = cif.getWord(lvl);
            if (word == 0) continue;

            for (int bit = 31; bit >= 0; bit--) {
                if ((word & (1 << bit)) == 0) continue;
                for (CifField f : CifField.values()) {
                    if (f.level == lvl && f.bitPosition == bit) {
                        ordered.add(f);
                        break;
                    }
                }
            }
        }
        return ordered;
    }

    /**
     * Number of 32‑bit words occupied by a context field. Defaults to 1.
     */
    private static int fieldWordCount(CifField f) {
        return switch (f) {
            case RF_REFERENCE_FREQUENCY, SAMPLE_RATE -> 2;
            case GAIN -> 1;
            default -> 1;   // most fields are 1 word
        };
    }

    public Optional<Double> getRfReferenceFrequencyHz() {
        return Optional.ofNullable(rfRefFreqHz);
    }

    // -----------------------------------------------------------------------

    public Optional<Double> getGainStage1dB() {
        return Optional.ofNullable(gainStage1dB);
    }

    /* -------------------------------------------------------------------- */
    /* Utilities                                                            */
    /* -------------------------------------------------------------------- */

    public Optional<Double> getGainStage2dB() {
        return Optional.ofNullable(gainStage2dB);
    }

    public Optional<Double> getSampleRateHz() {
        return Optional.ofNullable(sampleRateHz);
    }
}

