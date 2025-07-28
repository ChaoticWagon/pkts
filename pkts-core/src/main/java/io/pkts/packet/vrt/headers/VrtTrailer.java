package io.pkts.packet.vrt.headers;

import io.pkts.buffer.Buffer;

public class VrtTrailer {

    private static final int ENABLES_MASK =      0xFFF00000;
    private static final int INDICATORS_MASK =   0x000FFF00;
    private static final int CTX_COUNT_EN_MASK = 0x00000080;
    private static final int CTX_COUNT_MASK =    0x0000007F;

    private final int word;

    public VrtTrailer(Buffer buffer) {
        this.word = (int) buffer.readUnsignedInt();
    }

    // enable bit getters

    public int getEnablesField() {
        return (word & ENABLES_MASK) >>> 20;
    }

    public int getIndicatorsField() {
        return (word & INDICATORS_MASK) >>> 8;
    }

    public boolean isContextCountEnabled() {
        return (word & CTX_COUNT_EN_MASK) != 0;
    }

    public int getContextPacketCount() {
        return word & CTX_COUNT_MASK;
    }

    public boolean isCalibratedTimeEnabled() {
        return (word & (1 << 31)) != 0;
    }

    public boolean isValidDataEnabled() {
        return (word & (1 << 30)) != 0;
    }

    public boolean isReferenceLockEnabled() {
        return (word & (1 << 29)) != 0;
    }

    public boolean isAgcMgcEnabled() {
        return (word & (1 << 28)) != 0;
    }

    public boolean isDetectedSignalEnabled() {
        return (word & (1 << 27)) != 0;
    }

    public boolean isSpectralInversionEnabled() {
        return (word & (1 << 26)) != 0;
    }

    public boolean isOverrangeEnabled() {
        return (word & (1 << 25)) != 0;
    }

    public boolean isSampleLossEnabled() {
        return (word & (1 << 24)) != 0;
    }

    // indicator bit getters

    public boolean isCalibratedTime() {
        return (word & (1 << 19)) != 0;
    }

    public boolean isValidData() {
        return (word & (1 << 18)) != 0;
    }

    public boolean isReferenceLock() {
        return (word & (1 << 17)) != 0;
    }

    public boolean isAgcActive() {
        return (word & (1 << 16)) != 0;
    } // AGC=1, MGC=0

    public boolean isDetectedSignal() {
        return (word & (1 << 15)) != 0;
    }

    public boolean isSpectralInversion() {
        return (word & (1 << 14)) != 0;
    }

    public boolean isOverrange() {
        return (word & (1 << 13)) != 0;
    }

    public boolean isSampleLoss() {
        return (word & (1 << 12)) != 0;
    }

    // user defined enable bits

    public boolean isUserEnable1() {
        return (word & (1 << 23)) != 0;
    }

    public boolean isUserEnable2() {
        return (word & (1 << 22)) != 0;
    }

    public boolean isUserEnable3() {
        return (word & (1 << 21)) != 0;
    }

    public boolean isUserEnable4() {
        return (word & (1 << 20)) != 0;
    }

    // user defined indicator bits

    public boolean isUserIndicator1() {
        return (word & (1 << 11)) != 0;
    }

    public boolean isUserIndicator2() {
        return (word & (1 << 10)) != 0;
    }

    public boolean isUserIndicator3() {
        return (word & (1 << 9)) != 0;
    }

    public boolean isUserIndicator4() {
        return (word & (1 << 8)) != 0;
    }

    @Override
    public String toString() {
        return String.format("VrtTrailer[enables=0x%03X, indicators=0x%03X, ctxCountEn=%b, ctxCount=%d]",
                getEnablesField(), getIndicatorsField(), isContextCountEnabled(), getContextPacketCount());
    }
}
