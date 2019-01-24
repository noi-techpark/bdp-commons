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

    public static final String STATION_TYPE_KEY          = "app.station.type";

    @Autowired
    private Environment env;

    //This must be initialized in application.properties file (for example Meteotrentino)
    private String origin;
    //This must be initialized in application.properties file (for example Meteostation)
    private String stationType;
    //This must be initialized in application.properties file (for example 900)
    private Integer period;

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

//    public List<MeteoTnDto> convertToInternalDTO(List<MeteoTnMeasurementListDto> dataList, String municipality, String codePrefix) throws Exception {
//        try {
//            LOG.debug("dataList: "+dataList);
//            if ( dataList == null ) {
//                return null;
//            }
//            List<MeteoTnDto> fetchedData = new ArrayList<MeteoTnDto>();
//            for (MeteoTnMeasurementListDto extDto : dataList) {
//                Map<String, String> stationAttributes = new HashMap<String, String>();
//                MeteoStationDto stationDto = convertExternalDtoToStationDto(stationAttributes);
//                MeteoTnDto intDto = new MeteoTnDto(stationDto, stationAttributes);
//                fetchedData.add(intDto);
//            }
//            LOG.debug("fetchedData: "+fetchedData);
//            return fetchedData;
//        } catch (Exception ex) {
//            LOG.error("ERROR: " + ex.getMessage(), ex);
//            throw ex;
//        }
//    }

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

//    public MeteoStationDto convertMeteoTnDtoToStationDto(MeteoTnDto ptnDto) {
//        MeteoStationDto station = null;
//        if ( ptnDto!=null ) {
//            station = new MeteoStationDto();
//            MeteoAreaServiceDto extDto = ptnDto.getMeteoArea();
//            String municipality = ptnDto.getMunicipality();
//
//            //From StationDTO
//            String id = toHexString(extDto.getName());
//            Double longitude = null;
//            Double latitude = null;
//            if ( extDto.getPosition() != null ) {
//                List<Double> position = extDto.getPosition();
//                if ( position.size() > 0 ) {
//                    longitude = position.get(0);
//                }
//                if ( position.size() > 1 ) {
//                    latitude = position.get(1);
//                }
//            }
//            station.setId(id);
//            station.setName(DCUtils.trunc(extDto.getName(), 255));
//            station.setLongitude(longitude);
//            station.setLatitude(latitude);
//            //OMITTED: protected String crs;
//            station.setOrigin(DCUtils.trunc(getOrigin(), 255));
//            station.setMunicipality(DCUtils.trunc(municipality, 255));
//            station.setStationType(getStationType());
//
//            //From MeteoStationDto
//            station.setSlots(extDto.getSlotsTotal());
//            station.setAddress(extDto.getDescription());
//            //OMITTED: station.setPhone(phone);
//        }
//
//        return station;
//    }

}
