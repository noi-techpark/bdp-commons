package it.bz.idm.bdp.dcbikesharingbz.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BikesharingBzDto implements Serializable {

    private static final long serialVersionUID = 8642860252556395832L;

    private List<BikesharingBzStationDto> stationList;

    public BikesharingBzDto() {
        this.stationList = new ArrayList<BikesharingBzStationDto>();
    }

    public List<BikesharingBzStationDto> getStationList() {
        return stationList;
    }

    public void setStationList(List<BikesharingBzStationDto> stationList) {
        this.stationList = stationList;
    }

    @Override
    public String toString() {
        return "BikesharingBzDto [stationList=" + stationList + "]";
    }

}
