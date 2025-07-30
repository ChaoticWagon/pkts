package io.pkts.packet.vrt;

import io.pkts.buffer.Buffer;
import io.pkts.packet.TransportPacket;
import io.pkts.packet.impl.ApplicationPacket;
import io.pkts.packet.vrt.headers.VrtHeaders;
import io.pkts.packet.vrt.payload.SignalDataPayload;

import java.util.Optional;

public interface VrtPacket extends ApplicationPacket {

    VrtHeaders getHeaders();

    Buffer payload();

    Optional<SignalDataPayload> getSignalDataPayload();

    @Override
    TransportPacket getParentPacket();
}
