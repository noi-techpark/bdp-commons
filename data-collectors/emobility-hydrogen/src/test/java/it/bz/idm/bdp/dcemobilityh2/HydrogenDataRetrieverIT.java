package it.bz.idm.bdp.dcemobilityh2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

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
import it.bz.idm.bdp.dto.SimpleRecordDto;

@ContextConfiguration(locations = { "classpath:/META-INF/spring/applicationContext.xml" })
public class HydrogenDataRetrieverIT extends AbstractJUnit4SpringContextTests {

    private static final Logger LOG = LogManager.getLogger(HydrogenDataRetrieverIT.class.getName());

    @Autowired
    private HydrogenDataPusher pusher;

    @Autowired
    private HydrogenDataRetriever reader;

    @Test
    public void testFetchData() {
        try {
            //Fetch data from source origin
            List<HydrogenDto> fetchData = reader.fetchData();

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

    private static final String TEST_FILE_FETCH = "/test_data/test_data_fetch.xml";
    private static final String TEST_FILE_PUSH  = "/test_data/test_data_push.xml";

    private static final String TEST_STATION_ID = "TEST_IDX";
    private static final String TEST_PLUG_ID    = "TEST_IDX-1";
    private static final String TEST_STATION_DATA_TYPE = "number-available";
    private static final String TEST_PLUG_DATA_TYPE = "echarging-plug-status";

    public static final String DATA_FETCH = "FETCH";
    public static final String DATA_PUSH  = "PUSH";


    @Test
    public void testConvertStationMeasurements() {

        try {
            String responseString = getTestData(DATA_FETCH);

            List<HydrogenDto> data = reader.convertResponseToInternalDTO(responseString);

            DataMapDto<RecordDtoImpl> stationRec = pusher.mapData(data);

            //Check that there is a branch for TEST_IDX station that contains a measurement for "number-available" Type
            Map<String, DataMapDto<RecordDtoImpl>> branch1 = stationRec.getBranch();
            assertNotNull("StationMeasurements: Branch Level 1 is null", branch1);

            DataMapDto<RecordDtoImpl> dataMapDto1 = branch1.get(TEST_STATION_ID);
            assertNotNull("StationMeasurements: DataMapDTO for station "+TEST_STATION_ID+" is null", dataMapDto1);

            Map<String, DataMapDto<RecordDtoImpl>> branch2 = dataMapDto1.getBranch();
            assertNotNull("StationMeasurements: Branch Level 2 is null", branch2);

            DataMapDto<RecordDtoImpl> dataMapDto2 = branch2.get(TEST_STATION_DATA_TYPE);
            assertNotNull("StationMeasurements: DataMapDTO for DataType "+TEST_STATION_DATA_TYPE+" is null", dataMapDto2);

            List<RecordDtoImpl> data2 = dataMapDto2.getData();
            assertNotNull("StationMeasurements: Records for DataType "+TEST_STATION_DATA_TYPE+" is null", data2);

            if ( data2.size() > 0 ) {
                SimpleRecordDto record = (SimpleRecordDto) data2.get(0);
                Object value = record.getValue();
                assertEquals("StationMeasurements: measurement value is wrong", 1D, value);
            } else {
                Assert.fail("No StationMeasurements found");
            }

        } catch (Exception e) {
            String msg = "Exception in testConvertStationMeasurements: " + e;
            LOG.error(msg, e);
            Assert.fail(msg);
        }

    }

    @Test
    public void testConvertPlugMeasurements() {

        try {
            String responseString = getTestData(DATA_FETCH);

            List<HydrogenDto> data = reader.convertResponseToInternalDTO(responseString);

            DataMapDto<RecordDtoImpl> plugRec = pusher.mapPlugData2Bdp(data);

            //Check that there is a branch for TEST_IDX-1 plug that contains a measurement for "available-state" Type
            Map<String, DataMapDto<RecordDtoImpl>> branch1 = plugRec.getBranch();
            assertNotNull("PlugMeasurements: Branch Level 1 is null", branch1);

            DataMapDto<RecordDtoImpl> dataMapDto1 = branch1.get(TEST_PLUG_ID);
            assertNotNull("PlugMeasurements: DataMapDTO for plug "+TEST_PLUG_ID+" is null", dataMapDto1);

            Map<String, DataMapDto<RecordDtoImpl>> branch2 = dataMapDto1.getBranch();
            assertNotNull("PlugMeasurements: Branch Level 2 is null", branch2);

            DataMapDto<RecordDtoImpl> dataMapDto2 = branch2.get(TEST_PLUG_DATA_TYPE);
            assertNotNull("PlugMeasurements: DataMapDTO for DataType "+TEST_PLUG_DATA_TYPE+" is null", dataMapDto2);

            List<RecordDtoImpl> data2 = dataMapDto2.getData();
            assertNotNull("PlugMeasurements: Records for DataType "+TEST_PLUG_DATA_TYPE+" is null", data2);

            if ( data2.size() > 0 ) {
                SimpleRecordDto record = (SimpleRecordDto) data2.get(0);
                Object value = record.getValue();
                assertEquals("PlugMeasurements: measurement value is wrong", 1D, value);
            } else {
                Assert.fail("No PlugMeasurements found");
            }

        } catch (Exception e) {
            String msg = "Exception in testConvertPlugMeasurements: " + e;
            LOG.error(msg, e);
            Assert.fail(msg);
        }

    }

    public static String getTestData(String dataType) {
        StringBuffer retval = new StringBuffer();

        Reader rr = null;
        BufferedReader br = null;

        try {
            LOG.debug("START read test data");
            String fileName =
                    DATA_PUSH.equals(dataType) ? TEST_FILE_PUSH :
                    DATA_FETCH.equals(dataType) ? TEST_FILE_FETCH :
                    TEST_FILE_FETCH;
            String URL = HydrogenDataRetrieverIT.class.getResource(fileName).getFile();
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

    public static void checkEquals(Object o1, Object o2, StringBuffer sb, String errMsg) {
        if ( o1 == o2 ) {
            return;
        }
        if ( o1!=null && o2==null ) {
            sb.append(" - "+errMsg);
            return;
        }
        if ( o1==null && o2!=null ) {
            sb.append(" - "+errMsg);
            return;
        }
        if ( o1.getClass() != o2.getClass() ) {
            sb.append(" - "+errMsg);
            return;
        }
        if ( !o1.equals(o2) ) {
            sb.append(" - "+errMsg+" ("+o2+")");
            return;
        }
    }

}
