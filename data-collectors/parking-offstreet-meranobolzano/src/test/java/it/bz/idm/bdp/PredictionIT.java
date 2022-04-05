package it.bz.idm.bdp;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import it.bz.idm.bdp.forecast.domain.ForecastStep;
import it.bz.idm.bdp.forecast.domain.ParkingForecast;
import it.bz.idm.bdp.forecast.domain.ParkingForecasts;

@ContextConfiguration(locations = { "/META-INF/spring/applicationContext.xml" })
public class PredictionIT extends AbstractJUnit4SpringContextTests{

	@Autowired
	private PredictionRetriever predictionRetriever;

	@Autowired
	private ParkingFrontEndRetriever parkingFrontEndRetriever;

	@Test
	public void testPredictionParsing(){
		ForecastStep predict = predictionRetriever.predict(60);
		assertNotNull(predict);
		assertNotNull(predict.getForecastStartDate().getTime());
	}
	@Test
	public void testParkingFrontEnd(){
		String[] activeStationIdentifers = parkingFrontEndRetriever.getActiveStationIdentifers();
		assertNotNull(activeStationIdentifers);
		assertFalse(activeStationIdentifers.length==0);
	}
	@Test
	public void testPredictionByStationId(){
		String[] identifiers = parkingFrontEndRetriever.getActiveStationIdentifers();
		assertNotNull(identifiers);
		for (String stationIdentifier:identifiers){
			ParkingForecasts predictions = predictionRetriever.predict(stationIdentifier);
			if (predictions!=null) {
				for (Integer period : new Integer[]{30,60,90,120,150,180,210,240}){
					ParkingForecast prediction = predictions.findByTime(period);
					assertNotNull(prediction);
				}
			}
		}
	}

}
