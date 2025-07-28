package io.pkts.packet.vrt.headers;

import java.util.Optional;

public record VrtHeaders(
        VrtPacketHeader packetHeader,
        Optional<Integer> streamld,
        Optional<VrtClassIdentifier> classId,
        Optional<Integer> integerTimestamp,
        Optional<Long> longTimestamp,
        Optional<VrtCif> cif_fields,
        Optional<VrtTrailer> trailer

) {}
