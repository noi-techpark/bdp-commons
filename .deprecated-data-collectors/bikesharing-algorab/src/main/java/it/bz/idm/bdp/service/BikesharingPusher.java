package it.bz.idm.bdp.service;

import org.springframework.stereotype.Component;

import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.ProvenanceDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.json.JSONPusher;

@Component
public class BikesharingPusher extends JSONPusher {

	@Override
	public String initIntegreenTypology() {
		return "BikesharingStation";
	}

	@Override
	public <T> DataMapDto<RecordDtoImpl> mapData(T arg0) {
		return null;
	}

	@Override
	public ProvenanceDto defineProvenance() {
		// TODO Auto-generated method stub
		return null;
	}

}