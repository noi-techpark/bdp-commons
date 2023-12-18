// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.idm.bdp;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.StationList;

@Component
public class JobScheduler {

	@Autowired
	private DataParser parser;

	@Autowired
	private HistoryRetriever historyRetriever;

	@Autowired
	private TrafficPusher pusher;

	public void syncStations(){
		Map<String, StationList> stations = parser.retrieveStations();
		pusher.syncStations("Meteostation",stations.get("meteo"));
		pusher.syncStations(stations.get("traffic"));
		pusher.syncStations("EnvironmentStation",stations.get("environment"));
	}
	public void syncDataTypes(){
		List<DataTypeDto> types = parser.retrieveDataTypes();
		pusher.syncDataTypes(types);
	}
	public void pushRecords(){
		Map<String, DataMapDto<RecordDtoImpl>> liveData = parser.retrieveLiveData();
		pusher.pushData(liveData.get(TrafficPusher.TRAFFIC_SENSOR_IDENTIFIER));
		pusher.pushData(TrafficPusher.METEOSTATION_IDENTIFIER,liveData.get(TrafficPusher.METEOSTATION_IDENTIFIER));
		pusher.pushData(TrafficPusher.ENVIRONMENTSTATION_IDENTIFIER,liveData.get(TrafficPusher.ENVIRONMENTSTATION_IDENTIFIER));
	}

	public void getHistory(){
		historyRetriever.getHistory(null);
	}
	public void getLatestHistory() {
		historyRetriever.getLatestHistory();
	}
}
