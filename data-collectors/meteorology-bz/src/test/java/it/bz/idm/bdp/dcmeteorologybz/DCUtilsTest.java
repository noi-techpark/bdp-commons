package it.bz.idm.bdp.dcmeteorologybz;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import it.bz.idm.bdp.dcmeteorologybz.dto.TimeSerieDto;

public class DCUtilsTest {

	@Test
	public void testPeriodCalculationIfArrayEmpty(){
		List<TimeSerieDto> measurements = new ArrayList<>();
		Integer period = DCUtils.calcPeriodUsingFirstTwoElements(measurements);
		assertNull(period);
	}
	@Test
	public void testPeriodCalculationIfOneMissesData(){
		List<TimeSerieDto> measurements = new ArrayList<>();
		TimeSerieDto obj1= new TimeSerieDto();
		TimeSerieDto obj2= new TimeSerieDto();
		
		obj1.setDATE("2020-05-01T14:00:00Z");
		measurements.add(obj1);
		measurements.add(obj2);
		Integer period = DCUtils.calcPeriodUsingFirstTwoElements(measurements);
		assertNull(period);
	}
	@Test
	public void testPeriodCalculationIfEverythingsOK(){
		List<TimeSerieDto> measurements = new ArrayList<>();
		TimeSerieDto obj1= new TimeSerieDto();
		TimeSerieDto obj2= new TimeSerieDto();
		
		obj1.setDATE("2020-05-01T14:00:00Z");
		obj2.setDATE("2020-05-01T15:00:00Z");
		measurements.add(obj1);
		measurements.add(obj2);
		Integer period = DCUtils.calcPeriodUsingFirstTwoElements(measurements);
		assertEquals(Integer.valueOf(3600), period);
	}
}
