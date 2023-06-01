// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.idm.bdp.dconstreetparkingbz;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.google.api.services.sheets.v4.model.ValueRange;

import it.bz.idm.bdp.dconstreetparkingbz.dto.OnstreetParkingBzSensorDto;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.StationList;
import it.bz.idm.bdp.util.NominatimException;
import it.bz.idm.bdp.util.NominatimLocationLookupUtil;

@Service
@PropertySource({ "classpath:/META-INF/spring/application.properties" })
public class OnstreetParkingBzDataConverter {

    private static final Logger LOG = LoggerFactory.getLogger(OnstreetParkingBzDataConverter.class.getName());

    public static final String DATA_TYPE_OCCUPIED        = "occupied";
    public static final String SENSOR_STATUS_FREE        = "free";
    public static final String SENSOR_STATUS_BUSY        = "busy";

    public static final String STATION_TYPE_SENSOR       = "ParkingSensor";

    @Value("${spreadsheet.ATTR_ID_NAME}")
    private String STATION_ATTR_ID_NAME;

    @Value("${spreadsheet.ATTR_ID2_NAME}")
    private String STATION_ATTR_ID2_NAME;

    @Value("${spreadsheet.ATTR_GROUP_NAME}")
    private String STATION_ATTR_GROUP_NAME;

    @Value("${spreadsheet.ATTR_LNG_NAME}")
    private String STATION_ATTR_LNG_NAME;

    @Value("${spreadsheet.ATTR_LAT_NAME}")
    private String STATION_ATTR_LAT_NAME;

    @Value("${spreadsheet.ATTR_DESC_NAME}")
    private String STATION_ATTR_DESC_NAME;

    public static final String ORIGIN_KEY                = "app.origin";
    public static final String PERIOD_KEY                = "app.period";
    public static final String CHECK_MISSING_STATION_KEY = "app.check_missing_stations";

    @Autowired
    private Environment env;

    //This must be initialized in application.properties file (for example "AXIANS")
    private String origin;
    //This must be initialized in application.properties file (for example 300)
    private Integer period;
    //This must be initialized in application.properties file (for example 300)
    private Boolean checkMissingStations;
    
    private NominatimLocationLookupUtil placeLookupUtil = new NominatimLocationLookupUtil(); 

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
    public boolean isCheckMissingStations() {
        if ( this.checkMissingStations == null ) {
            this.checkMissingStations = "true".equalsIgnoreCase(env.getProperty(CHECK_MISSING_STATION_KEY));
        }
        return this.checkMissingStations;
    }
    public String getStationType() {
        return STATION_TYPE_SENSOR;
    }

    /**
     * Converts the string provided by the Axians service in a more useful internal representation
     * 
     * @param jsonString
     * @return
     * @throws Exception
     */
    public OnstreetParkingBzSensorDto convertSensorResponseToInternalDTO(String jsonString) throws Exception {

        if ( LOG.isDebugEnabled() ) {
            LOG.debug("jsonString='"+jsonString+"'");
        }

        if ( DCUtils.paramNull(jsonString) ) {
            LOG.info("jsonString IS NULL, EXIT!");
            return null;
        }

        OnstreetParkingBzSensorDto dto = new OnstreetParkingBzSensorDto();

        //How much does it take to parse the JSON String with org.json?
        long jsonStart = System.currentTimeMillis();
        JSONObject joMain = new JSONObject(jsonString);
        long jsonEnd = System.currentTimeMillis();
        if ( LOG.isDebugEnabled() ) {
            LOG.debug("Time to parse with org.json: " + (jsonEnd-jsonStart));
        }

        //Get useful info for this sensor, we need only few fields. jsonStr is in the form:
        /*
        {
==>         "devEUI": "001bc50670100603",
            "appEUI": "70b3d59b60000003",
            "fPort": 1,
            "gatewayCount": 1,
            "rssi": -103,
            "data": "gAAAAywS9AABXlPUYgE6AAADJAAANfcA1t8=",
            "parsedData": {
                "Magnitude": 804,
                "Payload": "MEASURE",
                "Progtx": 13815,
                "Retries": 0,
                "Sensor-address": "0000032c",
==>             "Status": "free",
                "Time": 1582552162,
                "Version": 58
            },
            "loRaSNR": 6,
            "frequency": 868500000,
            "dataRate": {
                "modulation": "LORA",
                "spreadFactor": 7,
                "bandwidth": 125
            },
            "devAddr": "e992e28c",
            "fCntUp": 20,
==>         "time": "2020-02-24T13:37:00.436096605Z",
            "encrypted": false,
            "format": "base64",
            "gateways": [
                {
                    "mac": "00800000a0004693",
                    "time": "0001-01-01T00:00:00Z",
==>                 "timestamp": 4056627595,
                    "frequency": 868500000,
                    "channel": 2,
                    "rfChain": 1,
                    "crcStatus": 1,
                    "codeRate": "4/5",
                    "rssi": -103,
                    "loRaSNR": 6,
                    "size": 41,
                    "dataRate": {
                        "modulation": "LORA",
                        "spreadFactor": 7,
                        "bandwidth": 125
                    }
                }
            ]
        }
         */

        String devEUI = null;
        String status = null;
        String time = null;
        Long timestamp = null;

        JSONObject joParsedData = joMain.getJSONObject("parsedData");
        JSONArray gatewaysArray = joMain.getJSONArray("gateways");
        status = DCUtils.getJsonStringValue(joParsedData, "Status");
        devEUI = DCUtils.getJsonStringValue(joMain, "devEUI");
        time = DCUtils.getJsonStringValue(joMain, "time");
        LOG.debug("devEUI  = '"+devEUI+"'");
        LOG.debug("status  = '"+status+"'");
        LOG.debug("time    = '"+time+"'");

        int gatewaysLength = gatewaysArray!=null ? gatewaysArray.length() : 0;
        for ( int i=0 ; i<gatewaysLength ; i++ ) {
            JSONObject gatewayObject = gatewaysArray.getJSONObject(i);
            timestamp = DCUtils.getJsonLongValue(gatewayObject, "timestamp");
            LOG.debug("timestamp     = '"+timestamp+"'    ("+DCUtils.convertDateToString(new Date(timestamp))+")");
        }

        //If status=="free" then it is not occupied, in all other cases we consider the park as occupied
        Long valueOccupied = SENSOR_STATUS_FREE.equalsIgnoreCase(status) ? 0L : 1L;

        Date dateTime = DCUtils.convertStringTimezoneToDate(time);
        Long valueTimestamp = dateTime != null ? dateTime.getTime() : null;
        LOG.debug("dateTime      = '"+dateTime+"'");
        LOG.debug("longTime      = '"+valueTimestamp+"'");

        dto.setValueOccupied(valueOccupied);
        dto.setValueTimestamp(valueTimestamp);
        dto.setValueId(devEUI);

        dto.setJsonDevEUI(devEUI);
        dto.setJsonStatus(status);
        dto.setJsonTime(time);
        dto.setJsonTimestamp(timestamp);

        if (LOG.isDebugEnabled()) {
            LOG.debug("dto: "+dto); 
        }
        return dto;
    }
    public StationList convertSheetValueRangeToStationList(ValueRange valueRange) throws NominatimException {
        if ( LOG.isDebugEnabled() ) {
            LOG.debug("valueRange='"+valueRange+"'");
        }

        if ( DCUtils.paramNull(valueRange) ) {
            LOG.info("Spreadsheet valueRange IS NULL, EXIT!");
            return null;
        }

        //Get rows from spreadsheet
        List<List<Object>> valuesList = valueRange.getValues();

        //Convert rows in a StationList
        StationList retval = new StationList();
        List<String> header = new ArrayList<String>();
        if ( valuesList != null && valuesList.size() > 1 ) {
            for ( int i=0 ; i<valuesList.size() ; i++ ) {
                List<Object> values = valuesList.get(i);

                if ( i == 0 ) {

                    //First row contains headers
                    for ( int c=0 ; c < values.size() ; c++ ) {
                        Object value = values.get(c);
                        String colName = DCUtils.allowNulls(value).trim();
                        if ( DCUtils.paramNull(colName) ) {
                            colName = "COL_"+c;
                            LOG.warn("Column without name in spreadsheet, guessed_name: '"+colName+"'");
                        }
                        header.add(colName);
                    }

                } else {

                    //All other rows contain data. Put each cell value in corresponding Station attribute.
                    StationDto stationDto = new StationDto();
                    stationDto.setOrigin(DCUtils.trunc(getOrigin(), 255));
                    stationDto.setStationType(getStationType());
                    Map<String, Object> stationMetaData = new HashMap<>();
                    for ( int c=0 ; c < values.size() ; c++ ) {
                        Object value = values.get(c);
                        String colName = header.get(c);
                        String str = DCUtils.allowNulls(value);
                        LOG.debug("Google Spreadsheet: colIdx="+c+"  colName='"+colName+"'  colvalue='"+str+"'");
                        if (        STATION_ATTR_ID_NAME.equalsIgnoreCase(colName) ) {
                            //String id = DCUtils.lpad(str, 8, '0');
                            String id = str;
                            stationDto.setId(id);
                        } else if ( STATION_ATTR_ID2_NAME.equalsIgnoreCase(colName) ) {
                            stationMetaData.put("id2", str);
                        } else if ( STATION_ATTR_GROUP_NAME.equalsIgnoreCase(colName) ) {
                            stationMetaData.put(STATION_ATTR_GROUP_NAME, str);
                        } else if ( STATION_ATTR_DESC_NAME.equalsIgnoreCase(colName) ) {
                            stationDto.setName(str);
                        } else if ( STATION_ATTR_LAT_NAME.equalsIgnoreCase(colName) ) {
                            Double lat = DCUtils.convertStringToDouble(str);
                            stationDto.setLatitude(lat);
                        } else if ( STATION_ATTR_LNG_NAME.equalsIgnoreCase(colName) ) {
                            Double lng = DCUtils.convertStringToDouble(str);
                            stationDto.setLongitude(lng);
                        } else {
                            LOG.error("Found UNKNOWN column in spreadsheet: '"+colName+"'  index="+c);
                        }
                    }
                    stationMetaData.put("municipality", placeLookupUtil.lookupLocation(stationDto.getLongitude(), stationDto.getLatitude()));
                    stationDto.setMetaData(stationMetaData);
                    retval.add(stationDto);

                }
            }
        }


        if (LOG.isDebugEnabled()) {
            LOG.debug("retval: "+retval);
        }
        return retval;
    }

    public StationList getStation(String id, String name, Double latitude, Double longitude) throws NominatimException {

        ValueRange valueRange = new ValueRange();
        List<List<Object>> values = new ArrayList<>();
        List<Object> header = new ArrayList<>();
        List<Object> row = new ArrayList<>();
        values.add(header);
        values.add(row);
        valueRange.setValues(values);

        header.add(STATION_ATTR_ID_NAME);
        header.add(STATION_ATTR_GROUP_NAME);
        header.add(STATION_ATTR_DESC_NAME);
        header.add(STATION_ATTR_LAT_NAME);
        header.add(STATION_ATTR_LNG_NAME);

        row.add(id);
        row.add("UNKNOWN");
        row.add(name);
        row.add(latitude);
        row.add(longitude);

        StationList stationList = convertSheetValueRangeToStationList(valueRange);

        return stationList;
    }
}
