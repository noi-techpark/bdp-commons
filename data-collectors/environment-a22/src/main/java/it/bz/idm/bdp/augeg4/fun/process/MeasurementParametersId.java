package it.bz.idm.bdp.augeg4.fun.process;

import it.bz.idm.bdp.augeg4.dto.MeasurementId;

import java.util.Objects;

public class MeasurementParametersId {
    private final String controlUnitId;
    private final MeasurementId measurementId;
    private final String temperatureLevel;

    public MeasurementParametersId(String controlUnitId, MeasurementId measurementId, String temperature_level) {
        this.controlUnitId = controlUnitId;
        this.measurementId = measurementId;
        this.temperatureLevel = temperature_level;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MeasurementParametersId)) return false;
        MeasurementParametersId id = (MeasurementParametersId) o;
        return controlUnitId.equals(id.controlUnitId) &&
                measurementId.equals(id.measurementId) &&
                temperatureLevel.equals(id.temperatureLevel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(controlUnitId, measurementId, temperatureLevel);
    }

    @Override
    public String toString() {
        return "MeasurementParametersId{" +
                "controlUnitId='" + controlUnitId + '\'' +
                ", measurementId=" + measurementId + '\'' +
                ", temperatureLevel=" + temperatureLevel +
                '}';
    }
}
