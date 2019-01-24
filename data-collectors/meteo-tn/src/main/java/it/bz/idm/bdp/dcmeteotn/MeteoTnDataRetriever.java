package it.bz.idm.bdp.dcmeteotn;

import java.io.InputStream;
import java.io.StringWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.PostConstruct;
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
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import it.bz.idm.bdp.dcmeteotn.dto.MeteoTnDto;
import it.bz.idm.bdp.dcmeteotn.dto.MeteoTnMeasurementDto;
import it.bz.idm.bdp.dcmeteotn.dto.MeteoTnMeasurementListDto;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.StationDto;

@Component
@PropertySource({ "classpath:/META-INF/spring/application.properties" })
public class MeteoTnDataRetriever {

    private static final Logger LOG = LogManager.getLogger(MeteoTnDataRetriever.class.getName());

    @Autowired
    private Environment env;

    @Autowired
    private MeteoTnDataConverter converter;

    private HttpClientBuilder builderStations = HttpClients.custom();
    private HttpClientBuilder builderMeasurements = HttpClients.custom();
    private CloseableHttpClient clientStations;
    private CloseableHttpClient clientMeasurements;
    private XmlMapper mapper = new XmlMapper();

    private String endpointMethodStations;
    private String serviceUrlStations;

    private String endpointMethodMeasurements;
    private String serviceUrlMeasurements;
    private Map<String, String> measurementsParams;

    public MeteoTnDataRetriever() {
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
    public List<MeteoTnDto> convertStationsResponseToInternalDTO(String responseString) throws Exception {

        List<MeteoTnDto> dtoList = new ArrayList<MeteoTnDto>();

        List<Map<String, String>> dataMap = mapper.readValue(responseString, new TypeReference<List<Map<String, String>>>() {});

        //Convert in internal DTO without calling measurement service
        for (Map<String, String> station : dataMap) {
            StationDto stationDto = converter.convertExternalStationDtoToStationDto(station);
            MeteoTnDto dataByCity = new MeteoTnDto(stationDto, station);
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
    public MeteoTnDto convertMeasurementsResponseToInternalDTO(String responseString, Map<String, String> station) throws Exception {
        MeteoTnDto extDto = null;

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
        extDto = new MeteoTnDto(stationDto, station);
        List<MeteoTnMeasurementListDto> measurementTypes = extDto.getMeasurementTypes();
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
                MeteoTnMeasurementListDto measurementListDto = new MeteoTnMeasurementListDto(type1Name);
                measurementTypes.add(measurementListDto);
                List<MeteoTnMeasurementDto> measurements = measurementListDto.getMeasurements();

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
                            //if attribute is "UM" then the value is in the tag "value"
                            if ( "UM".equals(umKey) ) {
                                strTypeName = node2Name;
                                strValue = DCUtils.getElementTagValue(elem2, "value");
                            } else {
                                //if attribute is "UM_xxx" then the value is in the tag "xxx"
                                if ( umKey.startsWith("UM_") ) {
                                    strTypeName = umKey.substring(3);
                                    strValue = DCUtils.getElementTagValue(elem2, strTypeName);
                                } else {
                                    LOG.debug("How to deal with this attribute??? attr='"+umKey+"'  xml="+xml);
                                }
                            }
                            Object value = DCUtils.convertStringToDouble(strValue);
                            if ( value == null ) {
                                value = strValue;
                            }
                            MeteoTnMeasurementDto measurementDto = new MeteoTnMeasurementDto(xml, date, strTypeName, strUmName, value);
                            measurements.add(measurementDto);

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
     * A loop on all provided cities is performed:
     *    for every city a call to the meteo service is done and at the end 
     *    all data is collected in a single list.
     * Do not prevent exceptions from being thrown to not hide any malfunctioning.
     *
     * @throws Exception
     *             on error propagate exception to caller
     */
    public List<MeteoTnDto> fetchData() throws Exception {
        LOG.debug("START.fetchData");
        List<MeteoTnDto> dtoList = new ArrayList<MeteoTnDto>();
        try {
            StringBuffer err = new StringBuffer();

            //Call service that retrieves the list of stations
            String responseString = callRemoteService(clientStations, serviceUrlStations, endpointMethodStations, null);
            List<MeteoTnDto> stations = convertStationsResponseToInternalDTO(responseString);

            //Call service that retrieves the measurements for each station
            for (MeteoTnDto station : stations) {
                Map<String, String> stationAttrs = station.getStationAttributes();
                //We do not stop execution if one call goes in exception.
                //All exceptions are collected in a StringBuffer
                String key = stationAttrs.get("code");
                try {
                    MeteoTnDto dataByCity = fetchDataByStation(stationAttrs);
                    dtoList.add(dataByCity);
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

    public List<MeteoTnDto> fetchStations() throws Exception {
        LOG.debug("START.fetchStations");
        List<MeteoTnDto> dtoList = new ArrayList<MeteoTnDto>();
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
     * Fetch data from Meteo service for one specific city.
     * 
     * @param cityKey
     * @return
     * @throws Exception
     */
    public MeteoTnDto fetchDataByStation(Map<String, String> station) throws Exception {
        LOG.info("START.fetchDataByStation("+station+")");
        MeteoTnDto extDto = null;
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
            int size = extDto!=null && extDto.getMeasurementTypes()!=null ? extDto.getMeasurementTypes().size() : -1;
            LOG.debug("Data fetched for station "+station+": "+size);
        } catch (Exception ex) {
            LOG.error("ERROR in fetchData: " + ex.getMessage(), ex);
            throw ex;
        }
        LOG.info("END.fetchDataByStation("+station+")");
        return extDto;
    }

}
