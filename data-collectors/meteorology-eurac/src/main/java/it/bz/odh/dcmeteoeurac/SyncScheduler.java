package it.bz.odh.dcmeteoeurac;

import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientRequestException;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.SimpleRecordDto;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.StationList;
import it.bz.odh.dcmeteoeurac.dto.MetadataDto;

@Service
public class SyncScheduler {

	private static final Logger LOG = LoggerFactory.getLogger(SyncScheduler.class);

    private static final String STATION_ID_PREFIX = "EURAC_";
	private static final String DATATYPE_ID = "Temperature";
	
    @Autowired
    private Environment env;

	@Value("${odh_client.period}")
    private Integer period;

    @Lazy
    @Autowired
    private OdhClient odhClient;

    /**
     * Scheduled job A: Example to sync stations and data types
     * @throws IOException 
     * @throws ClientProtocolException 
     */
    @Scheduled(cron = "${scheduler.job_a}")
    public void syncJobA() throws IOException {
        LOG.info("Cron job A started: Sync Stations with type {} and data types", odhClient.getIntegreenTypology());
		
		ObjectMapper objectMapper = new ObjectMapper();
		
		String stationsUrl = env.getProperty("endpoint.stations.url");
		
        HttpClient client = HttpClientBuilder.create().build();    
        HttpResponse response = client.execute(new HttpGet(stationsUrl));
        HttpEntity entity = response.getEntity();
        String responseString = EntityUtils.toString(entity, "UTF-8");
        MetadataDto[] euracStations = objectMapper.readValue(responseString, MetadataDto[].class);

        StationList odhStationList = new StationList();
		for (MetadataDto s : euracStations) {
	        StationDto station = new StationDto(
	            STATION_ID_PREFIX + s.getId(),
                s.getName(),
                s.getLat(),
                s.getLon()
            );		
	        
	        station.setOrigin(odhClient.getProvenance().getLineage());
	        odhStationList.add(station);
		}

		try {
			odhClient.syncStations(odhStationList);
			LOG.info("Cron job A successful");
		} catch (WebClientRequestException e) {
			LOG.error("Cron job A failed: Request exception: {}", e.getMessage());
		}
    }

	/**
     * Scheduled job B: Example on how to send measurements
     */
    @Scheduled(cron = "${scheduler.job_b}")
    public void syncJobB() {

		
    }

}
