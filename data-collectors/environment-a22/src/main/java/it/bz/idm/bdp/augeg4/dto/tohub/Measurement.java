package it.bz.idm.bdp.augeg4.dto.tohub;


public class Measurement {

    private final String dataType;

    private final double value;

    public Measurement(String dataType, double value) {
        this.dataType = dataType;
        this.value = value;
    }

    public String getDataType() {
        return dataType;
    }

    public double getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Measurement{" +
                "dataType='" + dataType + '\'' +
                ", value=" + value +
                '}';
    }
}
