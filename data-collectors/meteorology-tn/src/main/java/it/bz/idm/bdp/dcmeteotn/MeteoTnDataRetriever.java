// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.idm.bdp.dcmeteotn;

import java.io.InputStream;
import java.io.StringWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
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
public class MeteoTnDataRetriever {

    private static final Logger LOG = LoggerFactory.getLogger(MeteoTnDataRetriever.class.getName());

    @Value("${endpoint.stations.url}")
    private String serviceUrlStations;

    @Value("${endpoint.measurements.url}")
    private String serviceUrlMeasurements;

    private String paramName = "codice";
    private String attrName = "code";

    @Autowired
    private MeteoTnDataConverter converter;

    private CloseableHttpClient httpClient = HttpClients.createDefault();
    private XmlMapper mapper = new XmlMapper();

    public MeteoTnDataRetriever() {
        LOG.debug("Create instance");
    }

    /**
     * Performs the call to Meteo service and returns exactly the response String
     * without particular processing or formatting
     * 
     * @return
     * @throws Exception
     */
    private String callRemoteService(CloseableHttpClient client, String serviceUrl, List<NameValuePair> endpointParams) throws Exception {
        LOG.debug("Start call to service: " + serviceUrl);

        HttpRequestBase request = new HttpGet(serviceUrl);

        URIBuilder uriBuilder = new URIBuilder(request.getURI());
        if (endpointParams != null && endpointParams.size() > 0) {
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
     * Converts the string returned by the Meteo service in a more useful internal
     * representation
     * 
     * @param responseString
     * @return
     * @throws Exception
     */
    public List<MeteoTnDto> convertStationsResponseToInternalDTO(String responseString) throws Exception {

        List<MeteoTnDto> dtoList = new ArrayList<MeteoTnDto>();

        List<Map<String, String>> dataMap = mapper.readValue(responseString,
                new TypeReference<List<Map<String, String>>>() {
                });

        // Convert in internal DTO without calling measurement service
        for (Map<String, String> station : dataMap) {
            StationDto stationDto = converter.convertExternalStationDtoToStationDto(station);
            MeteoTnDto dataByCity = new MeteoTnDto(stationDto, station);
            dtoList.add(dataByCity);
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("dtoList: " + dtoList);
        }
        return dtoList;
    }

    /**
     * Converts the string returned by the Meteo service in a more useful internal
     * representation
     * 
     * @param responseString
     * @return
     * @throws Exception
     */
    public MeteoTnDto convertMeasurementsResponseToInternalDTO(String responseString, Map<String, String> station)
            throws Exception {
        MeteoTnDto extDto = null;

        /*
         * Example of XML retrieved for measures of one station
         * 
         * <lastData xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         * xmlns:xsd="http://www.w3.org/2001/XMLSchema"
         * xmlns="http://www.meteotrentino.it/">
         * <temperature_list>
         * <air_temperature UM="°C">
         * <date>2018-12-17T00:00:00+01</date>
         * <value>-6.8</value>
         * </air_temperature>
         * </temperature_list>
         * <precipitation_list>
         * <precipitation UM="mm">
         * <date>2018-12-17T00:00:00+01</date>
         * <value>0</value>
         * </precipitation>
         * </precipitation_list>
         * <wind_list>
         * <wind10m UM_speed="m/s" UM_direction="gN">
         * <date>2018-12-18T02:00:00+01</date>
         * <speed_value>1.9</speed_value>
         * <direction_value>113</direction_value>
         * </wind10m>
         * </wind_list>
         * <global_radiation_list>
         * <global_radiation UM="W/mq">
         * <date>2018-12-17T00:00:00+01</date>
         * <value>0</value>
         * </global_radiation>
         * </global_radiation_list>
         * <relative_humidity_list>
         * <relative_humidity UM="%">
         * <date>2018-12-17T00:00:00+01</date>
         * <value>78</value>
         * </relative_humidity>
         * </relative_humidity_list>
         * <snow_depth_list/>
         * </lastData>
         */

        InputStream in = org.apache.commons.io.IOUtils.toInputStream(responseString, "UTF-8");
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document docXml = db.parse(in);
        Element root = docXml.getDocumentElement();

        if (LOG.isDebugEnabled()) {
            Node node = root.getFirstChild();
            analyzeNode(node, "");
        }

        StationDto stationDto = converter.convertExternalStationDtoToStationDto(station);
        extDto = new MeteoTnDto(stationDto, station);
        List<MeteoTnMeasurementListDto> measurementsByType = extDto.getMeasurementsByType();
        Map<String, DataTypeDto> dataTypes = extDto.getDataTypes();

        // Analyze XML in a dynamic and configurable way
        // Three levels: measurement_list, measurement_type, data
        Node node1 = root.getFirstChild();
        while (node1 != null) {
            String node1Name = node1.getNodeName();

            // First level
            if (node1.getNodeType() == Node.ELEMENT_NODE) {
                String type1Name = node1Name;
                if (type1Name.endsWith("_list")) {
                    type1Name = type1Name.substring(0, type1Name.length() - "_list".length());
                }
                MeteoTnMeasurementListDto measurementListDto = new MeteoTnMeasurementListDto(type1Name);
                measurementsByType.add(measurementListDto);
                List<MeteoTnMeasurementDto> measurements = measurementListDto.getMeasurements();

                Node node2 = node1.getFirstChild();
                while (node2 != null) {
                    String node2Name = node2.getNodeName();

                    // Second level
                    if (node2.getNodeType() == Node.ELEMENT_NODE) {
                        String node2Content = node2.getTextContent();
                        String xml = DCUtils.allowNulls(node2Content).trim();
                        NamedNodeMap attributes = node2.getAttributes();
                        Map<String, String> node2Attrs = DCUtils.getNodeAttributes(attributes);

                        Element elem2 = (Element) node2;

                        // The date is specified as tag "date"
                        String strDate = DCUtils.getElementTagValue(elem2, "date");
                        Date date = DCUtils.convertStringTimezoneToDate(strDate);

                        // The UM is specified as attribute, values in tags
                        Set<String> umKeySet = node2Attrs.keySet();
                        for (String umKey : umKeySet) {
                            String strUmName = node2Attrs.get(umKey);
                            String strValue = null;
                            String strTypeName = null;
                            // if attribute is "UM" then the value is in the tag "value", for example
                            // "air_temperature":
                            // <air_temperature UM="°C">
                            // <date>2018-12-17T00:00:00+01</date>
                            // <value>-6.8</value>
                            // </air_temperature>
                            if ("UM".equals(umKey)) {
                                strTypeName = node2Name;
                                strValue = DCUtils.getElementTagValue(elem2, "value");
                            } else {
                                // if attribute is "UM_xxx" then the value is in the tag "xxx_value", the name
                                // becomes "attr_xxx", for example "wind10m_speed":
                                // <wind10m UM_speed="m/s" UM_direction="gN">
                                // <date>2018-12-18T02:00:00+01</date>
                                // <speed_value>1.9</speed_value>
                                // <direction_value>113</direction_value>
                                // </wind10m>
                                if (umKey.startsWith("UM_")) {
                                    String tmpTypeName = umKey.substring(3);
                                    strValue = DCUtils.getElementTagValue(elem2, tmpTypeName + "_value");
                                    strTypeName = node2Name + "_" + tmpTypeName;
                                } else {
                                    LOG.debug("How to deal with this attribute??? attr='" + umKey + "'  xml=" + xml);
                                }
                            }
                            Object value = DCUtils.convertStringToDouble(strValue);
                            if (value == null) {
                                value = strValue;
                            }
                            MeteoTnMeasurementDto measurementDto = new MeteoTnMeasurementDto(xml, date, strTypeName,
                                    strUmName, value);
                            measurements.add(measurementDto);

                            // First time we find this data type, collect it
                            if (!dataTypes.containsKey(strTypeName)) {
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
            LOG.debug("extDto: " + extDto);
        }
        return extDto;
    }

    /**
     * Convenient method only for logging pourpose
     * 
     * @param node
     * @param indentation
     */
    private void analyzeNode(Node node, String indentation) {
        while (node != null) {
            String nodeName = node.getNodeName();
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                NamedNodeMap attributes = node.getAttributes();
                Map<String, String> nodeAttrs = DCUtils.getNodeAttributes(attributes);
                String nodeValue = node.getNodeValue();
                String nodeContent = node.getTextContent();
                String text = null;

                if (nodeAttrs != null && nodeAttrs.size() > 1) {
                    LOG.debug("***************" + indentation + "nodeName=" + nodeName + "  text=" + text
                            + "  nodeAttrs=" + nodeAttrs + "  nodeValue=" + nodeValue);
                }

                boolean hasChildren = false;
                node.normalize();
                NodeList childNodes = node.getChildNodes();
                for (int i = 0; childNodes != null && i < childNodes.getLength(); i++) {
                    Node childNode = childNodes.item(i);
                    if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                        hasChildren = true;
                    }
                }
                if (!hasChildren) {
                    text = nodeContent;
                }

                Node childNode = node.getFirstChild();

                LOG.debug(indentation + "nodeName=" + nodeName + "  text=" + text + "  nodeAttrs=" + nodeAttrs
                        + "  nodeValue=" + nodeValue);

                // Go down recursively, if child is null exit
                analyzeNode(childNode, indentation + " ");

            }
            node = node.getNextSibling();
        }
    }

    /**
     * Fetch data from Meteo service, to be integrated into the Open Data Hub.
     * A loop on all provided stations is performed:
     * for every station a call to the meteo service is done and at the end
     * all data is collected in a single list.
     * Do not prevent exceptions from being thrown to not hide any malfunctioning.
     *
     * @throws Exception
     *                   on error propagate exception to caller
     */
    public List<MeteoTnDto> fetchData() throws Exception {
        LOG.debug("START.fetchData");
        List<MeteoTnDto> dtoList = new ArrayList<MeteoTnDto>();
        try {
            StringBuffer err = new StringBuffer();

            // Call service that retrieves the list of stations
            String responseString = callRemoteService(httpClient, serviceUrlStations, null);
            List<MeteoTnDto> stations = convertStationsResponseToInternalDTO(responseString);

            // Call service that retrieves the measurements for each station
            for (MeteoTnDto station : stations) {
                Map<String, String> stationAttrs = station.getStationAttributes();
                // We do not stop execution if one call goes in exception.
                // All exceptions are collected in a StringBuffer
                String key = stationAttrs.get("code");
                try {
                    // Exclude invalid stations
                    boolean valid = station.isValid();
                    if (valid) {
                        MeteoTnDto dataByCity = fetchDataByStation(stationAttrs);
                        dtoList.add(dataByCity);
                    } else {
                        LOG.debug("EXCLUDE INVALID STATION: station_id=" + key + "  enddate="
                                + stationAttrs.get("enddate"));
                    }
                } catch (Exception ex) {
                    LOG.error("ERROR in fetchData: " + ex.getMessage(), ex);
                    String stackTrace = DCUtils.extractStackTrace(ex, -1);
                    err.append("\n***** EXCEPTION RETRIEVING DATA FOR STATION: '" + key + "' ******" + stackTrace);
                }
            }
            if (dtoList.size() == 0 && err.length() > 0) {
                throw new RuntimeException("NO DATA FETCHED: " + err);
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
    public List<MeteoTnDto> fetchStations() throws Exception {
        LOG.debug("START.fetchStations");
        List<MeteoTnDto> dtoList = new ArrayList<MeteoTnDto>();
        try {
            StringBuffer err = new StringBuffer();

            // Call service that retrieves the list of stations
            String responseString = callRemoteService(httpClient, serviceUrlStations, null);

            // Convert to internal representation
            dtoList = convertStationsResponseToInternalDTO(responseString);
            if (dtoList.size() == 0 && err.length() > 0) {
                throw new RuntimeException("NO DATA FETCHED: " + err);
            }
        } catch (Exception ex) {
            LOG.error("ERROR in fetchData: " + ex.getMessage(), ex);
            throw ex;
        }
        LOG.debug("END.fetchStations");
        return dtoList;
    }

    /**
     * Fetch measurement data from Meteo service for one specific station.
     * 
     * @param cityKey
     * @return
     * @throws Exception
     */
    public MeteoTnDto fetchDataByStation(Map<String, String> station) throws Exception {
        LOG.debug("START.fetchDataByStation(" + station + ")");
        MeteoTnDto extDto = null;
        List<NameValuePair> endpointParams = new ArrayList<NameValuePair>();
        endpointParams.add(new BasicNameValuePair(paramName, station.get(attrName)));

        String responseString = callRemoteService(httpClient, serviceUrlMeasurements, endpointParams);
        extDto = convertMeasurementsResponseToInternalDTO(responseString, station);
        int size = extDto != null && extDto.getMeasurementsByType() != null ? extDto.getMeasurementsByType().size()
                : -1;
        LOG.debug("Data fetched for station " + station + ": " + size);
        LOG.debug("END.fetchDataByStation(" + station + ")");
        return extDto;
    }
}
