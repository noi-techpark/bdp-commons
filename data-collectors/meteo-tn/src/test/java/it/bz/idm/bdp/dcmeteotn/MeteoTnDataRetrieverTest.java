package it.bz.idm.bdp.dcmeteotn;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import it.bz.idm.bdp.dcmeteotn.dto.MeteoTnDto;
import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.SimpleRecordDto;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.StationList;

@ContextConfiguration(locations = { "classpath:/META-INF/spring/applicationContext.xml" })
public class MeteoTnDataRetrieverTest extends AbstractJUnit4SpringContextTests {

    private static final Logger LOG = LogManager.getLogger(MeteoTnDataRetrieverTest.class.getName());

    @Autowired
    private MeteoTnDataPusher pusher;

    @Autowired
    private MeteoTnDataRetriever reader;

    private static final String TEST_FILE_FETCH_STATIONS     = "/test_data/test_data_fetch_stations.xml";
    private static final String TEST_FILE_FETCH_MEASUREMENTS = "/test_data/test_data_fetch_measurements.xml";
    private static final String TEST_FILE_PUSH_STATIONS      = "/test_data/test_data_push_stations.xml";
    private static final String TEST_FILE_PUSH_MEASUREMENTS  = "/test_data/test_data_push_measurements.xml";

    public static final String DATA_FETCH_STATIONS     = "FETCH_STATIONS";
    public static final String DATA_FETCH_MEASUREMENTS = "FETCH_MEASUREMENTS";
    public static final String DATA_PUSH_STATIONS      = "PUSH_STATIONS";
    public static final String DATA_PUSH_MEASUREMENTS  = "PUSH_MEASUREMENTS";

    public static final String MUNICIPALITY = "FETCH_MUNICIPALITY";
    private static final String TEST_STATION_ID_1 = "ST_001";
    private static final String TEST_STATION_ID_2 = "ST_002";
    private static final Date TEST_DATE_LAST_RECORD = DCUtils.convertStringTimezoneToDate("2019-01-18T14:00:00+01");
    private static final Integer TEST_PERIOD = new Integer(900);

    private static final String TEST_STATION_DATA_TYPE_1 = "air_temperature";
    private static final String TEST_STATION_DATA_TYPE_2 = "precipitation";
    private static final String TEST_STATION_DATA_TYPE_3 = "wind10m_speed";
    private static final String TEST_STATION_DATA_TYPE_4 = "wind10m_direction";
    private static final String TEST_STATION_DATA_TYPE_5 = "global_radiation";
    private static final String TEST_STATION_DATA_TYPE_6 = "relative_humidity";
    private static final String TEST_STATION_DATA_TYPE_7 = "snow_depth";

    @Test
    public void testConvertStationData() {

        try {
            String responseString = getTestData(DATA_FETCH_STATIONS);

            List<MeteoTnDto> data = reader.convertStationsResponseToInternalDTO(responseString);

            StationList stations = pusher.mapStations2Bdp(data);

            //Test data contains 2 records
            assertEquals(2, data.size());

            //Test data contains 1 station (1 station is rejected due to enddate!=null)
            assertEquals(1, stations.size());

            //Check that station list contains a station with ID=TEST_IDX and that input data is converted correctly
            StringBuffer errs = new StringBuffer();
            boolean station1Found = false;
            boolean station2Found = false;
            boolean allFound = false;
            for ( int i=0 ; !allFound && i<stations.size() ; i++ ) {
                StationDto station = stations.get(i);
                String id = station.getId();
                if ( TEST_STATION_ID_1.equals(id) ) {
                    station1Found = true;
                }
                if ( TEST_STATION_ID_2.equals(id) ) {
                    station2Found = true;
                    checkEquals(TEST_STATION_ID_2    , station.getId()            , errs, "STATION_2: ID is INCORRECT");
                    checkEquals("Ala (Maso Le Pozze)", station.getName()          , errs, "STATION_2: NAME is INCORRECT");
                    checkEquals(11.023828D           , station.getLongitude()     , errs, "STATION_2: LONGITUDE is INCORRECT");
                    checkEquals(45.786137D           , station.getLatitude()      , errs, "STATION_2: LATITUDE is INCORRECT");
                    checkEquals("meteotrentino"      , station.getOrigin()        , errs, "STATION_2: ORIGIN is INCORRECT");
                    checkEquals("Meteostation"       , station.getStationType()   , errs, "STATION_2: STATION_TYPE is INCORRECT");
                }
                allFound = !station1Found && station2Found;
            }
            if ( station1Found ) {
                Assert.fail("INVALID Station 1 found with id: " + TEST_STATION_ID_1);
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
            String responseString = getTestData(DATA_FETCH_MEASUREMENTS);
            Map<String, String> stationAttrs = new HashMap<String, String>();
            stationAttrs.put("code", TEST_STATION_ID_1);

            MeteoTnDto data = reader.convertMeasurementsResponseToInternalDTO(responseString, stationAttrs);
            data.setLastSavedRecord(TEST_DATE_LAST_RECORD);

            DataMapDto<RecordDtoImpl> stationRec = pusher.mapSingleStationData2Bdp(data);

            //Check there is a branch for TEST_STATION_ID_1 station
            Map<String, DataMapDto<RecordDtoImpl>> branch1 = stationRec.getBranch();
            assertNotNull("StationMeasurements: Branch Level 1 is null", branch1);

            //Check there is a data map for TEST_STATION_ID_1 station
            DataMapDto<RecordDtoImpl> dataMapDto1 = branch1.get(TEST_STATION_ID_1);
            assertNotNull("StationMeasurements: DataMapDTO for station "+TEST_STATION_ID_1+" is null", dataMapDto1);

            //Check there is data measurements in the map
            Map<String, DataMapDto<RecordDtoImpl>> branch2_1 = dataMapDto1.getBranch();
            DataMapDto<RecordDtoImpl> dataMapDto2_1 = branch2_1.get(TEST_STATION_DATA_TYPE_1);
            DataMapDto<RecordDtoImpl> dataMapDto2_2 = branch2_1.get(TEST_STATION_DATA_TYPE_2);
            DataMapDto<RecordDtoImpl> dataMapDto2_3 = branch2_1.get(TEST_STATION_DATA_TYPE_3);
            DataMapDto<RecordDtoImpl> dataMapDto2_4 = branch2_1.get(TEST_STATION_DATA_TYPE_4);
            DataMapDto<RecordDtoImpl> dataMapDto2_5 = branch2_1.get(TEST_STATION_DATA_TYPE_5);
            DataMapDto<RecordDtoImpl> dataMapDto2_6 = branch2_1.get(TEST_STATION_DATA_TYPE_6);
            DataMapDto<RecordDtoImpl> dataMapDto2_7 = branch2_1.get(TEST_STATION_DATA_TYPE_7);

            StringBuffer errs = new StringBuffer();

            //Types 1..6 must not be empty, type 7 should be empty
            checkNotEmpty(dataMapDto2_1          , errs, "Measures for type "+TEST_STATION_DATA_TYPE_1);
            checkNotEmpty(dataMapDto2_2          , errs, "Measures for type "+TEST_STATION_DATA_TYPE_2);
            checkNotEmpty(dataMapDto2_3          , errs, "Measures for type "+TEST_STATION_DATA_TYPE_3);
            checkNotEmpty(dataMapDto2_4          , errs, "Measures for type "+TEST_STATION_DATA_TYPE_4);
            checkNotEmpty(dataMapDto2_5          , errs, "Measures for type "+TEST_STATION_DATA_TYPE_5);
            checkNotEmpty(dataMapDto2_6          , errs, "Measures for type "+TEST_STATION_DATA_TYPE_6);
            checkNotExists(dataMapDto2_7         , errs, "Measures for type "+TEST_STATION_DATA_TYPE_7);

            //Check data older than TEST_DATE_LAST_RECORD is removed from the list
            Long lastTimestamp = TEST_DATE_LAST_RECORD.getTime();
            if ( dataMapDto2_1!=null && dataMapDto2_1.getData()!=null && dataMapDto2_1.getData().size()>0 ) {
                for (RecordDtoImpl record : dataMapDto2_1.getData()) {
                    Long recordTimestamp = record.getTimestamp();
                    if ( recordTimestamp == null ) {
                        errs.append("\n - Timestamp in measure IS NULL (should be NOT NULL)");
                    } else {
                        if ( lastTimestamp > recordTimestamp ) {
                            errs.append("\n - Timestamp in measure IS OLDER than last record. Last_Record="+lastTimestamp+"  Curr_Record="+recordTimestamp);
                        }
                    }
                    if ( record instanceof SimpleRecordDto ) {
                        SimpleRecordDto simpleRecord = (SimpleRecordDto) record;
                        Integer recordPeriod = simpleRecord.getPeriod();
                        checkEquals(TEST_PERIOD    , recordPeriod            , errs, "PERIOD is INCORRECT");
                    } else {
                        errs.append("\n - Measure is not of type SimpleRecordDto: "+record.getClass());
                    }
                }
            }

            if ( errs.length() > 0 ) {
                Assert.fail("Station converter failure: " + errs);
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
                    DATA_PUSH_STATIONS.equals(dataType) ? TEST_FILE_PUSH_STATIONS :
                    DATA_PUSH_MEASUREMENTS.equals(dataType) ? TEST_FILE_PUSH_MEASUREMENTS :
                    DATA_FETCH_STATIONS.equals(dataType) ? TEST_FILE_FETCH_STATIONS :
                    DATA_FETCH_MEASUREMENTS.equals(dataType) ? TEST_FILE_FETCH_MEASUREMENTS :
                    TEST_FILE_FETCH_STATIONS;
            String URL = MeteoTnDataRetrieverTest.class.getResource(fileName).getFile();
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
            sb.append("\n - "+errMsg+" (EXPECTED:'"+expected+"'  ACTUAL:'"+actual+"')");
            return;
        }
        if ( expected==null && actual!=null ) {
            sb.append("\n - "+errMsg+" (EXPECTED:'"+expected+"'  ACTUAL:'"+actual+"')");
            return;
        }
        if ( expected.getClass() != actual.getClass() ) {
            sb.append("\n - "+errMsg+" (EXPECTED:'"+expected+"'  ACTUAL:'"+actual+"')");
            return;
        }
        if ( !expected.equals(actual) ) {
            sb.append("\n - "+errMsg+" (EXPECTED:'"+expected+"'  ACTUAL:'"+actual+"')");
            return;
        }
    }

    private void checkNotExists(Object actual, StringBuffer sb, String errMsg) {
        if (actual != null ) {
            sb.append("\n - "+errMsg+" IS NOT NULL (should be NULL)");
            return;
        }
    }

    private void checkNotEmpty(DataMapDto<RecordDtoImpl> dataMap, StringBuffer sb, String errMsg) {
        if (dataMap == null ) {
            sb.append("\n - "+errMsg+" IS NULL (should be NOT NULL)");
            return;
        }
        List<RecordDtoImpl> data = dataMap.getData();
        if ( data==null || data.size()==0 ) {
            sb.append("\n - "+errMsg+" IS EMPTY (should contain some data)");
        }
    }

}
