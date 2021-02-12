package it.bz.noi.a22.vms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.ProvenanceDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.json.NonBlockingJSONPusher;

@Service
public class A22SignJSONPusher extends NonBlockingJSONPusher
{
	private String stationtype;
	private String origin;
	
	@Autowired
	private Environment env;
	
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
			origin = prop.getProperty("origin");
		}

		return stationtype;
	}
	@Override
	public ProvenanceDto defineProvenance() {
		return new ProvenanceDto(null, env.getProperty("provenance_name"), env.getProperty("provenance_version"),  origin);
	}

}
