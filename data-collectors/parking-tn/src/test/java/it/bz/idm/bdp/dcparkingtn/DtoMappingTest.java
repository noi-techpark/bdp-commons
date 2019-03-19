package it.bz.idm.bdp.dcparkingtn;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import it.bz.idm.bdp.dcparkingtn.dto.ParkingAreaServiceDto;
import it.bz.idm.bdp.dcparkingtn.dto.ParkingTnDto;
import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.StationDto;

@ContextConfiguration(locations = { "classpath:/META-INF/spring/applicationContext.xml" })
public class DtoMappingTest extends AbstractJUnit4SpringContextTests {

	@Autowired
	private ParkingTnDataPusher pusher;
	List<ParkingTnDto> data;

	@Before
	public void setup() {
		data = new ArrayList<>();
		String municipality ="Bozen";
		ParkingAreaServiceDto pA1 = new ParkingAreaServiceDto(),pA2 = new ParkingAreaServiceDto(),pA3 = new ParkingAreaServiceDto();
		StationDto s1 = new StationDto("hello",null,null,null),s2 = new StationDto("hello2",null,null,null),s3 = new StationDto("hello3",null,null,null);
		
		pA1.setAdditionalProperty("cupid",1);
		pA1.setName("Fero");
		pA1.setDescription("Worst parking ever");
		pA1.setMonitored(true);
		pA1.setSlotsTotal(500);
		pA1.setSlotsAvailable(5);
		pA1.setPosition(Arrays.asList(new Double[]{3d,2.222222222d}));
		
		pA2.setDescription("Best parking ever");
		pA2.setName("Hero");
		pA2.setMonitored(true);
		pA2.setSlotsTotal(500);
		pA2.setSlotsAvailable(5);
		pA2.setPosition(Arrays.asList(new Double[]{-23.3d,2.2200022d}));
		
		pA3.setAdditionalProperty("Go","sea");
		pA3.setDescription("Worst parking ever");
		pA3.setMonitored(false);
		pA3.setSlotsTotal(50);
		pA3.setSlotsAvailable(144);
		pA3.setPosition(Arrays.asList(new Double[]{8.468684d,5447.222222222d}));
		
		ParkingTnDto p1 = new ParkingTnDto(pA1, s1, municipality),p2= new ParkingTnDto(pA2, s2, municipality), p3 = new ParkingTnDto(pA3, s3, municipality);
		data.add(p1 );
		data.add(p2);
		data.add(p3);
	}
	
	@Test
	public void testRecordsMapping() {
		DataMapDto<RecordDtoImpl> mappedData = pusher.mapData(data);
		assertNotNull(mappedData);
		assertFalse(mappedData.getBranch().entrySet().isEmpty());
		for (Entry<String, DataMapDto<RecordDtoImpl>> entry :mappedData.getBranch().entrySet()) {
			for (Entry<String, DataMapDto<RecordDtoImpl>> records :entry.getValue().getBranch().entrySet()) { 
				assertEquals(ParkingTnDataPusher.PARKING_TYPE_IDENTIFIER, records.getKey());
				DataMapDto<RecordDtoImpl> value = records.getValue();
				assertNotNull(value);
				assertFalse(value.getData().isEmpty());
			}
		}
	}

}
