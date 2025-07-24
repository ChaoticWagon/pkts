package io.pkts.packet.vrt.headers;

import io.pkts.buffer.InputStreamBuffer;
import io.pkts.packet.vrt.VrtType;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class VrtPacketHeader {
    private final short packetSize;
    private final int packetCount;
    private final int tsf;
    private final int tsi;
    private final boolean[] indicators;
    private final boolean hasClass;
    private final int type;

    public VrtPacketHeader(@NotNull InputStreamBuffer buffer) throws IOException {
        this.type = buffer.getByte(0) >> 4;
        this.hasClass = buffer.getBit3(0);

        this.indicators = new boolean[] {
                buffer.getBit2(0),
                buffer.getBit1(0),
                buffer.getBit0(0),
        };

        this.tsi = buffer.getByte(1) >>> 6;
        this.tsf = buffer.getByte(1) & 0b00110000 >> 4;
        this.packetCount = buffer.getByte(1) & 0b00001111;
        this.packetSize = (short) (buffer.getByte(2) << 8 | buffer.getByte(3) & 0xFF);
    }

    public VrtType getVrtType() {
        return VrtType.valueOf(this.type);
    }

    public TSI getTimestampType() {
        return TSI.valueOf(this.tsi);
    }

    public TSF getTimestampFractionalType() {
        return TSF.valueOf(this.tsf);
    }

    public short getPacketSize() {
        return packetSize;
    }

    public int getPacketCount() {
        return packetCount;
    }

    public boolean[] getIndicators() {
        return indicators;
    }

    public boolean hasClass() {
        return hasClass;
    }
}
