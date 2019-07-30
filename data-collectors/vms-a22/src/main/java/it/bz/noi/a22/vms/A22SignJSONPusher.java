package it.bz.noi.a22.vms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.ProvenanceDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.json.JSONPusher;

@Component
public class A22SignJSONPusher extends JSONPusher
{

	@Autowired
	private Environment env;

	private String stationtype;
	@Override
	public <T> DataMapDto<RecordDtoImpl> mapData(T arg0)
	{
		throw new IllegalStateException("it is used by who?");
	}

	@Override
	public String initIntegreenTypology()
	{
		if(stationtype == null)
		{
			A22Properties prop = new A22Properties("a22sign.properties");

			stationtype = prop.getProperty("stationtype");
		}

		return stationtype;
	}
	@Override
	public ProvenanceDto defineProvenance() {
		return new ProvenanceDto(null, env.getProperty("provenance.name"), env.getProperty("provenance.version"),  env.getProperty("origin"));
	}

}
