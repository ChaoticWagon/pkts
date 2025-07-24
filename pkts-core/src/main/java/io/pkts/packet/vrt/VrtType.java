package io.pkts.packet.vrt;

public enum VrtType {
    SIGNAL_DATA_PACKET,
    SIGNAL_DATA_PACKET_W_STREAM,
    EXTENSION_DATA_PACKET,
    EXTENSION_DATA_PACKET_W_STREAM,
    CONTEXT_PACKET,
    EXTENSION_CONTEXT_PACKET,
    COMMAND_PACKET,
    EXTENSION_COMMAND_PACKET;

    public static VrtType valueOf(int value) {
        return switch (value) {
            case 1 -> SIGNAL_DATA_PACKET;
            case 2 -> SIGNAL_DATA_PACKET_W_STREAM;
            case 3 -> EXTENSION_DATA_PACKET;
            case 4 -> EXTENSION_DATA_PACKET_W_STREAM;
            case 5 -> CONTEXT_PACKET;
            case 6 -> EXTENSION_CONTEXT_PACKET;
            case 7 -> COMMAND_PACKET;
            case 8 -> EXTENSION_COMMAND_PACKET;
            default -> null;
        };
    }

    public static VrtType valueOf(byte value) {
        return valueOf(value >> 4);
    }
}
