package it.bz.noi.a22elaborations;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.ProvenanceDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.json.JSONPusher;

public class A22TrafficJSONPusher extends JSONPusher
{

	private String origin;
	private String provenanceVersion;
	private String provenanceName;

	public A22TrafficJSONPusher() {
		super.init();
	}

	public <T> DataMapDto<RecordDtoImpl> mapData(T arg0)
	{
		throw new IllegalStateException("it is used by who?");
	}

	@Override
	public String initIntegreenTypology()
	{
		return "TrafficSensor";
	}

	@Override
	public ProvenanceDto defineProvenance() {
		InputStream in = Utility.class.getResourceAsStream("elaborations.properties");
		Properties prop = new Properties();
		try {
			prop.load(in);
			origin = prop.getProperty("origin");
			provenanceName = prop.getProperty("provenance_name");
			provenanceVersion =prop.getProperty("provenance_version");
		} catch (IOException e) {
			e.printStackTrace();
		}
	return new ProvenanceDto(null, provenanceName, provenanceVersion, origin);
	}

}