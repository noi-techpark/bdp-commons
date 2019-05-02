package it.bz.idm.bdp.augeg4;

import it.bz.idm.bdp.augeg4.face.DataLinearizerFace;
import it.bz.idm.bdp.augeg4.face.DataServiceFace;
import it.bz.idm.bdp.augeg4.fun.linearize.DataLinearizer;
import it.bz.idm.bdp.augeg4.mock.DataConverterMock;
import it.bz.idm.bdp.augeg4.mock.DataPusherMock;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;

public class DataServiceTest {


    @Test
    public void should_push_mapped_data() throws Exception {
        // given
        DataPusherMock pusher = new DataPusherMock();
        DataLinearizerFace linearizer = new DataLinearizer();
        DataConverterMock converter = new DataConverterMock();
        DataServiceFace dataServiceFace = new DataService(pusher, linearizer, converter);

        // when
        dataServiceFace.pushData();

        // then
        assertTrue(pusher.getPushed());
    }
}
