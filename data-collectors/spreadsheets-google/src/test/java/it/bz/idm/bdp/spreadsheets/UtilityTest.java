package it.bz.idm.bdp.spreadsheets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import it.bz.idm.bdp.dto.StationDto;

@ContextConfiguration(locations = { "classpath*:/META-INF/spring/applicationContext.xml" })
@TestPropertySource(properties = {
	    "headers.addressId=address",
	    "composite.unique.key=name,address",
	    "headers.nameId=name",
	    "headers.longitudeId=longitude",
	    "headers.latitudeId=latitude"
	})
public class UtilityTest extends AbstractJUnit4SpringContextTests{

	@Autowired
	private ODHClient odhClient;

	private DataMappingUtil util = new DataMappingUtil();

	private List<Object> headerRow;
	private List<Object> dataRow;

	@Before
	public void setup() {
		headerRow = new ArrayList<>();
		headerRow.add("name");
		headerRow.add("address");
		headerRow.add("longitude");
		headerRow.add("latitude");
		headerRow.add("en:phone");
		headerRow.add("description");


		dataRow = new ArrayList<>();
		dataRow.add("Patrick");
		dataRow.add("My address");
		dataRow.add("21.213");
		dataRow.add("11.213");
		dataRow.add("3936936");
		dataRow.add("Das ist eine deutsche Beschreibung und die Sache sollte daher klar sein");
	}

	@Test
	public void testHeaderMapping() {
		Map<String, Short> hRow = util.listToMap(headerRow);
		assertNotNull(hRow);
		short expected = 0;
		assertEquals(new Short(expected),hRow.get("name"));
	}

	@Test
	public void testStationDtoMapping() {
		Map<String, Short> headerMap = util.listToMap(headerRow);
		StationDto station = odhClient.mapStation(headerMap, dataRow);
		assertNotNull(station);
		assertEquals(dataRow.get(0), station.getName());
		assertEquals(dataRow.get(1), station.getMetaData().get("address"));
		assertEquals(dataRow.get(2), station.getLongitude().toString());
		assertEquals(dataRow.get(3), station.getLatitude().toString());
		assertEquals(3936936l, station.getMetaData().get("en:phone"));
	}
	@Test
	public void testMapTextToLanguage() {
		Map<String, Short> headerMap = util.listToMap(headerRow);
		StationDto station = odhClient.mapStation(headerMap, dataRow);
		odhClient.guessLanguages(station.getMetaData());
		assertNotNull(station.getMetaData().get("description"));
		assertNotNull(station.getMetaData().get("description")instanceof Map);
		Map<String,String> langMap = (Map<String, String>) station.getMetaData().get("description");
		assertTrue(langMap.containsKey("de"));
	}

}
