package it.bz.idm.bdp.dcbikesharingmoqo;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import it.bz.idm.bdp.dcbikesharingmoqo.dto.BikeDto;
import it.bz.idm.bdp.dcbikesharingmoqo.dto.BikesharingMoqoPageDto;
import it.bz.idm.bdp.dcbikesharingmoqo.dto.LocationDto;
import it.bz.idm.bdp.dto.StationDto;

@Service
public class BikesharingMoqoDataConverter {

    private static final Logger LOG = LogManager.getLogger(BikesharingMoqoDataConverter.class.getName());

    public static final long   MILLIS_ONE_DAY            = 24 * 60 * 60 * 1000;

    public static final String ORIGIN_KEY                = "app.origin";
    public static final String PERIOD_KEY                = "app.period";
//    public static final String FETCH_PERIOD              = "app.fetch_period";
    public static final String STATION_TYPE_KEY          = "app.station.type";

    public static final String AUTH_TOKEN_KEY            = "app.auth.token";
    public static final String SELECTED_TEAM_KEY         = "app.auth.selectedTeam";

    @Autowired
    private Environment env;

    //This must be initialized in application.properties file (for example "Meteotrentino")
    private String origin;
    //This must be initialized in application.properties file (for example "Meteostation")
    private String stationType;
    //This must be initialized in application.properties file (for example 300)
    private Integer period;

    //This must be initialized in application.properties file
    private String authToken;
    //This must be initialized in application.properties file
    private String selectedTeam;

//    private ObjectMapper mapper = new ObjectMapper();

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
//    public Integer getFetchPeriod() {
//        if ( this.fetchPeriod == null ) {
//            String strFetchPeriod = env.getProperty(FETCH_PERIOD);
//            this.fetchPeriod = DCUtils.convertStringToInteger(strFetchPeriod);
//            if ( this.fetchPeriod == null ) {
//                this.fetchPeriod = 1;
//            }
//        }
//        return this.fetchPeriod;
//    }

    public String getAuthToken() {
        if ( this.authToken == null ) {
            this.authToken = env.getProperty(AUTH_TOKEN_KEY);
        }
        return this.authToken;
    }
    public String getSelectedTeam() {
        if ( this.selectedTeam == null ) {
            this.selectedTeam = env.getProperty(SELECTED_TEAM_KEY);
        }
        return this.selectedTeam;
    }

//    /**
//     * Converts External DTO (FeatureDto coming from JSON) to Internal DTO (List<BikesharingMoqoDto> to send to the dataHub).
//     *
//     * @param stationsArray
//     * @return
//     */
//    public List<BikesharingMoqoDto> convertExternalStationDtoListToInternalDtoList(FeaturesDto stationsArray) {
//        if ( LOG.isDebugEnabled() ) {
//            LOG.debug("stationsArray: "+stationsArray);
//        }
//        List<BikesharingMoqoDto> extDtoList = new ArrayList<BikesharingMoqoDto>();
//        if ( stationsArray == null ) {
//            return extDtoList;
//        }
//
//        List<Feature> stationList = stationsArray.getFeatures();
//        for (Feature stationObj : stationList) {
//            StationDto stationDto = convertExternalStationDtoToStationDto(stationObj);
//            BikesharingMoqoDto extDto = new BikesharingMoqoDto(stationDto, stationObj);
//            extDtoList.add(extDto);
//        }
//
//        if ( LOG.isDebugEnabled() ) {
//            LOG.debug("extDtoList: "+extDtoList);
//        }
//        return extDtoList;
//    }

//    /**
//     * Converts External DTO (Feature coming from JSON) to Internal DTO (StationDto to send to the dataHub).
//     * 
//     *  Example of JSON provided by the service
//     * {
//     *   "type": "Feature",
//     *   "geometry": {
//     *     "type": "Point",
//     *     "coordinates": [
//     *       669803.015640121,
//     *       5123442.04315501
//     *     ]
//     *   },
//     *   "properties": {
//     *     "SCODE": "89940PG",
//     *     "NAME_D": "ETSCH BEI SALURN",
//     *     "NAME_I": "ADIGE A SALORNO",
//     *     "NAME_L": "ETSCH BEI SALURN",
//     *     "NAME_E": "ETSCH BEI SALURN",
//     *     "ALT": 210,
//     *     "LONG": 11.20262,
//     *     "LAT": 46.243333
//     *   }
//     * }
//     *
//     * @param stationObj
//     * @return
//     */
//    public StationDto convertExternalStationDtoToStationDto(Feature stationObj) {
//        if ( LOG.isDebugEnabled() ) {
//            LOG.debug("stationObj: "+stationObj);
//        }
//        StationDto stationDto = null;
//        if ( stationObj != null ) {
//
//            Properties stationProps = stationObj.getProperties();
//
//            if ( stationProps == null ) {
//                LOG.warn("Station properties are null!: stationObj: "+stationObj);
//                return stationDto;
//            }
//
//            stationDto = new StationDto();
//
//            //From StationDTO
//            stationDto.setId(stationProps.getSCODE());
//            stationDto.setName(DCUtils.trunc(stationProps.getNAMEI(), 255));
//            stationDto.setLongitude(stationProps.getLONG());
//            stationDto.setLatitude(stationProps.getLAT());
//            stationDto.setElevation(DCUtils.convertIntegerToDouble(stationProps.getALT()));
//            //OMITTED: protected String crs;
//            stationDto.setOrigin(DCUtils.trunc(getOrigin(), 255));
//            //TODO: get municipality from coordinates....... station.setMunicipality(DCUtils.trunc(map.get("city"), 255));
//            stationDto.setStationType(getStationType());
//
//            //MetaData must be flat, if we try to add a structure like "name": {"it":"...","de":"..."} we get an error
//            Map<String, Object> metaData = new HashMap<String, Object>();
//            metaData.put("name_it", stationProps.getNAMEI());
//            metaData.put("name_de", stationProps.getNAMED());
//            metaData.put("name_en", stationProps.getNAMEE());
//            //TODO: ladino????
//            //names.put("it", stationProps.getNAMEL());
//            stationDto.setMetaData(metaData);
//
//            //From MeteoStationDto
//            //OMITTED: station.setArea(extDto.getSlotsTotal());
//        }
//
//        if ( LOG.isDebugEnabled() ) {
//            LOG.debug("stationDto: "+stationDto);
//        }
//        return stationDto;
//    }

    /**
     * Converts External DTO (Location coming from JSON) to Internal DTO (LocationDto to send to the dataHub).
     * 
     * Example of JSON provided by the service
     * {
     *   "id": 864868739,
     *   "license": "55",
     *   "exchange_type": "i_lock_it_v2",
     *   "car_type": "bike",
     *   "fuel_type": "other_fuel",
     *   "in_maintenance": false,
     *   "available": null,
     *   "location": { ... },
     *   "car_model": {
     *     "id": 979334833,
     *     "name": "Standard",
     *     "car_type": null,
     *     "model_name": "City Standard",
     *     "car_manufacturer": {
     *       "id": 1671070861,
     *       "name": "City"
     *     }
     *   },
     *   "image": {
     *     "url": "https://s3.eu-central-1.amazonaws.com/fleetbutler/uploads/car/image/864868739/3bf30652d66c2a8caa5c110038f21b5e.png",
     *     "medium_url": "https://s3.eu-central-1.amazonaws.com/fleetbutler/uploads/car/image/864868739/medium_3bf30652d66c2a8caa5c110038f21b5e.png",
     *     "thumb_url": "https://s3.eu-central-1.amazonaws.com/fleetbutler/uploads/car/image/864868739/thumb_3bf30652d66c2a8caa5c110038f21b5e.png"
     *   },
     *   "return_requirements": {
     *     "areas": [],
     *     "fuel": 0,
     *     "central_lock": "locked",
     *     "fuel_ignored_when_charging": false,
     *     "must_be_charging": null
     *   }
     * }
     * 
     * @param locationObject
     * @return
     */
    public BikeDto convertExternalCarJsonToBikeDto(JSONObject carObject) {
        if ( LOG.isDebugEnabled() ) {
            LOG.debug("carObject: "+carObject);
        }
        BikeDto bikeDto = null;
        if ( carObject != null ) {
            bikeDto = new BikeDto();

            String carId          = DCUtils.allowNulls(DCUtils.getJsonLongValue(   carObject, "id"                   ));
            String carType        = DCUtils.allowNulls(DCUtils.getJsonStringValue( carObject, "car_type"             ));
            String carName        = DCUtils.allowNulls(DCUtils.getJsonObjectValue( carObject, "car_model.model_name" ));
            String license        = DCUtils.allowNulls(DCUtils.getJsonStringValue( carObject, "license"              ));
            String cleanness      = DCUtils.allowNulls(DCUtils.getJsonStringValue( carObject, "cleanness"            ));
            String exchangeType   = DCUtils.allowNulls(DCUtils.getJsonStringValue( carObject, "exchange_type"        ));
            String bigImageUrl    = DCUtils.allowNulls(DCUtils.getJsonObjectValue( carObject, "image.url"            ));
            String medImageUrl    = DCUtils.allowNulls(DCUtils.getJsonObjectValue( carObject, "image.medium_url"     ));
            String tmbImageUrl    = DCUtils.allowNulls(DCUtils.getJsonObjectValue( carObject, "image.thumb_url"      ));
            String inMaintenance  = DCUtils.allowNulls(DCUtils.getJsonBooleanValue(carObject, "in_maintenance"       ));
            String available      = DCUtils.allowNulls(DCUtils.getJsonStringValue( carObject, "available"            ));

            bikeDto.setId           (carId        );
            bikeDto.setCarType      (carType      );
            bikeDto.setName         (carName      );
            bikeDto.setLicense      (license      );
            bikeDto.setCleanness    (cleanness    );
            bikeDto.setExchangeType (exchangeType );
            bikeDto.setBigImageUrl  (bigImageUrl  );
            bikeDto.setMedImageUrl  (medImageUrl  );
            bikeDto.setTmbImageUrl  (tmbImageUrl  );
            bikeDto.setInMaintenance(inMaintenance);
            bikeDto.setAvailable    (available    );

            JSONObject locationObject = carObject.getJSONObject("location");
            LocationDto carLocation = convertExternalLocationJsonToLocationDto(locationObject);
            bikeDto.setLocation(carLocation);

        }

        if ( LOG.isDebugEnabled() ) {
            LOG.debug("bikeDto: "+bikeDto);
        }
        return bikeDto;
    }

    /**
     * Converts External DTO (Location coming from JSON) to Internal DTO (LocationDto to send to the dataHub).
     * 
     * Example of JSON provided by the service
     * "location": {
     *   "city": "Meran",
     *   "postcode": "39012",
     *   "created_at": "2019-08-08T15:08:35+02:00",
     *   "street": "Bahnhofsplatz",
     *   "lat": 46.6724745760751,
     *   "lng": 11.1500811721644,
     *   "kind": "expected_pickup",
     *   "name": "Meran Bahnhof_Stazione Merano",
     *   "center_lat": 46.6724745760751,
     *   "center_lng": 11.1500811721644,
     *   "radius": 71,
     *   "polygon": [],
     *   "parking_name": "Meran Bahnhof_Stazione Merano",
     *   "navigational_lat": null,
     *   "navigational_lng": null
     * }
     * 
     * @param locationObject
     * @return
     */
    public LocationDto convertExternalLocationJsonToLocationDto(JSONObject locationObject) {
        if ( LOG.isDebugEnabled() ) {
            LOG.debug("locationObject: "+locationObject);
        }
        LocationDto locationDto = null;
        if ( locationObject != null ) {
            locationDto = new LocationDto();

            String city        = DCUtils.getJsonStringValue(locationObject, "city"        );
            String postcode    = DCUtils.getJsonStringValue(locationObject, "postcode"    );
            String street      = DCUtils.getJsonStringValue(locationObject, "street"      );
            String kind        = DCUtils.getJsonStringValue(locationObject, "kind"        );
            String name        = DCUtils.getJsonStringValue(locationObject, "name"        );
            String parkingName = DCUtils.getJsonStringValue(locationObject, "parking_name");
            Double longitude   = DCUtils.getJsonDoubleValue(locationObject, "lng"         );
            Double latitude    = DCUtils.getJsonDoubleValue(locationObject, "lat"         );

            locationDto.setCity       (city       );
            locationDto.setPostcode   (postcode   );
            locationDto.setStreet     (street     );
            locationDto.setKind       (kind       );
            locationDto.setName       (name       );
            locationDto.setParkingName(parkingName);
            locationDto.setLongitude  (longitude  );
            locationDto.setLatitude   (latitude   );
        }

        if ( LOG.isDebugEnabled() ) {
            LOG.debug("locationDto: "+locationDto);
        }
        return locationDto;
    }

    public StationDto convertBikeDtoToStationDto(BikeDto bikeDto) {
        if ( LOG.isDebugEnabled() ) {
            LOG.debug("bikeDto: "+bikeDto);
        }
        StationDto stationDto = null;
        if ( bikeDto != null ) {

            stationDto = new StationDto();
            Map<String, Object> stationMetaData = new HashMap<String, Object>();

            stationDto.setId(bikeDto.getId());
            stationDto.setName(bikeDto.getName()+" "+bikeDto.getLicense());
            //OMITTED: protected Double elevation;
            //OMITTED: protected String crs;
            stationDto.setOrigin(DCUtils.trunc(getOrigin(), 255));
            stationDto.setStationType(getStationType());
            stationDto.setMetaData(stationMetaData);

            stationMetaData.put("cleanness"             , bikeDto.getCleanness()     );
            stationMetaData.put("exchange_type"         , bikeDto.getExchangeType()  );
            stationMetaData.put("big_image_url"         , bikeDto.getBigImageUrl()   );
            stationMetaData.put("medium_image_url"      , bikeDto.getMedImageUrl()   );
            stationMetaData.put("thumb_image_url"       , bikeDto.getTmbImageUrl()   );
            stationMetaData.put("in_maintenance"        , bikeDto.getInMaintenance() );
            stationMetaData.put("available"             , bikeDto.getAvailable()     );

            if ( bikeDto.getLocation() != null ) {
                LocationDto locationDto = bikeDto.getLocation();
                stationMetaData.put("location_city"         , locationDto.getCity()       );
                stationMetaData.put("location_postcode"     , locationDto.getPostcode()   );
                stationMetaData.put("location_street"       , locationDto.getStreet()     );
                stationMetaData.put("location_lng"          , locationDto.getLongitude()  );
                stationMetaData.put("location_lat"          , locationDto.getLatitude()   );
                stationMetaData.put("location_kind"         , locationDto.getKind()       );
                stationMetaData.put("location_name"         , locationDto.getName()       );
                stationMetaData.put("location_parking_name" , locationDto.getParkingName());

                stationDto.setLongitude(locationDto.getLongitude());
                stationDto.setLatitude( locationDto.getLatitude() );
            }

        }
        return stationDto;
    }

    /**
     * Converts the string returned by the Bikesharing "/cars" service in a more useful internal representation
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
    public BikesharingMoqoPageDto convertCarsResponseToInternalDTO(String responseString) throws Exception {

        BikesharingMoqoPageDto dto = new BikesharingMoqoPageDto();

        long jsonStart = System.currentTimeMillis();
        JSONObject joMain = new JSONObject(responseString);
        long jsonEnd = System.currentTimeMillis();
        if ( LOG.isDebugEnabled() ) {
            LOG.debug("Time to parse with org.json: " + (jsonEnd-jsonStart));
        }

//        long jacksonStart = System.currentTimeMillis();
//        CarsList cars = mapper.readValue(responseString, new TypeReference<CarsList>() {});
//        long jacksonEnd = System.currentTimeMillis();
//        if ( LOG.isDebugEnabled() ) {
//            LOG.info("Time to parse with jackson: " + (jacksonEnd-jacksonStart));
//        }

        //Get information regarding next page (if null this is the last page)
        /* "pagination": {"total_pages": 6, "current_page": 1, "next_page": 2, "prev_page": null} */
        JSONObject joPage = joMain.getJSONObject("pagination");
        Long nextPage = DCUtils.getJsonLongValue(joPage, "next_page");
        dto.getPagination().setNextPage(nextPage);

        //Get information to store as StationDto
        JSONArray carsArray = joMain.getJSONArray("cars");
        int carsLength = carsArray!=null ? carsArray.length() : 0;
        for ( int i=0 ; i<carsLength ; i++ ) {
            JSONObject carObject = carsArray.getJSONObject(i);
            String carType = DCUtils.getJsonStringValue(carObject, "car_type");
            Long   carId   = DCUtils.getJsonLongValue(  carObject, "id");
            LOG.debug("carType="+carType);
            if ( !"bike".equalsIgnoreCase(carType) ) {
                LOG.warn("SKIP Car id="+carId+" thats is not of type Bike! carType=" + carType);
                continue;
            }

            BikeDto bikeDto = convertExternalCarJsonToBikeDto(carObject);
            dto.getBikeList().add(bikeDto);

            Object returnAreas = DCUtils.getJsonObjectValue(carObject, "return_requirements.areas");
            if ( returnAreas instanceof JSONArray ) {
                JSONArray retAreasArray = (JSONArray) returnAreas;
                for ( int a=0 ; a<retAreasArray.length() ; a++ ) {
                    JSONObject areaObject = retAreasArray.getJSONObject(a);
                    JSONObject areaLocationObject = areaObject.getJSONObject("center");
                    LocationDto areaDto = convertExternalLocationJsonToLocationDto(areaLocationObject);
                    String areaName = areaDto.getName();
                    bikeDto.getParkingAreaMap().put(areaName, areaDto);
                }
            }

        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("dto: "+dto); 
        }
        return dto;
    }

    public Map<String, LocationDto> getDistinctLocations(List<BikeDto> bikeList) {
        Map<String, LocationDto> locationMap = new HashMap<String, LocationDto>();

        for ( int i=0 ; bikeList!=null && i<bikeList.size() ; i++ ) {
            BikeDto bikeDto = bikeList.get(i);
            Map<String, LocationDto> parkingAreaMap = bikeDto.getParkingAreaMap();
            Set<String> keySet = parkingAreaMap!=null ? parkingAreaMap.keySet() : null;
            Iterator<String> iterator = keySet!=null ? keySet.iterator() : null;
            while (iterator!=null && iterator.hasNext()) {
                String areaName = iterator.next();
                LocationDto areaDto = parkingAreaMap.get(areaName);

                //Check if this area is already present in the full map
                LocationDto locDto = locationMap.get(areaName);
                if ( locDto != null ) {
                    //Check if data is the same
                    chackEqualsLocation(areaDto, locDto);
                } else {
                    locationMap.put(areaName, areaDto);
                }
            }
        }

        return locationMap;
    }

    public boolean chackEqualsLocation(LocationDto areaDto, LocationDto locDto) {
        boolean retval = true;
        if ( areaDto==null || locDto == null ) {
            return false;
        }

        boolean sameCity        = DCUtils.objectEquals(locDto.getCity()       , areaDto.getCity()       );
        boolean samePostcode    = DCUtils.objectEquals(locDto.getPostcode()   , areaDto.getPostcode()   );
        boolean sameStreet      = DCUtils.objectEquals(locDto.getStreet()     , areaDto.getStreet()     );
        boolean sameKind        = DCUtils.objectEquals(locDto.getKind()       , areaDto.getKind()       );
        boolean sameName        = DCUtils.objectEquals(locDto.getName()       , areaDto.getName()       );
        boolean sameParkingName = DCUtils.objectEquals(locDto.getParkingName(), areaDto.getParkingName());
        boolean sameLongitude   = DCUtils.objectEquals(locDto.getLongitude()  , areaDto.getLongitude()  );
        boolean sameLatitude    = DCUtils.objectEquals(locDto.getLatitude()   , areaDto.getLatitude()   );

        if ( !sameCity        ) { LOG.debug("DIFFERENT City        : loc="+locDto.getCity        ()+"  area="+areaDto.getCity        ()); retval = false; }
        if ( !samePostcode    ) { LOG.debug("DIFFERENT Postcode    : loc="+locDto.getPostcode    ()+"  area="+areaDto.getPostcode    ()); retval = false; }
        if ( !sameStreet      ) { LOG.debug("DIFFERENT Street      : loc="+locDto.getStreet      ()+"  area="+areaDto.getStreet      ()); retval = false; }
        if ( !sameKind        ) { LOG.debug("DIFFERENT Kind        : loc="+locDto.getKind        ()+"  area="+areaDto.getKind        ()); retval = false; }
        if ( !sameName        ) { LOG.debug("DIFFERENT Name        : loc="+locDto.getName        ()+"  area="+areaDto.getName        ()); retval = false; }
        if ( !sameParkingName ) { LOG.debug("DIFFERENT ParkingName : loc="+locDto.getParkingName ()+"  area="+areaDto.getParkingName ()); retval = false; }
        if ( !sameLongitude   ) { LOG.debug("DIFFERENT Longitude   : loc="+locDto.getLongitude   ()+"  area="+areaDto.getLongitude   ()); retval = false; }
        if ( !sameLatitude    ) { LOG.debug("DIFFERENT Latitude    : loc="+locDto.getLatitude    ()+"  area="+areaDto.getLatitude    ()); retval = false; }

        return retval;
    }

}
