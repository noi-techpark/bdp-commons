package it.bz.idm.bdp.dcmeteorologybz;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import javax.json.JsonArray;
//import javax.json.JsonObject;
//import javax.json.JsonValue;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.util.JSONWrappedObject;

import it.bz.idm.bdp.dcmeteorologybz.dto.Feature;
import it.bz.idm.bdp.dcmeteorologybz.dto.FeaturesDto;
import it.bz.idm.bdp.dcmeteorologybz.dto.Geometry;
import it.bz.idm.bdp.dcmeteorologybz.dto.MeteorologyBzDto;
import it.bz.idm.bdp.dcmeteorologybz.dto.Properties;
import it.bz.idm.bdp.dcmeteorologybz.dto.SensorDto;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.StationDto;

@Service
public class MeteorologyBzDataConverter {

    private static final Logger LOG = LogManager.getLogger(MeteorologyBzDataConverter.class.getName());

    public static final String ORIGIN_KEY                = "app.origin";
    public static final String PERIOD_KEY                = "app.period";
    public static final String CHECK_DATE_LAST_REC_KEY   = "app.checkDateOfLastRecord";
    public static final String PUSH_DATA_SINGLE_STATION  = "app.pushDataSingleStation";
    public static final String MIN_DATE_FROM             = "app.min_date_from";

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
    //This must be initialized in application.properties file (for example "201901010000")
    private String minDateFrom;

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
    public String getMinDateFrom() {
        if ( this.minDateFrom == null ) {
            this.minDateFrom = env.getProperty(MIN_DATE_FROM);
        }
        return this.minDateFrom;
    }

    public List<MeteorologyBzDto> convertExternalStationDtoListToInternalDtoList(/*JsonArray stationsArray*/ FeaturesDto stationsArray) {
        if ( LOG.isDebugEnabled() ) {
            LOG.debug("stationsArray: "+stationsArray);
        }
        List<MeteorologyBzDto> extDtoList = new ArrayList<MeteorologyBzDto>();
        if ( stationsArray == null ) {
            return extDtoList;
        }

//        List<JsonObject> stationList = stationsArray.getValuesAs(JsonObject.class);
//
//        for (JsonObject stationObj : stationList) {
//            StationDto stationDto = convertExternalStationDtoToStationDto(stationObj);
//            MeteorologyBzDto extDto = new MeteorologyBzDto(stationDto, stationObj);
//            extDtoList.add(extDto);
//        }

        List<Feature> stationList = stationsArray.getFeatures();
        for (Feature stationObj : stationList) {
            StationDto stationDto = convertExternalStationDtoToStationDto(stationObj);
            MeteorologyBzDto extDto = new MeteorologyBzDto(stationDto, stationObj);
            extDtoList.add(extDto);
        }

        if ( LOG.isDebugEnabled() ) {
            LOG.debug("extDtoList: "+extDtoList);
        }
        return extDtoList;
    }

    public StationDto convertExternalStationDtoToStationDto(/*JsonObject jsonObj*/ Feature stationObj) {
        if ( LOG.isDebugEnabled() ) {
            LOG.debug("stationObj: "+stationObj);
        }
        StationDto stationDto = null;
        if ( stationObj != null ) {

            Properties stationProps = stationObj.getProperties();
            //Geometry stationGeom = stationObj.getGeometry();

            if ( stationProps == null ) {
                LOG.warn("Station properties are null!: stationObj: "+stationObj);
                return stationDto;
            }

            stationDto = new StationDto();

            /*
             *  Example of JSON provided by the service
{
  "type": "Feature",
  "geometry": {
    "type": "Point",
    "coordinates": [
      669803.015640121,
      5123442.04315501
    ]
  },
  "properties": {
    "SCODE": "89940PG",
    "NAME_D": "ETSCH BEI SALURN",
    "NAME_I": "ADIGE A SALORNO",
    "NAME_L": "ETSCH BEI SALURN",
    "NAME_E": "ETSCH BEI SALURN",
    "ALT": 210,
    "LONG": 11.20262,
    "LAT": 46.243333
  }
}
             */

//            JsonObject geometryObj = jsonObj.getJsonObject("geometry");
//            if ( geometryObj != null ) {
//                JsonArray pointArray = geometryObj.getJsonArray("coordinates");
//                if ( pointArray != null ) {
//                    if ( pointArray.size()>0 ) {
//                        JsonValue jsonValue = pointArray.get(0);
//                        Double lat = DCUtils.convertStringToDouble(jsonValue);
//                        stationDto.setLongitude(lat);
//                    }
//                }
//            }

            //From StationDTO
            stationDto.setId(stationProps.getSCODE());
            stationDto.setName(DCUtils.trunc(stationProps.getNAMEI(), 255));
            stationDto.setLongitude(stationProps.getLONG());
            stationDto.setLatitude(stationProps.getLAT());
            stationDto.setElevation(DCUtils.convertIntegerToDouble(stationProps.getALT()));
            //OMITTED: protected String crs;
            stationDto.setOrigin(DCUtils.trunc(getOrigin(), 255));
            //TODO: get municipality from coordinates....... station.setMunicipality(DCUtils.trunc(map.get("city"), 255));
            stationDto.setStationType(getStationType());

            Map<String, Object> metaData = new HashMap<String, Object>();
//            Map<String, String> names = new HashMap<String, String>();
//            metaData.put("name", names);
//            names.put("it", stationProps.getNAMEI());
//            names.put("de", stationProps.getNAMED());
//            names.put("en", stationProps.getNAMEE());
            metaData.put("name_it", stationProps.getNAMEI());
            metaData.put("name_de", stationProps.getNAMED());
            metaData.put("name_en", stationProps.getNAMEE());
            //TODO: ladino????
            //names.put("it", stationProps.getNAMEL());
            stationDto.setMetaData(metaData);

            //From MeteoStationDto
            //OMITTED: station.setArea(extDto.getSlotsTotal());
        }

        if ( LOG.isDebugEnabled() ) {
            LOG.debug("stationDto: "+stationDto);
        }
        return stationDto;
    }

//    public DataTypeDto convertExternalSensorDtoToDataTypeDto(SensorDto sensorObj) {
//        if ( LOG.isDebugEnabled() ) {
//            LOG.debug("sensorObj: "+sensorObj);
//        }
//        DataTypeDto dataTypeDto = null;
//        if ( sensorObj != null ) {
//
//            /*
//             *  Example of JSON provided by the service
//{
//  "SCODE":"89940PG",
//  "TYPE":"WT",
//  "DESC_D":"Wassertemperatur",
//  "DESC_I":"Temperatura acqua",
//  "DESC_L":"Temperatura dl’ega",
//  "UNIT":"°C",
//  "DATE":"2019-02-20T11:10:00CET",
//  "VALUE":3.8
//}
//             */
//            String type = sensorObj.getTYPE();
//            String name = env.getProperty(type, type);
//            dataTypeDto = new DataTypeDto();
//            dataTypeDto.setName(name);
//            dataTypeDto.setUnit(sensorObj.getUNIT());
//            dataTypeDto.setDescription(sensorObj.getDESCI());
//            dataTypeDto.setPeriod(getPeriod());
//        }
//
//        if ( LOG.isDebugEnabled() ) {
//            LOG.debug("dataTypeDto: "+dataTypeDto);
//        }
//        return dataTypeDto;
//    }

    public DataTypeDto convertExternalSensorDtoToDataTypeDto(SensorDto sensorObj, Map<String, MeteorologyBzDto> stationMap) {
        if ( LOG.isDebugEnabled() ) {
            LOG.debug("sensorObj: "+sensorObj);
        }
        DataTypeDto dataTypeDto = null;
        if ( sensorObj != null ) {

            /*
             *  Example of JSON provided by the service
{
  "SCODE":"89940PG",
  "TYPE":"WT",
  "DESC_D":"Wassertemperatur",
  "DESC_I":"Temperatura acqua",
  "DESC_L":"Temperatura dl’ega",
  "UNIT":"°C",
  "DATE":"2019-02-20T11:10:00CET",
  "VALUE":3.8
}
             */
            String sensorType = sensorObj.getTYPE();
            String dataTypeName = env.getProperty(sensorType, sensorType);
            if ( dataTypeName.equals(sensorType) ) {
                LOG.warn("**** UNMAPPED DataType name: '"+sensorType+"'! A corresponding mapping entry must be added in types.properties file.");
            }
            dataTypeDto = new DataTypeDto();
            dataTypeDto.setName(dataTypeName);
            dataTypeDto.setUnit(sensorObj.getUNIT());
            dataTypeDto.setDescription(sensorObj.getDESCI());
            dataTypeDto.setPeriod(getPeriod());

            //Store also measurement in corresponding Station
            if ( stationMap != null ) {
                String scode = sensorObj.getSCODE();
                //Double value = sensorObj.getVALUE();
                //String strDate = sensorObj.getDATE();
                //Date date = DCUtils.convertStringTimezoneToDate(strDate);
                MeteorologyBzDto meteoDto = stationMap.get(scode);
                if ( meteoDto != null ) {
                    Map<String, DataTypeDto> dataTypes = meteoDto.getDataTypeMap();
                    if ( dataTypes.get(sensorType) == null ) {
                        dataTypes.put(sensorType, dataTypeDto);
                    }
                    List<SensorDto> sensorAttrs = meteoDto.getSensorDataList();
                    sensorAttrs.add(sensorObj);
                }
            }

        }

        if ( LOG.isDebugEnabled() ) {
            LOG.debug("dataTypeDto: "+dataTypeDto);
        }
        return dataTypeDto;
    }

}
