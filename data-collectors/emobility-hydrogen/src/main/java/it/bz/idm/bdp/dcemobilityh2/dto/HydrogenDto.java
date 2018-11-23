package it.bz.idm.bdp.dcemobilityh2.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import it.bz.idm.bdp.dto.StationDto;

public class HydrogenDto implements Serializable {

    private static final long serialVersionUID = 8642860252556395832L;

    private StationDto station;
    private List<StationDto> plugList;
    private List<ChargingPointsDtoV2> pointList;

    public HydrogenDto() {
        super();
    }

    public HydrogenDto(StationDto station, StationDto plug, ChargingPointsDtoV2 point) {
        this();
        List<StationDto> plugList = new ArrayList<StationDto>();
        plugList.add(plug);
        List<ChargingPointsDtoV2> pointList = new ArrayList<ChargingPointsDtoV2>();
        pointList.add(point);
        this.station = station;
        this.plugList = plugList;
        this.pointList = pointList;
    }

    public HydrogenDto(StationDto station, List<StationDto> plugList, List<ChargingPointsDtoV2> pointList) {
        super();
        this.station = station;
        this.plugList = plugList;
        this.pointList = pointList;
    }

    public StationDto getStation() {
        return station;
    }

    public void setStation(StationDto station) {
        this.station = station;
    }

    public List<StationDto> getPlugList() {
        return plugList;
    }

    public void setPlugList(List<StationDto> plugList) {
        this.plugList = plugList;
    }

    public List<ChargingPointsDtoV2> getPointList() {
        return pointList;
    }

    public void setPointList(List<ChargingPointsDtoV2> pointList) {
        this.pointList = pointList;
    }

}
