package it.bz.idm.bdp.dcmeteorologybz.dto;

import java.util.Date;

public class MeteorologyBzMeasurementDto {

    private String jsonString;
    private Date date;
    private String typeName;
    private String typeUm;
    private Object value;

    public MeteorologyBzMeasurementDto() {
    }

    public MeteorologyBzMeasurementDto(String jsonString, Date date, String typeName, String typeUm, Object value) {
        super();
        this.jsonString = jsonString;
        this.date = date;
        this.typeName = typeName;
        this.typeUm = typeUm;
        this.value = value;
    }

    public String getJsonString() {
        return jsonString;
    }

    public void setJsonString(String jsonString) {
        this.jsonString = jsonString;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeUm() {
        return typeUm;
    }

    public void setTypeUm(String typeUm) {
        this.typeUm = typeUm;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "MeteoBzMeasurementDto [jsonString=" + jsonString + ", date=" + date + ", typeName=" + typeName + ", typeUm=" + typeUm + ", value=" + value + "]";
    }

}
