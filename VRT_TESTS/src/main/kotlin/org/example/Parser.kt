package org.example

import io.pkts.Pcap
import io.pkts.packet.IPv4Packet
import io.pkts.packet.MACPacket
import io.pkts.packet.vrt.VrtPacket
import io.pkts.protocol.Protocol
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import java.io.File
import kotlin.jvm.optionals.getOrDefault

object Parser {

    fun parse(file: File): ObservableList<DeviceData> {
        var count: Int = 6
        val pcap: Pcap = Pcap.openStream(file)
        val deviceMap = FXCollections.observableHashMap<String,DeviceData>()

        pcap.loop { packet ->
            try {

                val vrt: VrtPacket = packet.getPacket(Protocol.VRT) as VrtPacket
                count++
                val streamId: Int = vrt.headers.streamId.getOrDefault(0)
                val ipv4: IPv4Packet = vrt.getPacket(Protocol.IPv4) as IPv4Packet
                val ip: String = ipv4.sourceIP.toString()
                val ether: MACPacket = vrt.getPacket(Protocol.ETHERNET_II) as MACPacket
                val mac: String = ether.sourceMacAddress

                val device = deviceMap.getOrPut(mac) {
                    DeviceData(
                        mac,
                        if (streamId == 0) "NONE" else streamId.toHexString(),
                        ip
                    )
                }

                device.addPacket(vrt)

            } catch (e: Exception) {
// do nothing for now
            }

            true

        }

        return FXCollections.observableArrayList<DeviceData>(deviceMap.values);
    }
}