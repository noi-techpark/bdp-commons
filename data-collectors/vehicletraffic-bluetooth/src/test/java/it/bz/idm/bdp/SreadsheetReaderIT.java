package it.bz.idm.bdp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.google.api.services.sheets.v4.model.ValueRange;

import it.bz.idm.bdp.service.SpreadsheetReader;
import it.bz.idm.bdp.util.BluetoothMappingUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/META-INF/spring/applicationContext*.xml" })
@WebAppConfiguration
public class SreadsheetReaderIT {

	private static final String SHEETNAME = "boxes";
	@Autowired
	private SpreadsheetReader reader;
	
	@Autowired
	private BluetoothMappingUtil util;
	
	@Test
	public void testValuesRetrieval() {
		ValueRange valueRange = reader.getWholeSheet(SHEETNAME);
		assertNotNull(valueRange);
		assertFalse(valueRange.getValues().isEmpty());
	}
	
	@Test
	public void testDataIntegrity() {
		ValueRange valueRange = reader.getWholeSheet(SHEETNAME);
		assertNotNull(valueRange);
		List<Map<String, String>> objs = util.convertToMap(valueRange);
		
		util.validate(objs);
	}
	@Test
	public void testGetValidRows() {
		ValueRange valueRange = reader.getWholeSheet(SHEETNAME);
		assertNotNull(valueRange);
		List<Map<String, String>> validEntries = util.getValidEntries();
		assertNotNull(validEntries);
		assertFalse(validEntries.isEmpty());
		
	}
	@Test
	public void testGetCoordinatesById() {
		Double[] coordinatesByIdentifier = util.getCoordinatesByIdentifier("proma");
		assertNotNull(coordinatesByIdentifier);
		assertEquals(2,coordinatesByIdentifier.length);

	}
	@Test
	public void testGetMetadataById() {
		Map<String,Object> metaDataByIdentifier = util.getMetaDataByIdentifier("proma");
		assertNotNull(metaDataByIdentifier);
	}
}
