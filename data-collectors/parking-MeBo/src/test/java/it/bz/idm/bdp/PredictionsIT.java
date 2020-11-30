package it.bz.idm.bdp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Map.Entry;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.SimpleRecordDto;
import it.bz.idm.bdp.forecast.domain.ParkingForecast;
import it.bz.idm.bdp.forecast.domain.ParkingForecasts;
import it.bz.idm.bdp.forecast.domain.TSPrediction;
import it.bz.idm.bdp.util.MappingUtil;

@ContextConfiguration(locations = { "/META-INF/spring/applicationContext.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
public class PredictionsIT extends AbstractJUnit4SpringContextTests{

	@Test
	public void testTypeMapGeneration() {
		ParkingForecasts predictions = new ParkingForecasts();

		ParkingForecast p1 = new ParkingForecast(),p2= new ParkingForecast(),p3 = new ParkingForecast(),p4 = new ParkingForecast(),p5 = new ParkingForecast(),p6 = new ParkingForecast(),p7 = new ParkingForecast(),p8 = new ParkingForecast();
		p1.setStartDate(new Timestamp(new Date().getTime()+50000));
		TSPrediction prediction1 = new TSPrediction(20,25,17,"Ready");
		p1.setPrediction(prediction1);

		p2.setStartDate(new Timestamp(new Date().getTime()+40000));
		TSPrediction prediction2 = new TSPrediction(21,5,100,"Ready To");
		p2.setPrediction(prediction2);

		p3.setStartDate(new Timestamp(new Date().getTime()+500000));
		TSPrediction prediction3 = new TSPrediction(110,125,177,"Ready To Rumble");
		p3.setPrediction(prediction3);

		p4.setStartDate(new Timestamp(new Date().getTime()+1523000));
		TSPrediction prediction4 = new TSPrediction(10011,10125,10717,"Ready To Rumble");
		p4.setPrediction(prediction4);

		p5.setStartDate(new Timestamp(new Date().getTime()+1101000));
		TSPrediction prediction5 = new TSPrediction(1,12,17,"Ready To Rumble");
		p5.setPrediction(prediction5);

		p6.setStartDate(new Timestamp(new Date().getTime()+15000));
		TSPrediction prediction6 = new TSPrediction(10,102,177,"Ready To Rumble");
		p6.setPrediction(prediction6);

		p7.setStartDate(new Timestamp(new Date().getTime()+150210));
		TSPrediction prediction7 = new TSPrediction(1201,1025,1027,"Ready To Rumble");
		p7.setPrediction(prediction7);

		p8.setStartDate(new Timestamp(new Date().getTime()+1500001));
		TSPrediction prediction8 = new TSPrediction(1000,1005,1027,"Ready To Rumble");
		p8.setPrediction(prediction8);

		predictions.getParkingForecasts().add(p1);
		predictions.getParkingForecasts().add(p2);
		predictions.getParkingForecasts().add(p3);
		predictions.getParkingForecasts().add(p4);
		predictions.getParkingForecasts().add(p5);
		predictions.getParkingForecasts().add(p6);
		predictions.getParkingForecasts().add(p7);
		predictions.getParkingForecasts().add(p8);

		DataMapDto<RecordDtoImpl> typeMap = MappingUtil.generateTypeMap(predictions);
		assertNotNull(typeMap);
		assertEquals(ParkingPusher.PREDICTION_FORECAST_TIMES_IN_MINUTES.length, typeMap.getBranch().entrySet().size());
		for (Entry<String, DataMapDto<RecordDtoImpl>> entry: typeMap.getBranch().entrySet()) {
			assertTrue(entry.getKey().startsWith(ParkingPusher.FORECAST_PREFIX));
			assertEquals(1,entry.getValue().getData().size());
			RecordDtoImpl recordDtoImpl = entry.getValue().getData().get(0);
			assertNotNull(recordDtoImpl);
			assertTrue(recordDtoImpl instanceof SimpleRecordDto);
		}
	}
}
