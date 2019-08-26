package it.bz.idm.bdp;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.ProvenanceDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.json.JSONPusher;

@Component
public class TrafficPusher extends JSONPusher {

	@Autowired
	private Environment env;

	public static final String TRAFFIC_SENSOR_IDENTIFIER = "TrafficSensor";

	public static final String ENVIRONMENTSTATION_IDENTIFIER = "EnvironmentStation";

	public static final String METEOSTATION_IDENTIFIER = "MeteoStation";

	@Override
	public String initIntegreenTypology() {
		return TRAFFIC_SENSOR_IDENTIFIER;
	}

	@Override
	public <T> DataMapDto<RecordDtoImpl> mapData(T data) {
		return null;
	}

	@Override
	public ProvenanceDto defineProvenance() {
		return new ProvenanceDto(null, env.getProperty("provenance.name"), env.getProperty("provenance.version"),  env.getProperty("origin"));
	}

}
