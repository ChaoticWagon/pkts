package io.pkts.packet.vrt.sample;

import io.pkts.packet.vrt.SignalPayloadFormat;

/* ------------------------------------------------------------------
 * Simple POJOs representing one decoded sample.
 * ------------------------------------------------------------------ */
public sealed interface Sample permits RealSample, ComplexCartesian, ComplexPolar {
    SignalPayloadFormat.SampleKind getSampleType();
}

