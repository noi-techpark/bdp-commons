package it.bz.idm.bdp.airquality;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import it.bz.idm.bdp.airquality.dto.AQBlockDto;
import it.bz.idm.bdp.airquality.dto.AQStationDto;
import it.bz.idm.bdp.airquality.parser.MyAirQualityListener;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.StationDto;

@ContextConfiguration(locations = { "classpath:/META-INF/spring/applicationContext.xml" })
public class ParserIT extends AbstractJUnit4SpringContextTests {

	private static final String FTP_LOCAL_TESTFOLDER = "/mapping-files";
	private static final String FTP_LOCAL_TESTFILE = FTP_LOCAL_TESTFOLDER + File.separator + "20170103_0030.dat";

	@Autowired
	private DataModel model;

	@Autowired
	private DataPusher pusher;

	@Autowired
	private DataCom com;

	@Before
	public void setUp() {
		try {
			model.parseMetaData(getFolderName(FTP_LOCAL_TESTFOLDER));
		} catch (Exception e) {
			e.printStackTrace(System.err);
			Assert.fail(e.getMessage());
		}
	}

	private String getFolderName(final String folder) {
		String URL = getClass().getResource(folder).getFile();
		File file = new File(URL);
		return file.getAbsolutePath();
	}

	@Test
	public void testModelParser() {
		try {
			model.parseMetaData(getFolderName(FTP_LOCAL_TESTFOLDER));
		} catch (Exception e) {
			e.printStackTrace(System.err);
			Assert.fail(e.getMessage());
		}
		Assert.assertEquals(11, model.getValidStationsAsList().size());
		Assert.assertEquals(19, model.validParameters.size());
		Assert.assertEquals(6, model.validMetrics.size());
		Assert.assertEquals(7, model.getValidErrorsAsList().size());
	}

	@Test
	public void testIllegalStation() throws IOException {
		String input = "ST04,6,22.20.00,04,04,17,0,M09,2,E,40,C,4,D,68.8,B,16.72,R,0,I,100,3,E,40,C,.5,D,39.3,B,6.06,R,0,I,100,9,E,40,C,3.2,D,30.8,B,10.66,R,0,I,100,10,E,40,C,13.4,D,14.2,B,13.8,R,0,I,100,11,E,40,C,22.7,D,24.3,B,23.3,R,0,I,100,12,E,40,C,51,D,57,B,54,R,0,I,100,13,E,40,C,990,D,991,B,990,R,0,I,100,27,E,40,C,8,D,10.2,B,8.5,R,0,I,100,80,E,40,C,5.8,D,7.1,B,6.1,R,0,I,100,#126,\n";
		MyAirQualityListener air = model.parse(input);

		/* Testing stations */
		AQStationDto station = air.getStations().get(0);
		Assert.assertEquals(4, station.getStation());
		Assert.assertEquals(22, station.getHour());
		Assert.assertEquals(17, station.getYear());
		Assert.assertEquals(8, station.getBlocks().size());

		/* Testing blocks */
		AQBlockDto block = air.getStations().get(0).getBlocks().get(0);
		Assert.assertEquals(2, block.getParameterType());
		Assert.assertTrue(block.getKeyValue().containsKey('B'));
		Assert.assertTrue(new Double(16.72).equals(block.getKeyValue().get('B')));
	}

	@Test
	public void testStationTimeAndDate() throws IOException {
		String input = "ST04,6,22.20.11,30,04,17,0,M09,2,E,40,C,4,D,68.8,B,16.72,R,0,I,100,3,E,40,C,.5,D,39.3,B,6.06,R,0,I,100,9,E,40,C,3.2,D,30.8,B,10.66,R,0,I,100,10,E,40,C,13.4,D,14.2,B,13.8,R,0,I,100,11,E,40,C,22.7,D,24.3,B,23.3,R,0,I,100,12,E,40,C,51,D,57,B,54,R,0,I,100,13,E,40,C,990,D,991,B,990,R,0,I,100,27,E,40,C,8,D,10.2,B,8.5,R,0,I,100,80,E,40,C,5.8,D,7.1,B,6.1,R,0,I,100,#126,\n";
		MyAirQualityListener air = model.parse(input);

		/* Testing time and date methods */
		AQStationDto station = air.getStations().get(0);
		Assert.assertEquals(22, station.getHour());
		Assert.assertEquals(20, station.getMinute());
		Assert.assertEquals(11, station.getSecond());
		Assert.assertEquals(17, station.getYear());
		Assert.assertEquals(4, station.getMonth());
		Assert.assertEquals(30, station.getDay());

		/*
		 * Taken from http://www.ruddwire.com/handy-code/date-to-millisecond-calculators
		 * with "Sun Apr 30 2017 22:20:11 GMT+2" as input, which is the Europe/Rome time zone.
		 */
		Assert.assertEquals(1493583611000L, station.getTimestamp());
	}

	@Test
	public void testParsingDataFile() throws Exception {
		MyAirQualityListener air = model.parse(new FileInputStream(getFolderName(FTP_LOCAL_TESTFILE)));

		model.parseMetaData(getFolderName(FTP_LOCAL_TESTFOLDER));
		pusher.setDataModel(model);

		/* Sync stations */
		List<StationDto> stationsODP = com.fetchStationsFromOpenDataPortal(model.validStationsFull);
		Assert.assertTrue(stationsODP != null && stationsODP.size() > 0);

		// TODO Mock this call...
		// pusher.syncStations(new StationList(stationsODP));

		/* Sync data types */
		List<DataTypeDto> dataTypes = new ArrayList<DataTypeDto>(model.validParametersFull.values());

		Assert.assertTrue(dataTypes != null && dataTypes.size() > 0);

		// TODO Mock this call...
		// pusher.syncDataTypes(dataTypes);
		List<AQStationDto> list = air.getStations();

		Assert.assertEquals(list.size(),
				air.getStatistics().get("stations total") - air.getStatistics().get("stations skipped"));

		int blockc = 0;
		for (AQStationDto s : list) {
			blockc += s.getBlocks().size();
		}

		Assert.assertEquals(blockc,
				air.getStatistics().get("blocks inside valid stations total") - air.getStatistics()
						.get("blocks inside valid stations skipped"));

		Assert.assertNotNull(pusher.mapData(list));

		// TODO Mock this call...
		// pusher.pushData();
	}

}
