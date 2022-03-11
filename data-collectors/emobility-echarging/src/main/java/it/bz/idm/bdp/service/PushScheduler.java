package it.bz.idm.bdp.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.StationList;
import it.bz.idm.bdp.service.dto.ChargerDtoV2;

@Service
public class PushScheduler {

	private static final Logger LOG = LoggerFactory.getLogger(PushScheduler.class);

	@Autowired
	private DataRetrieverAPIV2 retrieverV2;

	@Autowired
	private ChargePusher pusher;

	/**
	 * Some of these sync scheduled jobs take longer then their re-scheduling,
	 * so the former call fails or overwrites data that has been half-synced.
	 * To avoid this we use a single sync call, and put just a single job per
	 * scheduler. That is the job will not trigger, if it is still running.
	 */
	public void syncAll() {
		syncDataTypes();
		syncStationsV2();
		pushChargerDataV2();
	}

	public void syncStationsV2() {
		LOG.info("Sync Stations and Plugs: Fetching from source and parsing");
		List<ChargerDtoV2> fetchedSations = retrieverV2.fetchStations();
		StationList stations = pusher.map2bdp(fetchedSations);
		StationList plugs = pusher.mapPlugs2Bdp(fetchedSations);
		LOG.info(
			"Sync Stations and Plugs: {} stations and {} plugs found. Pushing to the writer.",
			stations == null ? 0 : stations.size(),
			plugs == null ? 0 : plugs.size()
		);
		if (stations != null && plugs != null) {
			pusher.syncStations(stations);
			pusher.syncStations("EChargingPlug", plugs);
		}
		LOG.info("Sync Stations and Plugs: Done");
	}
	public void pushChargerDataV2() {
		LOG.info("Sync Charger Data: Fetching from source and parsing");
		List<ChargerDtoV2> stations = retrieverV2.fetchStations();
		DataMapDto<RecordDtoImpl> map = pusher.mapData(stations);
		DataMapDto<RecordDtoImpl> plugRec = pusher.mapPlugData2Bdp(stations);
		LOG.info("Sync Charger Data: Pushing to the writer.");
		if (map != null && plugRec != null){
			pusher.pushData(map);
			pusher.pushData("EChargingPlug",plugRec);
		}
		LOG.info("Sync Charger Data: Fetching from source and parsing: Done");
	}
	public void syncDataTypes() {
		List<DataTypeDto> types = pusher.getDataTypes();
		if (types != null){
			pusher.syncDataTypes("EChargingPlug",types);
		}
		LOG.info("Sync Data Types: DONE!");
	}
}
