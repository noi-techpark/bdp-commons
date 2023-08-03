// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package com.opendatahub.dc.echarging.dzt;

import org.slf4j.LoggerFactory;

import java.util.List;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientRequestException;

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
        LOG.info("Job started. Syncing {} and {} stations", ECHARGING_STATION, ECHARGING_STATION);

		StationList odhStations = new StationList();
		StationList odhPlugs = new StationList();

		List<Station> dztStations = dztClient.getAllStations();

		for (Station dztStation : dztStations){
			StationDto station = new StationDto();
			station.setMetaData(null);	
			station.setOrigin(odhClient.getProvenance().getLineage());
			odhStations.add(station);
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
