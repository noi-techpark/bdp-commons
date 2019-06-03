package it.bz.idm.bdp.augeg4.fun.convert;

import it.bz.idm.bdp.augeg4.dto.MeasurementId;

/**
 * Class for the objects in the CSV files with the id mappings.
 */
public class MeasurementMapping {

    private final MeasurementId id;
    private final int processedId;
    private final String dataType;
    private final String unit;
    private final String description;
    private final String rtype;

    public MeasurementMapping(MeasurementId id, int processedId, String dataType, String unit, String description, String rtype) {
        this.id = id;
        this.processedId = processedId;
        this.dataType = dataType;
        this.unit = unit;
        this.description = description;
        this.rtype = rtype;
    }

    public MeasurementId getId() {
        return id;
    }

    public int getProcessedId() {
        return processedId;
    }

    public String getDataType() {
        return dataType;
    }

    public String getDescription() {
        return description;
    }

    public String getUnit() {
        return unit;
    }

    public String getRtype() {
        return rtype;
    }
}
