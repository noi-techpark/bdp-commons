package it.bz.idm.bdp.dcbikesharingmoqo.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BikeDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id             ;
    private String carType        ;
    private String name           ;
    private String license        ;
    private String cleanness      ;
    private String exchangeType   ;
    private String bigImageUrl    ;
    private String medImageUrl    ;
    private String tmbImageUrl    ;
    private Boolean inMaintenance ;
    private Boolean availability  ;
    private Boolean futureAvailability;
    private Date    availableFrom ;
    private Date    availableUntil;
    private Long    availableDuration;
    private LocationDto location  ;

    private Long measurementTimestamp;

    private List<AvailabilityDto> availabilityList;
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

    public Boolean getInMaintenance() {
        return inMaintenance;
    }

    public void setInMaintenance(Boolean inMaintenance) {
        this.inMaintenance = inMaintenance;
    }

    public Boolean getAvailability() {
        return availability;
    }

    public void setAvailability(Boolean availability) {
        this.availability = availability;
    }

    public Boolean getFutureAvailability() {
        return futureAvailability;
    }

    public void setFutureAvailability(Boolean futureAvailability) {
        this.futureAvailability = futureAvailability;
    }

    public Date getAvailableFrom() {
        return availableFrom;
    }

    public void setAvailableFrom(Date availableFrom) {
        this.availableFrom = availableFrom;
    }

    public Date getAvailableUntil() {
        return availableUntil;
    }

    public void setAvailableUntil(Date availableUntil) {
        this.availableUntil = availableUntil;
    }

    public Long getAvailableDuration() {
        return availableDuration;
    }

    public void setAvailableDuration(Long availableDuration) {
        this.availableDuration = availableDuration;
    }

    public LocationDto getLocation() {
        return location;
    }

    public void setLocation(LocationDto location) {
        this.location = location;
    }

    public Long getMeasurementTimestamp() {
        return measurementTimestamp;
    }

    public void setMeasurementTimestamp(Long measurementTimestamp) {
        this.measurementTimestamp = measurementTimestamp;
    }

    public List<AvailabilityDto> getAvailabilityList() {
        if ( availabilityList == null ) {
            availabilityList = new ArrayList<AvailabilityDto>();
        }
        return availabilityList;
    }

    public void setAvailabilityList(List<AvailabilityDto> availabilityList) {
        this.availabilityList = availabilityList;
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
                + ", medImageUrl=" + medImageUrl + ", tmbImageUrl=" + tmbImageUrl + ", inMaintenance=" + inMaintenance + ", availability=" + availability + ", futureAvailability=" + futureAvailability + ", availableFrom=" + availableFrom
                + ", availableUntil=" + availableUntil + ", availableDuration=" + availableDuration + ", location=" + location + "]";
    }

}
