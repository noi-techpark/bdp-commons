package it.bz.idm.bdp.augeg4;

import it.bz.idm.bdp.augeg4.face.DataPusherAugeFace;
import it.bz.idm.bdp.augeg4.face.DataPusherHubFace;
import it.bz.idm.bdp.augeg4.face.DataRetrieverFace;
import it.bz.idm.bdp.augeg4.face.DataServiceFace;
import it.bz.idm.bdp.augeg4.mock.DataPusherHubMockLog;
import it.bz.idm.bdp.augeg4.mock.DataRetrieverMock;
import it.bz.idm.bdp.augeg4.mock.DataPusherAugeMock;
import it.bz.idm.bdp.augeg4.mock.DataPusherHubMock;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public class DataServiceTest {


    private static final int PERIOD = 600;

    @Test
    public void should_push_mapped_data() throws Exception {
        // given
        DataRetrieverMock retriever = new DataRetrieverMock();
        DataPusherHubMock pusherHub = new DataPusherHubMock();
        DataPusherAugeFace pusherAuge = new DataPusherAugeMock();
        DataServiceFace dataService = new DataService(retriever, pusherHub, pusherAuge);

        // when
        dataService.pushData();

        // then
        assertTrue(pusherHub.getPushed());
    }


    @Test
    public void test_data_pipeline_from_retrieval_with_pusher_mock() throws Exception {
        // given
        DataRetrieverFace retriever = new DataRetrieverMock();
        DataPusherAugeFace pusherAuge = new DataPusherAugeMock();
        DataPusherHubFace pusherHub = new DataPusherHubMockLog(PERIOD);
        DataServiceFace dataService = new DataService(retriever, pusherHub, pusherAuge);
        // when
        assertEquals(0,((DataService) dataService).getDataToHubCount());
        dataService.pushData();
        // then
        assertEquals(0,((DataService) dataService).getDataToHubCount());
        // ... will print mocked auge data messages
    }

    @Test
    public void test_stations_pipeline_from_retrieval_with_pusher_mock() throws Exception {
        // given
        DataRetrieverFace retriever = new DataRetrieverMock();
        DataPusherAugeFace pusherAuge = new DataPusherAugeMock();
        DataPusherHubFace pusherHub = new DataPusherHubMockLog(PERIOD);
        DataServiceFace dataService = new DataService(retriever, pusherHub, pusherAuge);
        // when
        assertEquals(0,((DataService) dataService).getStationsCount());
        dataService.pushData(); // required for prepare stations data
        assertEquals(1,((DataService) dataService).getStationsCount());
        dataService.syncStations();
        // then
        // ... will print mocked auge data messages
        assertEquals(0,((DataService) dataService).getStationsCount());
    }

    @Test
    public void test_datatypes_pipeline_from_retrieval_with_pusher_mock() throws Exception {
        // given
        DataRetrieverFace retriever = new DataRetrieverMock();
        DataPusherAugeFace pusherAuge = new DataPusherAugeMock();
        DataPusherHubFace pusherHub = new DataPusherHubMockLog(PERIOD);
        DataServiceFace dataService = new DataService(retriever, pusherHub, pusherAuge);
        // when
        dataService.syncDataTypes();
        // then
        // ... will print mocked auge data messages
    }

}
