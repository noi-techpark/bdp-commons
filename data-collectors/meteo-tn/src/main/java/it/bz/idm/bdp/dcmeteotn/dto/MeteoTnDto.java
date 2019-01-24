package it.bz.idm.bdp.dcmeteotn.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.bz.idm.bdp.dcmeteotn.DCUtils;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.StationDto;

public class MeteoTnDto implements Serializable {

    private static final long serialVersionUID = 8642860252556395832L;

    private boolean valid;
    private StationDto station;
    private Map<String, String> stationAttributes;
    private Map<String, DataTypeDto> dataTypes;
    private List<MeteoTnMeasurementListDto> measurementTypes;

    public MeteoTnDto() {
    }

    public MeteoTnDto(StationDto station) {
        this(station, new HashMap<String, String>());
    }

    public MeteoTnDto(StationDto station, Map<String, String> stationAttributes) {
        this.station = station;
        this.stationAttributes = stationAttributes;
        this.dataTypes = new HashMap<String, DataTypeDto>();
        this.measurementTypes = new ArrayList<MeteoTnMeasurementListDto>();
    }

    public boolean isValid() {
        if ( stationAttributes!=null ) {
            if ( DCUtils.paramNotNull(stationAttributes.get("enddate")) ) {
                return false;
            } else {
                return true;
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

    public List<MeteoTnMeasurementListDto> getMeasurementTypes() {
        return measurementTypes;
    }

    public void setMeasurementTypes(List<MeteoTnMeasurementListDto> measurementTypes) {
        this.measurementTypes = measurementTypes;
    }

    @Override
    public String toString() {
        return "MeteoTnDto [valid=" + valid + ", station=" + station + ", stationAttributes=" + stationAttributes + ", dataTypes=" + dataTypes + ", measurementTypes=" + measurementTypes + "]";
    }

}
