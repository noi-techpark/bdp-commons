// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.idm.bdp.airquality;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import it.bz.idm.bdp.airquality.dto.AQStationDto;
import it.bz.idm.bdp.airquality.parser.MyAirQualityListener;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.StationList;

@ContextConfiguration(locations = { "classpath:/META-INF/spring/applicationContext.xml" })
public class ManualTesting extends AbstractJUnit4SpringContextTests {

	private static final String FTP_LOCAL_TESTFOLDER = "/mapping-files";
	private static final String FTP_LOCAL_TESTFILE = FTP_LOCAL_TESTFOLDER + File.separator + "20170103_0030.dat";

	@Autowired
	private JobScheduler js;

	@Autowired
	private DataModel model;

	@Autowired
	private DataPusher pusher;

	@Autowired
	private DataCom com;

	private String getFolderName(final String folder) {
		String URL = getClass().getResource(folder).getFile();
		File file = new File(URL);
		return file.getAbsolutePath();
	}

	@Test
	public void test() {
		try {
			js.pushDataToCollector();
			// js.deleteOldFiles();
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	@Test
	public void testDataPusher() throws Exception {
		MyAirQualityListener air = model.parse(new FileInputStream(getFolderName(FTP_LOCAL_TESTFILE)));

		model.parseMetaData(getFolderName(FTP_LOCAL_TESTFOLDER));
		pusher.setDataModel(model);

		/* Sync stations */
		List<StationDto> stationsODP = com.fetchStationsFromOpenDataPortal(model.validStationsFull);
		Assert.assertTrue(stationsODP != null && stationsODP.size() > 0);

		pusher.syncStations(new StationList(stationsODP));

		/* Sync data types */
		List<DataTypeDto> dataTypes = new ArrayList<DataTypeDto>(model.validParametersFull.values());

		Assert.assertTrue(dataTypes != null && dataTypes.size() > 0);

		// TODO Mock this call...
		pusher.syncDataTypes(dataTypes);
		List<AQStationDto> list = air.getStations();

		Assert.assertEquals(list.size(),
				air.getStatistics().get("stations total") - air.getStatistics().get("stations skipped"));

		int blockc = 0;
		for (AQStationDto s : list) {
			blockc += s.getBlocks().size();
		}

		Assert.assertEquals(blockc, air.getStatistics().get("blocks inside valid stations total") - air.getStatistics()
				.get("blocks inside valid stations skipped"));

		Assert.assertNotNull(pusher.mapData(list));

		pusher.pushData();
	}
}
