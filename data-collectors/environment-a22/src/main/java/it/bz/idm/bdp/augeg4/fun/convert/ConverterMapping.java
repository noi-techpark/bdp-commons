package it.bz.idm.bdp.augeg4.fun.convert;

/**
 * Class for the objects in the JSON files with the converter mappings.
 */
public class ConverterMapping {

    private int linearizedId;
    private String dataType;
    private String unit;
    private String description;
    private String rtype;

    public int getLinearizedId() {
        return linearizedId;
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
