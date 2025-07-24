package io.pkts.packet.vrt.impl;

import io.pkts.buffer.Buffer;
import io.pkts.packet.Packet;
import io.pkts.packet.PacketParseException;
import io.pkts.packet.TransportPacket;
import io.pkts.packet.impl.AbstractPacket;
import io.pkts.packet.vrt.VrtPacket;
import io.pkts.packet.vrt.headers.ClassId;
import io.pkts.packet.vrt.headers.VrtPacketHeader;
import io.pkts.protocol.Protocol;

import java.io.IOException;
import java.io.OutputStream;

public class VrtPacketImpl extends AbstractPacket implements VrtPacket {

    private final TransportPacket parent;
    private final Buffer headers;
    private final Buffer payload;

    public VrtPacketImpl(final TransportPacket parent, final Buffer payload, final Buffer headers) {
        super(Protocol.VRT, parent, payload);
        this.parent = parent;
        this.headers = headers;
        this.payload = payload;
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
    public VrtPacketHeader getHeader() {
        return null;
    }

    @Override
    public int getStreamId() {
        return 0;
    }

    @Override
    public ClassId getClassId() {
        return null;
    }

    @Override
    public boolean hasIntegerTimestamp() {
        return false;
    }

    @Override
    public int getIntegerTimestamp() {
        return 0;
    }

    @Override
    public boolean hasFractionalTimestamp() {
        return false;
    }

    @Override
    public int getFractionalTimestamp() {
        return 0;
    }
    @Override
    public byte[] payload() {
        return new byte[0];
    }

    @Override
    public TransportPacket getParentPacket() {
        return (TransportPacket) super.getParentPacket();
    }
}
