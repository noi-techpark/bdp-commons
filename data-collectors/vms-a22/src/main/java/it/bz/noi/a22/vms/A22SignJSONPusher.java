package it.bz.noi.a22.vms;

import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.ProvenanceDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.json.JSONPusher;

public class A22SignJSONPusher extends JSONPusher
{
	private String stationtype;
	private String origin;
	private String provenanceVersion;
	private String provenanceName;
	
	public A22SignJSONPusher() {
		super.init();
	}
	
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
			provenanceName = prop.getProperty("provenance.name");
			provenanceVersion = prop.getProperty("provenance.version");
			
		}

		return stationtype;
	}
	@Override
	public ProvenanceDto defineProvenance() {
		return new ProvenanceDto(null, provenanceName, provenanceVersion,  origin);
	}

}
