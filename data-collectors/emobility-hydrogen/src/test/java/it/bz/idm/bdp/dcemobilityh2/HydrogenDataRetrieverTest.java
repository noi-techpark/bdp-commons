package it.bz.idm.bdp.dcemobilityh2;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.lang.reflect.Method;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import it.bz.idm.bdp.dcemobilityh2.dto.HydrogenDto;
import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.StationList;

@ContextConfiguration(locations = { "classpath:/META-INF/spring/applicationContext.xml" })
public class HydrogenDataRetrieverTest extends AbstractJUnit4SpringContextTests {

    private static final Logger LOG = LogManager.getLogger(HydrogenDataRetrieverTest.class.getName());

    @Autowired
    private HydrogenDataPusher pusher;

    @Autowired
    private HydrogenDataRetriever reader;

    private static final String TEST_FILE   = "/test_data/test_data.xml";

    @Test
    public void testFetchData() {
        try {
            List<HydrogenDto> fetchData = reader.fetchData();
            assertEquals(4, fetchData.size());
        } catch (Exception e) {
            LOG.error("Exception in testFetchData: "+e, e);
            Assert.fail();
        }
    }

    @Test
    public void testConvertData() {

        try {
            String responseString = getTestData();

            List<HydrogenDto> data = reader.convertResponseToInternalDTO(responseString);

            StationList stations = pusher.mapStations2Bdp(data);
            StationList plugs    = pusher.mapPlugs2Bdp(data);
            DataMapDto<RecordDtoImpl> map = pusher.mapData(data);
            DataMapDto<RecordDtoImpl> plugRec = pusher.mapPlugData2Bdp(data);

            assertEquals(2, stations.size());
            assertEquals(2, plugs.size());

        } catch (Exception e) {
            LOG.error("Exception in testConvertData: "+e, e);
            Assert.fail();
        }

    }

    public static String getTestData() {
        StringBuffer retval = new StringBuffer();

        Reader rr = null;
        BufferedReader br = null;

        try {
            LOG.debug("START read test data");
            String URL = HydrogenDataRetrieverTest.class.getResource(TEST_FILE).getFile();
            File file = new File(URL);
            String testFilePath = file.getAbsolutePath();
            rr = new FileReader(testFilePath);
            br = new BufferedReader(rr);
            String line = br.readLine();
            while ( line != null ) {
                retval.append(line);
                line = br.readLine();
            }
        } catch (Exception ex) {
            LOG.error("EXCEPTION in getTestData: "+ex);
        } finally {
            close(br);
            close(rr);
        }

        LOG.debug("END read test data:\n" + retval);
        return retval.toString();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static void close(Object obj) {
        if ( obj != null ) {
            try {
                Class clazz = obj.getClass();
                Method m = clazz.getMethod("close", (Class[])null);
                m.invoke(obj, (Object[])null);
            } catch (Exception ex) {
                LOG.error("EXCEPTION during close: "+ex);
            }
        }
    }

}
