package it.bz.idm.bdp.augeg4;

import it.bz.idm.bdp.augeg4.face.DataConverterFace;
import it.bz.idm.bdp.augeg4.face.DataLinearizerFace;
import it.bz.idm.bdp.augeg4.fun.push.DataPusher;
import it.bz.idm.bdp.augeg4.fun.retrieve.DataRetrieverMock;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

@ContextConfiguration(locations = {"classpath:/META-INF/spring/applicationContext.xml"})
public class JobSchedulerWithMockedRetrieverTest extends AbstractJUnit4SpringContextTests {

    @Autowired
    private DataPusher pusher;

    private DataRetrieverMock retriever = new DataRetrieverMock();

    @Autowired
    private DataLinearizerFace linearizer;

    @Autowired
    private DataConverterFace converter;

    @Test
    public void test_scheduled_push_data_with_mocked_retriever () throws Exception {
        // given
        JobScheduler js = new JobScheduler(pusher, retriever, linearizer, converter);
        js.mockDataRetrievedFromAlgorab();
        js.pushStations();

        // when
        js.pushData();

        // then no exception is thrown
    }
}
