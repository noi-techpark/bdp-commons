// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.idm.bdp.dcmeteotn.dto;

import java.util.Date;

public class MeteoTnMeasurementDto {

    private String xmlString;
    private Date date;
    private String typeName;
    private String typeUm;
    private Object value;

    public MeteoTnMeasurementDto() {
    }

    public MeteoTnMeasurementDto(String xmlString, Date date, String typeName, String typeUm, Object value) {
        super();
        this.xmlString = xmlString;
        this.date = date;
        this.typeName = typeName;
        this.typeUm = typeUm;
        this.value = value;
    }

    public String getXmlString() {
        return xmlString;
    }

    public void setXmlString(String xmlString) {
        this.xmlString = xmlString;
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
        return "MeteoTnMeasurementDto [xmlString=" + xmlString + ", date=" + date + ", typeName=" + typeName + ", typeUm=" + typeUm + ", value=" + value + "]";
    }

}
