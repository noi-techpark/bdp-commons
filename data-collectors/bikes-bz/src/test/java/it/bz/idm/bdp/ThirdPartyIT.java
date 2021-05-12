package it.bz.idm.bdp;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.tempuri.xmlresponewebservice.ArrayOfXmlImAnagraficaImpiantoSensore.Sensore;
import org.tempuri.xmlresponewebservice.GetDataResult.XmlRwData;
import org.tempuri.xmlresponewebservice.GetMetadataStationResult;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/spring/applicationContext*.xml" })
public class ThirdPartyIT extends AbstractJUnit4SpringContextTests{

	private Logger logger = LogManager.getLogger(ThirdPartyIT.class);

	@Autowired
	private SoapClient soapClient;

	@Test
	public void testGetStationIds() {
		List<Integer> stationIdentifiers = soapClient.getStationIdentifiers();
		assertNotNull(stationIdentifiers);
		assertFalse(stationIdentifiers.isEmpty());
	}
	@Test
	public void testGetMetaDataStation() {
		List<Integer> stations = soapClient.getStationIdentifiers();
		List<Integer> failedRequests = new ArrayList<Integer>();
		for (Integer station : stations){
			try{
				GetMetadataStationResult getMetadataStationResult = soapClient.getStationMetaData(station);
				assertNotNull(getMetadataStationResult.getNome());
				for (Sensore sensore :getMetadataStationResult.getSensori().getSensore()){
					assertNotNull(sensore);
				}
			}catch(IllegalStateException ex){
				failedRequests.add(station);
				continue;
			}
		}
		if (!failedRequests.isEmpty()){
			logger.debug("metadata requests failed for station ids:" + Arrays.toString(failedRequests.toArray()));
		}
		assertTrue(failedRequests.isEmpty());
	}
	@Test
	public void testGetData() {
		List<Integer> stations = soapClient.getStationIdentifiers();
		List<Integer> failedRequests = new ArrayList<Integer>();
		int size = 0;
		for (Integer station : stations){
			try{
				List<XmlRwData> currentData = soapClient.getCurrentData(station);
				for(XmlRwData data : currentData){
					assertNotNull(data);
					assertNotNull(data.getTs());
				}
			}catch(IllegalStateException ex){
				failedRequests.add(station);
				continue;
			}
		}
		logger.debug("Total number of records"+size);
		if (!failedRequests.isEmpty()){
			logger.debug("getData requests failed for station ids:" + Arrays.toString(failedRequests.toArray()));
		}
		assertTrue(failedRequests.isEmpty());
	}

	@Test
	public void testGetHistoricalData() {
		List<Integer> stations = soapClient.getStationIdentifiers();
		XMLGregorianCalendar from = null,to = null;
		try {
			from = DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar());
			to = DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar());
			Duration duration = DatatypeFactory.newInstance().newDuration(1000*60*60*6*-1);
			from.add(duration);
		} catch (DatatypeConfigurationException e) {
			e.printStackTrace();
		}
		for (Integer station : stations){
			try{
			List<org.tempuri.xmlresponewebservice.GetDataHistoricalResult.XmlRwData> currentData = soapClient.getHistoryData(station,from,to);
			assertFalse(currentData.isEmpty());
			int truesize =0;
			for(org.tempuri.xmlresponewebservice.GetDataHistoricalResult.XmlRwData data : currentData){
				assertNotNull(data);
				truesize++;
			}
			logger.debug("station works: " + station + "and contains " + truesize + "records");
			}
			catch(Exception ex){
				logger.debug("station works not: " + station);
				continue;
			}
		}
	}
}
