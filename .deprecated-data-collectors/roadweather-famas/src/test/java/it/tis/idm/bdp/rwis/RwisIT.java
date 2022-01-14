package it.tis.idm.bdp.rwis;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.tempuri.ArrayOfInt;

import it.bz.idm.bdp.rwis.RWISFetch;

public class RwisIT {
	
	@Test
	public void testGetStations() {
		ArrayOfInt stationID = RWISFetch.getStationID();
		assertNotNull(stationID);
		assertFalse(stationID.getInt()==null && stationID.getInt().isEmpty());
	}

}
