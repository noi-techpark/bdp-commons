// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package com.opendatahub.dc.echarging.dzt;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
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
    }

    public Map<String, Object> buildStationQuery(TemporalAccessor laterThan){
        return Map.of(
            "@context", Map.of(
                "ometa", "http://onlim.com/meta/", 
                "sq", "http://www.onlim.com/shapequery/",
                "@vocab", "http://www.onlim.com/shapequery/"
            ),
            "sq:query", new Object[] {
                Map.of(
                    "ometa:dateModified", Map.of(
                        "sq:value", DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(laterThan),
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
            .uri("/api/ts/v2/kg/things?filterDsList=https%3A%2F%2Fsemantify.it%2Fds%2FE85TgOxMg")
            .header(HttpHeaders.CONTENT_TYPE, "application/ld+json")
            .header("x-api-key", apiKey)
            .header("page", Integer.toString(paging.currentPage))
            .header("page-size", Integer.toString(paging.pageSize))
            .bodyValue(queryStr)
            .retrieve()
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
}
