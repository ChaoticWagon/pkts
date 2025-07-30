package io.pkts.packet.vrt.impl;

import io.pkts.buffer.Buffer;
import io.pkts.packet.Packet;
import io.pkts.packet.PacketParseException;
import io.pkts.packet.TransportPacket;
import io.pkts.packet.impl.AbstractPacket;
import io.pkts.packet.vrt.VrtPacket;
import io.pkts.packet.vrt.headers.VrtHeaders;
import io.pkts.packet.vrt.payload.SignalDataPayload;
import io.pkts.packet.vrt.payload.VrtContextSession;
import io.pkts.protocol.Protocol;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Optional;

public class VrtPacketImpl extends AbstractPacket implements VrtPacket {

    private final VrtHeaders headers;
    private final Buffer payload;
    private final Optional<SignalDataPayload> sigPayload;
    private final Optional<VrtContextSession> ctx;

    public VrtPacketImpl(final TransportPacket parent, final VrtHeaders headers, final Buffer payload, final Optional<SignalDataPayload> sigPayload, Optional<VrtContextSession> ctx) {
        super(Protocol.VRT, parent, payload);
        this.headers = headers;
        this.payload = payload;
        this.sigPayload = sigPayload;
        this.ctx = ctx;
    }

    @Override
    public long getArrivalTime() {
        return 0;
    }

    @Override
    public void write(OutputStream out, Buffer payload) throws IOException {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public Packet clone() {
        return null;
    }

    @Override
    public Packet getNextPacket() throws IOException, PacketParseException {
        return null;
    }

    @Override
    public VrtHeaders getHeaders() {
        return headers;
    }

    @Override
    public Buffer payload() {
        return payload;
    }

    @Override
    public Optional<SignalDataPayload> getSignalDataPayload() {
        return sigPayload;
    }

    @Override
    public Optional<VrtContextSession> getContextSession() {
        return ctx;
    }

    @Override
    public TransportPacket getParentPacket() {
        return (TransportPacket) super.getParentPacket();
    }
}
