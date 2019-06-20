package it.bz.idm.bdp.augeg4.dto;

import java.util.Objects;

public class MeasurementId {

    private final int value;

    public MeasurementId(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MeasurementId)) return false;
        MeasurementId that = (MeasurementId) o;
        return getValue() == that.getValue();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getValue());
    }

    @Override
    public String toString() {
        return "MeasurementId{" +
                "value=" + value +
                '}';
    }
}
