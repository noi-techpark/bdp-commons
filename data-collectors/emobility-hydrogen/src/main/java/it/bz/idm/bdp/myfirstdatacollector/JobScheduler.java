package it.bz.idm.bdp.myfirstdatacollector;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.StationList;
import it.bz.idm.bdp.myfirstdatacollector.dto.CoolDataDto;

/**
 * Cronjob configuration can be found under src/main/resources/META-INF/spring/applicationContext.xml
 * XXX Do not forget to configure it!
 */
@Component
public class JobScheduler {

	private static final Logger LOG = LogManager.getLogger(JobScheduler.class.getName());

	@Autowired
	private Environment env;

	@Autowired
	private DataPusher pusher;

	@Autowired
	private DataRetrieval retrieval;

	/** JOB 1 */
	public void pushStations() throws Exception {
		List<CoolDataDto> data = retrieval.fetchData();

		StationList stationList = new StationList();
		for (CoolDataDto dto : data) {
			StationDto station = new StationDto();
			station.setId(dto.getStation());
			station.setName("Cool non-unique name for ID " + dto.getStation());
			station.setStationType(env.getProperty("station.type"));
			station.setOrigin(env.getProperty("origin")); // The source of our data set
			stationList.add(station);
		}

		try {
			pusher.syncStations(stationList);
		} catch (HttpClientErrorException e) {
			System.out.println(pusher + " - " + e + " - " + e.getResponseBodyAsString());
		}
	}

	/** JOB 2 */
	public void pushDataTypes() throws Exception {
		List<CoolDataDto> data = retrieval.fetchData();

		List<DataTypeDto> dataTypeList = new ArrayList<DataTypeDto>();
		for (CoolDataDto dto : data) {
			DataTypeDto type = new DataTypeDto();
			type.setName(dto.getName());
			type.setPeriod(env.getProperty("period", Integer.class));
			type.setUnit(dto.getUnit());
			dataTypeList.add(type);
		}

		try {
			pusher.syncDataTypes(dataTypeList);
		} catch (HttpClientErrorException e) {
			System.out.println(pusher + " - " + e + " - " + e.getResponseBodyAsString());
		}
	}

	/** JOB 3 */
	public void pushData() throws Exception {
		List<CoolDataDto> data = retrieval.fetchData();

		try {
			pusher.mapData(data);
			pusher.pushData();
		} catch (Exception e) {
			System.out.println(pusher + " - " + e);
			//LOG.error("Processing of failed: {}.", e);
			//throw e;
		}
	}
}