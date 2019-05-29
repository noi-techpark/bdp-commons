package it.bz.idm.bdp.augeg4.fun.process;

import it.bz.idm.bdp.augeg4.dto.MeasurementId;
import it.bz.idm.bdp.augeg4.fun.utils.CsvUtils;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Collection of MeasurementParameter used by MeasurementProcessor
 */
public class MeasurementProcessorParameters {

    private static final String PARAMETERS_FILE = "/mappings/processorParameters.csv";

    private static final String COLUMN_CONTROL_UNIT_ID = "Stazione";
    private static final String COLUMN_MEASUREMENT_ID = "Inquinante";

    private static final Logger LOG = LogManager.getLogger(MeasurementProcessorParameters.class.getName());

    private final Map<MeasurementParametersId, MeasurementParameters> parametersByMeasurementParametersId = new HashMap<>();


    public MeasurementProcessorParameters() {
        loadParametersList();
    }

    private void loadParametersList() {
        try {
            CsvUtils.readCsvFileFromResources(PARAMETERS_FILE)
                    .forEach(this::insertMeasurementParametersInMapFromCsvRecord);
        } catch (Exception e) {
            LOG.error("loadNameMappingsFromCsvFile() failed: {}", e.getMessage());
            throw new IllegalStateException("can't read " + PARAMETERS_FILE, e);
        }
    }

    private void insertMeasurementParametersInMapFromCsvRecord(CSVRecord record) {
        MeasurementParametersId id = getMeasurementParametersId(record);
        parametersByMeasurementParametersId.put(id, new MeasurementParameters(
                id,
                Double.parseDouble(record.get("a")),
                Double.parseDouble(record.get("b")),
                Double.parseDouble(record.get("c")),
                Double.parseDouble(record.get("d")),
                Double.parseDouble(record.get("e")),
                Double.parseDouble(record.get("f"))
        ));
    }

    private MeasurementParametersId getMeasurementParametersId(CSVRecord record) {
        return new MeasurementParametersId(
                record.get(COLUMN_CONTROL_UNIT_ID),
                new MeasurementId(Integer.parseInt(record.get(COLUMN_MEASUREMENT_ID)))
        );
    }

    Optional<MeasurementParameters> getMeasurementParameters(String controlUnitId, MeasurementId measurementId) {
        MeasurementParametersId id = new MeasurementParametersId(controlUnitId, measurementId);
        MeasurementParameters parameters = parametersByMeasurementParametersId.get(id);
        if (parameters == null) {
            LOG.warn("getMeasurementParameters() called with an unknown id: {}", id);
        }
        return Optional.ofNullable(parameters);
    }

    boolean hasMeasurementParameters(String controlUnitId, MeasurementId measurementId){
        return parametersByMeasurementParametersId.containsKey(
                new MeasurementParametersId(controlUnitId, measurementId));
    }


}
