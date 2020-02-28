package it.bz.idm.bdp.dconstreetparkingbz.dto;

import java.io.Serializable;

public class OnstreetParkingBzSensorDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String jsonSensorAddress;
    private String jsonStatus;
    private String jsonTime;
    private Long jsonTimestamp;

    private Long valueOccupied;
    private String valueId;
    private Long valueTimestamp;

    public OnstreetParkingBzSensorDto() {
    }

    public String getJsonSensorAddress() {
        return jsonSensorAddress;
    }

    public void setJsonSensorAddress(String jsonSensorAddress) {
        this.jsonSensorAddress = jsonSensorAddress;
    }

    public String getJsonStatus() {
        return jsonStatus;
    }

    public void setJsonStatus(String jsonStatus) {
        this.jsonStatus = jsonStatus;
    }

    public String getJsonTime() {
        return jsonTime;
    }

    public void setJsonTime(String jsonTime) {
        this.jsonTime = jsonTime;
    }

    public Long getJsonTimestamp() {
        return jsonTimestamp;
    }

    public void setJsonTimestamp(Long jsonTimestamp) {
        this.jsonTimestamp = jsonTimestamp;
    }

    public Long getValueOccupied() {
        return valueOccupied;
    }

    public void setValueOccupied(Long valueOccupied) {
        this.valueOccupied = valueOccupied;
    }

    public String getValueId() {
        return valueId;
    }

    public void setValueId(String valueId) {
        this.valueId = valueId;
    }

    public Long getValueTimestamp() {
        return valueTimestamp;
    }

    public void setValueTimestamp(Long valueTimestamp) {
        this.valueTimestamp = valueTimestamp;
    }

    @Override
    public String toString() {
        return "OnstreetParkingBzSensorDto [jsonSensorAddress=" + jsonSensorAddress + ", jsonStatus=" + jsonStatus + ", jsonTime=" + jsonTime + ", jsonTimestamp=" + jsonTimestamp + ", valueOccupied="
                + valueOccupied + ", valueId=" + valueId + ", valueTimestamp=" + valueTimestamp + "]";
    }

}
