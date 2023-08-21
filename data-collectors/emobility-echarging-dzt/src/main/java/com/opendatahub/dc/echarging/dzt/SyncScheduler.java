// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package com.opendatahub.dc.echarging.dzt;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientRequestException;

import com.opendatahub.dc.echarging.dzt.DZTClient.Plug;
import com.opendatahub.dc.echarging.dzt.DZTClient.Station;

import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.StationList;

@Service
public class SyncScheduler {
	public static final String ECHARGING_STATION = "EchargingStation";
	public static final String ECHARGING_PLUG = "EChargingPlug";

	private static final Logger log = LoggerFactory.getLogger(SyncScheduler.class);

    @Lazy
    @Autowired
    public OdhWriterClient odhClient;

	@Autowired
	public DZTClient dztClient;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@Value("${startDate}")
	public LocalDateTime lastSync;

    @Scheduled(cron = "${scheduler.job}")
    public void job() throws Exception {
        log.info("Job started. Syncing {} and {} stations", ECHARGING_STATION, ECHARGING_PLUG);

		StationList odhStations = new StationList();
		StationList odhPlugs = new StationList();

		// TODO: request max metadata.syncDate from ninja API to determine last good sync date, use that as starting point
		LocalDateTime currentSyncStart = LocalDateTime.now();
		
		log.info("DZT starting date: {}", lastSync.toString());
		List<Station> dztStations = dztClient.getAllStations(lastSync);

		for (Station dztStation : dztStations){
			StationDto station = new StationDto();
			station.setId(dztStation.id);
			station.setName(dztStation.name);
			station.setLatitude(dztStation.latitude);
			station.setLongitude(dztStation.longitude);
			
			Map<String, Object> stationMeta = new HashMap<>();
			stationMeta.put("addressCountry", dztStation.addressCountry);
			stationMeta.put("addressLocality", dztStation.addressLocality);
			stationMeta.put("postalCode", dztStation.addressPostalCode);
			stationMeta.put("streetAddress", dztStation.addressStreet);
			stationMeta.put("state", "ACTIVE");
			stationMeta.put("accessType", "UNKNOWN");
			stationMeta.put("capacity", dztStation.plugs.size());
			stationMeta.put("provider", dztStation.publisher);
			stationMeta.put("providerUrl", dztStation.publisherUrl);
			stationMeta.put("syncDate", currentSyncStart.format(DateTimeFormatter.ISO_DATE_TIME));

			station.setMetaData(stationMeta);	
			station.setOrigin(odhClient.getProvenance().getLineage());
			station.setStationType(ECHARGING_STATION);
			odhStations.add(station);

			int plugId = 0;
			for (Plug dztPlug : dztStation.plugs) {
				plugId++;
				StationDto plug = new StationDto();
				plug.setId(String.format("%s:%s", station.getId(), plugId));
				plug.setName(Integer.toString(plugId));
				plug.setLatitude(station.getLatitude());
				plug.setLongitude(station.getLongitude());
				
				Map<String, Object> outlet = new HashMap<>();
				outlet.put("id", String.format("%s:1", plug.getId()));
				outlet.put("outletTypeCode", dztPlug.socket); //TODO: map this to odh socket type
				outlet.put("maxPower", dztPlug.powerValue);
				outlet.put("powerUnit", dztPlug.powerUnitCode);
				outlet.put("outletType", dztPlug.socket);
				outlet.put("name", dztPlug.name);

				plug.setMetaData(Map.of("outlet", outlet));
				plug.setOrigin(station.getOrigin());
				plug.setStationType(ECHARGING_PLUG);
				odhPlugs.add(plug);
			}
		}

		// 3) Send it to the Open Data Hub INBOUND API (writer)
		// Don't deactivated stations not synced, we only ever get the last modified ones
		try {
			final int chunksize = 1000;
			odhClient.syncStations(ECHARGING_STATION, odhStations, chunksize, false, true);
			odhClient.syncStations(ECHARGING_PLUG, odhPlugs, chunksize, false, true);
			log.info("Cron job successful");
			lastSync = currentSyncStart;
		} catch (WebClientRequestException e) {
			log.error("Cron job failed: Request exception: {}", e.getMessage());
		}
    }
}
