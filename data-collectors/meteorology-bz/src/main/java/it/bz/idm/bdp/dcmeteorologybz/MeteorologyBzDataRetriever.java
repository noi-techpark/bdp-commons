package it.bz.idm.bdp.dcmeteorologybz;

import java.io.InputStream;
import java.io.StringWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.io.IOUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.bz.idm.bdp.dcmeteorologybz.dto.Feature;
import it.bz.idm.bdp.dcmeteorologybz.dto.FeaturesDto;

//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import it.bz.idm.bdp.dcmeteorologybz.dto.MeteorologyBzDto;
import it.bz.idm.bdp.dcmeteorologybz.dto.SensorDto;
import it.bz.idm.bdp.dcmeteorologybz.dto.TimeSerieDto;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.StationDto;

@Component
@PropertySource({ "classpath:/META-INF/spring/application.properties", "classpath:/META-INF/spring/types.properties" })
public class MeteorologyBzDataRetriever {

    private static final Logger LOG = LogManager.getLogger(MeteorologyBzDataRetriever.class.getName());

    @Autowired
    private Environment env;

    @Autowired
    private MeteorologyBzDataConverter converter;

    @Autowired
    private MeteorologyBzDataPusher pusher;

    private HttpClientBuilder builderStations = HttpClients.custom();
    private HttpClientBuilder builderSensors = HttpClients.custom();
    private HttpClientBuilder builderMeasurements = HttpClients.custom();
    private CloseableHttpClient clientStations;
    private CloseableHttpClient clientSensors;
    private CloseableHttpClient clientMeasurements;
    private ObjectMapper mapper = new ObjectMapper();
//    private XmlMapper mapper = new XmlMapper();

    private String endpointMethodStations;
    private String serviceUrlStations;

    private String endpointMethodSensors;
    private String serviceUrlSensors;

    private String endpointMethodMeasurements;
    private String serviceUrlMeasurements;
    private List<ServiceCallParam> measurementsParams;

    public MeteorologyBzDataRetriever() {
        LOG.debug("Create instance");
    }

    @PostConstruct
    private void initClient(){
        LOG.debug("Init");
        if (clientStations==null) {
            //Read config data from external bundle
            String strEndpointMethod   = env.getProperty("endpoint.stations.method");
            String strEndpointProtocol = env.getProperty("endpoint.stations.protocol");
            String strEndpointHost     = env.getProperty("endpoint.stations.host");
            String strEndpointPort     = env.getProperty("endpoint.stations.port");
            String strEndpointPath     = env.getProperty("endpoint.stations.path");

            LOG.debug("Read config:"+
                    "  endpoint.stations.protocol='"+strEndpointProtocol+"'"+
                    "  endpoint.stations.method='"+strEndpointMethod+"'"+
                    "  endpoint.stations.host='"+strEndpointHost+"'"+
                    "  endpoint.stations.port='"+strEndpointPort+"'"+
                    "  endpoint.stations.path='"+strEndpointPath+"'");

            //Create HTTP Client
            endpointMethodStations   = DCUtils.allowNulls(strEndpointMethod).trim();
            String  endpointProtocol = "http".equalsIgnoreCase(DCUtils.allowNulls(strEndpointProtocol).trim()) ? "http" : "https";
            String  defaultPort      = "http".equalsIgnoreCase(DCUtils.allowNulls(strEndpointProtocol).trim()) ? "80" : "443";
            String  endpointHost = DCUtils.mustNotEndWithSlash(DCUtils.allowNulls(strEndpointHost).trim());
            String  endpointPath = DCUtils.mustNotEndWithSlash(DCUtils.allowNulls(strEndpointPath).trim());
            Integer endpointPort = DCUtils.convertStringToInteger(DCUtils.defaultNulls(strEndpointPort, defaultPort));
            serviceUrlStations = endpointProtocol + "://" + endpointHost + ":" + endpointPort + "/" + endpointPath;

            clientStations = builderStations.build();

            LOG.debug("Http Client Stations created");
        }

        if (clientSensors==null) {
            //Read config data from external bundle
            String strEndpointMethod   = env.getProperty("endpoint.sensors.method");
            String strEndpointProtocol = env.getProperty("endpoint.sensors.protocol");
            String strEndpointHost     = env.getProperty("endpoint.sensors.host");
            String strEndpointPort     = env.getProperty("endpoint.sensors.port");
            String strEndpointPath     = env.getProperty("endpoint.sensors.path");

            LOG.debug("Read config:"+
                    "  endpoint.sensors.protocol='"+strEndpointProtocol+"'"+
                    "  endpoint.sensors.method='"+strEndpointMethod+"'"+
                    "  endpoint.sensors.host='"+strEndpointHost+"'"+
                    "  endpoint.sensors.port='"+strEndpointPort+"'"+
                    "  endpoint.sensors.path='"+strEndpointPath+"'");

            //Create HTTP Client
            endpointMethodSensors   = DCUtils.allowNulls(strEndpointMethod).trim();
            String  endpointProtocol = "http".equalsIgnoreCase(DCUtils.allowNulls(strEndpointProtocol).trim()) ? "http" : "https";
            String  defaultPort      = "http".equalsIgnoreCase(DCUtils.allowNulls(strEndpointProtocol).trim()) ? "80" : "443";
            String  endpointHost = DCUtils.mustNotEndWithSlash(DCUtils.allowNulls(strEndpointHost).trim());
            String  endpointPath = DCUtils.mustNotEndWithSlash(DCUtils.allowNulls(strEndpointPath).trim());
            Integer endpointPort = DCUtils.convertStringToInteger(DCUtils.defaultNulls(strEndpointPort, defaultPort));
            serviceUrlSensors = endpointProtocol + "://" + endpointHost + ":" + endpointPort + "/" + endpointPath;

            clientSensors = builderSensors.build();

            LOG.debug("Http Client Sensors created");
        }

        if (clientMeasurements==null) {
            //Read config data from external bundle
            String strEndpointMethod   = env.getProperty("endpoint.measurements.method");
            String strEndpointProtocol = env.getProperty("endpoint.measurements.protocol");
            String strEndpointHost     = env.getProperty("endpoint.measurements.host");
            String strEndpointPort     = env.getProperty("endpoint.measurements.port");
            String strEndpointPath     = env.getProperty("endpoint.measurements.path");

            LOG.debug("Read config:"+
                    "  endpoint.measurements.protocol='"+strEndpointProtocol+"'"+
                    "  endpoint.measurements.method='"+strEndpointMethod+"'"+
                    "  endpoint.measurements.host='"+strEndpointHost+"'"+
                    "  endpoint.measurements.port='"+strEndpointPort+"'"+
                    "  endpoint.measurements.path='"+strEndpointPath+"'");

            //Create HTTP Client
            endpointMethodMeasurements = DCUtils.allowNulls(strEndpointMethod).trim();
            String  endpointProtocol = "http".equalsIgnoreCase(DCUtils.allowNulls(strEndpointProtocol).trim()) ? "http" : "https";
            String  defaultPort      = "http".equalsIgnoreCase(DCUtils.allowNulls(strEndpointProtocol).trim()) ? "80" : "443";
            String  endpointHost = DCUtils.mustNotEndWithSlash(DCUtils.allowNulls(strEndpointHost).trim());
            String  endpointPath = DCUtils.mustNotEndWithSlash(DCUtils.allowNulls(strEndpointPath).trim());
            Integer endpointPort = DCUtils.convertStringToInteger(DCUtils.defaultNulls(strEndpointPort, defaultPort));
            serviceUrlMeasurements = endpointProtocol + "://" + endpointHost + ":" + endpointPort + "/" + endpointPath;

            clientMeasurements = builderMeasurements.build();

            measurementsParams = new ArrayList<ServiceCallParam>();
            boolean hasNext = true;
            int i=0;
            while ( hasNext ) {
                //Example of parameters to add
                //endpoint.measurements.param.0.param_name
                //endpoint.measurements.param.0.station_attr_name
                String paramName  = DCUtils.allowNulls(env.getProperty("endpoint.measurements.param."+i+".param_name")).trim();
                String paramValue = DCUtils.allowNulls(env.getProperty("endpoint.measurements.param."+i+".param_value")).trim();
                String stationAttrName = DCUtils.allowNulls(env.getProperty("endpoint.measurements.param."+i+".station_attr_name")).trim();
                String sensorAttrName  = DCUtils.allowNulls(env.getProperty("endpoint.measurements.param."+i+".sensor_attr_name")).trim();
                String functionName = DCUtils.allowNulls(env.getProperty("endpoint.measurements.param."+i+".function_name")).trim();
                if ( DCUtils.paramNotNull(paramName) ) {
                    ServiceCallParam param = new ServiceCallParam(paramName);
                    if ( DCUtils.paramNotNull(stationAttrName) ) {
                        param.type = ServiceCallParam.TYPE_STATION_VALUE;
                        param.value = stationAttrName;
                    } else if ( DCUtils.paramNotNull(sensorAttrName) ) {
                        param.type = ServiceCallParam.TYPE_SENSOR_VALUE;
                        param.value = sensorAttrName;
                    } else if ( DCUtils.paramNotNull(functionName) ) {
                        param.type = ServiceCallParam.TYPE_FUNCTION;
                        param.value = functionName;
                    } else if ( DCUtils.paramNotNull(paramValue) ) {
                        param.type = ServiceCallParam.TYPE_FIXED_VALUE;
                        param.value = paramValue;
                    }
                    if ( param.type!=null && param.value!=null ) {
                        measurementsParams.add(param);
                    } else {
                        LOG.warn("UNRECOGNIZED parameter type in application.properties file: '"+paramName+"'  index="+i+"");
                    }
                    i++;
                } else {
                    hasNext = false;
                }
            }

            LOG.debug("Http Client Measurements created");
        }
    }

    /**
     * Performs the call to Meteo service and returns exactly the response String without particular processing or formatting
     * 
     * @return
     * @throws Exception
     */
    private String callRemoteService(CloseableHttpClient client, String serviceUrl, String endpointMethod, List<NameValuePair> endpointParams) throws Exception {
        String url = serviceUrl;
        LOG.debug("Start call to service: " + url);

        // In our case it is not necessary to set particular headers
//        String xcallerHeader = env.getProperty("app.callerId");
//        String apikey = env.getProperty("app.apikey");
//        if (xcallerHeader != null)
//            get.setHeader("X-Caller-ID",xcallerHeader);
//        if (apikey != null)
//            get.setHeader("apikey",apikey);
//        get.setHeader("Accept","application/json");

        HttpRequestBase request = null;
        if ( "GET".equalsIgnoreCase(endpointMethod) ) {
            request = new HttpGet(url);
        } else {
            request = new HttpPost(url);
        }

        URIBuilder uriBuilder = new URIBuilder(request.getURI());
        if ( endpointParams!=null && endpointParams.size()>0 ) {
            uriBuilder.addParameters(endpointParams);
        }
        URI uri = uriBuilder.build();
        request.setURI(uri);

        LOG.debug("URI = " + uri);

        CloseableHttpResponse response = client.execute(request);
        InputStream entity = response.getEntity().getContent();
        StringWriter writer = new StringWriter();
        IOUtils.copy(entity, writer);
        String responseData = writer.toString();
        response.close();

        if (LOG.isDebugEnabled()) {
            LOG.debug("End call responseData = '" + responseData + "'");
        }
        return responseData;
    }

    /**
     * Converts the string returned by the Meteo service in a more useful internal representation
     * 
     * @param responseString
     * @return
     * @throws Exception
     */
    public List<MeteorologyBzDto> convertStationsResponseToInternalDTO(String responseString) throws Exception {

        List<MeteorologyBzDto> dtoList = new ArrayList<MeteorologyBzDto>();

//        StringReader strReader = new StringReader(responseString);
//        JsonReader jsonReader = Json.createReader(strReader);

        /*
         * Example JSON returned by the service
{
  "name": "station",
  "type": "FeatureCollection",
  "crs": {},
  "features": [
    ....
  ]
}
         */

//        //Get first level, we hava a JsonObject
//        JsonObject mainObj = jsonReader.readObject();
//
//        //Get list of stations, it is a JsonArray stored in attribute "features"
//        JsonArray featuresArray = mainObj.getJsonArray("features");
//        List<JsonObject> stationList = featuresArray.getValuesAs(JsonObject.class);
//
//        //Convert in internal DTO without calling measurement service
//        for (JsonObject stationObj : stationList) {
//            StationDto stationDto = converter.convertExternalStationDtoToStationDto(stationObj);
//            MeteorologyBzDto dataByCity = new MeteorologyBzDto(stationDto);
//            dtoList.add(dataByCity);
//        }

        FeaturesDto data = mapper.readValue(responseString, new TypeReference<FeaturesDto>() {});
        List<Feature> dataList = data!=null ? data.getFeatures() : null;

        if (LOG.isDebugEnabled()) {
            LOG.debug("data: "+data); 
            LOG.debug("dataList: "+dataList);
        }

        if ( dataList == null ) {
            return null;
        }
        for (Feature stationObj : dataList) {
            StationDto stationDto = converter.convertExternalStationDtoToStationDto(stationObj);
            MeteorologyBzDto meteoBzDto = new MeteorologyBzDto(stationDto);
            meteoBzDto.setStationAttributes(stationObj);
            dtoList.add(meteoBzDto);
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("dtoList: "+dtoList); 
        }
        return dtoList;
    }

//    /**
//     * Converts the string returned by the Meteo service in a more useful internal representation
//     * 
//     * @param responseString
//     * @return
//     * @throws Exception
//     */
//    public List<DataTypeDto> convertSensorsResponseToInternalDTO(String responseString) throws Exception {
//
//        List<DataTypeDto> dtoList = new ArrayList<DataTypeDto>();
//
//        /*
//         * Example JSON returned by the service
//[
//  {
//    "SCODE":"89940PG",
//    "TYPE":"WT",
//    "DESC_D":"Wassertemperatur",
//    "DESC_I":"Temperatura acqua",
//    "DESC_L":"Temperatura dl’ega",
//    "UNIT":"°C",
//    "DATE":"2019-02-20T11:10:00CET",
//    "VALUE":3.8
//  },
//  ...
//]
//         */
//
//        List<SensorDto> dataList = mapper.readValue(responseString, new TypeReference<List<SensorDto>>() {});
//        Set<String> sensorNames = new HashSet<String>();
//
//        if (LOG.isDebugEnabled()) {
//            LOG.debug("dataList: "+dataList);
//        }
//
//        if ( dataList == null ) {
//            return null;
//        }
//        for (SensorDto sensorObj : dataList) {
//            DataTypeDto dataTypeDto = converter.convertExternalSensorDtoToDataTypeDto(sensorObj);
//            String name = dataTypeDto!=null ? dataTypeDto.getName() : null;
//            if ( name!=null && !sensorNames.contains(name) ) {
//                sensorNames.add(name);
//                dtoList.add(dataTypeDto);
//            }
//        }
//
//        if (LOG.isDebugEnabled()) {
//            LOG.debug("dtoList: "+dtoList); 
//        }
//        return dtoList;
//    }

    /**
     * Converts the string returned by the Meteo service in a more useful internal representation.
     * Data regarding the measurements are added to the corresponding MeteorologyBzDto.
     * 
     * @param responseString
     * @return
     * @throws Exception
     */
    public List<DataTypeDto> convertSensorsResponseToInternalDTO(String responseString, List<MeteorologyBzDto> stationList) throws Exception {

        List<DataTypeDto> dtoList = new ArrayList<DataTypeDto>();

        /*
         * Example JSON returned by the service
[
  {
    "SCODE":"89940PG",
    "TYPE":"WT",
    "DESC_D":"Wassertemperatur",
    "DESC_I":"Temperatura acqua",
    "DESC_L":"Temperatura dl’ega",
    "UNIT":"°C",
    "DATE":"2019-02-20T11:10:00CET",
    "VALUE":3.8
  },
  ...
]
         */

        //Convert JSON string to External DTO
        List<SensorDto> dataList = mapper.readValue(responseString, new TypeReference<List<SensorDto>>() {});
        Set<String> sensorNames = new HashSet<String>();

        if (LOG.isDebugEnabled()) {
            LOG.debug("dataList: "+dataList);
        }

        if ( dataList == null ) {
            return null;
        }

        //Put Station data in a map for more convenience handling
        Map<String, MeteorologyBzDto> stationMap = null;
        if ( stationList != null ) {
            stationMap = new HashMap<String, MeteorologyBzDto>();
            for (MeteorologyBzDto meteoDto : stationList) {
                StationDto stationDto = meteoDto.getStation();
                String id = stationDto.getId();
                stationMap.put(id, meteoDto);
            }
        }

        //Convert External DTO to Internal DTO
        for (SensorDto sensorObj : dataList) {
            DataTypeDto dataTypeDto = converter.convertExternalSensorDtoToDataTypeDto(sensorObj, stationMap);
            String name = dataTypeDto!=null ? dataTypeDto.getName() : null;
            if ( name!=null && !sensorNames.contains(name) ) {
                sensorNames.add(name);
                dtoList.add(dataTypeDto);
            }
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("dtoList: "+dtoList); 
        }
        return dtoList;
    }

    /**
     * Converts the string returned by the Meteo service in a more useful internal representation
     * 
     * @param responseString
     * @return
     * @throws Exception
     */
    public List<TimeSerieDto> convertMeasurementsResponseToInternalDTO(String responseString) throws Exception {

        /*
         * Example of JSON retrieved for measures of one station and sensor
         * 
[
  {
    "DATE":"2019-06-01T13:20:00CEST",
    "VALUE":21.5
  },
  ...
]
         */

        //Convert JSON string to External DTO
        List<TimeSerieDto> dataList = mapper.readValue(responseString, new TypeReference<List<TimeSerieDto>>() {});

        if (LOG.isDebugEnabled()) {
            LOG.debug("dataList: "+dataList); 
        }
        return dataList;
    }

    /**
     * Fetch data from Meteo service, to be integrated into the Open Data Hub.
     * A loop on all provided stations is performed:
     *    for every station a call to the meteo service is done and at the end 
     *    all data is collected in a single list.
     * Do not prevent exceptions from being thrown to not hide any malfunctioning.
     *
     * @throws Exception
     *             on error propagate exception to caller
     */
    public List<MeteorologyBzDto> fetchData() throws Exception {
        LOG.info("START.fetchData");
        List<MeteorologyBzDto> dtoList = new ArrayList<MeteorologyBzDto>();
        try {
            StringBuffer err = new StringBuffer();

            //Call service that retrieves the list of stations
            String responseString = callRemoteService(clientStations, serviceUrlStations, endpointMethodStations, null);
            dtoList = convertStationsResponseToInternalDTO(responseString);
            int size = dtoList!=null ? dtoList.size() : 0;

            //fetch also DataTypes to fill sensor data list
            fetchDataTypes(dtoList);

            //Call service that retrieves the measurements for each station
            for (int i=0 ; i<size ; i++) {
                MeteorologyBzDto meteoDto = dtoList.get(i);
                String stationId = meteoDto.getStation()!=null ? meteoDto.getStation().getId() : null;
                LOG.info("fetchData, "+i+" of "+size+": stationId="+stationId);
                try {
                    fetchDataByStation(meteoDto);
                } catch (Exception ex) {
                    LOG.error("ERROR in fetchData: " + ex.getMessage(), ex);
                    String stackTrace = DCUtils.extractStackTrace(ex, -1);
                    err.append("\n***** EXCEPTION RETRIEVING DATA FOR STATION: '"+stationId+"' ******" + stackTrace);
                }
            }
            if ( dtoList.size()==0 && err.length()>0 ) {
                throw new RuntimeException("NO DATA FETCHED: "+err);
            }
        } catch (Exception ex) {
            LOG.error("ERROR in fetchData: " + ex.getMessage(), ex);
            throw ex;
        }
        LOG.info("END.fetchData");
        return dtoList;
    }

    /**
     * Fetch anagrafic data from Meteo service for all stations.
     * 
     * @return
     * @throws Exception
     */
    public List<MeteorologyBzDto> fetchStations() throws Exception {
        LOG.debug("START.fetchStations");
        List<MeteorologyBzDto> dtoList = new ArrayList<MeteorologyBzDto>();
        try {
            StringBuffer err = new StringBuffer();

            //Call service that retrieves the list of stations
            String responseString = callRemoteService(clientStations, serviceUrlStations, endpointMethodStations, null);

            //Convert to internal representation
            dtoList = convertStationsResponseToInternalDTO(responseString);
            if ( dtoList.size()==0 && err.length()>0 ) {
                throw new RuntimeException("NO DATA FETCHED: "+err);
            }
        } catch (Exception ex) {
            LOG.error("ERROR in fetchData: " + ex.getMessage(), ex);
            throw ex;
        }
        LOG.debug("END.fetchStations");
        return dtoList;
    }

//    /**
//     * Fetch anagrafic data from Meteo service for all dataTypes.
//     * All available DataTypes are fetched using the sensors service which provides a list of all measures,
//     * from the measures only a single distinct instance of each DataType is added to the output list.
//     * 
//     * @return
//     * @throws Exception
//     */
//    public List<DataTypeDto> fetchDataTypes() throws Exception {
//        LOG.debug("START.fetchDataTypes");
//        List<DataTypeDto> dtoList = new ArrayList<DataTypeDto>();
//        try {
//            StringBuffer err = new StringBuffer();
//
//            //Call service that retrieves the list of stations
//            String responseString = callRemoteService(clientSensors, serviceUrlSensors, endpointMethodSensors, null);
//
//            //Convert to internal representation
//            dtoList = convertSensorsResponseToInternalDTO(responseString, null);
//            if ( dtoList.size()==0 && err.length()>0 ) {
//                throw new RuntimeException("NO DATA FETCHED: "+err);
//            }
//        } catch (Exception ex) {
//            LOG.error("ERROR in fetchData: " + ex.getMessage(), ex);
//            throw ex;
//        }
//        LOG.debug("END.fetchDataTypes");
//        return dtoList;
//    }

    /**
     * Fetch anagrafic data from Meteo service for all dataTypes.
     * All available DataTypes are fetched using the sensors service which provides a list of all measures,
     * from the measures only a single distinct instance of each DataType is added to the output list.
     * 
     * @return
     * @throws Exception
     */
    public List<DataTypeDto> fetchDataTypes() throws Exception {
        return fetchDataTypes(null);
    }

    /**
     * Fetch anagrafic data from Meteo service for all dataTypes.
     * All available DataTypes are fetched using the sensors service which provides a list of all measures,
     * in each MeteorologyBzDto (representing a station), all measurements for that station are recorded.
     * 
     * If stationList is null, only the distinct list of DataTypes is returned.
     * 
     * @return
     * @throws Exception
     */
    public List<DataTypeDto> fetchDataTypes(List<MeteorologyBzDto> stationList) throws Exception {
        LOG.debug("START.fetchDataTypes");
        List<DataTypeDto> dtoList = new ArrayList<DataTypeDto>();
        try {
            StringBuffer err = new StringBuffer();

            //Call service that retrieves the list of stations
            String responseString = callRemoteService(clientSensors, serviceUrlSensors, endpointMethodSensors, null);

            //Convert to internal representation
            dtoList = convertSensorsResponseToInternalDTO(responseString, stationList);
            if ( dtoList.size()==0 && err.length()>0 ) {
                throw new RuntimeException("NO DATA FETCHED: "+err);
            }
        } catch (Exception ex) {
            LOG.error("ERROR in fetchData: " + ex.getMessage(), ex);
            throw ex;
        }
        LOG.debug("END.fetchDataTypes");
        return dtoList;
    }

    /**
     * Fetch measurement data from Meteo service for one specific station.
     * 
     * @param cityKey
     * @return
     * @throws Exception
     */
    public void fetchDataByStation(MeteorologyBzDto meteoDto) throws Exception {
        LOG.debug("START.fetchDataByStation("+meteoDto+")");

        if ( meteoDto==null || meteoDto.getStation()==null|| meteoDto.getSensorDataList()==null || meteoDto.getSensorDataList().size()==0 ) {
            return;
        }

        StationDto stationDto = meteoDto.getStation();
        Map<String, DataTypeDto> dataTypeMap = meteoDto.getDataTypeMap();
        for (SensorDto sensorDto : meteoDto.getSensorDataList()) {

            String stationId  = stationDto.getId();
            String sensorType = sensorDto.getTYPE();

            try {
                List<NameValuePair> endpointParams = new ArrayList<NameValuePair>();
                if ( measurementsParams!=null && measurementsParams.size()>0 ) {
                    for (ServiceCallParam entry : measurementsParams) {
                        String paramName  = entry.name;
                        String paramValue = null;

                        if ( ServiceCallParam.TYPE_FIXED_VALUE.equals(entry.type) ) {
                            paramValue = entry.value;
                        } else if ( ServiceCallParam.TYPE_STATION_VALUE.equals(entry.type) ) {
                            String attrName = entry.value;
                            paramValue = DCUtils.allowNulls(DCUtils.getProperty(attrName, stationDto));
                        } else if ( ServiceCallParam.TYPE_SENSOR_VALUE.equals(entry.type) ) {
                            String attrName = entry.value;
                            paramValue = DCUtils.allowNulls(DCUtils.getProperty(attrName, sensorDto));
                        } else if ( ServiceCallParam.TYPE_FUNCTION.equals(entry.type) ) {
                            if ( ServiceCallParam.FUNCTION_NAME_CURR_DATE.equals(entry.value) ) {
                                paramValue = DCUtils.convertDateToString(new Date(System.currentTimeMillis()), "yyyyMMddHHmm");
                            } else if ( ServiceCallParam.FUNCTION_NAME_LAST_DATE.equals(entry.value) ) {

                                //Fetch last record for sensor and station present in the data hub
                                //As default set value from env param "app.min_date_from"
                                DataTypeDto dataTypeDto = dataTypeMap!=null ? dataTypeMap.get(sensorType) : null;
                                Date lastSavedRecord = null;
                                String minDateFrom = converter.getMinDateFrom();
                                if ( minDateFrom == null ) {
                                    //If env param is not set, use a fixed default
                                    minDateFrom = "201701010800";
                                    LOG.warn("MIN DATE PARAM '"+MeteorologyBzDataConverter.MIN_DATE_FROM+"' NOT SET, USING DEFAULT VALUE: " + minDateFrom);
                                }
                                String dateFrom = minDateFrom;
                                try {
                                    lastSavedRecord = pusher.getLastSavedRecordForStationAndDataType(stationDto, dataTypeDto);
                                } catch (Exception ex) {
                                    LOG.warn("ERROR in getLastSavedRecordForStationAndDataType(stationId="+stationId+", dataType="+dataTypeDto+"): " + ex.getMessage());
                                    LOG.warn("USING DEFAULT VALUE: " + dateFrom);
                                }
                                //If lastSavedRecord is found, compare with minimum and take the greater between the two
                                if ( lastSavedRecord != null ) {
                                    meteoDto.getLastSavedRecordMap().put(sensorType, lastSavedRecord);
                                    String strLastDate = DCUtils.convertDateToString(lastSavedRecord, "yyyyMMddHHmm");
                                    if ( strLastDate.compareTo(minDateFrom) > 0 ) {
                                        dateFrom = strLastDate;
                                    }
                                }
                                paramValue = dateFrom;

                            }
                        }

                        if ( DCUtils.paramNotNull(paramName) && DCUtils.paramNotNull(paramValue) ) {
                            endpointParams.add(new BasicNameValuePair(paramName, paramValue));
                        }
                    }
                }

                String responseString = callRemoteService(clientMeasurements, serviceUrlMeasurements, endpointMethodMeasurements, endpointParams);

                List<TimeSerieDto> timeSeriesList = convertMeasurementsResponseToInternalDTO(responseString);
                int size = timeSeriesList!=null ? timeSeriesList.size() : -1;

                //Store TimeSerieList in DTO
                if ( size > 0 ) {
                    Map<String, List<TimeSerieDto>> timeSeriesMap = meteoDto.getTimeSeriesMap();
                    List<TimeSerieDto> list = timeSeriesMap.get(sensorType);
                    if ( list != null ) {
                        list.addAll(timeSeriesList);
                    } else {
                        timeSeriesMap.put(sensorType, timeSeriesList);
                    }
                }

                LOG.debug("Data fetched for station="+stationId+", sensor="+sensorType+": "+size);
            } catch (Exception ex) {
                LOG.error("ERROR in fetchData: " + ex.getMessage(), ex);
                throw ex;
            }

        }

        LOG.debug("END.fetchDataByStation("+stationDto+")");
        //return extDto;
    }

}
