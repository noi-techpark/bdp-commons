package it.bz.idm.bdp.dcbikesharingpapin.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BikesharingPapinStationDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id             ;
    private String name           ;
    private Double longitude      ; 
    private Double latitude       ;
    private Boolean isClose       ;
    private String startHour      ;
    private String endHour        ;
    private String lunchBreakStart;
    private String lunchBreakEnd  ;
    private Boolean bikeAvailable ;
    private String url            ;

    private Long measurementTimestamp;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Boolean getIsClose() {
        return isClose;
    }

    public void setIsClose(Boolean isClose) {
        this.isClose = isClose;
    }

    public String getStartHour() {
        return startHour;
    }

    public void setStartHour(String startHour) {
        this.startHour = startHour;
    }

    public String getEndHour() {
        return endHour;
    }

    public void setEndHour(String endHour) {
        this.endHour = endHour;
    }

    public String getLunchBreakStart() {
        return lunchBreakStart;
    }

    public void setLunchBreakStart(String lunchBreakStart) {
        this.lunchBreakStart = lunchBreakStart;
    }

    public String getLunchBreakEnd() {
        return lunchBreakEnd;
    }

    public void setLunchBreakEnd(String lunchBreakEnd) {
        this.lunchBreakEnd = lunchBreakEnd;
    }

    public Boolean getBikeAvailable() {
        return bikeAvailable;
    }

    public void setBikeAvailable(Boolean bikeAvailable) {
        this.bikeAvailable = bikeAvailable;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getMeasurementTimestamp() { return measurementTimestamp; }

    public void setMeasurementTimestamp(Long measurementTimestamp) { this.measurementTimestamp = measurementTimestamp; }

    public String getState() {
        if (this.getBikeAvailable()) {
            return "READY";
        }
        return "OUT_OF_SERVICE";
    }

    public String getClose() {
        if (this.getIsClose()) {
            return "CLOSED";
        }
        return "OPEN";
    }

    @Override
    public String toString() {
        return "BikesharingPapinStationDto [id=" + id + ", name=" + name + ", measurementTimestamp=" + measurementTimestamp + "]";
    }

}
