package it.bz.idm.bdp.dcbikesharingpapin;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import it.bz.idm.bdp.dcbikesharingpapin.dto.BikesharingPapinDto;
import it.bz.idm.bdp.dcbikesharingpapin.dto.BikesharingPapinStationDto;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.StationDto;

@Service
public class BikesharingPapinDataConverter {

    private static final Logger LOG = LogManager.getLogger(BikesharingPapinDataConverter.class.getName());

    public static final String DATA_TYPE_STATION_AVAILABILITY             = DataTypeDto.AVAILABILITY;
    public static final String DATA_TYPE_STATION_IS_CLOSE                 = "isClosed";

    public static final String STATION_METADATA_STATION_IS_CLOSE          = "isClose";
    public static final String STATION_METADATA_STATION_START_HOUR        = "startHour";
    public static final String STATION_METADATA_STATION_END_HOUR          = "endHour";
    public static final String STATION_METADATA_STATION_LUNCH_BREAK_START = "lunchBreakStart";
    public static final String STATION_METADATA_STATION_LUNCH_BREAK_END   = "lunchBreakEnd";
    public static final String STATION_METADATA_STATION_BIKE_AVAILABLE    = "bikeAvailable";
    public static final String STATION_METADATA_STATION_URL               = "url";

    public static final String STATION_TYPE_STATION      = "BikesharingStation";

    public static final String ORIGIN_KEY                = "app.origin";
    public static final String PERIOD_KEY                = "app.period";

    public static final String AUTH_TOKEN_KEY            = "app_auth_token";

    @Autowired
    private Environment env;

    //This must be initialized in application.properties file (for example "BIKE_SHARING_MERANO")
    private String origin;
    //This must be initialized in application.properties file (for example 300)
    private Integer period;

    //This must be initialized in application.properties file
    private String authToken;

//    private ObjectMapper mapper = new ObjectMapper();

    public String getOrigin() {
        if ( this.origin == null ) {
            this.origin = env.getProperty(ORIGIN_KEY);
        }
        return this.origin;
    }

    public Integer getPeriod() {
        if ( this.period == null ) {
            this.period = env.getProperty(PERIOD_KEY, Integer.class);
        }
        return this.period;
    }

    public String getStationTypeStation() {
        return STATION_TYPE_STATION;
    }

    public String getAuthToken() {
        if ( this.authToken == null ) {
            this.authToken = env.getProperty(AUTH_TOKEN_KEY);
        }
        return this.authToken;
    }

    /**
     * Converts External DTO (Station coming from JSON) to a more convenient internal DTO (StationDto).
     *
     * @param stationObject
     * @return
     */
    public BikesharingPapinStationDto convertExternalStationJsonToStationDto(JSONObject stationObject) {
        if ( LOG.isDebugEnabled() ) {
            LOG.debug("stationObject: "+stationObject);
        }
        BikesharingPapinStationDto stationDto = null;
        if ( stationObject != null ) {
            stationDto = new BikesharingPapinStationDto();

            Double _id               = DCUtils.getJsonDoubleValue(stationObject, "id"                );
            String id = DCUtils.convertDoubleToInteger(_id).toString();

            Object _name             = DCUtils.getJsonObjectValue(stationObject, "name.en"           );
            String name = _name.toString();

            String _longitude        = DCUtils.getJsonStringValue(stationObject, "longitude"         );
            Double longitude = DCUtils.convertStringToDouble(_longitude);

            String _latitude          = DCUtils.getJsonStringValue(stationObject, "latitude"         );
            Double latitude = DCUtils.convertStringToDouble(_latitude);

            Boolean isClose          = DCUtils.getJsonBooleanValue(stationObject, "isClose"          );
            String startHour         = DCUtils.getJsonStringValue(stationObject, "startHour"         );
            String endHour           = DCUtils.getJsonStringValue(stationObject, "endHour"           );
            String lunchBreakStart   = DCUtils.getJsonStringValue(stationObject, "lunchBreakStart"   );
            String lunchBreakEnd     = DCUtils.getJsonStringValue(stationObject, "lunchBreakEnd"     );
            Boolean bikeAvailable    = DCUtils.getJsonBooleanValue(stationObject, "bikeAvailable"    );
            String url               = DCUtils.getJsonStringValue(stationObject, "url"               );

            stationDto.setId               ( id                );
            stationDto.setName             ( name              );
            stationDto.setLongitude        ( longitude         );
            stationDto.setLatitude         ( latitude          );
            stationDto.setIsClose          ( isClose           );
            stationDto.setStartHour        ( startHour         );
            stationDto.setEndHour          ( endHour           );
            stationDto.setLunchBreakStart  ( lunchBreakStart   );
            stationDto.setLunchBreakEnd    ( lunchBreakEnd     );
            stationDto.setBikeAvailable    ( bikeAvailable     );
            stationDto.setUrl              ( url               );
        }

        if ( LOG.isDebugEnabled() ) {
            LOG.debug("stationDto: "+stationDto);
        }
        return stationDto;
    }

    /**
     * Converts the internal representation of the BikesharingStation in StationDto used by the Open Data Hub.
     * 
     * @param bikesharingStationDto
     * @return
     */
    public StationDto convertBikesharingStationDtoToStationDto(BikesharingPapinStationDto bikesharingStationDto) {
        if ( LOG.isDebugEnabled() ) {
            LOG.debug("bikesharingStationDto: "+bikesharingStationDto);
        }
        StationDto stationDto = null;
        if ( bikesharingStationDto != null ) {

            stationDto = new StationDto();
            Map<String, Object> stationMetaData = new HashMap<String, Object>();

            stationDto.setId(DCUtils.trunc(getOrigin(), 255)+":"+bikesharingStationDto.getId());
            stationDto.setName(bikesharingStationDto.getName());
            //OMITTED: protected Double elevation;
            //OMITTED: protected String crs;
            stationDto.setOrigin(DCUtils.trunc(getOrigin(), 255));
            stationDto.setStationType(getStationTypeStation());
            stationDto.setLatitude(bikesharingStationDto.getLatitude());
            stationDto.setLongitude(bikesharingStationDto.getLongitude());

            //stationMetaData.put(BikesharingPapinDataConverter.STATION_METADATA_STATION_IS_CLOSE, bikesharingStationDto.getIsClose() );
            stationMetaData.put(BikesharingPapinDataConverter.STATION_METADATA_STATION_START_HOUR, bikesharingStationDto.getStartHour() );
            stationMetaData.put(BikesharingPapinDataConverter.STATION_METADATA_STATION_END_HOUR, bikesharingStationDto.getEndHour() );
            stationMetaData.put(BikesharingPapinDataConverter.STATION_METADATA_STATION_LUNCH_BREAK_START, bikesharingStationDto.getLunchBreakStart() );
            stationMetaData.put(BikesharingPapinDataConverter.STATION_METADATA_STATION_LUNCH_BREAK_END, bikesharingStationDto.getLunchBreakEnd() );
            //stationMetaData.put(BikesharingPapinDataConverter.STATION_METADATA_STATION_BIKE_AVAILABLE, bikesharingStationDto.getBikeAvailable() );
            stationMetaData.put(BikesharingPapinDataConverter.STATION_METADATA_STATION_URL, bikesharingStationDto.getUrl() );

            //Call to setMetaData must be done when the Map is completely filled
            stationDto.setMetaData(stationMetaData);

        }
        return stationDto;
    }

    /**
     * Converts the string returned by the service in a more useful internal representation
     * 
     * @param responseString
     * @return
     * @throws Exception
     */
    public BikesharingPapinDto convertStationsResponseToInternalDTO(String responseString) throws Exception {

        BikesharingPapinDto dto = new BikesharingPapinDto();

        //How much does it take to parse the JSON String with org.json?
        long jsonStart = System.currentTimeMillis();
        JSONArray stationsArray = new JSONArray(responseString);
        long jsonEnd = System.currentTimeMillis();
        if ( LOG.isDebugEnabled() ) {
            LOG.debug("Time to parse with org.json: " + (jsonEnd-jsonStart));
        }

        //Get information to store as StationDto
        int stationsLength = stationsArray!=null ? stationsArray.length() : 0;
        for ( int i=0 ; i<stationsLength ; i++ ) {
            JSONObject stationObject = stationsArray.getJSONObject(i);

            BikesharingPapinStationDto stationDto = convertExternalStationJsonToStationDto(stationObject);
            dto.getStationList().add(stationDto);

        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("dto: "+dto);
        }
        return dto;
    }

}
