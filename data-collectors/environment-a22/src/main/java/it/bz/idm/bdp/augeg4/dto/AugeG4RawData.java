package it.bz.idm.bdp.augeg4.dto;

import java.util.Date;
import java.util.List;

public class AugeG4RawData {

    private String controlUnitId;

    private Date dateTimeAcquisition;

    private List<RawMeasurement> measurements;

    public AugeG4RawData(String controlUnitId, Date dateTimeAcquisition, List<RawMeasurement> measurements) {
        this.controlUnitId = controlUnitId;
        this.dateTimeAcquisition = dateTimeAcquisition;
        this.measurements = measurements;
    }

    public String getControlUnitId() {
        return controlUnitId;
    }

    public Date getDateTimeAcquisition() {
        return dateTimeAcquisition;
    }

    public List<RawMeasurement> getMeasurements() {
        return measurements;
    }

    @Override
    public String toString() {
        return "AugeG4RawData{" +
                "controlUnitId='" + controlUnitId + '\'' +
                ", dateTimeAcquisition=" + dateTimeAcquisition +
                ", measurements=" + measurements +
                '}';
    }
}
