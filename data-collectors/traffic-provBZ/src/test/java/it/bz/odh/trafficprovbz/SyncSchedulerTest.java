package it.bz.odh.trafficprovbz;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class SyncSchedulerTest {

	//@Mock
	FamasClient famasClient = Mockito.mock(FamasClient.class);

	//@Mock
	OdhClientBluetoothSensor odhClientBluetoothSensor = Mockito.mock(OdhClientBluetoothSensor.class);
	OdhClientTrafficSensor odhClientTrafficSensor = Mockito.mock(OdhClientTrafficSensor.class);


	@Test
	void checkSyncStations() throws Exception {
		Mockito.when(famasClient.getClassificationSchemas()).thenReturn(null);
		Mockito.when(famasClient.getStationsData()).thenReturn(null);

		SyncScheduler sut = new SyncScheduler(odhClientTrafficSensor, odhClientBluetoothSensor, famasClient);

		sut.syncJobStations();
	}

	@Test
	void checkSyncTrafficMeasurements() throws Exception {
		Mockito.when(famasClient.getAggregatedDataOnStations(null, null, null)).thenReturn(null);

		SyncScheduler sut = new SyncScheduler(odhClientTrafficSensor, odhClientBluetoothSensor, famasClient);

		sut.syncJobTrafficMeasurements();
	}

	@Test
	void checkSyncBluetoothMeasurements() throws Exception {
		Mockito.when(famasClient.getPassagesDataOnStations(null, null, null)).thenReturn(null);

		SyncScheduler sut = new SyncScheduler(odhClientTrafficSensor, odhClientBluetoothSensor, famasClient);

		sut.syncJobBluetoothMeasurements();
	}
}
