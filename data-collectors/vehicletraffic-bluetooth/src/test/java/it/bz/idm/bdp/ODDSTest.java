package it.bz.idm.bdp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.OddsRecordDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.SimpleRecordDto;
import it.bz.idm.bdp.util.BluetoothMappingUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/META-INF/spring/applicationContext*.xml" })
@WebAppConfiguration
public class ODDSTest {

	@Autowired
	private BluetoothMappingUtil util;

	private List<OddsRecordDto> records = new ArrayList<OddsRecordDto>();

	@Before
	public void setup(){
		OddsRecordDto record = new OddsRecordDto();
		record.setGathered_on(new Date());
		record.setMac("FINTAMAC");
		record.setStationcode("proma");
		records.add(record);

		OddsRecordDto record2 = new OddsRecordDto();
		record2.setMac("FINTAMAC2");
		record2.setStationcode("promabasd");
		records.add(record2);

		OddsRecordDto record3 = new OddsRecordDto();
		record3.setGathered_on(new Date());
		record3.setStationcode("psadroma");
		records.add(record3);

	}

	@Test
	public void testRemoveCorruptedRecords(){
		OddsRecordDto.removeCorruptedData(records);
		assertEquals(1,records.size());
		assertFalse(records.isEmpty());
	}

	@Test
	public void testDataCast(){
		OddsRecordDto.removeCorruptedData(records);
		DataMapDto<RecordDtoImpl> parsedData = util.mapData(records);
		assertNotNull(parsedData);
		for (Map.Entry<String, DataMapDto<RecordDtoImpl>> entry : parsedData.getBranch().entrySet()){

			Map<String, DataMapDto<RecordDtoImpl>> branch = entry.getValue().getBranch();
			assertFalse(branch.isEmpty());
			for (Map.Entry<String, DataMapDto<RecordDtoImpl>> records : branch.entrySet()){
				List<? extends RecordDtoImpl> data = records.getValue().getData();
				assertFalse(data.isEmpty());
				assertTrue(data.get(0) instanceof SimpleRecordDto);
			}
		}
	}

}
