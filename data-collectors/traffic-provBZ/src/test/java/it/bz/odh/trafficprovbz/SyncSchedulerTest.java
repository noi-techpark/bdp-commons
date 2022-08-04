package it.bz.odh.trafficprovbz;

import it.bz.idm.bdp.dto.StationList;
import it.bz.odh.trafficprovbz.dto.ClassificationSchemaDto;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

class SyncSchedulerTest {

	//@Mock
	FamasClient famasClient = Mockito.mock(FamasClient.class);

	//@Mock
	OdhClient odhClient = Mockito.mock(OdhClient.class);

	@Test
	void test() {
		assertThat(true).isEqualTo(true);
	}

	@Test
	void checkSyncStations() throws Exception {
		Mockito.when(famasClient.getClassificationSchemas()).thenReturn(null);
		Mockito.when(famasClient.getStationsData()).thenReturn(null);

		SyncScheduler sut = new SyncScheduler(odhClient, famasClient);

		sut.syncJobStations();
	}

	@Test
	void checkSyncTrafficMeasurements() throws Exception {
		Mockito.when(famasClient.getAggregatedDataOnStations(null, null)).thenReturn(null);

		SyncScheduler sut = new SyncScheduler(odhClient, famasClient);

		sut.syncJobTrafficMeasurements();
	}

	@Test
	void checkSyncBluetoothMeasurements() throws Exception {
		String STATION_ID = "1";
		Mockito.when(famasClient.getPassagesDataOnStations(STATION_ID, null, null)).thenReturn(null);

		SyncScheduler sut = new SyncScheduler(odhClient, famasClient);

		sut.syncJobBluetoothMeasurements();
	}
}
