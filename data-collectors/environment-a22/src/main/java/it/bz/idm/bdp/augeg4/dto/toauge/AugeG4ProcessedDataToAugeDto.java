package it.bz.idm.bdp.augeg4.dto.toauge;

import java.util.Date;
import java.util.List;

public class AugeG4ProcessedDataToAugeDto {
    private final Date dateTimeAcquisition;

    private final Date dateTimeLinearization;

    private final String controlUnitId;

    private final List<ProcessedResValToAuge> resVal;

    public AugeG4ProcessedDataToAugeDto(Date dateTimeAcquisition, Date dateTimeLinearization, String controlUnitId, List<ProcessedResValToAuge> resVal) {
        this.dateTimeAcquisition = dateTimeAcquisition;
        this.dateTimeLinearization = dateTimeLinearization;
        this.controlUnitId = controlUnitId;
        this.resVal = resVal;
    }

    public Date getDateTimeAcquisition() {
        return dateTimeAcquisition;
    }

    public Date getDateTimeLinearization() {
        return dateTimeLinearization;
    }

    public String getControlUnitId() {
        return controlUnitId;
    }

    public List<ProcessedResValToAuge> getResVal() {
        return resVal;
    }

    @Override
    public String toString() {
        return "AugeG4ProcessedDataToAugeDto{" +
                "dateTimeAcquisition=" + dateTimeAcquisition +
                ", dateTimeLinearization=" + dateTimeLinearization +
                ", controlUnitId='" + controlUnitId + '\'' +
                ", resVal=" + resVal +
                '}';
    }
}
