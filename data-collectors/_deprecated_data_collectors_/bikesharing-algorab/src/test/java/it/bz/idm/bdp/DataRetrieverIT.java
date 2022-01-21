package it.bz.idm.bdp;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.service.DataRetriever;

@ContextConfiguration(locations = { "classpath:/META-INF/spring/applicationContext.xml"})
@PropertySource({"classpath:/META-INF/spring/types.properties","classpath:/META-INF/spring/application.properties"})
public class DataRetrieverIT extends AbstractJUnit4SpringContextTests{

	@Autowired
	private DataRetriever retriever;
	
	@Test
	public void testSyncStations() throws JsonParseException, JsonMappingException, IllegalStateException, IOException{
		List<StationDto> retrieveStations = retriever.retrieveStations();
		assertNotNull(retrieveStations);
		
	}
	@Test
	public void testSyncDataTypes() throws JsonParseException, JsonMappingException, IllegalStateException, IOException{
		List<DataTypeDto> retrieveDataTypes = retriever.retrieveDataTypes();
		assertNotNull(retrieveDataTypes);
	}
	@Test
	public void testSyncBikes() throws JsonParseException, JsonMappingException, IllegalStateException, IOException{
		List<StationDto> retrieveBicycles = retriever.retrieveBicycles();
		assertNotNull(retrieveBicycles);
		
	}
	@Test
	public void testStationData() throws JsonParseException, JsonMappingException, IllegalStateException, IOException{
        Map<String, Integer> stationData = retriever.retrieveStationData("1");
        assertNotNull(stationData);
		
	}
	@Test
	public void testRetrieveStationData() throws JsonParseException, JsonMappingException, IllegalStateException, IOException, ParseException{
		 DataMapDto<RecordDtoImpl> data = retriever.retrieveCurrentState();
	     assertNotNull(data);
	}
	@Test
	public void testRetrieveBicyclesData() throws JsonParseException, JsonMappingException, IllegalStateException, IOException, ParseException{
		 DataMapDto<RecordDtoImpl> data = retriever.retrieveBicyclesData();
	     assertNotNull(data);
	}


}
