package io.pkts.packet.vrt;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A simple in‑memory registry of SignalPayloadFormat descriptors, keyed by
 * either (StreamID) or (OUI, InfoClass, PacketClass) from the ClassID.
 */
public final class SignalFormatRegistry {

    private static final SignalFormatRegistry instance = new SignalFormatRegistry();

    /** Keyed by StreamID (Integer) */
    private final Map<Integer, SignalPayloadFormat> byStreamId = new ConcurrentHashMap<>();

    /** Keyed by full ClassID triple: OUI-InfoCode-PacketCode */
    private final Map<ClassIdKey, SignalPayloadFormat> byClassId = new ConcurrentHashMap<>();

    private SignalFormatRegistry() { }

    public static SignalFormatRegistry getInstance() {
        return instance;
    }

    /**
     * Register a format based on Stream ID.
     * This is the usual path for simple systems where each stream is known.
     */
    public void register(int streamId, SignalPayloadFormat fmt) {
        byStreamId.put(streamId, fmt);
    }

    /**
     * Register a format based on ClassID triple.
     */
    public void register(long oui, int infoClass, int packetClass, SignalPayloadFormat fmt) {
        byClassId.put(new ClassIdKey(oui, infoClass, packetClass), fmt);
    }

    /**
     * Lookup first by StreamID, then by ClassID if you have one.
     */
    public Optional<SignalPayloadFormat> lookup(Optional<Integer> streamId,
                                                Optional<ClassIdKey> classId) {
        if (streamId.isPresent()) {
            SignalPayloadFormat fmt = byStreamId.get(streamId.get());
            if (fmt != null) {
                return Optional.of(fmt);
            }
        }
        if (classId.isPresent()) {
            SignalPayloadFormat fmt = byClassId.get(classId.get());
            if (fmt != null) {
                return Optional.of(fmt);
            }
        }
        return Optional.empty();
    }

    /** Immutable key for ClassID-based lookup. */
    public static final class ClassIdKey {
        public final long oui;
        public final int infoClass;
        public final int packetClass;
        public ClassIdKey(long oui, int infoClass, int packetClass) {
            this.oui         = oui;
            this.infoClass   = infoClass;
            this.packetClass = packetClass;
        }
        @Override
        public boolean equals(Object o) {
            if (!(o instanceof ClassIdKey k)) return false;
            return k.oui == oui
                    && k.infoClass == infoClass
                    && k.packetClass == packetClass;
        }
        @Override
        public int hashCode() {
            int h = Long.hashCode(oui);
            h = 31*h + infoClass;
            h = 31*h + packetClass;
            return h;
        }
    }
}
