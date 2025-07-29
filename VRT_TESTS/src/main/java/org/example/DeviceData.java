package org.example;

import io.pkts.packet.vrt.VrtPacket;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import java.util.ArrayList;

public class DeviceData {

    private final SimpleStringProperty deviceName;
    private final SimpleStringProperty streamId;
    private final SimpleStringProperty ipAddress;
    private final SimpleIntegerProperty packetCount;

    private final ArrayList<VrtPacket> packets;

    public DeviceData(String deviceName, String streamId, String ipAddress) {
        this.deviceName = new SimpleStringProperty(deviceName);
        this.streamId = new SimpleStringProperty(streamId);
        this.ipAddress = new SimpleStringProperty(ipAddress);
        this.packetCount = new SimpleIntegerProperty(0);
        this.packets = new ArrayList<>();
    }

    void addPacket(VrtPacket packet) {
        packets.add(packet);
        setPacketCount(packets.size());
    }

    ArrayList<VrtPacket> getPackets() {
        return packets;
    }

    String getDeviceName() {
        return deviceName.get();
    }

    void setDeviceName(String deviceName) {
        this.deviceName.set(deviceName);
    }

    SimpleStringProperty deviceNameProperty() {
        return deviceName;
    }

    String getStreamId() {
        return streamId.get();
    }

    void setStreamId(String streamId) {
        this.streamId.set(streamId);
    }

    SimpleStringProperty streamIdProperty() {
        return streamId;
    }

    String getIpAddress() {
        return ipAddress.get();
    }

    void setIpAddress(String ipAddress) {
        this.ipAddress.set(ipAddress);
    }

    SimpleStringProperty ipAddressProperty() {
        return ipAddress;
    }

    int getPacketCount() {
        return packetCount.get();
    }

    void setPacketCount(int packetCount) {
        this.packetCount.set(packetCount);
    }

    SimpleIntegerProperty packetCountProperty() {
        return packetCount;
    }

}