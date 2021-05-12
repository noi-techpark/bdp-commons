package it.bz.idm.bdp;

import java.util.List;

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
	private BikeCountPusher pusher;

	public void syncStations(){
		StationList stations = parser.retrieveStations();
		pusher.syncStations(stations);

	}
	public void syncDataTypes(){
		List<DataTypeDto> types = parser.retrieveDataTypes();
		pusher.syncDataTypes(types);
	}
	public void pushRecords(){
		DataMapDto<RecordDtoImpl> liveData = parser.retrieveLiveData();
		pusher.pushData(liveData);
	}

	public void getHistory(){
		historyRetriever.getHistory(null);
	}
	public void getLatestHistory() {
		historyRetriever.getLatestHistory();
	}
}
