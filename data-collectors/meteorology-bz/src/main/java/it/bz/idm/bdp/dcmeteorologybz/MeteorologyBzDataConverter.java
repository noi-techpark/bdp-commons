package it.bz.idm.bdp.dcmeteorologybz;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import it.bz.idm.bdp.dcmeteorologybz.dto.Feature;
import it.bz.idm.bdp.dcmeteorologybz.dto.FeaturesDto;
import it.bz.idm.bdp.dcmeteorologybz.dto.MeteorologyBzDto;
import it.bz.idm.bdp.dcmeteorologybz.dto.Properties;
import it.bz.idm.bdp.dcmeteorologybz.dto.SensorDto;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.StationDto;

@Service
public class MeteorologyBzDataConverter {

    private static final Logger LOG = LogManager.getLogger(MeteorologyBzDataConverter.class.getName());

    public static final long   MILLIS_ONE_DAY            = 24 * 60 * 60 * 1000;

    public static final String ORIGIN_KEY                = "app.origin";
    public static final String PERIOD_KEY                = "app.period";
    public static final String CHECK_DATE_LAST_REC_KEY   = "app.checkDateOfLastRecord";
    public static final String PUSH_DATA_SINGLE_STATION  = "app.pushDataSingleStation";
    public static final String MIN_DATE_FROM             = "app.min_date_from";
    public static final String FETCH_PERIOD              = "app.fetch_period";

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
    //This must be initialized in application.properties file (for example "1")
    private Integer fetchPeriod;

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
    public Integer getFetchPeriod() {
        if ( this.fetchPeriod == null ) {
            String strFetchPeriod = env.getProperty(FETCH_PERIOD);
            this.fetchPeriod = DCUtils.convertStringToInteger(strFetchPeriod);
            if ( this.fetchPeriod == null ) {
                this.fetchPeriod = 1;
            }
        }
        return this.fetchPeriod;
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

    /**
     * Converts External DTO (FeatureDto coming from JSON) to Internal DTO (List<MeteorologyBzDto> to send to the dataHub).
     *
     * @param stationsArray
     * @return
     */
    public List<MeteorologyBzDto> convertExternalStationDtoListToInternalDtoList(FeaturesDto stationsArray) {
        if ( LOG.isDebugEnabled() ) {
            LOG.debug("stationsArray: "+stationsArray);
        }
        List<MeteorologyBzDto> extDtoList = new ArrayList<MeteorologyBzDto>();
        if ( stationsArray == null ) {
            return extDtoList;
        }

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

    /**
     * Converts External DTO (Feature coming from JSON) to Internal DTO (StationDto to send to the dataHub).
     * 
     *  Example of JSON provided by the service
     * {
     *   "type": "Feature",
     *   "geometry": {
     *     "type": "Point",
     *     "coordinates": [
     *       669803.015640121,
     *       5123442.04315501
     *     ]
     *   },
     *   "properties": {
     *     "SCODE": "89940PG",
     *     "NAME_D": "ETSCH BEI SALURN",
     *     "NAME_I": "ADIGE A SALORNO",
     *     "NAME_L": "ETSCH BEI SALURN",
     *     "NAME_E": "ETSCH BEI SALURN",
     *     "ALT": 210,
     *     "LONG": 11.20262,
     *     "LAT": 46.243333
     *   }
     * }
     *
     * @param stationObj
     * @return
     */
    public StationDto convertExternalStationDtoToStationDto(Feature stationObj) {
        if ( LOG.isDebugEnabled() ) {
            LOG.debug("stationObj: "+stationObj);
        }
        StationDto stationDto = null;
        if ( stationObj != null ) {

            Properties stationProps = stationObj.getProperties();

            if ( stationProps == null ) {
                LOG.warn("Station properties are null!: stationObj: "+stationObj);
                return stationDto;
            }

            stationDto = new StationDto();

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

            //MetaData must be flat, if we try to add a structure like "name": {"it":"...","de":"..."} we get an error
            Map<String, Object> metaData = new HashMap<String, Object>();
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

    /**
     * Converts External DTO (SensorDto coming from JSON) to Internal DTO (DataTypeDto to send to the dataHub).
     * If stationMap is not null, sensor data is stored als inside the MeteorologyBzDto object corresponding to the Station.
     * 
     * Example of JSON provided by the service
     * {
     *   "SCODE":"89940PG",
     *   "TYPE":"WT",
     *   "DESC_D":"Wassertemperatur",
     *   "DESC_I":"Temperatura acqua",
     *   "DESC_L":"Temperatura dl’ega",
     *   "UNIT":"°C",
     *   "DATE":"2019-02-20T11:10:00CET",
     *   "VALUE":3.8
     * }
     * 
     * @param sensorObj
     * @param stationMap
     * @return
     */
    public DataTypeDto convertExternalSensorDtoToDataTypeDto(SensorDto sensorObj, Map<String, MeteorologyBzDto> stationMap) {
        if ( LOG.isDebugEnabled() ) {
            LOG.debug("sensorObj: "+sensorObj);
        }
        DataTypeDto dataTypeDto = null;
        if ( sensorObj != null ) {

            String sensorType = sensorObj.getTYPE();
            String dataTypeName = env.getProperty(sensorType, sensorType);
            if ( dataTypeName.equals(sensorType) ) {
                LOG.warn("**** UNMAPPED DataType name: '"+sensorType+"'! A corresponding mapping entry must be added in types.properties file.");
            }
            dataTypeDto = new DataTypeDto();
            dataTypeDto.setName(dataTypeName);
            dataTypeDto.setUnit(sensorObj.getUNIT());
            dataTypeDto.setDescription(sensorObj.getDESCI());
            Integer period = getPeriod();
            if ("precipitation".equals(dataTypeName))
                period = 300;
            else if ("hydrometric-level".equals(dataTypeName))
                period = 3600;
			dataTypeDto.setPeriod(period);

            //Store also measurement in corresponding Station
            if ( stationMap != null ) {
                String scode = sensorObj.getSCODE();
                MeteorologyBzDto meteoDto = stationMap.get(scode);
                if ( meteoDto != null ) {
                    Map<String, DataTypeDto> dataTypes = meteoDto.getDataTypeMap();
                    //Add data type and sensor data only if not already present (for some stations data is present more times for same dataType!!!)
                    if ( dataTypes.get(sensorType) == null ) {
                        dataTypes.put(sensorType, dataTypeDto);
                        List<SensorDto> sensorAttrs = meteoDto.getSensorDataList();
                        sensorAttrs.add(sensorObj);
                    }
                }
            }

        }

        if ( LOG.isDebugEnabled() ) {
            LOG.debug("dataTypeDto: "+dataTypeDto);
        }
        return dataTypeDto;
    }

}
