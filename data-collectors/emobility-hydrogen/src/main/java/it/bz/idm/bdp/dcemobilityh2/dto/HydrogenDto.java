package it.bz.idm.bdp.dcemobilityh2.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import it.bz.idm.bdp.dto.emobility.ChargingPointsDtoV2;
import it.bz.idm.bdp.dto.emobility.EchargingPlugDto;
import it.bz.idm.bdp.dto.emobility.EchargingStationDto;

public class HydrogenDto implements Serializable {

    private static final long serialVersionUID = 8642860252556395832L;

    private EchargingStationDto station;
    private List<EchargingPlugDto> plugList;
    private List<ChargingPointsDtoV2> pointList;

    public HydrogenDto() {
        super();
    }

    public HydrogenDto(EchargingStationDto station, EchargingPlugDto plug, ChargingPointsDtoV2 point) {
        this();
        List<EchargingPlugDto> plugList = new ArrayList<EchargingPlugDto>();
        plugList.add(plug);
        List<ChargingPointsDtoV2> pointList = new ArrayList<ChargingPointsDtoV2>();
        pointList.add(point);
        this.station = station;
        this.plugList = plugList;
        this.pointList = pointList;
    }

    public HydrogenDto(EchargingStationDto station, List<EchargingPlugDto> plugList, List<ChargingPointsDtoV2> pointList) {
        super();
        this.station = station;
        this.plugList = plugList;
        this.pointList = pointList;
    }

    public EchargingStationDto getStation() {
        return station;
    }

    public void setStation(EchargingStationDto station) {
        this.station = station;
    }

    public List<EchargingPlugDto> getPlugList() {
        return plugList;
    }

    public void setPlugList(List<EchargingPlugDto> plugList) {
        this.plugList = plugList;
    }

    public List<ChargingPointsDtoV2> getPointList() {
        return pointList;
    }

    public void setPointList(List<ChargingPointsDtoV2> pointList) {
        this.pointList = pointList;
    }

}
