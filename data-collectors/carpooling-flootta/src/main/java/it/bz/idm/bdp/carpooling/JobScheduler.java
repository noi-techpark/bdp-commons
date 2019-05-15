package it.bz.idm.bdp.carpooling;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.bz.idm.bdp.carpooling.dto.generated.JServices;
import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;

@Component
public class JobScheduler {

	@Autowired
	private CarpoolingPusher pusher;

	@Autowired
	private DataRetriever retriever;

	public void syncStations(){
		pusher.syncStations(retriever.getHubIds());
	}

	public void syncUser(){
		pusher.syncStations("CarpoolingUser",retriever.getUsers());
	}
	public void pushOriginData(){
		JServices response = retriever.getCurrentStats();
		DataMapDto<RecordDtoImpl> parsedData = pusher.mapData(response);
		pusher.pushData("CarpoolingService",parsedData);
	}
	public void syncDataTypes(){
		pusher.syncDataTypes(retriever.getDataTypes());
	}
	public void createRootStation(){
		pusher.syncStations("CarpoolingService",retriever.generateOriginStation());
	}

}
