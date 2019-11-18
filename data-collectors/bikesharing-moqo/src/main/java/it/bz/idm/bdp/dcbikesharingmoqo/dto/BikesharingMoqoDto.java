package it.bz.idm.bdp.dcbikesharingmoqo.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BikesharingMoqoDto implements Serializable {

    private static final long serialVersionUID = 8642860252556395832L;

    private List<BikeDto> bikeList;
    private Map<String, LocationDto> locationMap;

    public BikesharingMoqoDto() {
        this.bikeList = new ArrayList<BikeDto>();
        this.locationMap = new HashMap<String, LocationDto>();
    }

    public List<BikeDto> getBikeList() {
        return bikeList;
    }

    public void setBikeList(List<BikeDto> bikeList) {
        this.bikeList = bikeList;
    }

    public Map<String, LocationDto> getLocationMap() {
        return locationMap;
    }

    public void setLocationMap(Map<String, LocationDto> locationMap) {
        this.locationMap = locationMap;
    }

    @Override
    public String toString() {
        return "BikesharingMoqoPageDto [bikeList=" + bikeList + ", locationMap=" + locationMap + "]";
    }

}
