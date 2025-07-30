package io.pkts.packet.vrt;

public final class SignalPayloadFormat {

    public final int ipfBits;          // 1…64  – Item‑Packing‑Field width
    public final int dataItemBits;     // 1…64  – Data‑item width
    public final int eventTagBits;     // 0…7   – 0 == none
    public final int channelTagBits;   // 0…15  – 0 == none
    public final PackingMode packing;
    public final SampleKind sampleKind;
    public final ItemFormat itemFormat;
    public final int repeatCount;      // 1…65 536 (sample‑component OR channel)
    public final int vectorWidth;      // 0/1 => not using sample vectors

    public SignalPayloadFormat(int ipfBits, int dataItemBits, int eventTagBits, int channelTagBits, PackingMode packing, SampleKind sampleKind, ItemFormat itemFormat, int repeatCount, int vectorWidth) {

        /* — validations according to many rules skipped for brevity — */
        this.ipfBits = ipfBits;
        this.dataItemBits = dataItemBits;
        this.eventTagBits = eventTagBits;
        this.channelTagBits = channelTagBits;
        this.packing = packing;
        this.sampleKind = sampleKind;
        this.itemFormat = itemFormat;
        this.repeatCount = repeatCount;
        this.vectorWidth = vectorWidth;
    }

    /* Helper: total bits consumed by tags + data */
    public int usedBitsPerIPF() {
        return dataItemBits + eventTagBits + channelTagBits;
    }

    public enum PackingMode {LINK_EFFICIENT, PROCESSING_EFFICIENT}

    public enum SampleKind {REAL, COMPLEX_CARTESIAN, COMPLEX_POLAR}

    public enum ItemFormat {
        U_FIX, S_FIX, U_FLOAT, S_FLOAT, IEEE754_32, IEEE754_64, IEEE754_16, U_FIX_NORMLESS, S_FIX_NORMLESS
    }
}

