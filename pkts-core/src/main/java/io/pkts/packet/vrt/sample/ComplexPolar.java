package io.pkts.packet.vrt.sample;

import io.pkts.packet.vrt.SignalPayloadFormat;

import java.util.Optional;

public record ComplexPolar(Number amplitude, Number phase, Optional<Integer> channel,
                           Optional<Integer> eventBits) implements Sample {
    @Override
    public SignalPayloadFormat.SampleKind getSampleType() {
        return SignalPayloadFormat.SampleKind.COMPLEX_POLAR;
    }
}
