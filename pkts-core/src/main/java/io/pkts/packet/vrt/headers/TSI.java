package io.pkts.packet.vrt.headers;

public enum TSI {
    NONE,
    UTC,
    GPS,
    OTHER;

    public static TSI valueOf(int value) {
        return switch (value) {
            case 0 -> NONE;
            case 1 -> UTC;
            case 2 -> GPS;
            case 3 -> OTHER;
            default -> null;
        };
    }

    public static TSI valueOf(byte value) {
        return valueOf(value >> 6);
    }
}
