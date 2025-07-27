package io.pkts.packet.vrt.headers;

import io.pkts.buffer.Buffer;
import io.pkts.packet.vrt.VrtType;

public class VrtPacketHeader {
    private static final int PACKET_TYPE_MASK  = 0xF0000000; // bits 31–28
    private static final int CLASS_ID_MASK     = 0x08000000; // bit     27
    private static final int INDICATORS_MASK   = 0x07000000; // bits 26–24
    private static final int TSI_MASK          = 0x00C00000; // bits 23–22
    private static final int TSF_MASK          = 0x00300000; // bits 21–20
    private static final int PACKET_COUNT_MASK = 0x000F0000; // bits 19–16
    private static final int PACKET_SIZE_MASK  = 0x0000FFFF; // bits 15–0


    // Raw 32‑bit header word
    private final int word;

    public VrtPacketHeader(final Buffer buffer) {
        this.word = (int) buffer.getUnsignedInt(0);
    }

    /**
     * Raw 4‑bit packet type value (0–15).
     */
    public int getPacketTypeRaw() {
        return (word & PACKET_TYPE_MASK) >>> 28;
    }

    /**
     * The VrtType enum for this packet.
     */
    public VrtType getVrtType() {
        return VrtType.valueOf(getPacketTypeRaw());
    }

    /**
     * Does the packet include a Class ID field? (C bit)
     */
    public boolean hasClassId() {
        return (word & CLASS_ID_MASK) != 0;
    }

    /**
     * Raw 3‑bit packet‑specific indicators field.
     */
    public int getIndicatorsField() {
        return (word & INDICATORS_MASK) >>> 24;
    }

    /**
     * Indicator bit 0 (bit 26).
     */
    public boolean isIndicator0() {
        return (word & (1 << 26)) != 0;
    }

    /**
     * Indicator bit 1 (bit 25).
     */
    public boolean isIndicator1() {
        return (word & (1 << 25)) != 0;
    }

    /**
     * Indicator bit 2 (bit 24).
     */
    public boolean isIndicator2() {
        return (word & (1 << 24)) != 0;
    }

    /**
     * Raw 2‑bit integer‑seconds timestamp type (TSI).
     */
    public int getTsiRaw() {
        return (word & TSI_MASK) >>> 22;
    }

    /**
     * Raw 2‑bit fractional‑seconds timestamp type (TSF).
     */
    public int getTsfRaw() {
        return (word & TSF_MASK) >>> 20;
    }

    /**
     * 4‑bit packet count (0–15).
     */
    public int getPacketCount() {
        return (word & PACKET_COUNT_MASK) >>> 16;
    }

    /**
     * 16‑bit packet size (in 32‑bit words).
     */
    public int getPacketSize() {
        return word & PACKET_SIZE_MASK;
    }

    @Override
    public String toString() {
        return String.format(
                "VrtPacketHeader[type=%d, classId=%b, ind=0x%X, TSI=%d, TSF=%d, count=%d, size=%d]",
                getPacketTypeRaw(),
                hasClassId(),
                getIndicatorsField(),
                getTsiRaw(),
                getTsfRaw(),
                getPacketCount(),
                getPacketSize()
        );
    }
}
