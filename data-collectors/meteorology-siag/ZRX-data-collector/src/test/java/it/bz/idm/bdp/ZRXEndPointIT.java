package it.bz.idm.bdp;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.service.MeteoUtil;

@ContextConfiguration(locations = { "/META-INF/spring/applicationContext.xml" })
public class ZRXEndPointIT extends AbstractJUnit4SpringContextTests{
	
	@Autowired
	private MeteoUtil util;

	@Test
	public void testSyncStations(){
		String meteoDataAsJsonString = util.getMeteoDataAsJsonString();
		assertNotNull(meteoDataAsJsonString);
		List<StationDto> stationsFromJson = util.getStationsFromJson(meteoDataAsJsonString);
		assertNotNull(stationsFromJson);
		assertTrue(!stationsFromJson.isEmpty());
		for (StationDto dto : stationsFromJson){
			assertNotNull(dto.getId());
		}
	}
	@Test
	public void testSyncDataTypes(){
		String meteoDataAsJsonString = util.getMeteoDataAsJsonString();
		assertNotNull(meteoDataAsJsonString);
		List<DataTypeDto> dataTypesFromJson = util.getDataTypesFromJson(meteoDataAsJsonString);
		assertNotNull(dataTypesFromJson);
		assertTrue(!dataTypesFromJson.isEmpty());
		for (DataTypeDto dto : dataTypesFromJson){
			assertNotNull(dto.getName());
		}
	}
	@Test
	public void testGetData(){
		String meteoDataAsJsonString = util.getMeteoDataAsJsonString();
		assertNotNull(meteoDataAsJsonString);
		DataMapDto<RecordDtoImpl> records = util.getRecordsFromJson(meteoDataAsJsonString);
		assertNotNull(records);
		assertTrue(!records.getData().isEmpty());
		for (Map.Entry<String, DataMapDto<RecordDtoImpl>> entry:records.getBranch().entrySet()){
			assertTrue(entry.getValue().getBranch().size()>=0);
			for(Map.Entry<String,DataMapDto<RecordDtoImpl>> typeEntry:entry.getValue().getBranch().entrySet()){
				assertTrue(typeEntry.getValue().getBranch().size()>=0);
			}
		}
	}
}
