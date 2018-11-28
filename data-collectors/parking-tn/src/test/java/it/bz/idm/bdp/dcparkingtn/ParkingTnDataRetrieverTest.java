package it.bz.idm.bdp.dcparkingtn;

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

import it.bz.idm.bdp.dcparkingtn.dto.ParkingTnDto;
import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.SimpleRecordDto;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.StationList;

@ContextConfiguration(locations = { "classpath:/META-INF/spring/applicationContext.xml" })
public class ParkingTnDataRetrieverTest extends AbstractJUnit4SpringContextTests {

    private static final Logger LOG = LogManager.getLogger(ParkingTnDataRetrieverTest.class.getName());

    @Autowired
    private ParkingTnDataPusher pusher;

    @Autowired
    private ParkingTnDataRetriever reader;

    private static final String TEST_FILE_FETCH = "/test_data/test_data_fetch.json";
    private static final String TEST_FILE_PUSH  = "/test_data/test_data_push_TRENTO.json";

    private static final String TEST_STATION_ID_1 = "FETCH_PREFIX:name1";
    private static final String TEST_STATION_ID_2 = "FETCH_PREFIX:name2";
    private static final String TEST_STATION_DATA_TYPE = "number-available";

    public static final String DATA_FETCH = "FETCH";
    public static final String DATA_PUSH  = "PUSH";
    public static final String MUNICIPALITY = "FETCH_MUNICIPALITY";
    public static final String CODE_PREFIX = "FETCH_PREFIX:";

    @Test
    public void testConvertStationData() {

        try {
            String responseString = getTestData(DATA_FETCH);

            List<ParkingTnDto> data = reader.convertResponseToInternalDTO(responseString, MUNICIPALITY, CODE_PREFIX);

            StationList stations = pusher.mapStations2Bdp(data);

            //Test data contains 4 records
            assertEquals(4, data.size());

            //Test data contains 3 stations (1 station is rejected due to available=-2)
            assertEquals(3, stations.size());

            //Check that station list contains a station with ID=TEST_IDX and that input data is converted correctly 
            StringBuffer errs = new StringBuffer();
            boolean station1Found = false;
            boolean station2Found = false;
            boolean allFound = false;
            for ( int i=0 ; !allFound && i<stations.size() ; i++ ) {
                StationDto station =  stations.get(i);
                String id = station.getId();
                if ( TEST_STATION_ID_1.equals(id) ) {
                    station1Found = true;
                    checkEquals(TEST_STATION_ID_1    , station.getId()            , errs, "STATION_1: ID is INCORRECT");
                    checkEquals("NAME - 1"           , station.getName()          , errs, "STATION_1: NAME is INCORRECT");
                    checkEquals(11D                  , station.getLongitude()     , errs, "STATION_1: LONGITUDE is INCORRECT");
                    checkEquals(46D                  , station.getLatitude()      , errs, "STATION_1: LATITUDE is INCORRECT");
                    checkEquals("FETCH_MUNICIPALITY" , station.getMetaData().get("municipality"), errs, "STATION_1: MUNICIPALITY is INCORRECT");
                    checkEquals("FBK"                , station.getOrigin()        , errs, "STATION_1: ORIGIN is INCORRECT");
                    checkEquals("ParkingStation"     , station.getStationType()   , errs, "STATION_1: STATION_TYPE is INCORRECT");
                }
                if ( TEST_STATION_ID_2.equals(id) ) {
                    station2Found = true;
                    checkEquals(TEST_STATION_ID_2    , station.getId()            , errs, "STATION_2: ID is INCORRECT");
                    checkEquals("NAME 2"             , station.getName()          , errs, "STATION_2: NAME is INCORRECT");
                    checkEquals(11D                  , station.getLongitude()     , errs, "STATION_2: LONGITUDE is INCORRECT");
                    checkEquals(46D                  , station.getLatitude()      , errs, "STATION_2: LATITUDE is INCORRECT");
                    checkEquals("FETCH_MUNICIPALITY" , station.getMetaData().get("municipality")  , errs, "STATION_2: MUNICIPALITY is INCORRECT");
                    checkEquals("FBK"                , station.getOrigin()        , errs, "STATION_2: ORIGIN is INCORRECT");
                    checkEquals("ParkingStation"     , station.getStationType()   , errs, "STATION_2: STATION_TYPE is INCORRECT");
                }
                allFound = station1Found && station2Found;
            }
            if ( !station1Found ) {
                Assert.fail("No station 1 found with id: " + TEST_STATION_ID_1);
            }
            if ( !station2Found ) {
                Assert.fail("No station 2 found with id: " + TEST_STATION_ID_2);
            }
            if ( errs.length() > 0 ) {
                Assert.fail("Station converter failure: " + errs);
            }

        } catch (Exception e) {
            String msg = "Exception in testConvertStationData: " + e;
            LOG.error(msg, e);
            Assert.fail(msg);
        }

    }

    @Test
    public void testConvertStationMeasurements() {

        try {
            String responseString = getTestData(DATA_FETCH);

            List<ParkingTnDto> data = reader.convertResponseToInternalDTO(responseString, MUNICIPALITY, CODE_PREFIX);

            DataMapDto<RecordDtoImpl> stationRec = pusher.mapData(data);

            //Check that there is a branch for TEST_IDX station that contains a measurement for "number-available" Type
            Map<String, DataMapDto<RecordDtoImpl>> branch1 = stationRec.getBranch();
            assertNotNull("StationMeasurements: Branch Level 1 is null", branch1);

            DataMapDto<RecordDtoImpl> dataMapDto1 = branch1.get(TEST_STATION_ID_1);
            assertNotNull("StationMeasurements: DataMapDTO for station "+TEST_STATION_ID_1+" is null", dataMapDto1);

            List<RecordDtoImpl> data1 = dataMapDto1.getData();
            assertNotNull("StationMeasurements: Records for DataType "+TEST_STATION_DATA_TYPE+" is null", data1);

            if ( data1.size() > 0 ) {
                SimpleRecordDto record = (SimpleRecordDto) data1.get(0);
                Object value = record.getValue();
                assertEquals("StationMeasurements: measurement value is wrong", 50, value);
            } else {
                Assert.fail("No StationMeasurements found");
            }

        } catch (Exception e) {
            String msg = "Exception in testConvertStationMeasurements: " + e;
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
            String URL = ParkingTnDataRetrieverTest.class.getResource(fileName).getFile();
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

    private void checkEquals(Object expected, Object actual, StringBuffer sb, String errMsg) {
        if ( expected == actual ) {
            return;
        } 
        if ( expected!=null && actual==null ) {
            sb.append(" - "+errMsg+" (EXPECTED:'"+expected+"'  ACTUAL:'"+actual+"')");
            return;
        }
        if ( expected==null && actual!=null ) {
            sb.append(" - "+errMsg+" (EXPECTED:'"+expected+"'  ACTUAL:'"+actual+"')");
            return;
        }
        if ( expected.getClass() != actual.getClass() ) {
            sb.append(" - "+errMsg+" (EXPECTED:'"+expected+"'  ACTUAL:'"+actual+"')");
            return;
        }
        if ( !expected.equals(actual) ) {
            sb.append(" - "+errMsg+" (EXPECTED:'"+expected+"'  ACTUAL:'"+actual+"')");
            return;
        }
    }

}
