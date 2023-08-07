// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package com.opendatahub.dc.echarging.dzt;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
		
		log.info("DZT starting date: {}", lastSync.toString());
		List<Station> dztStations = dztClient.getAllStations(lastSync);

		for (Station dztStation : dztStations){
			StationDto station = new StationDto();
			station.setId(dztStation.id);
			station.setName(dztStation.name);
			station.setLatitude(dztStation.latitude);
			station.setLongitude(dztStation.longitude);
			Map<String, Object> stationMeta = Map.of(
				"addressCountry", dztStation.addressCountry,
				"addressLocality", dztStation.addressLocality,
				"postalCode", dztStation.addressPostalCode,
				"streetAddress", dztStation.addressStreet,
				"state", "ACTIVE",
				"accessType", "UNKNOWN",
				"capacity", dztStation.plugs.size(),
				"provider", dztStation.publisher,
				"providerUrl", dztStation.publisherUrl
			);
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
				Map<String, Object> plugMeta = Map.of(
					"outlet", Map.of(
						"id", String.format("%s:1", plug.getId()),
						"outletTypeCode", dztPlug.socket, // TODO: map this to ODH type
						"maxPower", dztPlug.powerValue,
						"powerUnit", dztPlug.powerUnitCode,
						"outletType", dztPlug.socket,
						"name", dztPlug.name
					)
				);
				plug.setMetaData(plugMeta);
				plug.setOrigin(station.getOrigin());
				plug.setStationType(ECHARGING_PLUG);
				odhPlugs.add(plug);
			}
		}

		// 3) Send it to the Open Data Hub INBOUND API (writer)
		try {
			odhClient.syncStations(ECHARGING_STATION, odhStations);
			odhClient.syncStations(ECHARGING_PLUG, odhPlugs);
			log.info("Cron job successful");
		} catch (WebClientRequestException e) {
			log.error("Cron job failed: Request exception: {}", e.getMessage());
		}
    }
}
