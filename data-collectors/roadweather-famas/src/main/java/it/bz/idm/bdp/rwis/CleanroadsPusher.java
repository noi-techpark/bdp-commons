package it.bz.idm.bdp.rwis;
import org.springframework.stereotype.Component;

import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.ProvenanceDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.json.JSONPusher;


@Component
public class CleanroadsPusher extends JSONPusher{
	
	@Override
	public String initIntegreenTypology() {
		return "RWISstation";
	}

	@Override
	public <T> DataMapDto<RecordDtoImpl> mapData(T arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ProvenanceDto defineProvenance() {
		// TODO Auto-generated method stub
		return null;
	}
}
