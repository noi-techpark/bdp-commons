package it.bz.noi.a22.vms;

import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.json.JSONPusher;

public class A22SignJSONPusher extends JSONPusher
{

	public <T> DataMapDto<RecordDtoImpl> mapData(T arg0)
	{
		throw new IllegalStateException("it is used by who?");
	}

	@Override
	public String initIntegreenTypology()
	{
		return "a22-sign";
	}

}