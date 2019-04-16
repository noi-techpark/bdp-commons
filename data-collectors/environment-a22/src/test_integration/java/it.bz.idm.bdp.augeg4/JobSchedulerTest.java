package it.bz.idm.bdp.augeg4;

import it.bz.idm.bdp.augeg4.face.DataPusherFace;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

@ContextConfiguration(locations = {"classpath:/META-INF/spring/applicationContext.xml"})
public class JobSchedulerTest extends AbstractJUnit4SpringContextTests {

    @Autowired
    private JobScheduler js;

    @Autowired
    private DataPusherFace pusher;

    @Test
    public void testScheduledSyncStations() throws Exception {
        js.pushStations();
    }

    @Test
    public void testScheduledSyncDataTypes() throws Exception {
        js.pushDataTypes();
    }

    @Test
    @Ignore
    public void testScheduledPushData() throws Exception {
        js.pushData();
    }

}
