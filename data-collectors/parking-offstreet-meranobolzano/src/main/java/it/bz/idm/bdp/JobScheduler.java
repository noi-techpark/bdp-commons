// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.idm.bdp;

import java.io.IOException;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import it.bz.idm.bdp.dto.DataTypeDto;

@Service
public class JobScheduler {

	@Autowired
	private ParkingPusher pusher;

	@PostConstruct
	public void syncDataTypes() {
		try {
			List<DataTypeDto> dataTypeList = ParkingPusher.getDataTypeList();
			pusher.syncDataTypes(dataTypeList);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Scheduled(cron = "${scheduler.slots}")
	public void currentSlots() {
		try {
			pusher.pushData();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Scheduled(cron = "${scheduler.stations}")
	public void parkingStations() throws IOException {
		try {
			pusher.pushParkingMetaData();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// @Scheduled(cron = "${scheduler.job}")
	// public void pushPredictions(){
	// try {
	// pusher.pushPredictionData();
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }

}
