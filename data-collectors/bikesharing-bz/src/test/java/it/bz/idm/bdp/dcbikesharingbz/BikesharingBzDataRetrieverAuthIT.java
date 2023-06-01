// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.idm.bdp.dcbikesharingbz;

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

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import it.bz.idm.bdp.dcbikesharingbz.dto.BikesharingBzDto;
import it.bz.idm.bdp.dcbikesharingbz.dto.BikesharingBzStationDto;
import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.StationList;

@ContextConfiguration(locations = { "classpath:/META-INF/spring/applicationContext.xml" })
public class BikesharingBzDataRetrieverAuthIT extends AbstractJUnit4SpringContextTests {

    private static final Logger LOG = LoggerFactory.getLogger(BikesharingBzDataRetrieverAuthIT.class.getName());

    @Autowired
    private BikesharingMappingUtil mappingUtil;

    @Autowired
    private BikesharingBzDataConverter converter;

//    @Autowired
//    private BikesharingBzDataRetriever reader;

    private static final String TEST_FILE_FETCH_STATIONS     = "/test_data/test_data_stations.json";
    private static final String TEST_FILE_FETCH_MEASUREMENTS = "/test_data/test_data_station_STATION_ID.json";

//    private static final String TEST_FILE_PUSH_STATIONS      = "/test_data/test_data_push_page_PAGE_NUM.json";
//    private static final String TEST_FILE_PUSH_MEASUREMENTS  = "/test_data/test_data_push_availab_STATION_ID.json";

    public static final String DATA_FETCH_STATIONS     = "FETCH_STATIONS";
    public static final String DATA_FETCH_MEASUREMENTS = "FETCH_MEASUREMENTS";

//    public static final String DATA_PUSH_STATIONS      = "PUSH_STATIONS";
//    public static final String DATA_PUSH_MEASUREMENTS  = "PUSH_MEASUREMENTS";

    private static final String TEST_STATION_ID_1 = "TEST_ID01";
    private static final String TEST_STATION_TYPE = BikesharingBzDataConverter.STATION_TYPE_STATION;

    private static final String TEST_BAY_ID_1     = "TEST_ID01_BAY01";
    private static final String TEST_BAY_TYPE     = BikesharingBzDataConverter.STATION_TYPE_BAY;

    private static final String TEST_BICYCLE_ID_1 = "TEST_ID01_BAY01_BIKE";
    private static final String TEST_BICYCLE_TYPE = BikesharingBzDataConverter.STATION_TYPE_BICYCLE;

    @Test
    public void testConvertDate() {
        String str1 = "2019-06-01T13:20:00CEST";
        String str2 = "2019-03-31T01:40:00CET";
        String str3 = "2019-01-12T20:50:00CET";
        String str4 = "2019-10-23T08:09:28+02:00";
        Date d1 = DCUtils.convertStringTimezoneToDate(str1);
        Date d2 = DCUtils.convertStringTimezoneToDate(str2);
        Date d3 = DCUtils.convertStringTimezoneToDate(str3);
        Date d4 = DCUtils.convertStringTimezoneToDate(str4);
        LOG.debug("d1="+d1);
        LOG.debug("d2="+d2);
        LOG.debug("d3="+d3);
        LOG.debug("d4="+d4);

        Date d9 = new Date();
        String str9_1 = DCUtils.convertDateToString(d9, "yyyy-MM-dd'T'HH:mm:ssX");
        String str9_2 = DCUtils.convertDateToString(d9, "yyyy-MM-dd'T'HH:mm:ssZ");
        String str9_3 = DCUtils.convertDateToString(d9, "yyyy-MM-dd'T'HH:mm:ss");
        LOG.debug("str9_1="+str9_1);
        LOG.debug("str9_2="+str9_2);
        LOG.debug("str9_3="+str9_3);

    }

    @Test
    public void testConvertStationData() {

        try {
            List<BikesharingBzStationDto> data = readFetchData();

            StationList stations = mappingUtil.mapStations2Bdp(data);

            //Test data is not null and contains 9 records
            assertNotNull(stations);
            assertEquals(9, stations.size());

            //Check that station list contains a station with ID=TEST_IDX and that input data is converted correctly
            StringBuffer errs = new StringBuffer();
            StationDto station = findStation(stations, TEST_STATION_ID_1);
            if ( station != null ) {
                checkEquals(TEST_STATION_ID_1                             , station.getId()            , errs, "STATION_1: ID is INCORRECT");
                checkEquals("TEST_Viale della Stazione - Bahnhofsallee"   , station.getName()          , errs, "STATION_1: NAME is INCORRECT");
                checkEquals(11.355516D                                    , station.getLongitude()     , errs, "STATION_1: LONGITUDE is INCORRECT");
                checkEquals(46.497459D                                    , station.getLatitude()      , errs, "STATION_1: LATITUDE is INCORRECT");
                checkEquals("BIKE_SHARING_BOLZANO"                        , station.getOrigin()        , errs, "STATION_1: ORIGIN is INCORRECT");
                checkEquals(TEST_STATION_TYPE                             , station.getStationType()   , errs, "STATION_1: STATION_TYPE is INCORRECT");
            }
            if ( station == null ) {
                Assert.fail("No station 1 found with id: " + TEST_STATION_ID_1);
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
    public void testConvertBayData() {

        try {
            List<BikesharingBzStationDto> data = readFetchData();

            StationList bays = mappingUtil.mapBays2Bdp(data);

            //Test data is not null
            assertNotNull(bays);

            //Check that station list contains a station with ID=TEST_IDX and that input data is converted correctly
            StringBuffer errs = new StringBuffer();
            StationDto bay = findStation(bays, TEST_BAY_ID_1);
            if ( bay != null ) {
                checkEquals(TEST_BAY_ID_1         , bay.getId()            , errs, "BAY_1: ID is INCORRECT");
                checkEquals(TEST_BAY_ID_1         , bay.getName()          , errs, "BAY_1: NAME is INCORRECT");
                checkEquals(11.355516D            , bay.getLongitude()     , errs, "BAY_1: LONGITUDE is INCORRECT");
                checkEquals(46.497459D            , bay.getLatitude()      , errs, "BAY_1: LATITUDE is INCORRECT");
                checkEquals("BIKE_SHARING_BOLZANO", bay.getOrigin()        , errs, "BAY_1: ORIGIN is INCORRECT");
                checkEquals(TEST_BAY_TYPE         , bay.getStationType()   , errs, "BAY_1: STATION_TYPE is INCORRECT");

                //Check if we have exactly 12 bays children of station 1
                int bayCounter = 0;
                for (StationDto bayItem : bays) {
                    String parentStationId = bayItem.getParentStation();
                    if ( TEST_STATION_ID_1.equals(parentStationId) ) {
                        bayCounter++;
                    }
                }
                checkEquals(12, bayCounter, errs, "STATION_1: BAYS_CONTER is INCORRECT");
            }
            if ( bay == null ) {
                Assert.fail("No bay 1 found with id: " + TEST_BAY_ID_1);
            }
            if ( errs.length() > 0 ) {
                Assert.fail("Bay converter failure: " + errs);
            }

        } catch (Exception e) {
            String msg = "Exception in testConvertStationData: " + e;
            LOG.error(msg, e);
            Assert.fail(msg);
        }

    }

    @Test
    public void testConvertBicycleData() {

        try {
            List<BikesharingBzStationDto> data = readFetchData();

            StationList bicycles = mappingUtil.mapBicycles2Bdp(data);

            //Test data is not null
            assertNotNull(bicycles);

            //Check that station list contains a station with ID=TEST_IDX and that input data is converted correctly
            StringBuffer errs = new StringBuffer();
            StationDto bicycle = findStation(bicycles, TEST_BICYCLE_ID_1);
            if ( bicycle != null ) {
                checkEquals(TEST_BICYCLE_ID_1     , bicycle.getId()            , errs, "BICYCLE_1: ID is INCORRECT");
                checkEquals("TEST_Sunshine"       , bicycle.getName()          , errs, "BICYCLE_1: NAME is INCORRECT");
                checkEquals(11.355516D            , bicycle.getLongitude()     , errs, "BICYCLE_1: LONGITUDE is INCORRECT");
                checkEquals(46.497459D            , bicycle.getLatitude()      , errs, "BICYCLE_1: LATITUDE is INCORRECT");
                checkEquals("BIKE_SHARING_BOLZANO", bicycle.getOrigin()        , errs, "BICYCLE_1: ORIGIN is INCORRECT");
                checkEquals(TEST_BICYCLE_TYPE     , bicycle.getStationType()   , errs, "BICYCLE_1: STATION_TYPE is INCORRECT");
            }
            if ( bicycle == null ) {
                Assert.fail("No bicycle 1 found with id: " + TEST_BAY_ID_1);
            }
            if ( errs.length() > 0 ) {
                Assert.fail("Bay converter failure: " + errs);
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

            //Get data of first Station
            List<BikesharingBzStationDto> data = readFetchData();

            //Convert in Data Hub data structure
            DataMapDto<RecordDtoImpl> stationRec = mappingUtil.mapData(data);

            //Check there is a branch for TEST_STATION_ID_1 station
            Map<String, DataMapDto<RecordDtoImpl>> branch1 = stationRec.getBranch();
            assertNotNull("StationMeasurements: Branch Level 1 is null", branch1);

            //Check there is a data map for TEST_STATION_ID_1 station
            DataMapDto<RecordDtoImpl> dataMapDto1 = branch1.get(TEST_STATION_ID_1);
            assertNotNull("StationMeasurements: DataMapDTO for station "+TEST_STATION_ID_1+" is null", dataMapDto1);

            //Check there is data measurements in the map
            StringBuffer errs = new StringBuffer();
            Map<String, DataMapDto<RecordDtoImpl>> branch2 = dataMapDto1.getBranch();
            assertNotNull("StationMeasurements: Branch Level 2 is null", branch2);

            Object valueAvailability = findMeasure(branch2, BikesharingBzDataConverter.DATA_TYPE_STATION_AVAILABILITY);
            Object valueNrAvailable  = findMeasure(branch2, BikesharingBzDataConverter.DATA_TYPE_STATION_NUMBER_AVAILABE);
            Object valueTotalBays    = findMeasure(branch2, BikesharingBzDataConverter.DATA_TYPE_STATION_TOTAL_BAYS);
            Object valueFreeBays     = findMeasure(branch2, BikesharingBzDataConverter.DATA_TYPE_STATION_FREE_BAYS);

            checkEquals("READY", valueAvailability , errs, "STATION_1: "+BikesharingBzDataConverter.DATA_TYPE_STATION_AVAILABILITY   +" is INCORRECT");
            checkEquals( 8L    , valueNrAvailable  , errs, "STATION_1: "+BikesharingBzDataConverter.DATA_TYPE_STATION_NUMBER_AVAILABE+" is INCORRECT");
            checkEquals(12L    , valueTotalBays    , errs, "STATION_1: "+BikesharingBzDataConverter.DATA_TYPE_STATION_TOTAL_BAYS     +" is INCORRECT");
            checkEquals( 3L    , valueFreeBays     , errs, "STATION_1: "+BikesharingBzDataConverter.DATA_TYPE_STATION_FREE_BAYS      +" is INCORRECT");

            if ( errs.length() > 0 ) {
                Assert.fail("Station converter failure: " + errs);
            }

        } catch (Exception e) {
            String msg = "Exception in testConvertStationMeasurements: " + e;
            LOG.error(msg, e);
            Assert.fail(msg);
        }

    }

    @Test
    public void testConvertBayMeasurements() {

        try {

            //Get data of first Bay
            List<BikesharingBzStationDto> data = readFetchData();

            //Convert in Data Hub data structure
            DataMapDto<RecordDtoImpl> stationRec = mappingUtil.mapBayData(data);

            //Check there is a branch for TEST_BAY_ID_1 station
            Map<String, DataMapDto<RecordDtoImpl>> branch1 = stationRec.getBranch();
            assertNotNull("BayMeasurements: Branch Level 1 is null", branch1);

            //Check there is a data map for TEST_BAY_ID_1 station
            DataMapDto<RecordDtoImpl> dataMapDto1 = branch1.get(TEST_BAY_ID_1);
            assertNotNull("BayMeasurements: DataMapDTO for station "+TEST_BAY_ID_1+" is null", dataMapDto1);

            //Check there is data measurements in the map
            StringBuffer errs = new StringBuffer();
            Map<String, DataMapDto<RecordDtoImpl>> branch2 = dataMapDto1.getBranch();
            assertNotNull("BayMeasurements: Branch Level 2 is null", branch2);

            Object valueAvailability = findMeasure(branch2, BikesharingBzDataConverter.DATA_TYPE_BAY_AVAILABILITY);
            Object valueUsageState   = findMeasure(branch2, BikesharingBzDataConverter.DATA_TYPE_BAY_USAGE_STATE);

            checkEquals("READY"   , valueAvailability , errs, "BAY_1: "+BikesharingBzDataConverter.DATA_TYPE_BAY_AVAILABILITY  +" is INCORRECT");
            checkEquals("PRELIEVO", valueUsageState   , errs, "BAY_1: "+BikesharingBzDataConverter.DATA_TYPE_BAY_USAGE_STATE   +" is INCORRECT");

            if ( errs.length() > 0 ) {
                Assert.fail("Bay converter failure: " + errs);
            }

        } catch (Exception e) {
            String msg = "Exception in testConvertBayMeasurements: " + e;
            LOG.error(msg, e);
            Assert.fail(msg);
        }

    }


    @Test
    public void testConvertBicycleMeasurements() {

        try {

            //Get data of first Bay
            List<BikesharingBzStationDto> data = readFetchData();

            //Convert in Data Hub data structure
            DataMapDto<RecordDtoImpl> stationRec = mappingUtil.mapBicycleData(data);

            //Check there is a branch for TEST_BICYCLE_ID_1 station
            Map<String, DataMapDto<RecordDtoImpl>> branch1 = stationRec.getBranch();
            assertNotNull("BicycleMeasurements: Branch Level 1 is null", branch1);

            //Check there is a data map for TEST_BICYCLE_ID_1 station
            DataMapDto<RecordDtoImpl> dataMapDto1 = branch1.get(TEST_BICYCLE_ID_1);
            assertNotNull("BicycleMeasurements: DataMapDTO for station "+TEST_BICYCLE_ID_1+" is null", dataMapDto1);

            //Check there is data measurements in the map
            StringBuffer errs = new StringBuffer();
            Map<String, DataMapDto<RecordDtoImpl>> branch2 = dataMapDto1.getBranch();
            assertNotNull("BicycleMeasurements: Branch Level 2 is null", branch2);

            Object valueAvailability = findMeasure(branch2, BikesharingBzDataConverter.DATA_TYPE_BICYCLE_AVAILABILITY);
            Object valueBatteryState = findMeasure(branch2, BikesharingBzDataConverter.DATA_TYPE_BICYCLE_BATTERY_STATE);

            checkEquals("1"       , valueAvailability , errs, "BICYCLE_1: "+BikesharingBzDataConverter.DATA_TYPE_BICYCLE_AVAILABILITY  +" is INCORRECT");
            checkEquals("CHARGED" , valueBatteryState , errs, "BICYCLE_1: "+BikesharingBzDataConverter.DATA_TYPE_BICYCLE_BATTERY_STATE +" is INCORRECT");

            if ( errs.length() > 0 ) {
                Assert.fail("Bicycle converter failure: " + errs);
            }

        } catch (Exception e) {
            String msg = "Exception in testConvertBayMeasurements: " + e;
            LOG.error(msg, e);
            Assert.fail(msg);
        }

    }

    private StationDto findStation(StationList stations, String stationId) {
        StationDto station = null;
        for ( int i=0 ; station==null && i<stations.size() ; i++ ) {
            StationDto item = stations.get(i);
            String id = item.getId();
            if ( stationId.equals(id) ) {
                station = item;
            }
        }
        return station;
    }

    private Object findMeasure(Map<String, DataMapDto<RecordDtoImpl>> branch, String dataTypeName) {
        if ( branch == null || dataTypeName == null ) {
            return null;
        }
        DataMapDto<RecordDtoImpl> dataMapDto = branch.get(dataTypeName);
        List<RecordDtoImpl> data = dataMapDto!=null ? dataMapDto.getData() : null;
        RecordDtoImpl recordDtoImpl = data!=null && data.size()>0 ? data.get(0) : null;
        Object value = recordDtoImpl!=null ? recordDtoImpl.getValue() : null;
        return value;
    }

    private List<BikesharingBzStationDto> readFetchData() throws Exception {
        //Convert station data
        String responseString = getTestData(DATA_FETCH_STATIONS, null, null);
        BikesharingBzDto bikesharingBzDto = converter.convertStationsResponseToInternalDTO(responseString);
        List<BikesharingBzStationDto> data = bikesharingBzDto.getStationList();

        List<BikesharingBzStationDto> retval = new ArrayList<>();
        Long tsNow = System.currentTimeMillis();

        //Convert availability data
        for (BikesharingBzStationDto stationDto : data) {
            String stationId = stationDto.getId();
            String responseStringDetails = getTestData(DATA_FETCH_MEASUREMENTS, ServiceCallParam.FUNCTION_NAME_STATION_ID, stationId);
            if ( DCUtils.paramNotNull(responseStringDetails) ) {
                BikesharingBzStationDto stationDetails = converter.convertStationDetailResponseToInternalDTO(responseStringDetails);
                stationDetails.setMeasurementTimestamp(tsNow);
                retval.add(stationDetails);
            }
        }

        return retval;
    }

    public static String getTestData(String dataType, String paramName, String paramValue) {
        StringBuffer retval = new StringBuffer();

        Reader rr = null;
        BufferedReader br = null;

        try {
            LOG.debug("START read test data");
            String fileName =
//                    DATA_PUSH_STATIONS.equals(dataType)      ? TEST_FILE_PUSH_STATIONS :
//                    DATA_PUSH_MEASUREMENTS.equals(dataType)  ? TEST_FILE_PUSH_MEASUREMENTS :
                    DATA_FETCH_STATIONS.equals(dataType)     ? TEST_FILE_FETCH_STATIONS :
                    DATA_FETCH_MEASUREMENTS.equals(dataType) ? TEST_FILE_FETCH_MEASUREMENTS :
                    TEST_FILE_FETCH_STATIONS;
            if ( paramName != null && paramValue != null ) {
                fileName = fileName.replace(paramName, paramValue);
            }
            URL url = BikesharingBzDataRetrieverAuthIT.class.getResource(fileName);
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
