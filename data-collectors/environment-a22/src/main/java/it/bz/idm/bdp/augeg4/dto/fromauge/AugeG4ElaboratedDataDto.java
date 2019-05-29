package it.bz.idm.bdp.augeg4.dto.fromauge;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * DTO received from Algorab, which contains elaborated {@link ElaboratedResVal}.
 */
public class AugeG4ElaboratedDataDto {

    private Date dateTimeAcquisition;

    private String controlUnitId;

    private List<ElaboratedResVal> resVal;


    public AugeG4ElaboratedDataDto() {}

    public String getControlUnitId() {
        return controlUnitId;
    }

    public void setControlUnitId(String controlUnitId) {
        this.controlUnitId = controlUnitId;
    }

    public Date getDateTimeAcquisition() {
        return dateTimeAcquisition;
    }

    public List<ElaboratedResVal> getResVal() {
        return resVal;
    }

    public void setDateTimeAcquisition(Date dateTimeAcquisition) {
        this.dateTimeAcquisition = dateTimeAcquisition;
    }

    public void setResVal(List<ElaboratedResVal> resVal) {
        this.resVal = resVal;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AugeG4ElaboratedDataDto that = (AugeG4ElaboratedDataDto) o;
        return Objects.equals(dateTimeAcquisition, that.dateTimeAcquisition) &&
                Objects.equals(controlUnitId, that.controlUnitId) &&
                Objects.equals(resVal, that.resVal);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dateTimeAcquisition, controlUnitId, resVal);
    }


    @Override
    public String toString() {
        return "AugeG4ElaboratedDataDto{" +
                "dateTimeAcquisition=" + dateTimeAcquisition +
                ", controlUnitId='" + controlUnitId + '\'' +
                ", resVal=" + resVal +
                '}';
    }
}
