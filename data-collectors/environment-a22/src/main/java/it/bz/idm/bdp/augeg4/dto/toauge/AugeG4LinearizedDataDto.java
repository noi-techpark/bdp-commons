package it.bz.idm.bdp.augeg4.dto.toauge;

import it.bz.idm.bdp.augeg4.dto.fromauge.AugeG4FromAlgorabDataDto;

import java.util.Date;
import java.util.List;

/**
 * Linearized {@link AugeG4FromAlgorabDataDto}
 */
public class AugeG4LinearizedDataDto {

    private final Date dateTimeAcquisition;

    private final Date dateTimeLinearization;

    private final String controlUnitId;

    private final List<LinearResVal> resVal;

    public AugeG4LinearizedDataDto(String controlUnitId, Date dateTimeAcquisition, Date dateTimeLinearization, List<LinearResVal> resVal) {
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

    public List<LinearResVal> getResVal() {
        return resVal;
    }

    @Override
    public String toString() {
        return "AugeG4LinearizedDataDto{" +
                "dateTimeAcquisition=" + dateTimeAcquisition +
                ", dateTimeLinearization=" + dateTimeLinearization +
                ", controlUnitId='" + controlUnitId + '\'' +
                ", resVal=" + resVal +
                '}';
    }
}
