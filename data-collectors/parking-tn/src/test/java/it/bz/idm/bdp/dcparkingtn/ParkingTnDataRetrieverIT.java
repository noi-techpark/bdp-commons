package it.bz.idm.bdp.dcparkingtn;

import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import it.bz.idm.bdp.dcparkingtn.dto.ParkingTnDto;

@ContextConfiguration(locations = { "classpath:/META-INF/spring/applicationContext.xml" })
public class ParkingTnDataRetrieverIT extends AbstractJUnit4SpringContextTests {

    private static final Logger LOG = LogManager.getLogger(ParkingTnDataRetrieverIT.class.getName());

    @Autowired
    private ParkingTnDataRetriever reader;

    @Test
    public void testFetchData() {
        try {
            //Fetch data from source origin
            List<ParkingTnDto> fetchData = reader.fetchData();

            //Check there is at least one item in the list
            assertNotNull("Fetched data IS NULL", fetchData);
            if ( fetchData.size() == 0 ) {
                Assert.fail("Fetched data IS EMPTY");
            }

        } catch (Exception e) {
            String msg = "Exception in testFetchData: " + e;
            LOG.error(msg, e);
            Assert.fail(msg);
        }
    }

}
