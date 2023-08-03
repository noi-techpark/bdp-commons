// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package com.opendatahub.dc.echarging.dzt;

import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientRequestException;

import com.opendatahub.dc.echarging.dzt.DZTClient.Plug;
import com.opendatahub.dc.echarging.dzt.DZTClient.Station;

import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.StationList;

@Service
public class SyncScheduler {
	private static final String ECHARGING_STATION = "EchargingStation";
	private static final String ECHARGING_PLUG = "EChargingPlug";

	private static final Logger LOG = LoggerFactory.getLogger(SyncScheduler.class);

    @Lazy
    @Autowired
    public OdhClient odhClient;

	@Autowired
	public DZTClient dztClient;

    @Scheduled(cron = "${scheduler.job}")
    public void job() throws Exception {
        LOG.info("Job started. Syncing {} and {} stations", ECHARGING_STATION, ECHARGING_PLUG);

		StationList odhStations = new StationList();
		StationList odhPlugs = new StationList();

		List<Station> dztStations = dztClient.getAllStations();

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
			LOG.info("Cron job A successful");
		} catch (WebClientRequestException e) {
			LOG.error("Cron job A failed: Request exception: {}", e.getMessage());
		}
    }
}
