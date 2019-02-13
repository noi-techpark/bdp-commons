package it.bz.idm.bdp.dcmeteotn.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.bz.idm.bdp.dcmeteotn.DCUtils;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.meteo.MeteoStationDto;

public class MeteoTnDto implements Serializable {

    private static final long serialVersionUID = 8642860252556395832L;

    private boolean valid;
    private MeteoStationDto station;
    private Date lastSavedRecord;
    private boolean checkLastSavedRecord;
    private Map<String, String> stationAttributes;
    private Map<String, DataTypeDto> dataTypes;
    private List<MeteoTnMeasurementListDto> measurementsByType;

    public MeteoTnDto() {
    }

    public MeteoTnDto(MeteoStationDto station) {
        this(station, new HashMap<String, String>());
    }

    public MeteoTnDto(MeteoStationDto station, Map<String, String> stationAttributes) {
        this.checkLastSavedRecord = true;
        this.station = station;
        this.stationAttributes = stationAttributes;
        this.dataTypes = new HashMap<String, DataTypeDto>();
        this.measurementsByType = new ArrayList<MeteoTnMeasurementListDto>();
    }

    public boolean isValid() {
        if ( stationAttributes!=null ) {
            if ( DCUtils.paramNotNull(stationAttributes.get("enddate")) ) {
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

    public MeteoStationDto getStation() {
        return station;
    }

    public void setStation(MeteoStationDto station) {
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

    public Map<String, String> getStationAttributes() {
        return stationAttributes;
    }

    public void setStationAttributes(Map<String, String> stationAttributes) {
        this.stationAttributes = stationAttributes;
    }

    public Map<String, DataTypeDto> getDataTypes() {
        return dataTypes;
    }

    public void setDataTypes(Map<String, DataTypeDto> dataTypes) {
        this.dataTypes = dataTypes;
    }

    public List<MeteoTnMeasurementListDto> getMeasurementsByType() {
        return measurementsByType;
    }

    public void setMeasurementsByType(List<MeteoTnMeasurementListDto> measurementsByType) {
        this.measurementsByType = measurementsByType;
    }

    @Override
    public String toString() {
        return "MeteoTnDto [valid=" + valid + ", station=" + station + ", stationAttributes=" + stationAttributes + ", dataTypes=" + dataTypes + ", measurementsByType=" + measurementsByType + "]";
    }

}
