// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.idm.bdp;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.StationList;

@ContextConfiguration(locations = { "/META-INF/spring/applicationContext*.xml" })
public class RetrieverIT extends AbstractJUnit4SpringContextTests{

	@Autowired
	private DataParser parser;

	@Autowired
	private HistoryRetriever historyRetriever;

	@Test
	public void testRetrieveStations(){
		StationList stations = parser.retrieveStations();
		assertNotNull(stations);
		assertFalse(stations.isEmpty());
		for (StationDto station:stations){
			assertNotNull(station.getId());
		}
	}

	@Test
	public void testRetrieveDataTpyes(){
		List<DataTypeDto> retrieveDataTypes = parser.retrieveDataTypes();
		assertNotNull(retrieveDataTypes);
		assertFalse(retrieveDataTypes.isEmpty());
		for (DataTypeDto dto: retrieveDataTypes){
			assertNotNull(dto.getName());
			assertNotNull(dto.getDescription());
		}
	}
	@Test
	public void testRetrieveLiveData(){
		DataMapDto<RecordDtoImpl> liveData = parser.retrieveLiveData();
		assertNotNull(liveData);
		assertFalse(liveData.getBranch().isEmpty());
		for (Map.Entry<String,DataMapDto<RecordDtoImpl>> entry: liveData.getBranch().entrySet()){
			assertNotNull(entry.getValue());
			assertNotNull(entry.getKey());
			DataMapDto<RecordDtoImpl> value = entry.getValue();
			for (Map.Entry<String, DataMapDto<RecordDtoImpl>> typeentry : value.getBranch().entrySet()){
				assertNotNull(typeentry.getKey());
				for (RecordDtoImpl dto : typeentry.getValue().getData()){
					assertNotNull(dto);
				}
			}
		}
	}

	@Test
	public void testHistoryData() {
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		Date startingDate;
		try {
			startingDate = format.parse("01/01/2019");
			XMLGregorianCalendar from = null, to = null;
			GregorianCalendar cal = new GregorianCalendar();
			Duration duration = DatatypeFactory.newInstance().newDuration(1000 * 60 * 60 * 3);
			cal.setTime(startingDate);
			from = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
			to = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
			to.add(duration);
			DataMapDto<RecordDtoImpl> retrieveHistoricData = parser.retrieveHistoricData(from, to);
			for (Map.Entry<String, DataMapDto<RecordDtoImpl>> entry : retrieveHistoricData.getBranch().entrySet()) {
				for (Map.Entry<String, DataMapDto<RecordDtoImpl>> e : entry.getValue().getBranch().entrySet()) {
					long ts = 0;
					Collections.sort(e.getValue().getData());
					for (RecordDtoImpl dto : e.getValue().getData()) {
						if (ts < dto.getTimestamp())
							ts = dto.getTimestamp();
					}
					System.out.println("Station " + entry.getKey() + " Type " + e.getKey() + " number "
							+ e.getValue().getData().size() + "|Latest:" + ts);
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (DatatypeConfigurationException e) {
			e.printStackTrace();
		} finally {
		}
	}

	@Test
	public void testFetchNewestExistingDate() {
		Date fetchNewestExistingDate = historyRetriever.fetchNewestExistingDate();
		assertNotNull(fetchNewestExistingDate);
	}
}
