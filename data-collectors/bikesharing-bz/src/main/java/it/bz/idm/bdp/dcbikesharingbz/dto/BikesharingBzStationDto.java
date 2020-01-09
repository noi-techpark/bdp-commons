package it.bz.idm.bdp.dcbikesharingbz.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BikesharingBzStationDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String baysJsonString;

    private String id             ;
    private String address        ;
    private String name           ;
    private String state          ;
    private Double longitude      ; 
    private Double latitude       ; 
    private Long   totalBays      ;
    private Long   freeBays       ;
    private Long   availableVehicles;

    private Long measurementTimestamp;

    private List<BikesharingBzBayDto> bayList;

    public BikesharingBzStationDto() {
        this.bayList = new ArrayList<BikesharingBzBayDto>();
    }

    public String getBaysJsonString() {
        return baysJsonString;
    }

    public void setBaysJsonString(String baysJsonString) {
        this.baysJsonString = baysJsonString;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
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

    public Long getTotalBays() {
        return totalBays;
    }

    public void setTotalBays(Long totalBays) {
        this.totalBays = totalBays;
    }

    public Long getFreeBays() {
        return freeBays;
    }

    public void setFreeBays(Long freeBays) {
        this.freeBays = freeBays;
    }

    public Long getAvailableVehicles() {
        return availableVehicles;
    }

    public void setAvailableVehicles(Long availableVehicles) {
        this.availableVehicles = availableVehicles;
    }

    public Long getMeasurementTimestamp() {
        return measurementTimestamp;
    }

    public void setMeasurementTimestamp(Long measurementTimestamp) {
        this.measurementTimestamp = measurementTimestamp;
    }

    public List<BikesharingBzBayDto> getBayList() {
        return bayList;
    }

    public void setBayList(List<BikesharingBzBayDto> bayList) {
        this.bayList = bayList;
    }

    @Override
    public String toString() {
        return "BikesharingBzStationDto [id=" + id + ", address=" + address + ", name=" + name + ", state=" + state + ", longitude=" + longitude + ", latitude=" + latitude + ", totalBays=" + totalBays
                + ", freeBays=" + freeBays + ", availableVehicles=" + availableVehicles + ", measurementTimestamp=" + measurementTimestamp + ", bayList=" + bayList + "]";
    }

}
