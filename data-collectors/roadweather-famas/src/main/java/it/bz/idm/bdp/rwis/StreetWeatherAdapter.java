package it.bz.idm.bdp.rwis;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import cleanroadsdatatype.cleanroadswebservices.GetDataTypesResult;
import cleanroadsdatatype.cleanroadswebservices.GetDataTypesResult.XmlDataType;
import cleanroadsdatatype.cleanroadswebservices.GetMetadataStationResult;
import it.bz.idm.bdp.dto.BDPAdapter;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.StationDto;

@Component
@PropertySource("classpath:/META-INF/spring/types.properties")
public class StreetWeatherAdapter implements BDPAdapter{

	private static final String STATION_NAMESPACE = "rwis";
	private static final String DATA_ORIGIN = "InfoMobility";

	@Autowired
	private Environment env;

	@Override
	public StationDto convert2StationDto(Object station) {
		if (station instanceof GetMetadataStationResult){
			GetMetadataStationResult mres = (GetMetadataStationResult) station;
			StationDto dto = new StationDto(STATION_NAMESPACE + mres.getId(), mres.getNome(), mres.getLatit(), mres.getLongit());
			dto.setOrigin(DATA_ORIGIN);
			return dto;
		}
		throw new IllegalStateException("Pls use Object of type "+GetMetadataStationResult.class.getName());
	}

	@Override
	public List<DataTypeDto> convert2DatatypeDtos(List<? extends Object> types) {
		List<DataTypeDto> dtos = new ArrayList<DataTypeDto>();
		for (int i = 0; i < types.size(); i++) {
			if (types.get(i) instanceof GetDataTypesResult.XmlDataType){
				XmlDataType t = (XmlDataType) types.get(i);
				String descr = env.getProperty(String.valueOf(t.getId()));
				if (descr == null)
					throw new IllegalStateException("New undocumented type in cleanroads retriever");
				DataTypeDto dto = new DataTypeDto(descr, t.getUm(), "acquisition interval = " + t.getAcqInterv(), null);
				dtos.add(dto);
			}
		}
		return dtos;
	}
}
