package it.bz.idm.bdp.augeg4.fun.process;

import it.bz.idm.bdp.augeg4.dto.MeasurementId;
import it.bz.idm.bdp.augeg4.fun.utils.CsvUtils;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.math.BigDecimal;
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
    private static final String COLUMN_TEMPERATURE_LEVEL = "Temperatura";

    private static final Logger LOG = LoggerFactory.getLogger(MeasurementProcessorParameters.class.getName());
    public static final double HIGH_TEMPERATURE_VALUE = 20.0;
    public static final String HIGH_TEMPERATURE_LABEL = "20";

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
                new BigDecimal(record.get("a")),
                new BigDecimal(record.get("b")),
                new BigDecimal(record.get("c")),
                new BigDecimal(record.get("d")),
                new BigDecimal(record.get("e")),
                new BigDecimal(record.get("f"))
        ));
    }

    private MeasurementParametersId getMeasurementParametersId(CSVRecord record) {
        //System.out.println("temp_level="+record.get(COLUMN_TEMPERATURE_LEVEL));
        return new MeasurementParametersId(
                record.get(COLUMN_CONTROL_UNIT_ID),
                new MeasurementId(Integer.parseInt(record.get(COLUMN_MEASUREMENT_ID))),
                record.get(COLUMN_TEMPERATURE_LEVEL));
    }

    Optional<MeasurementParameters> getMeasurementParameters(String controlUnitId, MeasurementId measurementId, Double temperature) {
        String temperatureLevel;
        if (temperature != null && temperature < HIGH_TEMPERATURE_VALUE) temperatureLevel = HIGH_TEMPERATURE_LABEL;
        else temperatureLevel = "";
        MeasurementParametersId id = new MeasurementParametersId(controlUnitId, measurementId,temperatureLevel);
        MeasurementParameters parameters = parametersByMeasurementParametersId.get(id);
        if (parameters == null) {
            LOG.warn("getMeasurementParameters() called with an unknown id: {}", id);
        }
        return Optional.ofNullable(parameters);
    }

    boolean hasMeasurementParameters(String controlUnitId, MeasurementId measurementId, String temperature_level){
        return parametersByMeasurementParametersId.containsKey(
                new MeasurementParametersId(controlUnitId, measurementId, temperature_level));
    }


}
