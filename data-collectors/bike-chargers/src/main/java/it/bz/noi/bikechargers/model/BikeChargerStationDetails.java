package it.bz.noi.bikechargers.model;

import java.util.ArrayList;
import java.util.List;

public class BikeChargerStationDetails {

    private Integer totalBays;
    private Integer freeBay;
    private Integer availableVehicles;
    private List<BikeChargerBayStation> bayStations = new ArrayList<>();

    public Integer getTotalBays() {
        return totalBays;
    }

    public void setTotalBays(Integer totalBays) {
        this.totalBays = totalBays;
    }

    public Integer getFreeBay() {
        return freeBay;
    }

    public void setFreeBay(Integer freeBay) {
        this.freeBay = freeBay;
    }

    public Integer getAvailableVehicles() {
        return availableVehicles;
    }

    public void setAvailableVehicles(Integer availableVehicles) {
        this.availableVehicles = availableVehicles;
    }

    public boolean addBayStation(BikeChargerBayStation bayStation) {
        return bayStations.add(bayStation);
    }

    public List<BikeChargerBayStation> getBayStations() {
        return bayStations;
    }

    @Override
    public String toString() {
        return "BikeChargerStationDetails{" +
                "totalBays=" + totalBays +
                ", freeBay=" + freeBay +
                ", availableVehicles=" + availableVehicles +
                ", bayStations=" + bayStations +
                '}';
    }
}
