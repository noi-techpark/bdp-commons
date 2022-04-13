package it.bz.idm.bdp.augeg4.fun.convert.tohub;

import it.bz.idm.bdp.augeg4.fun.utils.CsvUtils;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class StationMappings {

    private static final String STATION_MAPPINGS_FILE_NAME = "/mappings/stationMappings.csv";

    private static final String COLUMN_CONTROL_UNIT_ID = "controlUnitId";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_LATITUDE = "latitude";
    private static final String COLUMN_LONGITUDE = "longitude";

    private static final Logger LOG = LoggerFactory.getLogger(StationMappings.class.getName());

    private final Map<String, StationMapping> stationByControlUnitId = new HashMap<>();

    public StationMappings() {
        loadNameMappingsFromCsvFile();
    }

    private void loadNameMappingsFromCsvFile() {
        try {
            CsvUtils.readCsvFileFromResources(STATION_MAPPINGS_FILE_NAME)
                    .forEach(this::insertStationMappingInMapByCsvRecord);
        } catch (Exception e) {
            LOG.error("loadNameMappingsFromCsvFile() failed: {}", e.getMessage());
            throw new IllegalStateException("can't read " + STATION_MAPPINGS_FILE_NAME, e);
        }
    }

    private void insertStationMappingInMapByCsvRecord(CSVRecord record) {
        StationMapping mapping = getStationMappingFromCsvRecord(record);
        stationByControlUnitId.put(mapping.getControlUnitId(), mapping);
    }

    private StationMapping getStationMappingFromCsvRecord(CSVRecord record) {
        return new StationMapping(
                record.get(COLUMN_CONTROL_UNIT_ID),
                record.get(COLUMN_NAME),
                Double.parseDouble(record.get(COLUMN_LATITUDE)),
                Double.parseDouble(record.get(COLUMN_LONGITUDE))
        );
    }

    public Optional<StationMapping> getMapping(String controlUnitId) {
        StationMapping mapping = stationByControlUnitId.get(controlUnitId);
        if (mapping == null) {
            LOG.warn("getMapping() called with an unknown control unit id: {}", controlUnitId);
        }
        return Optional.ofNullable(mapping);
    }
}
