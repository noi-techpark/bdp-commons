package it.bz.idm.bdp.augeg4.fun.convert;

import it.bz.idm.bdp.augeg4.dto.MeasurementId;
import it.bz.idm.bdp.augeg4.fun.utils.CsvUtils;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MeasurementMappings {

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_PROCESSED_ID = "processedId";
    private static final String COLUMN_DATA_TYPE = "dataType";
    private static final String COLUMN_UNIT = "unit";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_RTYPE = "rtype";
    private static final String COLUMN_TO_PROCESS = "toProcess";
    private static final String DATA_TYPE_NAME_SUFFIX_RAW = "_raw";
    private static final String DATA_TYPE_NAME_SUFFIX_PROCESSED = "_processed";
    private static final Logger LOG = LogManager.getLogger(MeasurementMappings.class.getName());

    private static final String MEASUREMENT_MAPPINGS_FILE_NAME = "/mappings/measurementMappings.csv";

    private final Map<MeasurementId, MeasurementMapping> mappingById = new HashMap<>();

    public MeasurementMappings() {
        loadMappingsFromCsvFile();
    }

    private void loadMappingsFromCsvFile (){
        try {
            CsvUtils.readCsvFileFromResources(MEASUREMENT_MAPPINGS_FILE_NAME)
                    .forEach(this::insertMappingInMapByCsvRecord);
        } catch (Exception e) {
            LOG.error("loadMappingsFromCsvFile() failed: {}", e.getMessage());
            throw new IllegalStateException("can't read " + MEASUREMENT_MAPPINGS_FILE_NAME, e);
        }
    }

    private void insertMappingInMapByCsvRecord(CSVRecord record) {
                MeasurementMapping rawDatatype = new MeasurementMapping(
                        new MeasurementId(Integer.parseInt(record.get(COLUMN_ID))),
                        Integer.parseInt(record.get(COLUMN_PROCESSED_ID)),
                        record.get(COLUMN_DATA_TYPE)+DATA_TYPE_NAME_SUFFIX_RAW,
                        record.get(COLUMN_UNIT),
                        record.get(COLUMN_DESCRIPTION),
                        record.get(COLUMN_RTYPE)
                );
                mappingById.put(rawDatatype.getId(), rawDatatype);
                if ("true".equals(record.get(COLUMN_TO_PROCESS).toLowerCase())) {
                    MeasurementMapping processedDatatype = new MeasurementMapping(
                            new MeasurementId(Integer.parseInt(record.get(COLUMN_ID))*-1),
                            Integer.parseInt(record.get(COLUMN_PROCESSED_ID)),
                            record.get(COLUMN_DATA_TYPE)+DATA_TYPE_NAME_SUFFIX_PROCESSED,
                            record.get(COLUMN_UNIT),
                            record.get(COLUMN_DESCRIPTION),
                            record.get(COLUMN_RTYPE)
                    );
                    mappingById.put(processedDatatype.getId(), processedDatatype);

                }
    }

    public Optional<MeasurementMapping> getMapping(MeasurementId id) {
        MeasurementMapping mapping = mappingById.get(id);
        if (mapping == null) {
            LOG.warn("getMapping() called with an unknown id: {}", id);
        }
        return Optional.ofNullable(mapping);
    }

    public Collection<MeasurementMapping> getMappings() {
        return mappingById.values();
    }
}
