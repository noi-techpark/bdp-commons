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

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cleanroadsdatatype.cleanroadswebservices.ArrayOfXmlImAnagraficaStazioneStationType.StationType;
import cleanroadsdatatype.cleanroadswebservices.ArrayOfXmlImClassConfigXmlClassificazioneClasse.Classe;
import cleanroadsdatatype.cleanroadswebservices.ArrayOfXmlImDataTypesXmlDataTypeClassifiSpec.ClassifiSpec;
import cleanroadsdatatype.cleanroadswebservices.GetClassifConfigResult.XmlClassificazione;
import cleanroadsdatatype.cleanroadswebservices.GetDataResult.XmlRwData;
import cleanroadsdatatype.cleanroadswebservices.GetDataTypesResult.XmlDataType;
import cleanroadsdatatype.cleanroadswebservices.GetMetadataStationResult;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/spring/applicationContext*.xml" })
public class ThirdPartyIT extends AbstractJUnit4SpringContextTests{

	private Logger logger = Logger.getLogger(ThirdPartyIT.class);
	
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
				for (StationType type :getMetadataStationResult.getStationTypeList().getStationType()){
					assertNotNull(type);
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
	public void testGetStationConfig() {
		List<Integer> stations = soapClient.getStationIdentifiers();
		List<Integer> failedRequests = new ArrayList<Integer>();
		for (Integer station : stations){
			try{
				List<XmlClassificazione> stationConfig = soapClient.getStationConfig(station);
				for(XmlClassificazione config:stationConfig){
				}

				assertFalse(stationConfig.isEmpty());
			}catch(IllegalStateException ex){
				failedRequests.add(station);
				continue;
			}
		}
		if (!failedRequests.isEmpty()){
			logger.debug("Station config requests failed for station ids:" + Arrays.toString(failedRequests.toArray()));
		}
	}
	@Test 
	public void testGetDataTypes(){
		List<Integer> stations = soapClient.getStationIdentifiers();
		List<Integer> failedRequests = new ArrayList<Integer>();
		for (Integer station : stations){
			try{
				List<XmlDataType> stationDataTypes = soapClient.getStationDataTypes(station);
				for (XmlDataType type : stationDataTypes){
					assertNotNull(type.getId());
					if (type.getClassificazioni() != null)
						for (ClassifiSpec spec:type.getClassificazioni().getClassifiSpec()){
						}
				}
			}catch(IllegalStateException ex){
				failedRequests.add(station);
				continue;
			}
		}
		if (!failedRequests.isEmpty()){
			logger.debug("Datatype requests failed for station ids:" + Arrays.toString(failedRequests.toArray()));
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
				List<XmlDataType> stationDataTypes = soapClient.getStationDataTypes(station);
				if (stationDataTypes.isEmpty()) 
					continue; 
				List<XmlRwData> currentData = soapClient.getCurrentData(station,stationDataTypes);
				for(XmlRwData data : currentData){
					assertNotNull(data);
					assertNotNull(data.getTs());
					if (data.getClassifDataList()!=null)
						size+=data.getClassifDataList().getClassifData().size();
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
			List<XmlDataType> stationDataTypes = soapClient.getStationDataTypes(station);
			List<cleanroadsdatatype.cleanroadswebservices.GetDataHistoricalResult.XmlRwData> currentData = soapClient.getHistoryData(station,stationDataTypes,from,to);
			assertFalse(currentData.isEmpty());
			int truesize =0;
			for(cleanroadsdatatype.cleanroadswebservices.GetDataHistoricalResult.XmlRwData data : currentData){
				if (data.getClassifDataList() != null){
					truesize += data.getClassifDataList().getClassifData().size();
				}
				else truesize++;
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
