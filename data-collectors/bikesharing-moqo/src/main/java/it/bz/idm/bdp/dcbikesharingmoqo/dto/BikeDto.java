package it.bz.idm.bdp.dcbikesharingmoqo.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class BikeDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id           ;
    private String carType      ;
    private String name         ;
    private String license      ;
    private String cleanness    ;
    private String exchangeType ;
    private String bigImageUrl  ;
    private String medImageUrl  ;
    private String tmbImageUrl  ;
    private String inMaintenance;
    private String available    ;
    private LocationDto location;

    private Map<String, LocationDto> parkingAreaMap;

    public BikeDto() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCarType() {
        return carType;
    }

    public void setCarType(String carType) {
        this.carType = carType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getCleanness() {
        return cleanness;
    }

    public void setCleanness(String cleanness) {
        this.cleanness = cleanness;
    }

    public String getExchangeType() {
        return exchangeType;
    }

    public void setExchangeType(String exchangeType) {
        this.exchangeType = exchangeType;
    }

    public String getBigImageUrl() {
        return bigImageUrl;
    }

    public void setBigImageUrl(String bigImageUrl) {
        this.bigImageUrl = bigImageUrl;
    }

    public String getMedImageUrl() {
        return medImageUrl;
    }

    public void setMedImageUrl(String medImageUrl) {
        this.medImageUrl = medImageUrl;
    }

    public String getTmbImageUrl() {
        return tmbImageUrl;
    }

    public void setTmbImageUrl(String tmbImageUrl) {
        this.tmbImageUrl = tmbImageUrl;
    }

    public String getInMaintenance() {
        return inMaintenance;
    }

    public void setInMaintenance(String inMaintenance) {
        this.inMaintenance = inMaintenance;
    }

    public String getAvailable() {
        return available;
    }

    public void setAvailable(String available) {
        this.available = available;
    }

    public LocationDto getLocation() {
        return location;
    }

    public void setLocation(LocationDto location) {
        this.location = location;
    }

    public Map<String, LocationDto> getParkingAreaMap() {
        if ( parkingAreaMap == null ) {
            parkingAreaMap = new HashMap<String, LocationDto>();
        }
        return parkingAreaMap;
    }

    public void setParkingAreaMap(Map<String, LocationDto> parkingAreaMap) {
        this.parkingAreaMap = parkingAreaMap;
    }

    @Override
    public String toString() {
        return "BikeDto [id=" + id + ", carType=" + carType + ", name=" + name + ", license=" + license + ", cleanness=" + cleanness + ", exchangeType=" + exchangeType + ", bigImageUrl=" + bigImageUrl
                + ", medImageUrl=" + medImageUrl + ", tmbImageUrl=" + tmbImageUrl + ", inMaintenance=" + inMaintenance + ", available=" + available + ", location=" + location + "]";
    }

}
