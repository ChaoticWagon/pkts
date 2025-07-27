package io.pkts.packet.vrt.headers;

import io.pkts.buffer.Buffer;

public class ClassId {

    // Word 1 masks
    private static final int RESERVED1_MASK    = 0x80000000;  // bit 31
    private static final int OUI_MASK          = 0x7FFFFF80;  // bits 30–7
    private static final int INFO_CLASS_MASK   = 0x0000007F;  // bits 6–0

    // Word 2 masks
    private static final int PACKET_CLASS_MASK = 0xFF000000;  // bits 31–24
    private static final int RESERVED2_MASK    = 0x00FFFFE0;  // bits 23–5
    private static final int PAD_COUNT_MASK    = 0x0000001F;  // bits 4–0

    private final int word1;
    private final int word2;

    public ClassId(final Buffer buffer) {
        this.word1 = (int) buffer.getUnsignedInt(0);
        this.word2 = (int) buffer.getUnsignedInt(1);

        // Validate reserved bits per Rule 5.1.3‑5
        if ((word1 & RESERVED1_MASK) != 0) {
            throw new IllegalArgumentException("Reserved bit in Class ID word1 must be zero");
        }
        if ((word2 & RESERVED2_MASK) != 0) {
            throw new IllegalArgumentException("Reserved bits in Class ID word2 must be zero");
        }
    }

    /**
     * Organizationally Unique Identifier (24‑bit unsigned).
     */
    public int getOUI() {
        return (word1 & OUI_MASK) >>> 7;
    }

    /**
     * Originator‑assigned Information Class code (7‑bit unsigned).
     */
    public int getInformationClassCode() {
        return word1 & INFO_CLASS_MASK;
    }

    /**
     * Originator‑assigned Packet Class code (8‑bit unsigned).
     */
    public int getPacketClassCode() {
        return (word2 & PACKET_CLASS_MASK) >>> 24;
    }

    /**
     * Number of pad bits (0–31).
     */
    public int getPadBitCount() {
        return word2 & PAD_COUNT_MASK;
    }

    @Override
    public String toString() {
        return String.format(
                "VrtClassIdentifier[OUI=0x%06X, infoClass=%d, packetClass=%d, padBits=%d]",
                getOUI(),
                getInformationClassCode(),
                getPacketClassCode(),
                getPadBitCount()
        );
    }
}
