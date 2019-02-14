package it.bz.idm.bdp.dcmeteotn;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import it.bz.idm.bdp.dcmeteotn.dto.MeteoTnDto;
import it.bz.idm.bdp.dto.StationDto;

@Service
public class MeteoTnDataConverter {

    private static final Logger LOG = LogManager.getLogger(MeteoTnDataConverter.class.getName());

    public static final String ORIGIN_KEY                = "app.origin";
    public static final String PERIOD_KEY                = "app.period";
    public static final String CHECK_DATE_LAST_REC_KEY   = "app.checkDateOfLastRecord";
    public static final String PUSH_DATA_SINGLE_STATION  = "app.pushDataSingleStation";

    public static final String STATION_TYPE_KEY          = "app.station.type";

    @Autowired
    private Environment env;

    //This must be initialized in application.properties file (for example "Meteotrentino")
    private String origin;
    //This must be initialized in application.properties file (for example "Meteostation")
    private String stationType;
    //This must be initialized in application.properties file (for example 900)
    private Integer period;
    //This must be initialized in application.properties file (for example "true")
    private String checkDateOfLastRecord;
    //This must be initialized in application.properties file (for example "false")
    private String pushDataSingleStation;

    public String getOrigin() {
        if ( this.origin == null ) {
            this.origin = env.getProperty(ORIGIN_KEY);
        }
        return this.origin;
    }
    public String getStationType() {
        if ( this.stationType == null ) {
            this.stationType = env.getProperty(STATION_TYPE_KEY);
        }
        return this.stationType;
    }
    public Integer getPeriod() {
        if ( this.period == null ) {
            this.period = env.getProperty(PERIOD_KEY, Integer.class);
        }
        return this.period;
    }
    public boolean isCheckDateOfLastRecord() {
        boolean check = true;
        if ( this.checkDateOfLastRecord == null ) {
            this.checkDateOfLastRecord = env.getProperty(CHECK_DATE_LAST_REC_KEY);
        }
        if ( "false".equalsIgnoreCase(this.checkDateOfLastRecord) ) {
            check = false;
        }
        return check;
    }
    public boolean isPushDataSingleStation() {
        boolean check = false;
        if ( this.pushDataSingleStation == null ) {
            this.pushDataSingleStation = env.getProperty(PUSH_DATA_SINGLE_STATION);
        }
        check = "true".equalsIgnoreCase(this.pushDataSingleStation);
        return check;
    }

    public List<MeteoTnDto> convertExternalStationDtoListToInternalDtoList(List<Map<String, String>> list) {
        if ( LOG.isDebugEnabled() ) {
            LOG.debug("list: "+list);
        }
        List<MeteoTnDto> extDtoList = new ArrayList<MeteoTnDto>();
        if ( list == null ) {
            return extDtoList;
        }
        for (Map<String, String> map : list) {
            StationDto stationDto = convertExternalStationDtoToStationDto(map);
            MeteoTnDto extDto = new MeteoTnDto(stationDto, map);
            extDtoList.add(extDto);
        }

        if ( LOG.isDebugEnabled() ) {
            LOG.debug("extDtoList: "+extDtoList);
        }
        return extDtoList;
    }

    public StationDto convertExternalStationDtoToStationDto(Map<String, String> map) {
        if ( LOG.isDebugEnabled() ) {
            LOG.debug("map: "+map);
        }
        StationDto stationDto = null;
        if ( map != null ) {
            stationDto = new StationDto();

            /*
             *  Example of xml provided by the service
                <pointOfMeasureInfo>
                  <code>T0154</code>
                  <name>Ala (Convento)</name>
                  <shortname>Ala Convento</shortname>
                  <elevation>165</elevation>
                  <latitude>45.757117</latitude>
                  <longitude>10.999871</longitude>
                  <east>655530</east>
                  <north>5069007</north>
                  <startdate>1921-01-01</startdate>
                  <enddate>2005-06-22</enddate>
                </pointOfMeasureInfo>
             */

            //From StationDTO
            stationDto.setId(map.get("code"));
            stationDto.setName(DCUtils.trunc(map.get("name"), 255));
            stationDto.setLongitude(DCUtils.convertStringToDouble(map.get("longitude")));
            stationDto.setLatitude(DCUtils.convertStringToDouble(map.get("latitude")));
            //OMITTED: protected String crs;
            stationDto.setOrigin(DCUtils.trunc(getOrigin(), 255));
            //TODO: get municipality from coordinates....... station.setMunicipality(DCUtils.trunc(map.get("city"), 255));
            stationDto.setStationType(env.getProperty(STATION_TYPE_KEY));

            //From MeteoStationDto
            //OMITTED: station.setArea(extDto.getSlotsTotal());
        }

        if ( LOG.isDebugEnabled() ) {
            LOG.debug("stationDto: "+stationDto);
        }
        return stationDto;
    }

}
