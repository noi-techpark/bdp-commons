package it.bz.idm.bdp.dcmeteorologybz.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import javax.json.JsonObject;

import it.bz.idm.bdp.dcmeteorologybz.DCUtils;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.StationDto;

public class MeteorologyBzDto implements Serializable {

    private static final long serialVersionUID = 8642860252556395832L;

    private boolean valid;
    private StationDto station;
    private Date lastSavedRecord;
    private boolean checkLastSavedRecord;
//    private JsonObject stationAttributes;
    private Feature stationAttributes;
    private Map<String, DataTypeDto> dataTypes;
    private List<MeteorologyBzMeasurementListDto> measurementsByType;

    public MeteorologyBzDto() {
        this(null, null);
    }

    public MeteorologyBzDto(StationDto station) {
        this(station, null);
    }

    public MeteorologyBzDto(StationDto station, /*JsonObject*/Feature stationAttributes) {
        this.checkLastSavedRecord = true;
        this.station = station;
        this.stationAttributes = stationAttributes;
        this.dataTypes = new HashMap<String, DataTypeDto>();
        this.measurementsByType = new ArrayList<MeteorologyBzMeasurementListDto>();
    }

    public boolean isValid() {
        if ( stationAttributes!=null ) {
            //TODO: check!!
            if ( stationAttributes!=null ) { //&& DCUtils.paramNotNull(stationAttributes.get("enddate")) ) {
                valid = false;
            } else {
                valid = true;
            }
        }
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public StationDto getStation() {
        return station;
    }

    public void setStation(StationDto station) {
        this.station = station;
    }

    public Date getLastSavedRecord() {
        return lastSavedRecord;
    }

    public void setLastSavedRecord(Date lastSavedRecord) {
        this.lastSavedRecord = lastSavedRecord;
    }

    public boolean isCheckLastSavedRecord() {
        return checkLastSavedRecord;
    }

    public void setCheckLastSavedRecord(boolean checkLastSavedRecord) {
        this.checkLastSavedRecord = checkLastSavedRecord;
    }

    public /*JsonObject*/ Feature getStationAttributes() {
        return stationAttributes;
    }

    public void setStationAttributes(/*JsonObject*/ Feature stationAttributes) {
        this.stationAttributes = stationAttributes;
    }

    public Map<String, DataTypeDto> getDataTypes() {
        return dataTypes;
    }

    public void setDataTypes(Map<String, DataTypeDto> dataTypes) {
        this.dataTypes = dataTypes;
    }

    public List<MeteorologyBzMeasurementListDto> getMeasurementsByType() {
        return measurementsByType;
    }

    public void setMeasurementsByType(List<MeteorologyBzMeasurementListDto> measurementsByType) {
        this.measurementsByType = measurementsByType;
    }

    @Override
    public String toString() {
        return "MeteorologyBzDto [valid=" + valid + ", station=" + station + ", stationAttributes=" + stationAttributes + ", dataTypes=" + dataTypes + ", measurementsByType=" + measurementsByType + "]";
    }

}
