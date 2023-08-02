// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package com.opendatahub.dc.echarging.dzt;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

/**
 * Implements webservice calls to DZT knowledge graph for Echarging station retrieval.
 * consult calls.http for examples
 */
@Service
public class DZTClient {
    @Value("${dztBaseUrl}")
    public String baseUrl;
    @Value("${dztApiKey}")
    public String apiKey;

    private final static int PAGESIZE = 50;

    @Autowired
    public ObjectMapper mapper;

    public static final class PagingContext implements Cloneable {
        public int currentPage = 0;
        public String seed;
        public int totalCount;
        public final int pageSize = PAGESIZE;

        public PagingContext nextPage() throws Exception{
            var clone = (PagingContext) this.clone();
            clone.currentPage++;
            return clone;
        }
    }

    public void getTheThings() throws Exception {
        LocalDateTime from = LocalDateTime.parse("2023-01-01T00:00:00");
        var query = buildStationQuery(from);
        PagingContext paging = new PagingContext();
        var stationIds = getStationsPage(query, paging);

        stationIds.stream().forEach(s -> System.out.println(s));

        String detailJson = getStationDetail("100001001");
        Station s = parseJsonToStation(detailJson);
    }

    public Map<String, Object> buildStationQuery(TemporalAccessor modifiedAfter){
        return Map.of(
            "@context", Map.of(
                "ometa", "http://onlim.com/meta/", 
                "sq", "http://www.onlim.com/shapequery/",
                "@vocab", "http://www.onlim.com/shapequery/"
            ),
            "sq:query", new Object[] {
                Map.of(
                    "ometa:dateModified", Map.of(
                        "sq:value", DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(modifiedAfter),
                        "sq:op", ">",
                        "sq:datatype", "dateTime")
                )
            }
        );
    }

    private List<String> getStationsPage(Map<String, Object> query, PagingContext paging) throws Exception {
        String queryStr = mapper.writeValueAsString(query);
        String respStr = WebClient.create(baseUrl)
            .post()
            .uri(uriBuilder -> uriBuilder
                .path("/api/ts/v2/kg/things")
                .queryParam("filterDsList","https://semantify.it/ds/E85TgOxMg") 
                .build())
            .header(HttpHeaders.CONTENT_TYPE, "application/ld+json")
            .header("x-api-key", apiKey)
            .header("page", Integer.toString(paging.currentPage))
            .header("page-size", Integer.toString(paging.pageSize))
            .bodyValue(queryStr)
            .retrieve()
            .onStatus(HttpStatus.INTERNAL_SERVER_ERROR::equals, 
                response -> response.bodyToMono(String.class).map(Exception::new))
            .bodyToMono(String.class)
            .block();

        var jsonPath = JsonPath.parse(respStr);

        paging.seed = jsonPath.read("$.metaData.sortSeed");
        paging.totalCount = jsonPath.read("$.metaData.total");

        // find the list of station URLs
        List<String> stationUrls = jsonPath.read("$.data[*].['@id']");

        // extract IDs from URLs (it's the last part)
        var stationIds = stationUrls.stream().map(s -> s.replaceAll(".*/(\\w+)$", "$1")).toList();

        return stationIds; 
    }

    public static final class Plug {
        public String socket;
        public String name;
        public String powerUnitCode;
        public String powerUnitText;
        public String powerValue;
    }

    public static final class Station {
        public String id;
        public String name;
        public Double latitude;
        public Double longitude;
        public String addressStreet;
        public String addressPostalCode;
        public String addressLocality;
        public String addressCountry;
        public List<Plug> plugs = new ArrayList<>();
    }

    private static Double parseDoubleOrNull(String s) {
        if (!s.isEmpty()) {
            return Double.parseDouble(s);
        } else {
            return null;
        }
    }

    public static Station parseJsonToStation(String json) throws Exception {
        Station s = new Station();

        var jp = JsonPath.parse(json);
        
        s.id = jp.read("$.[0][\"https://schema.org/identifier\"][\"https://schema.org/value\"][\"@value\"]");
        s.name = jp.read("$.[0][\"https://schema.org/name\"]");
        s.latitude = parseDoubleOrNull(jp.read("$.[0][\"https://schema.org/geo\"][\"https://schema.org/latitude\"][\"@value\"]"));
        s.longitude = parseDoubleOrNull(jp.read("$.[0][\"https://schema.org/geo\"][\"https://schema.org/longitude\"][\"@value\"]"));
        s.addressCountry = jp.read("$.[0][\"https://schema.org/address\"][\"https://schema.org/addressCountry\"]");
        s.addressLocality = jp.read("$.[0][\"https://schema.org/address\"][\"https://schema.org/addressLocality\"]");
        s.addressPostalCode = jp.read("$.[0][\"https://schema.org/address\"][\"https://schema.org/postalCode\"]");
        s.addressStreet = jp.read("$.[0][\"https://schema.org/address\"][\"https://schema.org/streetAddress\"]");

        var plugs = jp.read("$.[0][\"https://odta.io/voc/hasCharger\"]");

        // check if single or list
        if (plugs instanceof List) {
            for (var plug : (List) plugs) {
                var jpp = JsonPath.parse(plug);

            }

        } else if (plugs != null) {

        }

        return s;
    }

    private String getStationDetail(String stationId) throws Exception {
        String json = WebClient.create(baseUrl)
            .get()
            .uri(uriBuilder -> uriBuilder
                    .path("/api/ts/v2/kg/things")
                    .pathSegment(stationId)
                    .queryParam("ns", "http://onlim.com/entity/Ladestationen-Api-Bund/ECarChargingStation/")
                    .build())
            .header(HttpHeaders.CONTENT_TYPE, "application/json")
            .header("x-api-key", apiKey)
            .retrieve()
            .onStatus(HttpStatus.INTERNAL_SERVER_ERROR::equals,
                    response -> response.bodyToMono(String.class).map(Exception::new))
            .bodyToMono(String.class)
            .block();
        return json;
    }
}
