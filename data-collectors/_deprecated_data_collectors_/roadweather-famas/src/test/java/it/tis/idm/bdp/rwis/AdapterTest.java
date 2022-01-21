package it.tis.idm.bdp.rwis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import cleanroadsdatatype.cleanroadswebservices.GetDataTypesResult;
import cleanroadsdatatype.cleanroadswebservices.GetMetadataStationResult;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.rwis.StreetWeatherAdapter;

@ContextConfiguration(locations = { "classpath:/META-INF/spring/applicationContext.xml" })
public class AdapterTest extends AbstractJUnit4SpringContextTests{

	@Autowired
	private StreetWeatherAdapter adapter;
	
	@Test
	public void testConvertStations(){
		GetMetadataStationResult mres = new GetMetadataStationResult();
		mres.setId(32);
		mres.setNome("Hellp");
		StationDto converted = adapter.convert2StationDto(mres);
		assertTrue(converted instanceof StationDto);
		StationDto dto = (StationDto) converted;
		assertNotNull(dto.getId());
		assertNotNull(dto.getName());
	}
	
	@Test
	public void testDataTypes(){
		GetDataTypesResult.XmlDataType type = new GetDataTypesResult.XmlDataType();
		type.setId(12);
		type.setUm("Degree");
		type.setDescr("");
		List<GetDataTypesResult.XmlDataType> types = new ArrayList<GetDataTypesResult.XmlDataType>();
		types.add(type);
		List<? extends DataTypeDto> converted = adapter.convert2DatatypeDtos(types);
		assertNotNull(converted);
		assertFalse(converted.isEmpty());
		for (DataTypeDto dto : converted){
			assertNotNull(dto.getName());
			assertFalse(dto.getName().isEmpty());
			assertEquals("wind-speed",dto.getName());
			assertNotNull(dto.getUnit());
			assertFalse(dto.getUnit().isEmpty());
			assertEquals("Degree", dto.getUnit());
		}
	}
	
	
}
