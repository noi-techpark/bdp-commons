/*
 *  A22 Events Data Collector - Main Application
 *
 *  (C) 2021 NOI Techpark Südtirol / Alto Adige
 *
 *  changelog:
 *  2021-06-04  1.0 - thomas.nocker@catch-solve.tech
 */

package it.bz.noi.a22.events;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.vividsolutions.jts.geom.*;
import it.bz.idm.bdp.dto.EventDto;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@Configuration
@PropertySource("classpath:it/bz/noi/a22/events/a22connector.properties")
@PropertySource("classpath:it/bz/noi/a22/events/odh-writer.properties")
public class MainA22Events {

    @Value("${a22url}")
    private String a22ConnectorURL;

    @Value("${a22user}")
    private String a22ConnectorUsr;

    @Value("${a22password}")
    private String a22ConnectorPwd;

    private static final String METADTA_PREFIX = "a22_events.metadata.";

    private static final String STATION_METADATA_ID = "id";
    private static final String STATION_METADATA_FASCIA_ORARIA = "fascia_oraria";
    private static final String STATION_METADATA_IDCORSIA = "idcorsia";
    private static final String STATION_METADATA_IDDIREZIONE = "iddirezione";
    private static final String STATION_METADATA_IDTIPOEVENTO = "idtipoevento";
    private static final String STATION_METADATA_IDSOTTOTIPOEVENTO = "idsottotipoevento";
    private static final String STATION_METADATA_METRO_INIZIO = "metro_fine";
    private static final String STATION_METADATA_METRO_FINE = "metro_inizio";

    private static final Logger LOG = LoggerFactory.getLogger(MainA22Events.class);

    private final A22Properties metadataMappingProperties;
    private final A22Properties a22EventsProperties;
    @Autowired
    private A22EventEventsJSONPusher pusher;
    private final String categoryPrefix;
    private final UUID uuidNamespace;

    public MainA22Events() {
        this.metadataMappingProperties = new A22Properties("a22eventsmetadatamapping.properties");
        this.a22EventsProperties = new A22Properties("a22events.properties");
        this.categoryPrefix = a22EventsProperties.getProperty("categoryPrefix");
        this.uuidNamespace = UUID.fromString(a22EventsProperties.getProperty("uuidNamescpace"));
    }


    @PostConstruct
    public void init() {
        try (Reader reader = new InputStreamReader(getClass().getResourceAsStream("SottotipiEventi.csv"));
            CSVParser parser = CSVFormat.Builder.create(CSVFormat.EXCEL).setHeader().build().parse(reader) ) {
            for (CSVRecord rec : parser) {
                String idSottotipo = rec.get("IdSottotipo");
                String descrizione = rec.get("Descrizione");
                this.metadataMappingProperties.setProperty(METADTA_PREFIX + STATION_METADATA_IDSOTTOTIPOEVENTO + "." + idSottotipo, descrizione);
            }
        } catch (Exception e) {
            LOG.error("Unable to parse sottotipi eventi csv file");
            throw new RuntimeException(e);
        }
    }

    public void execute() {
        long startTime = System.currentTimeMillis();
        try {
            LOG.info("Start MainA22Events");

            // step 1
            // create a Connector instance: this will perform authentication and store the session
            //
            // the session will last 24 hours unless de-authenticated before - however, if a user
            // deauthenticates one session, all sessions of the same user will be de-authenticated;
            // this means each running application neeeds their own username
            A22EventConnector A22Service = setupA22ServiceConnector();

            // step 2
            // fetch and print all events ("eventi/lista/storici") in a certain time range
            LOG.info("step 2: fetch and print all events (\"eventi/lista/storici\")");
            HashMap<String, EventDto> inactiveStations = new HashMap<>();
            try {
                long scanWindowSeconds = Long.parseLong(a22EventsProperties.getProperty("scanWindowSeconds"));
                long lastTimeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(a22EventsProperties.getProperty("lastTimestamp")).getTime() / 1000;

                do {
                    LOG.info("Get all events between {} and {}",
                            Instant.ofEpochSecond(lastTimeStamp).atZone(ZoneId.systemDefault()).toLocalDate(),
                            Instant.ofEpochSecond(lastTimeStamp + scanWindowSeconds).atZone(ZoneId.systemDefault()).toLocalDate());
                    List<A22Event> events = A22Service.getEvents(lastTimeStamp, lastTimeStamp + scanWindowSeconds);
                    LOG.info("got " + events.size() + " events");
                    List<EventDto> eventDtoList = new ArrayList<>();
                    for (A22Event event : events) {
                        EventDto eventDto = getEventDtoFromA22Event(event);
						if (EventDto.isValid(eventDto, false)) {
							if (!inactiveStations.containsKey(eventDto.getUuid())) {
								inactiveStations.put(eventDto.getUuid(), eventDto);
								eventDtoList.add(eventDto);
							}
						} else {
							LOG.warn("The generated eventDto has missing required fields or an invalid interval range");
							LOG.warn("EVENTDTO {}", eventDto);
						}
                    }
                    pusher.addEvents(eventDtoList);
                    lastTimeStamp += scanWindowSeconds;
                } while (lastTimeStamp < System.currentTimeMillis() / 1000);
            } catch (Exception e) {
                LOG.error("step 2 failed, continuing anyway to read de-auth...", e);
            }

            // step 3
            // fetch and print all current events ("eventi/lista/attivi")
            LOG.info("step 3: fetch and print all current events (\"eventi/lista/attivi\")");
            try {
                List<A22Event> events = A22Service.getEvents(null, null);
                LOG.info("got " + events.size() + " events");
                List<EventDto> eventDtoList = new ArrayList<>();
                for (A22Event event : events) {
                    EventDto eventDto = getEventDtoFromA22Event(event);
					if (EventDto.isValid(eventDto, false))
						eventDtoList.add(eventDto);
					else
						LOG.warn("The generated eventDto has missing required fields.");
                }
                pusher.addEvents(eventDtoList);
            } catch (Exception e) {
                LOG.error("step 3 failed, continuing anyway to read de-auth...", e);
            }

            // step 4
            A22Service.close();
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error(Arrays.toString(e.getStackTrace()));
        } finally {
            long stopTime = System.currentTimeMillis();
            LOG.info("elaboration time (millis): " + (stopTime - startTime));
        }
    }

    private A22EventConnector setupA22ServiceConnector() throws IOException {
        return new A22EventConnector(a22ConnectorURL, a22ConnectorUsr, a22ConnectorPwd);
    }

    public EventDto getEventDtoFromA22Event(A22Event event) throws JsonProcessingException {
        EventDto eventDto = new EventDto();

        eventDto.setUuid(generateEventUuid(event), uuidNamespace);
        eventDto.setOrigin(a22EventsProperties.getProperty("origin"));
        eventDto.setCategory(String.format("%s:%s_%s",
			categoryPrefix,
			getMappingStringByPropertyId(STATION_METADATA_IDTIPOEVENTO, event.getIdtipoevento()),
			getMappingStringByPropertyId(STATION_METADATA_IDSOTTOTIPOEVENTO, event.getIdsottotipoevento()
		)));
		eventDto.setEventSeriesUuid(generateEventSeriesUuid(event), uuidNamespace);
		eventDto.setName(Long.toString(event.getId()));

        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        Coordinate coordinateInizio = new Coordinate(event.getLon_inizio(), event.getLat_inizio());
        Coordinate coordinateFine = new Coordinate(event.getLon_fine(), event.getLat_fine());
        Point[] points = new Point[]{geometryFactory.createPoint(coordinateInizio), geometryFactory.createPoint(coordinateFine)};
        MultiPoint multiPoint = new MultiPoint(points, geometryFactory);
        eventDto.setWktGeometry(multiPoint.toText());

        eventDto.setEventStart(event.getData_inizio() * 1000);
        eventDto.setEventEnd(event.getData_fine() == null ? null : event.getData_fine() * 1000);

        eventDto.getMetaData().put(STATION_METADATA_ID, event.getId());
        eventDto.getMetaData().put(STATION_METADATA_FASCIA_ORARIA, event.getFascia_oraria());
        eventDto.getMetaData().put(STATION_METADATA_IDCORSIA, getMappingStringByPropertyId(STATION_METADATA_IDCORSIA, event.getIdcorsia()));
        eventDto.getMetaData().put(STATION_METADATA_IDDIREZIONE, getMappingStringByPropertyId(STATION_METADATA_IDDIREZIONE, event.getIddirezione()));
        eventDto.getMetaData().put(STATION_METADATA_METRO_INIZIO, event.getMetro_inizio());
        eventDto.getMetaData().put(STATION_METADATA_METRO_FINE, event.getMetro_fine());
        eventDto.getMetaData().put(STATION_METADATA_IDTIPOEVENTO, event.getIdtipoevento());
        eventDto.getMetaData().put(STATION_METADATA_IDSOTTOTIPOEVENTO, event.getIdsottotipoevento());

		return eventDto;
    }

    private Map<String, Object> generateEventUuid(A22Event event) {
        Map<String, Object> uuidMap = new HashMap<>();
        uuidMap.put("id", event.getId());
        uuidMap.put("data_inizio", event.getData_inizio());
        uuidMap.put("idtipoevento", event.getIdtipoevento());
        uuidMap.put("idsottotipoevento", event.getIdsottotipoevento());
        uuidMap.put("lat_inizio", event.getLat_inizio());
        uuidMap.put("lon_inizio", event.getLon_inizio());
        uuidMap.put("lat_fine", event.getLat_fine());
        uuidMap.put("lon_fine", event.getLon_fine());
        return uuidMap;
    }

    private Map<String, Object> generateEventSeriesUuid(A22Event event) {
        Map<String, Object> uuidMap = new HashMap<>();
        uuidMap.put("id", event.getId());
        return uuidMap;
    }

    public String getMappingStringByPropertyId(String idProperty, Long id) {
        String defaultValue = metadataMappingProperties.getProperty(METADTA_PREFIX + idProperty + ".*");
        String ret = metadataMappingProperties.getProperty(METADTA_PREFIX + idProperty + "." + id, defaultValue);
        if (ret == null) {
            LOG.warn("Unable to find the following '{}' string: {}", idProperty, id.toString());
            ret = idProperty + ":" + id;
        }
        return ret;
    }

}
