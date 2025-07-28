package io.pkts.packet.vrt.headers;

import io.pkts.buffer.Buffer;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

public class VrtCif {

    /**
     * Which CIF word (0…7).
     */
    public enum Level {
        CIF0(0), CIF1(1), CIF2(2), CIF3(3), CIF4(4), CIF5(5), CIF6(6), CIF7(7);
        private final int bit;

        Level(final int bit) {
            this.bit = bit;
        }

        public int getEnableBit() {
            return bit;
        }
    }

    /**
     * All known CIF fields (Table 9.1‑1) with level, bit position, and a human name.
     */
    public enum CifField {
        // --- CIF0 fields (Legacy + enable bits) ---
        CONTEXT_FIELD_CHANGE_INDICATOR     (Level.CIF0, 31, "Context Field Change Indicator"),
        REFERENCE_POINT_IDENTIFIER         (Level.CIF0, 30, "Reference Point Identifier"),
        BANDWIDTH                          (Level.CIF0, 29, "Bandwidth"),
        IF_REFERENCE_FREQUENCY             (Level.CIF0, 28, "IF Reference Frequency"),
        RF_REFERENCE_FREQUENCY             (Level.CIF0, 27, "RF Reference Frequency"),
        RF_REFERENCE_FREQUENCY_OFFSET      (Level.CIF0, 26, "RF Reference Frequency Offset"),
        IF_BAND_OFFSET                     (Level.CIF0, 25, "IF Band Offset"),
        REFERENCE_LEVEL                    (Level.CIF0, 24, "Reference Level"),
        GAIN                               (Level.CIF0, 23, "Gain"),
        OVER_RANGE_COUNT                   (Level.CIF0, 22, "Over‑range Count"),
        SAMPLE_RATE                        (Level.CIF0, 21, "Sample Rate"),
        TIMESTAMP_ADJUSTMENT               (Level.CIF0, 20, "Timestamp Adjustment"),
        TIMESTAMP_CALIBRATION_TIME         (Level.CIF0, 19, "Timestamp Calibration Time"),
        TEMPERATURE                        (Level.CIF0, 18, "Temperature"),
        DEVICE_IDENTIFIER                  (Level.CIF0, 17, "Device Identifier"),
        STATE_EVENT_INDICATORS             (Level.CIF0, 16, "State/Event Indicators"),
        SIGNAL_DATA_PAYLOAD_FORMAT         (Level.CIF0, 15, "Signal Data Packet Payload Format"),
        FORMATTED_GPS                      (Level.CIF0, 14, "Formatted GPS"),
        FORMATTED_INS                      (Level.CIF0, 13, "Formatted INS"),
        ECEF_EPHEMERIS                     (Level.CIF0, 12, "ECEF Ephemeris"),
        RELATIVE_EPHEMERIS                 (Level.CIF0, 11, "Relative Ephemeris"),
        EPHEMERIS_REF_ID                   (Level.CIF0, 10, "Ephemeris Ref ID"),
        GPS_ASCII                          (Level.CIF0, 9, "GPS ASCII"),
        CONTEXT_ASSOCIATION_LISTS          (Level.CIF0, 8, "Context Association Lists"),
        FIELD_ATTRIBUTES_ENABLE            (Level.CIF0, 7, "Field Attributes Enable"),
        CIF3_ENABLE                        (Level.CIF0, 3, "CIF3 Enable"),
        CIF2_ENABLE                        (Level.CIF0, 2, "CIF2 Enable"),
        CIF1_ENABLE                        (Level.CIF0, 1, "CIF1 Enable"),

        // --- CIF1 fields                 (Spatial, Signal, Spectral, I/O, Control identifiers) ---
        AUX_BANDWIDTH                      (Level.CIF1, 13, "Aux Bandwidth"),
        ARRAY_OF_CIFS                      (Level.CIF1, 11, "Array of CIFs"),
        SPECTRUM                           (Level.CIF1, 10, "Spectrum"),
        SECTOR_SCAN_STEP                   (Level.CIF1, 9, "Sector Scan/Step"),
        INDEX_LIST                         (Level.CIF1, 7, "Index List"),
        DISCRETE_IO_32                     (Level.CIF1, 6, "Discrete I/O                                      (32‑bit)"),
        DISCRETE_IO_64                     (Level.CIF1, 5, "Discrete I/O                                      (64‑bit)"),
        HEALTH_STATUS                      (Level.CIF1, 4, "Health Status"),
        V49_SPEC_COMPLIANCE                (Level.CIF1, 3, "V49 Spec Compliance"),
        VERSION_BUILD                      (Level.CIF1, 2, "Version and Build Code"),
        BUFFER_SIZE                        (Level.CIF1, 1, "Buffer Size"),

        // --- CIF2 fields                 (Identifiers/tags) ---
        EMS_DEVICE_TYPE                    (Level.CIF2, 13, "EMS Device Type"),
        EMS_DEVICE_INSTANCE                (Level.CIF2, 12, "EMS Device Instance"),
        MODULATION_CLASS                   (Level.CIF2, 11, "Modulation Class"),
        MODULATION_TYPE                    (Level.CIF2, 10, "Modulation Type"),
        FUNCTION_ID                        (Level.CIF2, 9, "Function ID"),
        FUNCTION_PRIORITY_ID               (Level.CIF2, 6, "Function Priority ID"),
        COMM_PRIORITY_ID                   (Level.CIF2, 5, "Communication Priority ID"),
        RF_FOOTPRINT                       (Level.CIF2, 4, "RF Footprint"),
        RF_FOOTPRINT_RANGE                 (Level.CIF2, 3, "RF Footprint Range"),

        // --- CIF3 fields                 (Temporal, Environmental) ---
        TIMESTAMP_DETAILS                  (Level.CIF3, 31, "Timestamp Details"),
        TIMESTAMP_SKEW                     (Level.CIF3, 30, "Timestamp Skew"),
        SIBLINGS_SID                       (Level.CIF3, 29, "Sibling                                          (s) SID"),
        PARENTS_SID                        (Level.CIF3, 28, "Parent                                           (s) SID"),
        CHILDREN_SID                       (Level.CIF3, 27, "Children SID"),
        CITED_MESSAGE_ID                   (Level.CIF3, 26, "Cited Message ID"),
        EVENT_ID                           (Level.CIF3, 7, "Event ID"),
        AIR_TEMPERATURE                    (Level.CIF3, 7, "Air Temperature"),          // actually bit 7
        SEA_GROUND_TEMPERATURE             (Level.CIF3, 6, "Sea/Ground Temperature"),
        HUMIDITY                           (Level.CIF3, 5, "Humidity"),
        BAROMETRIC_PRESSURE                (Level.CIF3, 4, "Barometric Pressure"),
        SEA_SWELL_STATE                    (Level.CIF3, 3, "Sea and Swell State"),
        TROPOSPHERIC_STATE                 (Level.CIF3, 2, "Tropospheric State"),
        NETWORK_ID                         (Level.CIF3, 1, "Network ID"),

        // --- CIF7 fields                 (Attributes—reserved for future) ---
        AIR_TEMPERATURE_ATTR               (Level.CIF7, 7, "Air Temperature"),    // reuse names to illustrate
        BAROMETRIC_PRESSURE_ATTR           (Level.CIF7, 4, "Barometric Pressure")
        // … add more as V49.x defines them

        ;

        public final Level level;
        public final int bitPosition;
        public final String description;

        CifField(final Level level, final int bitPosition, final String description) {
            this.level = level;
            this.bitPosition = bitPosition;
            this.description = description;
        }
    }

    private final Map<Level, Integer> words = new EnumMap<>(Level.class);

    public VrtCif(final Buffer buffer) {
        // read CIF0
        final int cif0 = (int) buffer.readUnsignedInt();
        words.put(Level.CIF0, cif0);

        // read optional CIF1…CIF7 in numeric order
        for (Level lvl : Level.values()) {
            if (lvl == Level.CIF0) continue;
            // bit n in CIF0 enables CIF<n>
            if ((cif0 & (1 << lvl.getEnableBit())) != 0) {
                words.put(lvl, (int) buffer.readUnsignedInt());
            }
        }
    }

    /**
     * Which CIF levels are present in this packet.
     */
    public Set<Level> getPresentLevels() {
        return words.keySet();
    }

    /**
     * The raw 32‑bit word for a given CIF level, or 0 if absent.
     */
    public int getWord(final Level level) {
        return words.getOrDefault(level, 0);
    }

    /**
     * True if the given CIF field bit is set.
     */
    public boolean isSet(final CifField field) {
        return (getWord(field.level) & (1 << field.bitPosition)) != 0;
    }

    /**
     * Human‑readable name for a field.
     */
    public String getDescription(final CifField field) {
        return field.description;
    }

    /**
     * Return a map of all CIF fields whose bit is set, mapping each to Boolean.TRUE.
     */
    public Map<CifField, Boolean> getEnabledFields() {
        Map<CifField, Boolean> enabled = new EnumMap<>(CifField.class);
        for (CifField field : CifField.values()) {
            if (isSet(field)) {
                enabled.put(field, Boolean.TRUE);
            }
        }
        return enabled;
    }

    @Override
    public String toString() {
        return "VrtCif" + words.entrySet().stream()
                .map(e -> e.getKey() + "=0x" + Integer.toHexString(e.getValue()))
                .toList();
    }
}