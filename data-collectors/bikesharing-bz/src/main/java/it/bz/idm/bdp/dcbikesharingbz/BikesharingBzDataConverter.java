package it.bz.idm.bdp.dcbikesharingbz;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import it.bz.idm.bdp.dcbikesharingbz.dto.BikesharingBzBayDto;
import it.bz.idm.bdp.dcbikesharingbz.dto.BikesharingBzDto;
import it.bz.idm.bdp.dcbikesharingbz.dto.BikesharingBzStationDto;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.StationDto;

@Service
public class BikesharingBzDataConverter {

    private static final Logger LOG = LogManager.getLogger(BikesharingBzDataConverter.class.getName());

    public static final String DATA_TYPE_STATION_AVAILABILITY        = DataTypeDto.AVAILABILITY;
    public static final String DATA_TYPE_STATION_NUMBER_AVAILABE     = DataTypeDto.NUMBER_AVAILABE;
    public static final String DATA_TYPE_STATION_TOTAL_BAYS          = "total-bays";
    public static final String DATA_TYPE_STATION_FREE_BAYS           = "free-bays";

    public static final String DATA_TYPE_BAY_AVAILABILITY            = DataTypeDto.AVAILABILITY;
    public static final String DATA_TYPE_BAY_USAGE_STATE             = "usage-state";

    public static final String DATA_TYPE_BICYCLE_AVAILABILITY        = DataTypeDto.AVAILABILITY;
    public static final String DATA_TYPE_BICYCLE_BATTERY_STATE       = "battery-state";

    public static final String STATION_METADATA_STATION_ADDRESS      = "address";
    public static final String STATION_METADATA_STATION_TOTAL_BAYS   = "total-bays";
    //public static final String STATION_METADATA_BAYS_DETAIL  = "bays-detail";

    public static final String STATION_METADATA_BAY_CHARGER          = "charger";
    public static final String STATION_METADATA_BAY_USE              = "use";

    public static final String STATION_METADATA_BICYCLE_ELECTRIC     = "electric";
    public static final String STATION_METADATA_BICYCLE_LAT          = "lat";
    public static final String STATION_METADATA_BICYCLE_LNG          = "lng";

    public static final String STATION_STATE_OUT_OF_SERVICE  = "OUT_OF_SERVICE";
    public static final String STATION_STATE_READY           = "READY";
    public static final String STATION_STATE_OFFLINE         = "OFFLINE";

    public static final String BAY_STATE_OUT_OF_SERVICE      = "OUT_OF_SERVICE";
    public static final String BAY_STATE_READY               = "READY";

    public static final String BAY_USE_SHARING               = "SHARING";

    public static final String BAY_USAGE_STATE_PRELIEVO      = "PRELIEVO";
    public static final String BAY_USAGE_STATE_RICONSEGNA    = "RICONSEGNA";

    public static final String STATION_TYPE_STATION      = "BikesharingStation";
    public static final String STATION_TYPE_BAY          = "Bicyclestationbay";
    public static final String STATION_TYPE_BICYCLE      = "Bicycle";

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

    public String getStationTypeBay() {
        return STATION_TYPE_BAY;
    }

    public String getStationTypeBicycle() {
        return STATION_TYPE_BICYCLE;
    }

    public String getAuthToken() {
        if ( this.authToken == null ) {
            this.authToken = env.getProperty(AUTH_TOKEN_KEY);
        }
        return this.authToken;
    }

    /**
     * Converts External DTO (Bay coming from JSON) to a more convenient internal DTO (BayDto).
     * 
     * Example of JSON provided by the service
     * {
     *     "label": "Piazza Gries - Grieserplatz:1",
     *     "state": "READY",
     *     "charger": true,
     *     "use": [
     *         "SHARING"
     *     ],
     *     "usageState": [
     *         "PRELIEVO"
     *     ],
     *     "vehicle": {
     *         "batteryState": "CHARGED",
     *         "code": "City 62",
     *         "vehicleType": {
     *             "name": "Sunshine",
     *             "electric": true
     *         }
     *     }
     * }
     * 
     * @param bayObject
     * @return
     */
    public BikesharingBzBayDto convertExternalBayJsonToBayDto(JSONObject bayObject) {
        if ( LOG.isDebugEnabled() ) {
            LOG.debug("bayObject: "+bayObject);
        }
        BikesharingBzBayDto bayDto = null;
        if ( bayObject != null ) {
            bayDto = new BikesharingBzBayDto();

            //String bayId          = DCUtils.allowNulls(DCUtils.getJsonLongValue(   bayObject, "id"                   ));
            String  label          = DCUtils.allowNulls(DCUtils.getJsonStringValue(     bayObject, "label"             ));
            String  state          = DCUtils.allowNulls(DCUtils.getJsonObjectValue(     bayObject, "state" ));
            Boolean charger        = DCUtils.getJsonBooleanValue(                       bayObject, "charger"       );
            String  use            = DCUtils.allowNulls(DCUtils.getJsonStringArrayItem( bayObject, "use"           , 0));
            String  usageState     = DCUtils.allowNulls(DCUtils.getJsonStringArrayItem( bayObject, "usageState"    , 0));
            Boolean vehiclePresent = !bayObject.isNull("vehicle");

            bayDto.setLabel         (label);
            bayDto.setState         (state);
            bayDto.setCharger       (charger);
            bayDto.setUse           (use);
            bayDto.setUsageState    (usageState);
            bayDto.setVehiclePresent(vehiclePresent);

            if ( vehiclePresent ) {
                String  vehicleBatteryState = DCUtils.allowNulls(  DCUtils.getJsonObjectValue( bayObject, "vehicle.batteryState"         ));
                String  vehicleCode         = DCUtils.allowNulls(  DCUtils.getJsonObjectValue( bayObject, "vehicle.code"                 ));
                String  vehicleName         = DCUtils.allowNulls(  DCUtils.getJsonObjectValue( bayObject, "vehicle.vehicleType.name"     ));
                Boolean vehicleElectric     = DCUtils.objectEquals(DCUtils.getJsonObjectValue( bayObject, "vehicle.vehicleType.electric" ), Boolean.TRUE);

                bayDto.setVehicleBatteryState(vehicleBatteryState);
                bayDto.setVehicleCode(vehicleCode);
                bayDto.setVehicleName(vehicleName);
                bayDto.setVehicleElectric(vehicleElectric);
            }

        }

        if ( LOG.isDebugEnabled() ) {
            LOG.debug("bayDto: "+bayDto);
        }
        return bayDto;
    }

    /**
     * Converts External DTO (Station coming from JSON) to a more convenient internal DTO (StationDto).
     * 
     * Example of JSON provided by the service
     * {
     *     "id": "BAh7CEkiCGdpZAY6BkVUSSIsZ2lkOi8vYmt3L1NoYXJpbmdTdGF0aW9uLzE5OT9leHBpcmVzX2luBjsAVEkiDHB1cnBvc2UGOwBUSSIMZGVmYXVsdAY7AFRJIg9leHBpcmVzX2F0BjsAVDA=--1bd9304bdb17a53ff75a7080179f84c9d2776825",
     *     "name": "Piazza Gries - Grieserplatz",
     *     "address": "Corso della Libertà, 117",
     *     "state": "READY",
     *     "lat": 46.502365,
     *     "lng": 11.335204,
     *     "totalBays": 12,
     *     "freeBay": 8,
     *     "availableVehicles": 4,
     *     "bays": {
     *         "nodes": [
     *             {
     *              ....
     *             }
     *         ]
     *     }
     * }
     * 
     * @param stationObject
     * @return
     */
    public BikesharingBzStationDto convertExternalStationJsonToStationDto(JSONObject stationObject) {
        if ( LOG.isDebugEnabled() ) {
            LOG.debug("stationObject: "+stationObject);
        }
        BikesharingBzStationDto stationDto = null;
        if ( stationObject != null ) {
            stationDto = new BikesharingBzStationDto();

            String id                = DCUtils.getJsonStringValue(stationObject, "id"                );
            String name              = DCUtils.getJsonStringValue(stationObject, "name"              );
            String address           = DCUtils.getJsonStringValue(stationObject, "address"           );
            String state             = DCUtils.getJsonStringValue(stationObject, "state"             );
            Double longitude         = DCUtils.getJsonDoubleValue(stationObject, "lng"               );
            Double latitude          = DCUtils.getJsonDoubleValue(stationObject, "lat"               );
            Long   totalBays         = DCUtils.getJsonLongValue(  stationObject, "totalBays"         );
            Long   freeBay           = DCUtils.getJsonLongValue(  stationObject, "freeBay"           );
            Long   availableVehicles = DCUtils.getJsonLongValue(  stationObject, "availableVehicles" );

            stationDto.setId               ( id                );
            stationDto.setName             ( name              );
            stationDto.setAddress          ( address           );
            stationDto.setState            ( state             );
            stationDto.setLongitude        ( longitude         );
            stationDto.setLatitude         ( latitude          );
            stationDto.setTotalBays        ( totalBays         );
            stationDto.setFreeBays         ( freeBay           );
            stationDto.setAvailableVehicles( availableVehicles );

            JSONObject bays = stationObject.optJSONObject("bays");
            JSONArray nodes = bays != null ? bays.optJSONArray("nodes") : null;
            if ( nodes != null ) {
                stationDto.setBaysJsonString(nodes.toString());
                for ( int i=0 ; i<nodes.length() ; i++ ) {
                    JSONObject bayObject = nodes.getJSONObject(i);
                    BikesharingBzBayDto bayDto = convertExternalBayJsonToBayDto(bayObject);
                    if ( bayDto != null ) {
                        bayDto.setParentStation(stationDto);
                        stationDto.getBayList().add(bayDto);
                    }
                }
            }

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
    public StationDto convertBikesharingStationDtoToStationDto(BikesharingBzStationDto bikesharingStationDto) {
        if ( LOG.isDebugEnabled() ) {
            LOG.debug("bikesharingStationDto: "+bikesharingStationDto);
        }
        StationDto stationDto = null;
        if ( bikesharingStationDto != null ) {

            stationDto = new StationDto();
            Map<String, Object> stationMetaData = new HashMap<String, Object>();

            stationDto.setId(bikesharingStationDto.getId());
            stationDto.setName(bikesharingStationDto.getName());
            //OMITTED: protected Double elevation;
            //OMITTED: protected String crs;
            stationDto.setOrigin(DCUtils.trunc(getOrigin(), 255));
            stationDto.setStationType(getStationTypeStation());
            stationDto.setLatitude(bikesharingStationDto.getLatitude());
            stationDto.setLongitude(bikesharingStationDto.getLongitude());

            stationMetaData.put(BikesharingBzDataConverter.DATA_TYPE_STATION_TOTAL_BAYS       , bikesharingStationDto.getTotalBays() );
            stationMetaData.put(BikesharingBzDataConverter.STATION_METADATA_STATION_ADDRESS   , bikesharingStationDto.getAddress() );

            //Call to setMetaData must be done when the Map is completely filled
            stationDto.setMetaData(stationMetaData);

        }
        return stationDto;
    }

    /**
     * Converts the internal representation of the BikesharingBay in StationDto used by the Open Data Hub (Station at Bay level).
     * 
     * @param bikesharingBayDto
     * @return
     */
    public StationDto convertBikesharingBayDtoToStationDto_Bay(BikesharingBzBayDto bikesharingBayDto) {
        if ( LOG.isDebugEnabled() ) {
            LOG.debug("bikesharingBayDto: "+bikesharingBayDto);
        }
        StationDto stationDto = null;
        if ( bikesharingBayDto != null ) {

            stationDto = new StationDto();
            Map<String, Object> stationMetaData = new HashMap<String, Object>();

            stationDto.setId(bikesharingBayDto.getLabel());
            stationDto.setName(bikesharingBayDto.getLabel());
            //OMITTED: protected Double elevation;
            //OMITTED: protected String crs;
            stationDto.setOrigin(DCUtils.trunc(getOrigin(), 255));
            stationDto.setStationType(getStationTypeBay());
            if ( bikesharingBayDto.getParentStation() != null ) {
                stationDto.setLatitude(bikesharingBayDto.getParentStation().getLatitude());
                stationDto.setLongitude(bikesharingBayDto.getParentStation().getLongitude());
                //Parent for the Bay is the Station, its ID is the id Attribute
                stationDto.setParentStation(bikesharingBayDto.getParentStation().getId());
            }

            stationMetaData.put(BikesharingBzDataConverter.STATION_METADATA_BAY_CHARGER   , bikesharingBayDto.getCharger() );
            stationMetaData.put(BikesharingBzDataConverter.STATION_METADATA_BAY_USE       , bikesharingBayDto.getUse() );

            //Call to setMetaData must be done when the Map is completely filled
            stationDto.setMetaData(stationMetaData);

        }
        return stationDto;
    }

    /**
     * Converts the internal representation of the BikesharingBay in StationDto used by the Open Data Hub (Station at Bicycle level).
     * 
     * @param bikesharingBayDto
     * @return
     */
    public StationDto convertBikesharingBayDtoToStationDto_Bicycle(BikesharingBzBayDto bikesharingBayDto) {
        if ( LOG.isDebugEnabled() ) {
            LOG.debug("bikesharingBayDto: "+bikesharingBayDto);
        }
        StationDto stationDto = null;
        if ( bikesharingBayDto != null && Boolean.TRUE.equals(bikesharingBayDto.getVehiclePresent()) ) {

            stationDto = new StationDto();
            Map<String, Object> stationMetaData = new HashMap<String, Object>();

            //Parent for the Bicycle is the Bay, its ID is the label attribute
            stationDto.setParentStation(bikesharingBayDto.getLabel());
            stationDto.setId(bikesharingBayDto.getVehicleCode());
            stationDto.setName(bikesharingBayDto.getVehicleName());
            //OMITTED: protected Double elevation;
            //OMITTED: protected String crs;
            stationDto.setOrigin(DCUtils.trunc(getOrigin(), 255));
            stationDto.setStationType(getStationTypeBicycle());
            if ( bikesharingBayDto.getParentStation() != null ) {
                stationDto.setLatitude(bikesharingBayDto.getParentStation().getLatitude());
                stationDto.setLongitude(bikesharingBayDto.getParentStation().getLongitude());

                stationMetaData.put(BikesharingBzDataConverter.STATION_METADATA_BICYCLE_LAT   , bikesharingBayDto.getParentStation().getLatitude() );
                stationMetaData.put(BikesharingBzDataConverter.STATION_METADATA_BICYCLE_LNG   , bikesharingBayDto.getParentStation().getLongitude() );
            }

            stationMetaData.put(BikesharingBzDataConverter.STATION_METADATA_BICYCLE_ELECTRIC  , bikesharingBayDto.getVehicleElectric() );

            //Call to setMetaData must be done when the Map is completely filled
            stationDto.setMetaData(stationMetaData);

        }
        return stationDto;
    }

    /**
     * Converts the string returned by the Bikesharing "/api/v1/stations" service in a more useful internal representation
     * 
     * @param responseString
     * @return
     * @throws Exception
     */
    /**
     * @param responseString
     * @return
     * @throws Exception
     */
    public BikesharingBzDto convertStationsResponseToInternalDTO(String responseString) throws Exception {

        BikesharingBzDto dto = new BikesharingBzDto();

        //How much does it take to parse the JSON String with org.json?
        long jsonStart = System.currentTimeMillis();
        JSONArray stationsArray = new JSONArray(responseString);
        long jsonEnd = System.currentTimeMillis();
        if ( LOG.isDebugEnabled() ) {
            LOG.debug("Time to parse with org.json: " + (jsonEnd-jsonStart));
        }

        //How much does it take to parse the JSON String with Jackson (5-10 times slower than org.json)?
//        long jacksonStart = System.currentTimeMillis();
//        CarsList cars = mapper.readValue(responseString, new TypeReference<CarsList>() {});
//        long jacksonEnd = System.currentTimeMillis();
//        if ( LOG.isDebugEnabled() ) {
//            LOG.info("Time to parse with jackson: " + (jacksonEnd-jacksonStart));
//        }

        //Get information to store as StationDto
        int stationsLength = stationsArray!=null ? stationsArray.length() : 0;
        for ( int i=0 ; i<stationsLength ; i++ ) {
            JSONObject stationObject = stationsArray.getJSONObject(i);

            BikesharingBzStationDto stationDto = convertExternalStationJsonToStationDto(stationObject);
            dto.getStationList().add(stationDto);

        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("dto: "+dto); 
        }
        return dto;
    }

    /**
     * Converts the string returned by the Bikesharing "/cars/{id}/availability" service in a more useful internal representation
     * 
     * @param responseString
     * @return
     * @throws Exception
     */
    /**
     * @param responseString
     * @return
     * @throws Exception
     */
    public BikesharingBzStationDto convertStationDetailResponseToInternalDTO(String responseString) throws Exception {

        BikesharingBzStationDto retval = new BikesharingBzStationDto();

        //How much does it take to parse the JSON String with org.json?
        long jsonStart = System.currentTimeMillis();
        JSONObject joMain = new JSONObject(responseString);
        long jsonEnd = System.currentTimeMillis();
        if ( LOG.isDebugEnabled() ) {
            LOG.debug("Time to parse with org.json: " + (jsonEnd-jsonStart));
        }

        //How much does it take to parse the JSON String with Jackson?
//        long jacksonStart = System.currentTimeMillis();
//        CarsList cars = mapper.readValue(responseString, new TypeReference<CarsList>() {});
//        long jacksonEnd = System.currentTimeMillis();
//        if ( LOG.isDebugEnabled() ) {
//            LOG.info("Time to parse with jackson: " + (jacksonEnd-jacksonStart));
//        }

        BikesharingBzStationDto stationDto = convertExternalStationJsonToStationDto(joMain);
        retval = stationDto;

        if (LOG.isDebugEnabled()) {
            LOG.debug("retval: "+retval); 
        }
        return retval;
    }

}
