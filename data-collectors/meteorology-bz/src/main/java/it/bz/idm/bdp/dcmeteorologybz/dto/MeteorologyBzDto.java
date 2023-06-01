// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.idm.bdp.dcmeteorologybz.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import javax.json.JsonObject;

//import it.bz.idm.bdp.dcmeteorologybz.DCUtils;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.StationDto;

public class MeteorologyBzDto implements Serializable {

    private static final long serialVersionUID = 8642860252556395832L;

    private StationDto station;
    private boolean checkLastSavedRecord;
    private Feature stationAttributes;
    private Map<String, DataTypeDto> dataTypeMap;
    private Map<String, List<TimeSerieDto>> timeSeriesMap;
    private Map<String, Date> lastSavedRecordMap;
    private List<SensorDto> sensorDataList;

    public MeteorologyBzDto() {
        this(null, null);
    }

    public MeteorologyBzDto(StationDto station) {
        this(station, null);
    }

    public MeteorologyBzDto(StationDto station, Feature stationAttributes) {
        this.checkLastSavedRecord = true;
        this.station = station;
        this.stationAttributes = stationAttributes;
        this.dataTypeMap = new HashMap<String, DataTypeDto>();
        this.timeSeriesMap = new HashMap<String, List<TimeSerieDto>>();
        lastSavedRecordMap = new HashMap<String, Date>();
        this.sensorDataList = new ArrayList<SensorDto>();
    }

    public StationDto getStation() {
        return station;
    }

    public void setStation(StationDto station) {
        this.station = station;
    }

    public boolean isCheckLastSavedRecord() {
        return checkLastSavedRecord;
    }

    public void setCheckLastSavedRecord(boolean checkLastSavedRecord) {
        this.checkLastSavedRecord = checkLastSavedRecord;
    }

    public Feature getStationAttributes() {
        return stationAttributes;
    }

    public void setStationAttributes(Feature stationAttributes) {
        this.stationAttributes = stationAttributes;
    }

    public Map<String, DataTypeDto> getDataTypeMap() {
        return dataTypeMap;
    }

    public void setDataTypeMap(Map<String, DataTypeDto> dataTypeMap) {
        this.dataTypeMap = dataTypeMap;
    }

    public Map<String, List<TimeSerieDto>> getTimeSeriesMap() {
        return timeSeriesMap;
    }

    public void setTimeSeriesMap(Map<String, List<TimeSerieDto>> timeSeriesMap) {
        this.timeSeriesMap = timeSeriesMap;
    }

    public Map<String, Date> getLastSavedRecordMap() {
        return lastSavedRecordMap;
    }

    public void setLastSavedRecordMap(Map<String, Date> lastSavedRecordMap) {
        this.lastSavedRecordMap = lastSavedRecordMap;
    }

    public List<SensorDto> getSensorDataList() {
        return sensorDataList;
    }

    public void setSensorDataList(List<SensorDto> sensorDataList) {
        this.sensorDataList = sensorDataList;
    }

    @Override
    public String toString() {
        return "MeteorologyBzDto [station=" + station + ", stationAttributes=" + stationAttributes + ", dataTypeMap=" + dataTypeMap + ", lastSavedRecordMap=" + lastSavedRecordMap + ", sensorDataList=" + sensorDataList + ", timeSeriesMap=" + timeSeriesMap + "]";
    }

}
