// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.odh.spreadsheets.dto;

import java.util.ArrayList;
import java.util.List;

import it.bz.idm.bdp.dto.StationDto;

public class MappingResult {
    private List<StationDto> stationDtos = new ArrayList<StationDto>();

    private DataTypeWrapperDto dataType;

    public List<StationDto> getStationDtos() {
        return stationDtos;
    }

    public void setStationDtos(List<StationDto> stationDtos) {
        this.stationDtos = stationDtos;
    }

    public DataTypeWrapperDto getDataType() {
        return dataType;
    }

    public void setDataType(DataTypeWrapperDto dataType) {
        this.dataType = dataType;
    }
}
