package it.bz.idm.bdp.augeg4;

import it.bz.idm.bdp.augeg4.face.DataLinearizerFace;
import it.bz.idm.bdp.augeg4.fun.linearize.DataLinearizer;
import it.bz.idm.bdp.augeg4.mock.DataConverterMock;
import it.bz.idm.bdp.augeg4.mock.DataPusherMock;
import it.bz.idm.bdp.augeg4.mock.DataRetrieverMock;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;

public class DataServiceTest {


    @Test
    public void should_push_mapped_data() throws Exception {
        // given
        DataPusherMock pusher = new DataPusherMock();
        DataRetrieverMock retrieval = new DataRetrieverMock();
        DataLinearizerFace linearizer = new DataLinearizer();
        DataConverterMock converter = new DataConverterMock();
        DataService dataService = new DataService(pusher, retrieval, linearizer, converter);
        // when
        dataService.pushData();
        // then
        assertTrue(retrieval.getRetrieved());
        assertTrue(pusher.getPushed());
    }
}
