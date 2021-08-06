package it.bz.noi.ondemandmerano.model;

import java.util.HashMap;

public class OnDemandMeranoVehicle {

    private String licensePlateNumber;
    private String type;
    private HashMap<String, Object> operator;
    private HashMap<String, Object> capacityMax;
    private HashMap<String, Object> capacityUsed;
    private String recordTime;
    private OnDemandServicePositionPoint position;

    public String getLicensePlateNumber() {
        return licensePlateNumber;
    }

    public void setLicensePlateNumber(String licensePlateNumber) {
        this.licensePlateNumber = licensePlateNumber;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public HashMap<String, Object> getOperator() {
        return operator;
    }

    public void setOperator(HashMap<String, Object> operator) {
        this.operator = operator;
    }

    public HashMap<String, Object> getCapacityMax() {
        return capacityMax;
    }

    public void setCapacityMax(HashMap<String, Object> capacityMax) {
        this.capacityMax = capacityMax;
    }

    public HashMap<String, Object> getCapacityUsed() {
        return capacityUsed;
    }

    public void setCapacityUsed(HashMap<String, Object> capacityUsed) {
        this.capacityUsed = capacityUsed;
    }

    public String getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(String recordTime) {
        this.recordTime = recordTime;
    }

    public OnDemandServicePositionPoint getPosition() {
        return position;
    }

    public void setPosition(OnDemandServicePositionPoint position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return "OnDemandMeranoVehicle{" +
                "licensePlateNumber='" + licensePlateNumber + '\'' +
                ", type='" + type + '\'' +
                ", operator=" + operator +
                ", capacityMax=" + capacityMax +
                ", capacityUsed=" + capacityUsed +
                ", recordTime='" + recordTime + '\'' +
                ", position=" + position +
                '}';
    }
}
