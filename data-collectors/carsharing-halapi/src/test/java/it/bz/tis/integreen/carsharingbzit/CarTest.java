package it.bz.tis.integreen.carsharingbzit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.StationList;

public class CarTest {
	
	private static final String AVAILABLE_VEHICLES_FIELD = "availableVehicles";
	private StationList vehicles = new StationList();
	private StationList stations = new StationList();
	
	@Before
	public void setup() {
		StationDto station1 = new StationDto("s1","Hello im cap",null,-45.4);
		StationDto station2 = new StationDto("s2","Hello im lap",null,45.4);
		stations.add(station1);
		stations.add(station2);

		StationDto car1 = new StationDto("c1","Hello im dego",null,null);
		car1.setParentStation("s1");
		StationDto car2= new StationDto("c2","Hello im ego",42.4,null);
		car2.setParentStation("s1");
		StationDto car3 = new StationDto("c3","Hello im sego",-1.,45.4);
		car3.setParentStation("s2");
		vehicles.add(car1);
		vehicles.add(car2);
		vehicles.add(car3);
	}
	
	@Test
	public void testCountingCars() {
		ConnectorLogic.countVehiclesPerStation(stations, vehicles);
		for(StationDto station: stations) {
			Object object = station.getMetaData().get(AVAILABLE_VEHICLES_FIELD);
			assertNotNull(object);
			assertTrue(object instanceof Integer);
			Integer count = (Integer) object;
			assertTrue(count>0);
		}
		assertEquals(new Integer(2), ((Integer)stations.get(0).getMetaData().get(AVAILABLE_VEHICLES_FIELD)));
	}

}
