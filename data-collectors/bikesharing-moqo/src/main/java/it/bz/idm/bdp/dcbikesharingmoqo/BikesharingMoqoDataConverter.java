package it.bz.idm.bdp.dcbikesharingmoqo;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import it.bz.idm.bdp.dcbikesharingmoqo.dto.AvailabilityDto;
import it.bz.idm.bdp.dcbikesharingmoqo.dto.BikeDto;
import it.bz.idm.bdp.dcbikesharingmoqo.dto.BikesharingMoqoPageDto;
import it.bz.idm.bdp.dcbikesharingmoqo.dto.LocationDto;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.StationDto;

@Service
public class BikesharingMoqoDataConverter {

    private static final Logger LOG = LoggerFactory.getLogger(BikesharingMoqoDataConverter.class.getName());

    public static final String DATA_TYPE_AVAILABILITY        = DataTypeDto.AVAILABILITY;
    public static final String DATA_TYPE_FUTURE_AVAILABILITY = DataTypeDto.FUTURE_AVAILABILITY;
    public static final String DATA_TYPE_IN_MAINTENANCE      = "in-maintenance";

    public static final String STATION_TYPE              = "Bicycle";

    public static final String ORIGIN_KEY                = "app.origin";
    public static final String PERIOD_KEY                = "app.period";

    public static final String STATION_FUTURE_AVAIL_MINS = "app.station.future_availability.minutes";

    public static final String AUTH_TOKEN_KEY            = "app_auth_token";
    public static final String SELECTED_TEAM_KEY         = "app_auth_selectedTeam";

    @Autowired
    private Environment env;

    //This must be initialized in application.properties file (for example "BIKE_SHARING_MERANO")
    private String origin;
    //This must be initialized in application.properties file (for example 300)
    private Integer period;
    //This must be initialized in application.properties file (for example 60)
    private Integer futureAvailMinutes;

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
    public Integer getPeriod() {
        if ( this.period == null ) {
            this.period = env.getProperty(PERIOD_KEY, Integer.class);
        }
        return this.period;
    }
    public String getStationType() {
        return STATION_TYPE;
    }
    public Integer getFutureAvailMinutes() {
        if ( this.futureAvailMinutes == null ) {
            this.futureAvailMinutes = env.getProperty(STATION_FUTURE_AVAIL_MINS, Integer.class);
        }
        return this.futureAvailMinutes;
    }

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

    /**
     * Converts External DTO (Car coming from JSON) to a more convenient internal DTO (BikeDto).
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
            Boolean inMaintenance = DCUtils.getJsonBooleanValue(carObject, "in_maintenance"       );
            //String available      = DCUtils.allowNulls(DCUtils.getJsonStringValue( carObject, "available"            ));

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
            //bikeDto.setAvailable    (available    );

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
     * Converts External DTO (Location coming from JSON) to a more convenient internal DTO (LocationDto).
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

    /**
     * Converts the internal representation of the Bike in StationDto user by the Open Data Hub.
     * 
     * @param bikeDto
     * @return
     */
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

            //stationMetaData.put("cleanness"             , bikeDto.getCleanness()          );
            stationMetaData.put("exchange_type"         , bikeDto.getExchangeType()       );
            stationMetaData.put("big_image_url"         , bikeDto.getBigImageUrl()        );
            stationMetaData.put("medium_image_url"      , bikeDto.getMedImageUrl()        );
            stationMetaData.put("thumb_image_url"       , bikeDto.getTmbImageUrl()        );
            stationMetaData.put(BikesharingMoqoDataConverter.DATA_TYPE_IN_MAINTENANCE       , DCUtils.convertBooleanToLong(bikeDto.getInMaintenance())      );
            stationMetaData.put(BikesharingMoqoDataConverter.DATA_TYPE_AVAILABILITY         , DCUtils.convertBooleanToLong(bikeDto.getAvailability())       );
            stationMetaData.put(BikesharingMoqoDataConverter.DATA_TYPE_FUTURE_AVAILABILITY  , DCUtils.convertBooleanToLong(bikeDto.getFutureAvailability()) );

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

            //Call to setMetaData must be done when the Map is completely filled
            stationDto.setMetaData(stationMetaData);

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

        //How much does it take to parse the JSON String with org.json?
        long jsonStart = System.currentTimeMillis();
        JSONObject joMain = new JSONObject(responseString);
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
    public List<AvailabilityDto> convertAvailabilityResponseToInternalDTO(String responseString) throws Exception {

        List<AvailabilityDto> dtoList = new ArrayList<AvailabilityDto>();

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

        //Get information to store as AvailabilityDto
        JSONArray availArray = joMain.getJSONArray("availability_slots");
        int availLength = availArray!=null ? availArray.length() : 0;
        for ( int i=0 ; i<availLength ; i++ ) {
            AvailabilityDto dto = new AvailabilityDto();

            JSONObject availObject = availArray.getJSONObject(i);
            Boolean available = DCUtils.getJsonBooleanValue(availObject, "available");
            String  strFrom   = DCUtils.getJsonStringValue( availObject, "from");
            String  strUntil  = DCUtils.getJsonStringValue( availObject, "until");
            Long    duration  = DCUtils.getJsonLongValue(   availObject, "duration");

            Date    from      = DCUtils.convertStringTimezoneToDate(strFrom);
            Date    until     = DCUtils.convertStringTimezoneToDate(strUntil);

            dto.setAvailable(available);
            dto.setStrFrom(  strFrom  );
            dto.setStrUntil( strUntil );
            dto.setDuration( duration );
            dto.setFrom(     from     );
            dto.setUntil(    until    );

            dtoList.add(dto);

        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("dtoList: "+dtoList); 
        }
        return dtoList;
    }

    /**
     * Evaluate attributes availability and future_avalilability, looking into the Availability slots
     * return true if consistent availability data is found, false otherwise
     * 
     * @param bikeDto
     * @param availDtoList
     */
    public boolean calculateBikeAvailability(BikeDto bikeDto, List<AvailabilityDto> availDtoList) {
        if ( bikeDto == null || availDtoList == null ) {
            return false;
        }

        String bikeId = bikeDto.getId();
        String bikeLicense = bikeDto.getLicense();

        //1. get an ordered list of Availabilities, make a loop to check all items
        //   get also current avalilability, it is used to understand if the bike is available NOW.
        List<AvailabilityDto> orderedAvailDtoList = new ArrayList<AvailabilityDto>();
        AvailabilityDto currentBikeAvailability = null;
        //Add 1 second to current time, measurements can have a slot that starts exactly now
        long dtNowMillis = System.currentTimeMillis() + 1 * 1000;
        for ( int i=0 ; i<availDtoList.size() ; i++ ) {
            AvailabilityDto availDto = availDtoList.get(i);
            Boolean slotAvailable = availDto.getAvailable();
            Date slotFrom = availDto.getFrom();
            Date slotUntil = availDto.getUntil();
            long slotFromMillis  = slotFrom!=null  ? slotFrom.getTime()  : 0;
            long slotUntilMillis = slotUntil!=null ? slotUntil.getTime() : Long.MAX_VALUE;
            boolean slotFromBeforeNow = slotFrom == null  || slotFromMillis <= dtNowMillis;
            boolean slotUntilAfterNow = slotUntil == null || slotUntilMillis > dtNowMillis;
            if ( LOG.isDebugEnabled() ) {
                LOG.debug("Bike id="+bikeId+"  rn="+bikeLicense+" : "+availDto);
            }
            //Slot contains NOW, take value
            if ( slotFromBeforeNow && slotUntilAfterNow ) {
                if ( currentBikeAvailability == null ) {
                    currentBikeAvailability = availDto;
                    if ( LOG.isDebugEnabled() ) {
                        LOG.debug("Bike id="+bikeId+"  rn="+bikeLicense+" : "+"Found currentBikeAvailability="+currentBikeAvailability);
                    }
                } else {
                    LOG.warn("INCONSISTENT BEHAVIOUR: Check Availability, found another slot that contains current time! New value="+slotAvailable+" currentValue="+currentBikeAvailability);
                }
            } else if ( slotFromMillis > dtNowMillis && slotUntilMillis > dtNowMillis ) {
                //Put slot in the ordered list, consider only future slots
                int idx = -1;
                for ( int j=0 ; idx<0 && j<orderedAvailDtoList.size() ; j++ ) {
                    AvailabilityDto checkDto = availDtoList.get(j);
                    Date checkFrom  = checkDto.getFrom();
                    Date checkUntil = checkDto.getUntil();
                    long checkFromMillis  = checkFrom!=null  ? checkFrom.getTime()  : 0;
                    long checkUntilMillis = checkUntil!=null ? checkUntil.getTime() : Long.MAX_VALUE;
                    if ( LOG.isDebugEnabled() ) {
                        LOG.debug("Order Availability for Bike id="+bikeId+"  rn="+bikeLicense+" : "+"checkFrom="+checkFrom+" checkUntil="+checkUntil);
                    }
                    //if slotDto.from < checkDto.from we must insert the item before examined one
                    if ( slotFromMillis < checkFromMillis ) {
                        if ( LOG.isDebugEnabled() ) {
                            LOG.debug("Check From is lesser: slotFrom="+slotFrom+" checkFrom="+checkFrom);
                        }
                        idx = j;
                    } else if ( checkFromMillis == slotFromMillis ) {
                        //if slotDto.from == checkDto.from, compare also until
                        if ( slotUntilMillis <= checkUntilMillis ) {
                            if ( LOG.isDebugEnabled() ) {
                                LOG.debug("Check until is lesser: slotFrom="+slotFrom+" checkFrom="+checkFrom+"  slotUntil="+slotUntil+" checkUntil="+checkUntil);
                            }
                            idx = j;
                        }
                    }
                }
                //If slotDto has lesser from or until then insert the item in found position, otherwise add it at the end
                if ( idx >= 0 ) {
                    orderedAvailDtoList.add(idx, availDto);
                } else {
                    orderedAvailDtoList.add(availDto);
                }
            }
        }

        //If we do not find any information about currentAvalilability, set the bike in maintenance
        if ( currentBikeAvailability == null || currentBikeAvailability.getAvailable() == null ) {
            if ( LOG.isDebugEnabled() ) {
                LOG.debug("Exit check, null values found: currentBikeAvailability="+currentBikeAvailability);
            }
            bikeDto.setAvailability(false);
            bikeDto.setFutureAvailability(false);
            bikeDto.setInMaintenance(true);
            return false;
        }

        //2. having currentBikeAvailability, look for next AvailabilityDto that has a different value to calculate future_availability
        //   Take only slots where dateFrom is less than NOW + STATION_FUTURE_AVAIL_MINS
        Boolean availability = currentBikeAvailability.getAvailable();
        Boolean futureAvailability = null;
        long deltaFuture = 60 * 1000 * getFutureAvailMinutes();
        for ( int i=0 ; futureAvailability==null && i<orderedAvailDtoList.size() ; i++ ) {
            AvailabilityDto nextAvailDto = orderedAvailDtoList.get(i);
            Boolean currentAvailable = currentBikeAvailability.getAvailable();
            Boolean nextAvailable = nextAvailDto.getAvailable();
            Date nextFrom = nextAvailDto.getFrom();
            if ( LOG.isDebugEnabled() ) {
                LOG.debug("Check next, currentAvailable="+currentAvailable+"  nextFrom="+nextFrom+"  nextAvailable="+nextAvailable);
            }
            if ( nextAvailable!=null && !nextAvailable.equals(currentAvailable) ) {
                long nextFromMillis = nextFrom!=null ? nextFrom.getTime() : 0;
                if ( LOG.isDebugEnabled() ) {
                    LOG.debug("Check next, nextFromMillis="+nextFromMillis+"  dtNowMillis + deltaFuture="+(dtNowMillis + deltaFuture));
                }
                if ( nextFromMillis <= (dtNowMillis + deltaFuture) ) {
                    futureAvailability = nextAvailable;
                    if ( LOG.isDebugEnabled() ) {
                        LOG.debug("Found change in future_availability, futureAvailability="+futureAvailability+"  availability="+availability);
                    }
                }
            }
        }
        if ( futureAvailability == null ) {
            futureAvailability = availability;
        }

        bikeDto.setMeasurementTimestamp(dtNowMillis);
        bikeDto.setAvailability(availability);
        bikeDto.setFutureAvailability(futureAvailability);
        return true;
    }

    /**
     * Evaluate attributes available, until and from for the bike, looking into the Availability slots.
     * FOR NOW THIS IS NOT USED!
     * 
     * @param bikeDto
     * @param availDtoList
     */
    public void calculateBikeAvailability_FromUntil(BikeDto bikeDto, List<AvailabilityDto> availDtoList) {
        if ( bikeDto == null || availDtoList == null ) {
            return;
        }

        String bikeId = bikeDto.getId();
        String bikeLicense = bikeDto.getLicense();

        Boolean bikeAvailable   = false;
        Date bikeAvailableFrom  = null;
        Date bikeAvailableUntil = null;
        Long bikeAvailableDuration = null;
        long dtNowMillis = System.currentTimeMillis() + 1 * 1000;
        //Date dtNow = new Date(dtNowMillis);
        for ( int i=0 ; i<availDtoList.size() ; i++ ) {
            AvailabilityDto availDto = availDtoList.get(i);
            Boolean slotAvailable = availDto.getAvailable();
            Date slotFrom = availDto.getFrom();
            Date slotUntil = availDto.getUntil();
            Long slotDuration = availDto.getDuration();
            long slotFromMillis  = slotFrom!=null  ? slotFrom.getTime()  : 0;
            long slotUntilMillis = slotUntil!=null ? slotUntil.getTime() : 0;
            boolean slotFromBeforeNow = slotFrom == null  || slotFromMillis <= dtNowMillis;
            boolean slotUntilAfterNow = slotUntil == null || slotUntilMillis > dtNowMillis;
            if ( LOG.isDebugEnabled() ) {
                LOG.debug("Bike id="+bikeId+"  rn="+bikeLicense+" : "+availDto);
            }

            if ( slotFromBeforeNow && slotUntilAfterNow ) {
                //NOW is BETWEEN current slot ==> take values from it
                LOG.debug("Bike id="+bikeId+"  rn="+bikeLicense+" SLOT IS BETWEEN NOW: "+availDto);
                bikeAvailable = slotAvailable;
                bikeAvailableFrom = null;
                bikeAvailableUntil = slotUntil;
                if ( Boolean.TRUE.equals(slotAvailable) ) {
                    bikeAvailableDuration = slotDuration;
                }
            } else if ( !slotFromBeforeNow && slotUntilAfterNow ) {
                //NOW is BEFORE  current slot ==> 
                LOG.debug("Bike id="+bikeId+"  rn="+bikeLicense+" SLOT IS BEFORE  NOW: "+availDto);
                if ( Boolean.TRUE.equals(slotAvailable) ) {
                    //Bike will be available in the future, take "from" and "until" but do not change value of available
                    bikeAvailableFrom = slotFrom;
                    bikeAvailableUntil = slotUntil;
                    //bikeAvailableDuration = (slotFromMillis-dtNowMillis) / 1000;
                } else {
                    //Bike will be unavailable in the future, take "from" and calculate availability duration
                    bikeAvailableUntil = slotFrom;
                    bikeAvailableDuration = (slotFromMillis-dtNowMillis) / 1000;
                }
            } else if ( slotFromBeforeNow && !slotUntilAfterNow ) {
                //NOW is AFTER   current slot, DO NOT CONSIDER IT!!!
            } else {
                //Impossible situation!!!
                LOG.warn("Inconsistent availability slot for bike id="+bikeId+"  rn="+bikeLicense+" : "+availDto);
            }

        }

        bikeDto.setMeasurementTimestamp(dtNowMillis);
        bikeDto.setAvailability(bikeAvailable);
        bikeDto.setAvailableFrom(bikeAvailableFrom);
        bikeDto.setAvailableUntil(bikeAvailableUntil);
        bikeDto.setAvailableDuration(bikeAvailableDuration);
    }

    /**
     * Create a map with only one instance of each possible location (parking station).
     * 
     * @param bikeList
     * @return
     */
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
