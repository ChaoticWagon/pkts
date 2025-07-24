package io.pkts.packet.vrt.headers;

import java.util.Optional;

public record VrtHeaders(
        VrtPacketHeader packetHeader,
        Optional<Integer> streamld,
        Optional<ClassId> classId,
        Optional<Integer> integerTimestamp,
        Optional<Long> longTimestamp,
        Optional<VrtTrailer> trailer

) {}
