package it.bz.noi.a22.vms;

import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.json.JSONPusher;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Properties;

public class A22SignJSONPusher extends JSONPusher
{

	private String stationtype;

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

}