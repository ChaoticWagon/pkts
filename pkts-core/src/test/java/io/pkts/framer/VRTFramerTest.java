package io.pkts.framer;

import io.pkts.PacketHandler;
import io.pkts.Pcap;
import io.pkts.PktsTestBase;
import io.pkts.packet.Packet;
import io.pkts.packet.vrt.VrtPacket;
import io.pkts.protocol.Protocol;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

public class VRTFramerTest extends PcapFramerTest {

    @Test
    public void testVRT() throws IOException {
        InputStream stream = PktsTestBase.class.getResourceAsStream("context_example.pcap");
        final Pcap pcap = Pcap.openStream(stream);

        pcap.loop(new PacketHandler() {
            @Override
            public boolean nextPacket(Packet packet) throws IOException {
                if (packet.hasProtocol(Protocol.UDP)) {
                    System.out.println(((VrtPacket) packet.getPacket(Protocol.VRT)).getHeaders().toString());
                }
                return true;
            }
        });
    }

}
