package it.bz.idm.bdp.augeg4.dto.tohub;


public class Measurement {

    private final String dataType;

    private final double rawValue;

    private final double processedValue;

    public Measurement(String dataType, double rawValue, double processedValue) {
        this.dataType = dataType;
        this.rawValue = rawValue;
        this.processedValue = processedValue;
    }

    public String getDataType() {
        return dataType;
    }

    public double getRawValue() {
        return rawValue;
    }

    public double getProcessedValue() {
        return processedValue;
    }

    @Override
    public String toString() {
        return "Measurement{" +
                "dataType='" + dataType + '\'' +
                ", rawValue=" + rawValue +
                ", processedValue=" + processedValue +
                '}';
    }
}
