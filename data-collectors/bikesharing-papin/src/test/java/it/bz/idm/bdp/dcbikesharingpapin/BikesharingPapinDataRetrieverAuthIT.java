package it.bz.idm.bdp.dcbikesharingpapin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import it.bz.idm.bdp.dcbikesharingpapin.dto.BikesharingPapinDto;
import it.bz.idm.bdp.dcbikesharingpapin.dto.BikesharingPapinStationDto;
import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.StationList;

@ContextConfiguration(locations = { "classpath:/META-INF/spring/applicationContext.xml" })
public class BikesharingPapinDataRetrieverAuthIT extends AbstractJUnit4SpringContextTests {

    private static final Logger LOG = LogManager.getLogger(BikesharingPapinDataRetrieverAuthIT.class.getName());

    @Autowired
    private BikesharingMappingUtil mappingUtil;

    @Autowired
    private BikesharingPapinDataConverter converter;

//    @Autowired
//    private BikesharingPapinDataRetriever reader;

    private static final String TEST_FILE_FETCH_STATIONS     = "/test_data/test_data_stations.json";
    public static final String DATA_FETCH_STATIONS     = "FETCH_STATIONS";

// public static final String DATA_PUSH_STATIONS      = "PUSH_STATIONS";

    @Test
    public void testConvertStationData() {

        try {
            List<BikesharingPapinStationDto> data = readFetchData();

            StationList stations = mappingUtil.mapStations2Bdp(data);

            //Test data is not null and contains 137 records
            assertNotNull(stations);
            assertEquals(137, stations.size());

        } catch (Exception e) {
            String msg = "Exception in testConvertStationData: " + e;
            LOG.error(msg, e);
            Assert.fail(msg);
        }

    }

    private List<BikesharingPapinStationDto> readFetchData() throws Exception {
        //Convert station data
        String responseString = getTestData(DATA_FETCH_STATIONS, null, null);
        BikesharingPapinDto BikesharingPapinDto = converter.convertStationsResponseToInternalDTO(responseString);
        List<BikesharingPapinStationDto> data = BikesharingPapinDto.getStationList();

        return data;
    }

    public static String getTestData(String dataType, String paramName, String paramValue) {
        StringBuffer retval = new StringBuffer();

        Reader rr = null;
        BufferedReader br = null;

        try {
            LOG.debug("START read test data");
            String fileName = TEST_FILE_FETCH_STATIONS;
            if ( paramName != null && paramValue != null ) {
                fileName = fileName.replace(paramName, paramValue);
            }
            URL url = BikesharingPapinDataRetrieverAuthIT.class.getResource(fileName);
            if ( url == null ) {
                return null;
            }
            String filePath = url.getFile();
            File file = new File(filePath);
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
