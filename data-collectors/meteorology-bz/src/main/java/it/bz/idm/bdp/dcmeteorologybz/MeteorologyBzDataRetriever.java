package it.bz.idm.bdp.dcmeteorologybz;

import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.PostConstruct;
//import javax.json.Json;
//import javax.json.JsonArray;
//import javax.json.JsonObject;
//import javax.json.JsonReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.bz.idm.bdp.dcmeteorologybz.dto.Feature;
import it.bz.idm.bdp.dcmeteorologybz.dto.FeaturesDto;

//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import it.bz.idm.bdp.dcmeteorologybz.dto.MeteorologyBzDto;
import it.bz.idm.bdp.dcmeteorologybz.dto.MeteorologyBzMeasurementDto;
import it.bz.idm.bdp.dcmeteorologybz.dto.MeteorologyBzMeasurementListDto;
import it.bz.idm.bdp.dcmeteorologybz.dto.SensorDto;
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
    private Map<String, String> measurementsParams;

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

            measurementsParams = new HashMap<String, String>();
            boolean hasNext = true;
            int i=0;
            while ( hasNext ) {
                //Example of parameters to add
                //endpoint.measurements.param.0.param_name
                //endpoint.measurements.param.0.station_attr_name
                String paramName = DCUtils.allowNulls(env.getProperty("endpoint.measurements.param."+i+".param_name")).trim();
                String stationAttrName = DCUtils.allowNulls(env.getProperty("endpoint.measurements.param."+i+".station_attr_name")).trim();
                if ( DCUtils.paramNotNull(paramName) && DCUtils.paramNotNull(stationAttrName) ) {
                    measurementsParams.put(paramName, stationAttrName);
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
            MeteorologyBzDto dataByCity = new MeteorologyBzDto(stationDto);
            dtoList.add(dataByCity);
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
    public List<DataTypeDto> convertSensorsResponseToInternalDTO(String responseString) throws Exception {

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

        List<SensorDto> dataList = mapper.readValue(responseString, new TypeReference<List<SensorDto>>() {});
        Set<String> sensorNames = new HashSet<String>();

        if (LOG.isDebugEnabled()) {
            LOG.debug("dataList: "+dataList);
        }

        if ( dataList == null ) {
            return null;
        }
        for (SensorDto sensorObj : dataList) {
            DataTypeDto dataTypeDto = converter.convertExternalSensorDtoToDataTypeDto(sensorObj);
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
    public MeteorologyBzDto convertMeasurementsResponseToInternalDTO(String responseString, Map<String, String> station) throws Exception {
        MeteorologyBzDto extDto = null;

        /*
         * Example of XML retrieved for measures of one station
         * 
<lastData xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns="http://www.meteotrentino.it/">
  <temperature_list>
    <air_temperature UM="°C">
      <date>2018-12-17T00:00:00+01</date>
      <value>-6.8</value>
    </air_temperature>
  </temperature_list>
  <precipitation_list>
    <precipitation UM="mm">
      <date>2018-12-17T00:00:00+01</date>
      <value>0</value>
    </precipitation>
  </precipitation_list>
  <wind_list>
    <wind10m UM_speed="m/s" UM_direction="gN">
      <date>2018-12-18T02:00:00+01</date>
      <speed_value>1.9</speed_value>
      <direction_value>113</direction_value>
    </wind10m>
  </wind_list>
  <global_radiation_list>
    <global_radiation UM="W/mq">
      <date>2018-12-17T00:00:00+01</date>
      <value>0</value>
    </global_radiation>
  </global_radiation_list>
  <relative_humidity_list>
    <relative_humidity UM="%">
      <date>2018-12-17T00:00:00+01</date>
      <value>78</value>
    </relative_humidity>
  </relative_humidity_list>
  <snow_depth_list/>
</lastData>
         */

        InputStream in = org.apache.commons.io.IOUtils.toInputStream(responseString, "UTF-8");
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document docXml = db.parse(in);
        Element root = docXml.getDocumentElement();

        if ( LOG.isDebugEnabled() ) {
            Node node = root.getFirstChild();
            analyzeNode(node, "");
        }

        StationDto stationDto = converter.convertExternalStationDtoToStationDto(station);
        extDto = new MeteorologyBzDto(stationDto, station);
        List<MeteorologyBzMeasurementListDto> measurementsByType = extDto.getMeasurementsByType();
        Map<String, DataTypeDto> dataTypes = extDto.getDataTypes();

        // Analyze XML in a dynamic and configurable way
        //Three levels: measurement_list, measurement_type, data
        Node node1 = root.getFirstChild();
        while ( node1 != null ) {
            String node1Name = node1.getNodeName();

            //First level
            if ( node1.getNodeType() == Node.ELEMENT_NODE ) {
                String type1Name = node1Name;
                if ( type1Name.endsWith("_list") ) {
                    type1Name = type1Name.substring(0, type1Name.length() - "_list".length());
                }
                MeteorologyBzMeasurementListDto measurementListDto = new MeteorologyBzMeasurementListDto(type1Name);
                measurementsByType.add(measurementListDto);
                List<MeteorologyBzMeasurementDto> measurements = measurementListDto.getMeasurements();

                Node node2 = node1.getFirstChild();
                while ( node2 != null ) {
                    String node2Name = node2.getNodeName();

                    //Second level
                    if ( node2.getNodeType() == Node.ELEMENT_NODE ) {
                        String node2Content = node2.getTextContent();
                        String xml = DCUtils.allowNulls(node2Content).trim();
                        NamedNodeMap attributes = node2.getAttributes();
                        Map<String, String> node2Attrs = DCUtils.getNodeAttributes(attributes);

                        Element elem2 = (Element) node2;

                        //The date is specified as tag "date"
                        String strDate = DCUtils.getElementTagValue(elem2, "date");
                        Date date = DCUtils.convertStringTimezoneToDate(strDate);

                        //The UM is specified as attribute, values in tags
                        Set<String> umKeySet = node2Attrs.keySet();
                        for (String umKey : umKeySet) {
                            String strUmName = node2Attrs.get(umKey);
                            String strValue = null;
                            String strTypeName = null;
                            //if attribute is "UM" then the value is in the tag "value", for example "air_temperature":
                            //<air_temperature UM="°C">
                            //  <date>2018-12-17T00:00:00+01</date>
                            //  <value>-6.8</value>
                            //</air_temperature>
                            if ( "UM".equals(umKey) ) {
                                strTypeName = node2Name;
                                strValue = DCUtils.getElementTagValue(elem2, "value");
                            } else {
                                //if attribute is "UM_xxx" then the value is in the tag "xxx_value", the name becomes "attr_xxx", for example "wind10m_speed":
                                //<wind10m UM_speed="m/s" UM_direction="gN">
                                //  <date>2018-12-18T02:00:00+01</date>
                                //  <speed_value>1.9</speed_value>
                                //  <direction_value>113</direction_value>
                                //</wind10m>
                                if ( umKey.startsWith("UM_") ) {
                                    String tmpTypeName = umKey.substring(3);
                                    strValue = DCUtils.getElementTagValue(elem2, tmpTypeName+"_value");
                                    strTypeName = node2Name + "_" + tmpTypeName;
                                } else {
                                    LOG.debug("How to deal with this attribute??? attr='"+umKey+"'  xml="+xml);
                                }
                            }
                            Object value = DCUtils.convertStringToDouble(strValue);
                            if ( value == null ) {
                                value = strValue;
                            }
                            MeteorologyBzMeasurementDto measurementDto = new MeteorologyBzMeasurementDto(xml, date, strTypeName, strUmName, value);
                            measurements.add(measurementDto);

                            //First time we find this data type, collect it
                            if ( !dataTypes.containsKey(strTypeName) ) {
                                DataTypeDto dataTypeDto = new DataTypeDto();
                                dataTypeDto.setName(strTypeName);
                                dataTypeDto.setUnit(strUmName);
                                dataTypeDto.setPeriod(converter.getPeriod());
                                dataTypes.put(strTypeName, dataTypeDto);
                            }
                        }

                    }
                    node2 = node2.getNextSibling();
                }

            }
            node1 = node1.getNextSibling();
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("extDto: "+extDto); 
        }
        return extDto;
    }

    /**
     * Convenient method only for logging pourpose
     * @param node
     * @param indentation
     */
    private void analyzeNode(Node node, String indentation) {
        while ( node != null ) {
            String nodeName = node.getNodeName();
            if ( node.getNodeType() == Node.ELEMENT_NODE ) {
                NamedNodeMap attributes = node.getAttributes();
                Map<String, String> nodeAttrs = DCUtils.getNodeAttributes(attributes);
                String nodeValue = node.getNodeValue();
                String nodeContent = node.getTextContent();
                String text = null;

                if ( nodeAttrs!=null && nodeAttrs.size()>1 ) {
                    LOG.debug("***************"+indentation+"nodeName="+nodeName+"  text="+text+"  nodeAttrs="+nodeAttrs+"  nodeValue="+nodeValue);
                }

                boolean hasChildren = false;
                node.normalize();
                NodeList childNodes = node.getChildNodes();
                for (int i=0 ; childNodes!=null && i<childNodes.getLength() ; i++) {
                    Node childNode = childNodes.item(i);
                    if ( childNode.getNodeType() == Node.ELEMENT_NODE ) {
                        hasChildren = true;
                    }
                }
                if ( !hasChildren ) {
                    text = nodeContent;
                }

                Node childNode = node.getFirstChild();

                LOG.debug(indentation+"nodeName="+nodeName+"  text="+text+"  nodeAttrs="+nodeAttrs+"  nodeValue="+nodeValue);

                //Go down recursively, if child is null exit
                analyzeNode(childNode, indentation+" ");

            }
            node = node.getNextSibling();
        }
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
        LOG.debug("START.fetchData");
        List<MeteorologyBzDto> dtoList = new ArrayList<MeteorologyBzDto>();
        try {
            StringBuffer err = new StringBuffer();

            //Call service that retrieves the list of stations
            String responseString = callRemoteService(clientStations, serviceUrlStations, endpointMethodStations, null);
            List<MeteorologyBzDto> stations = convertStationsResponseToInternalDTO(responseString);

            //Call service that retrieves the measurements for each station
            for (MeteorologyBzDto station : stations) {
                Map<String, String> stationAttrs = station.getStationAttributes();
                //We do not stop execution if one call goes in exception.
                //All exceptions are collected in a StringBuffer 
                String key = stationAttrs.get("code");
                try {
                    //Exclude invalid stations
                    boolean valid = station.isValid();
                    if ( valid ) {
                        MeteorologyBzDto dataByCity = fetchDataByStation(stationAttrs);
                        dtoList.add(dataByCity);
                    } else {
                        LOG.debug("EXCLUDE INVALID STATION: station_id="+key+"  enddate="+stationAttrs.get("enddate"));
                    }
                } catch (Exception ex) {
                    LOG.error("ERROR in fetchData: " + ex.getMessage(), ex);
                    String stackTrace = DCUtils.extractStackTrace(ex, -1);
                    err.append("\n***** EXCEPTION RETRIEVING DATA FOR STATION: '"+key+"' ******" + stackTrace);
                }
            }
            if ( dtoList.size()==0 && err.length()>0 ) {
                throw new RuntimeException("NO DATA FETCHED: "+err);
            }
        } catch (Exception ex) {
            LOG.error("ERROR in fetchData: " + ex.getMessage(), ex);
            throw ex;
        }
        LOG.debug("END.fetchData");
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

    /**
     * Fetch anagrafic data from Meteo service for all stations.
     * 
     * @return
     * @throws Exception
     */
    public List<DataTypeDto> fetchDataTypes() throws Exception {
        LOG.debug("START.fetchDataTypes");
        List<DataTypeDto> dtoList = new ArrayList<DataTypeDto>();
        try {
            StringBuffer err = new StringBuffer();

            //Call service that retrieves the list of stations
            String responseString = callRemoteService(clientSensors, serviceUrlSensors, endpointMethodSensors, null);

            //Convert to internal representation
            dtoList = convertSensorsResponseToInternalDTO(responseString);
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
    public MeteorologyBzDto fetchDataByStation(Map<String, String> station) throws Exception {
        LOG.debug("START.fetchDataByStation("+station+")");
        MeteorologyBzDto extDto = null;
        try {
            List<NameValuePair> endpointParams = new ArrayList<NameValuePair>();
            if ( measurementsParams!=null && measurementsParams.size()>0 ) {
                Set<Entry<String, String>> entrySet = measurementsParams.entrySet();
                for (Entry<String, String> entry : entrySet) {
                    String paramName = entry.getKey();
                    String attrName = entry.getValue();
                    String paramValue = station.get(attrName);
                    if ( DCUtils.paramNotNull(paramName) && DCUtils.paramNotNull(paramValue) ) {
                        endpointParams.add(new BasicNameValuePair(paramName, paramValue));
                    }
                }
            }
            String responseString = callRemoteService(clientMeasurements, serviceUrlMeasurements, endpointMethodMeasurements, endpointParams);
            extDto = convertMeasurementsResponseToInternalDTO(responseString, station);
            int size = extDto!=null && extDto.getMeasurementsByType()!=null ? extDto.getMeasurementsByType().size() : -1;
            LOG.debug("Data fetched for station "+station+": "+size);
        } catch (Exception ex) {
            LOG.error("ERROR in fetchData: " + ex.getMessage(), ex);
            throw ex;
        }
        LOG.debug("END.fetchDataByStation("+station+")");
        return extDto;
    }

}
