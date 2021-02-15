package it.bz.idm.bdp;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.SimpleRecordDto;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/spring/applicationContext*.xml" })
public class ParsingTestIT {

	@Test
	public void testEmptyMapRemoval() {
		Map<String, DataMapDto<RecordDtoImpl>> map = new HashMap<>();
		DataMapDto<RecordDtoImpl> datamap = new DataMapDto<>(),typeMaps1 = new DataMapDto<>(),typeMaps2 = new DataMapDto<>(),recordMap1s1t1 = new DataMapDto<>(),emptyDataMap= new DataMapDto<>();
		List<RecordDtoImpl> records = new ArrayList<>();
		records.add(new SimpleRecordDto(1332143312l, 32.));
		recordMap1s1t1.setData(records );
		typeMaps1.getBranch().put("ozone", recordMap1s1t1);
		typeMaps1.getBranch().put("hey", emptyDataMap);
		datamap.getBranch().put("station1", typeMaps1);
		datamap.getBranch().put("station2", typeMaps2);
		map.put("traffic", datamap);
		emptyDataMap.setData(new ArrayList<>());
		DataParser.removeEmptyDataSets(map);
		assertNull(datamap.getBranch().get("station2"));
		assertNotNull(datamap.getBranch().get("station1"));
		assertNotNull(datamap.getBranch().get("station1").getBranch().get("ozone"));
		assertFalse(datamap.getBranch().get("station1").getBranch().get("ozone").getData().isEmpty());
	}

}
