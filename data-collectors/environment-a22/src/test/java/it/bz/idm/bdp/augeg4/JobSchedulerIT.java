package it.bz.idm.bdp.augeg4;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

@ContextConfiguration(locations = {"classpath:/META-INF/spring/applicationContext.xml"})
public class JobSchedulerIT extends AbstractJUnit4SpringContextTests {

    @Autowired
    private JobScheduler js;

    @Test
    public void test_load_previously_synced_stations_at_deploy () throws Exception {
        //  TODO: The method is already called by Spring, do we keep anyway the test?
        js.loadPreviouslySyncedStations();
    }

    @Test
    public void test_scheduled_sync_DataTypes_at_deploy() throws Exception {
        js.pushDataTypes();
    }

    @Test
    public void test_scheduled_sync_stations() throws Exception {
        js.pushStations();
    }

    @Test
    @Ignore
    public void test_scheduled_push_data() throws Exception {
        js.pushData();
    }

}
