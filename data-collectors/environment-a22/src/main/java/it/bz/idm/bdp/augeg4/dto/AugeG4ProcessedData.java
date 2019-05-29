package it.bz.idm.bdp.augeg4.dto;

import java.util.Date;
import java.util.List;

public class AugeG4ProcessedData {

    private final String controlUnitId;

    private final Date dateTimeAcquisition;

    private final Date dateTimeProcessing;

    private final List<ProcessedMeasurement> measurements;

    public AugeG4ProcessedData(String controlUnitId, Date dateTimeAcquisition, Date dateTimeProcessing, List<ProcessedMeasurement> measurements) {
        this.controlUnitId = controlUnitId;
        this.dateTimeAcquisition = dateTimeAcquisition;
        this.dateTimeProcessing = dateTimeProcessing;
        this.measurements = measurements;
    }

    public String getControlUnitId() {
        return controlUnitId;
    }

    public Date getDateTimeAcquisition() {
        return dateTimeAcquisition;
    }

    public Date getDateTimeProcessing() {
        return dateTimeProcessing;
    }

    public List<ProcessedMeasurement> getMeasurements() {
        return measurements;
    }

    @Override
    public String toString() {
        return "AugeG4ProcessedData{" +
                "controlUnitId='" + controlUnitId + '\'' +
                ", dateTimeAcquisition=" + dateTimeAcquisition +
                ", dateTimeProcessing=" + dateTimeProcessing +
                ", measurements=" + measurements +
                '}';
    }
}
