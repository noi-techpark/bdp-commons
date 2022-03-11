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

	private static final int DATA_CHUNK_SIZE = 50;
	private static final int STATION_CHUNK_SIZE = 25;

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

		int chunks = (int) Math.ceil((float) fetchedSations.size() / STATION_CHUNK_SIZE);
		LOG.info(
			"Sync Charger Data: Found {} chargers. Splitting into {} chunks of max. {} each!",
			fetchedSations.size(),
			chunks,
			STATION_CHUNK_SIZE
		);

		for (int i = 0; i < chunks; i++) {
			// We have the following interval boundaries for subList: [from, to)
			int from = STATION_CHUNK_SIZE * i;
			int to = from + STATION_CHUNK_SIZE;
			if (to > fetchedSations.size())
				to = fetchedSations.size();
			List<ChargerDtoV2> stationChunk = fetchedSations.subList(from, to);
			StationList stations = pusher.map2bdp(stationChunk);
			StationList plugs = pusher.mapPlugs2Bdp(stationChunk);
			LOG.info(
				"Sync Stations and Plugs: Pushing {} stations and {} plugs to the writer: Chunk {} of {}",
				stations == null ? 0 : stations.size(),
				plugs == null ? 0 : plugs.size(),
				i+1,
				chunks
			);
			if (stations != null && plugs != null) {
				pusher.syncStations(stations);
				pusher.syncStations("EChargingPlug", plugs);
			}
		}
		LOG.info("Sync Stations and Plugs: Done");
	}


	public void pushChargerDataV2() {
		LOG.info("Sync Charger Data: Fetching from source and parsing");
		List<ChargerDtoV2> stations = retrieverV2.fetchStations();

		int chunks = (int) Math.ceil((float) stations.size() / DATA_CHUNK_SIZE);
		LOG.info(
			"Sync Charger Data: Found {} stations. Splitting into {} chunks of max. {} each!",
			stations.size(),
			chunks,
			DATA_CHUNK_SIZE
		);

		for (int i = 0; i < chunks; i++) {
			// We have the following interval boundaries for subList: [from, to)
			int from = DATA_CHUNK_SIZE * i;
			int to = from + DATA_CHUNK_SIZE;
			if (to > stations.size())
				to = stations.size();
			List<ChargerDtoV2> stationChunk = stations.subList(from, to);
			DataMapDto<RecordDtoImpl> map = pusher.mapData(stationChunk);
			DataMapDto<RecordDtoImpl> plugRec = pusher.mapPlugData2Bdp(stationChunk);
			LOG.info("Sync Charger Data: Pushing to the writer: Chunk {} of {}", i+1, chunks);
			if (map != null && plugRec != null){
				pusher.pushData(map);
				pusher.pushData("EChargingPlug", plugRec);
			}
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
