package it.bz.idm.bdp.rwis;
import org.springframework.stereotype.Component;

import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.ProvenanceDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.json.JSONPusher;
import org.springframework.beans.factory.annotation.Value;


@Component
public class CleanroadsPusher extends JSONPusher {

	@Value("${stationtype}")
    private String stationtype;

    @Value("${provenance.name}")
    private String provenanceName;

    @Value("${provenance.version}")
    private String provenanceVersion;

    @Value("${origin}")
    private String origin;
	
	@Override
	public String initIntegreenTypology() {
		return stationtype;
	}

	@Override
	public <T> DataMapDto<RecordDtoImpl> mapData(T arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ProvenanceDto defineProvenance() {
		return new ProvenanceDto(null, provenanceName, provenanceVersion, origin);
	}
}
