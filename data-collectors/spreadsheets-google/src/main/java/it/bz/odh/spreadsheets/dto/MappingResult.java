package it.bz.odh.spreadsheets.dto;

import java.util.ArrayList;
import java.util.List;

import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.StationDto;

public class MappingResult {
    private List<StationDto> stationDtos = new ArrayList<StationDto>();

    private DataTypeDto dataType;

    public List<StationDto> getStationDtos() {
        return stationDtos;
    }

    public void setStationDtos(List<StationDto> stationDtos) {
        this.stationDtos = stationDtos;
    }

    public void setDataType(DataTypeDto type) {
        this.dataType=type;
    }
    public DataTypeDto getDataType() {
        return dataType;
    }
}
