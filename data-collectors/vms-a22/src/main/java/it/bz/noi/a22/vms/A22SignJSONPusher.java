package it.bz.noi.a22.vms;

import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.ProvenanceDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.json.JSONPusher;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Properties;

public class A22SignJSONPusher extends JSONPusher
{

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
		return new ProvenanceDto(null, "dc-vms-a22", "0.1.0-SNAPSHOT", "");
	}

}
