package it.bz.idm.bdp.augeg4.fun.process;

import it.bz.idm.bdp.augeg4.dto.MeasurementId;

import java.util.Objects;

public class MeasurementParametersId {
    final String controlUnitId;
    final MeasurementId measurementId;

    public MeasurementParametersId(String controlUnitId, MeasurementId measurementId) {
        this.controlUnitId = controlUnitId;
        this.measurementId = measurementId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MeasurementParametersId)) return false;
        MeasurementParametersId id = (MeasurementParametersId) o;
        return controlUnitId.equals(id.controlUnitId) &&
                measurementId.equals(id.measurementId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(controlUnitId, measurementId);
    }

    @Override
    public String toString() {
        return "MeasurementParametersId{" +
                "controlUnitId='" + controlUnitId + '\'' +
                ", measurementId=" + measurementId +
                '}';
    }
}
