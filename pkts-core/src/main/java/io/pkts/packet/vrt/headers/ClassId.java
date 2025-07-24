package io.pkts.packet.vrt.headers;

import io.pkts.buffer.Buffer;

public class ClassId {
    private int padBitCount;
    private int oui;
    private short infoClassCode;
    private short packetClassCode;

    public ClassId(Buffer buffer) {

    }
}
