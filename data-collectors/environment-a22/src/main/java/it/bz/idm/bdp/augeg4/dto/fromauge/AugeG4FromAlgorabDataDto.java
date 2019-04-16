package it.bz.idm.bdp.augeg4.dto.fromauge;

import java.util.Date;
import java.util.List;

/**
 * DTO come da documento di protocollo Algorab.
 */
public class AugeG4FromAlgorabDataDto {

    private Date dateTimeAcquisition;

    private String controlUnitId;

    private List<RawResVal> resVal;

    public String getControlUnitId() {
        return controlUnitId;
    }

    public void setControlUnitId(String controlUnitId) {
        this.controlUnitId = controlUnitId;
    }

    public Date getDateTimeAcquisition() {
        return dateTimeAcquisition;
    }

    public List<RawResVal> getResVal() {
        return resVal;
    }

    public void setDateTimeAcquisition(Date dateTimeAcquisition) {
        this.dateTimeAcquisition = dateTimeAcquisition;
    }

    public void setResVal(List<RawResVal> resVal) {
        this.resVal = resVal;
    }
}
