package io.pkts.packet.vrt.headers;

public enum TSF {
    NONE,
    SAMPLE_COUNT,
    REAL_TIME_PICOSECONDS,
    FREE_RUNNING_COUNT;

    public static TSF valueOf(int value) {
        return switch (value) {
            case 0 -> NONE;
            case 1 -> SAMPLE_COUNT;
            case 2 -> REAL_TIME_PICOSECONDS;
            case 3 -> FREE_RUNNING_COUNT;
            default -> null;
        };
    }

    public static TSF valueOf(byte value) {
        return valueOf(value & 0b00110000 >> 4);
    }
}
