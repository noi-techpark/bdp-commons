package it.bz.idm.bdp.augeg4;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

@ContextConfiguration(locations = {"classpath:/META-INF/spring/applicationContext.xml"})
public class JobSchedulerTest extends AbstractJUnit4SpringContextTests {

    @Autowired
    private JobScheduler js;

    @Test
    public void test_scheduled_sync_stations() throws Exception {
        js.pushStations();
    }

    @Test
    public void test_scheduled_sync_DataTypes() throws Exception {
        js.pushDataTypes();
    }

    @Test
    @Ignore
    public void test_scheduled_push_data() throws Exception {
        js.pushData();
    }

}
