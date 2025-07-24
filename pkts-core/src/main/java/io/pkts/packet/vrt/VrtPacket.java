package io.pkts.packet.vrt;

import io.pkts.packet.TransportPacket;
import io.pkts.packet.impl.ApplicationPacket;
import io.pkts.packet.vrt.headers.ClassId;
import io.pkts.packet.vrt.headers.VrtPacketHeader;

public interface VrtPacket extends ApplicationPacket {

    VrtPacketHeader getHeader();

    default boolean hasStreamId() {
        return getHeader().getVrtType() != VrtType.SIGNAL_DATA_PACKET || getHeader().getVrtType() != VrtType.EXTENSION_CONTEXT_PACKET;
    }

    int getStreamId();

    default boolean hasClassId() {
        return getHeader().hasClass();
    }

    ClassId getClassId();

    boolean hasIntegerTimestamp();

    int getIntegerTimestamp();

    boolean hasFractionalTimestamp();

    int getFractionalTimestamp();

    byte[] payload();

    @Override
    TransportPacket getParentPacket();
}
