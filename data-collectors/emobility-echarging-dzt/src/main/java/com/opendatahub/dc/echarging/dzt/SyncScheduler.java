// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package com.opendatahub.dc.echarging.dzt;

import org.slf4j.LoggerFactory;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientRequestException;

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

		StationList stationList = new StationList();
		StationList plugList = new StationList();

		dztClient.getTheThings();

		// get most recent station update
		// get all stations newer than timestamp
		// for each station in list
		// get station detail
		// populate station object
		// populate plug objects

		StationDto station = new StationDto();

		station.setMetaData(null);	

		station.setOrigin(odhClient.getProvenance().getLineage());

		stationList.add(station);

		// 3) Send it to the Open Data Hub INBOUND API (writer)
		try {
			odhClient.syncStations(ECHARGING_STATION, stationList);
			odhClient.syncStations(ECHARGING_PLUG, plugList);
			LOG.info("Cron job A successful");
		} catch (WebClientRequestException e) {
			LOG.error("Cron job A failed: Request exception: {}", e.getMessage());
		}
    }
}
