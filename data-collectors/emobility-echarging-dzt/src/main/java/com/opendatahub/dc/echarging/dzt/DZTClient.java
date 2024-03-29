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
import java.util.Optional;
import java.util.concurrent.ForkJoinPool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    
    private final static Logger log = LoggerFactory.getLogger(DZTClient.class);

    @Autowired
    public ObjectMapper mapper;

    public static final class PagingContext implements Cloneable {
        public int currentPage = 1;
        public String seed;
        public int totalCount;
        public int totalPages;
        public final int pageSize = PAGESIZE;

        public PagingContext nextPage() throws Exception{
            var clone = (PagingContext) this.clone();
            clone.currentPage++;
            return clone;
        }
    }

    public static final class Plug {
        public String socket;
        public String name;
        public String powerUnitCode;
        public String powerUnitText;
        public Double powerValue;
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
        public String publisher;
        public String publisherUrl;
        public List<Plug> plugs = new ArrayList<>();
    }
    
    @Value("${dztWorkerThreads}")
    public int threadPoolSize;

    public List<Station> getStationsPage(Map<String, Object> query, PagingContext paging) throws Exception {
        log.debug("Getting station ID list of page {} of {}", paging.currentPage + 1, paging.totalPages);
        List<String> sids = getStationsPageIds(query, paging);
        
        // Run the single detail requests in parallel in a separate thread pool. 
        // Querying all stations in sequence would be too slow
        ForkJoinPool pool = new ForkJoinPool(threadPoolSize);
        List<Station> stationPage;
        try{
            stationPage = pool.submit(
                () -> sids.parallelStream()
                    .map(id -> {
                        log.debug("Requesting detail for station.id = {} ", id);
                        String json = null;
                        int errorCount = 0;
                        final int maxTries = 3;
                        try {
                            while (true) {
                                try {
                                    json = getStationDetail(id);
                                    break;
                                } catch (Exception e){
                                    // random errors are crashing the 1h elaboration, so we retry a few times before giving up
                                    if (++errorCount < maxTries) {
                                        log.warn("Exception while requesting station detail id = " + id + ". will retry... ", e);
                                        continue;
                                    } else {
                                        log.error("Exception while requesting station detail id = " + id + ". panic! ", e);
                                        throw e;
                                    }
                                }
                            } 
                            return DZTParser.parseJsonToStation(json);
                        } catch (Exception e) {
                            log.error("Exception encountered while getting details for station id = " + id, e);
                            log.error("Dumping json: {}", json);
                            throw new RuntimeException(e);
                        }})
                    .filter(station -> station.id != null) // filter stations without ID already, they are usually all null fields
                    .toList()
            ).get();
        } finally {
            // always properly shutdown pool to prevent memory leaks
            pool.shutdown();
        }
        log.debug("Page details have been fully retrieved");
        return stationPage;
    }

    public List<Station> getAllStations(LocalDateTime modifiedSince) throws Exception {
        var query = buildStationQuery(modifiedSince);
        PagingContext paging = new PagingContext();

        List<Station> stations = new ArrayList<>();
        do {
            stations.addAll(getStationsPage(query, paging));
            paging = paging.nextPage();
        } while (paging.currentPage <= paging.totalPages);

        return stations;
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

    private List<String> getStationsPageIds(Map<String, Object> query, PagingContext paging) throws Exception {
        String queryStr = mapper.writeValueAsString(query);
        String respStr = WebClient.create(baseUrl)
            .post()
            .uri(uriBuilder -> uriBuilder
                .path("/api/ts/v2/kg/things")
                .queryParam("filterDsList","https://semantify.it/ds/E85TgOxMg") 
                .queryParamIfPresent("sortSeed", Optional.ofNullable(paging.seed)) // Not present yet on first call
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

        try {
            //Configuration conf = Configuration.defaultConfiguration().addOptions( Option.SUPPRESS_EXCEPTIONS);
            var jsonPath = JsonPath.parse(respStr);

            paging.seed = jsonPath.read("$.metaData.sortSeed");
            paging.totalCount = jsonPath.read("$.metaData.total");

            if (paging.totalPages == 0) {
                paging.totalPages = (int) Math.ceil(paging.totalCount / paging.pageSize);
                log.debug("Total station count is {}, which is {} pages", paging.totalCount, paging.totalPages);
            }

            // find the list of station URLs
            List<String> stationUrls = jsonPath.read("$.data[*].['@id']");

            // extract IDs from URLs (it's the last part)
            var stationIds = stationUrls.stream().map(s -> s.replaceAll(".*/(\\w+)$", "$1")).toList();

            return stationIds; 
        } catch (Exception e) {
            log.error("Error encountered parsing stations page {} seed {} ", paging.currentPage, paging.seed);
            log.error("Dumping response json: {}", respStr);
            log.error("Propagating original Exception", e);
            throw e;
        }
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
