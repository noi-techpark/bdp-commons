package it.bz.odh.spreadsheets.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.ProvenanceDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.json.NonBlockingJSONPusher;

@Lazy
@Component
public class ODHClient extends NonBlockingJSONPusher{

	@Value(value="${stationtype}")
	private String stationtype;


	@Value("${provenance.name}")
	private String provenanceName;
	@Value("${provenance.version}")
	private String provenanceVersion;
	
    @Value("${spreadsheetId}")
    private String origin;

	@Override
	public <T> DataMapDto<RecordDtoImpl> mapData(T data) {
		return null;
	}

	@Override
	public String initIntegreenTypology() {
		return stationtype;
	}

	@Override
	public ProvenanceDto defineProvenance() {
		return new ProvenanceDto(null, provenanceName, provenanceVersion, origin);
	}

}
