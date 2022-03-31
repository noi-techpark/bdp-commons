package it.bz.idm.bdp.dcbikesharingmoqo;

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

import it.bz.idm.bdp.dcbikesharingmoqo.dto.AvailabilityDto;
import it.bz.idm.bdp.dcbikesharingmoqo.dto.BikeDto;
import it.bz.idm.bdp.dcbikesharingmoqo.dto.BikesharingMoqoPageDto;
import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.StationList;

@ContextConfiguration(locations = { "classpath:/META-INF/spring/applicationContext.xml" })
public class BikesharingMoqoDataRetrieverTestIT extends AbstractJUnit4SpringContextTests {

    private static final Logger LOG = LoggerFactory.getLogger(BikesharingMoqoDataRetrieverTestIT.class.getName());

    @Autowired
    private BikesharingMoqoDataPusher pusher;

    @Autowired
    private BikesharingMoqoDataConverter converter;

//    @Autowired
//    private BikesharingMoqoDataRetriever reader;

    private static final String TEST_FILE_FETCH_STATIONS     = "/test_data/test_data_fetch_page_PAGE_NUM.json";
    private static final String TEST_FILE_FETCH_MEASUREMENTS = "/test_data/test_data_fetch_availab_STATION_ID.json";

    private static final String TEST_FILE_PUSH_STATIONS      = "/test_data/test_data_push_page_PAGE_NUM.json";
    private static final String TEST_FILE_PUSH_MEASUREMENTS  = "/test_data/test_data_push_availab_STATION_ID.json";

    public static final String DATA_FETCH_STATIONS     = "FETCH_STATIONS";
    public static final String DATA_FETCH_MEASUREMENTS = "FETCH_MEASUREMENTS";

    public static final String DATA_PUSH_STATIONS      = "PUSH_STATIONS";
    public static final String DATA_PUSH_MEASUREMENTS  = "PUSH_MEASUREMENTS";

    private static final String TEST_STATION_ID_1 = "825813160";
    private static final String TEST_STATION_TYPE = BikesharingMoqoDataConverter.STATION_TYPE;

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
            List<BikeDto> data = readFetchData();

            StationList stations = pusher.mapStations2Bdp(data);

            //Test data contains 4 records
            assertEquals(10, data.size());

            //Check that station list contains a station with ID=TEST_IDX and that input data is converted correctly
            StringBuffer errs = new StringBuffer();
            boolean station1Found = false;
            for ( int i=0 ; !station1Found && i<stations.size() ; i++ ) {
                StationDto station = stations.get(i);
                String id = station.getId();
                if ( TEST_STATION_ID_1.equals(id) ) {
                    station1Found = true;
                    checkEquals(TEST_STATION_ID_1    , station.getId()            , errs, "STATION_1: ID is INCORRECT");
                    checkEquals("City Standard 60"   , station.getName()          , errs, "STATION_1: NAME is INCORRECT");
                    checkEquals(11.1500811721644D    , station.getLongitude()     , errs, "STATION_1: LONGITUDE is INCORRECT");
                    checkEquals(46.6724745760751D    , station.getLatitude()      , errs, "STATION_1: LATITUDE is INCORRECT");
                    checkEquals("BIKE_SHARING_MERANO", station.getOrigin()        , errs, "STATION_1: ORIGIN is INCORRECT");
                    checkEquals(TEST_STATION_TYPE    , station.getStationType()   , errs, "STATION_1: STATION_TYPE is INCORRECT");
                }
            }
            if ( !station1Found ) {
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
    public void testConvertMeasurements() {

        try {

            //Get data of first Station (Bike)
            List<BikeDto> data = readFetchData();
            BikeDto bikeDto = data.get(0);

            List<DataTypeDto> allDataTypes = pusher.mapDataTypes2Bdp();

            //Convert in Data Hub data structure
            List<BikeDto> list = new ArrayList<BikeDto>();
            list.add(bikeDto);
            DataMapDto<RecordDtoImpl> stationRec = pusher.mapData(list);

            //Check there is a branch for TEST_STATION_ID_1 station
            Map<String, DataMapDto<RecordDtoImpl>> branch1 = stationRec.getBranch();
            assertNotNull("StationMeasurements: Branch Level 1 is null", branch1);

            //Check there is a data map for TEST_STATION_ID_1 station
            DataMapDto<RecordDtoImpl> dataMapDto1 = branch1.get(TEST_STATION_ID_1);
            assertNotNull("StationMeasurements: DataMapDTO for station "+TEST_STATION_ID_1+" is null", dataMapDto1);

            //Check there is data measurements in the map
            StringBuffer errs = new StringBuffer();
            Map<String, DataMapDto<RecordDtoImpl>> branch2 = dataMapDto1.getBranch();
            for ( int j=0 ; j<allDataTypes.size() ; j++ ) {
                DataTypeDto dataTypeDto = allDataTypes.get(j);
                String dataTypeName = dataTypeDto.getName();
                DataMapDto<RecordDtoImpl> dataMapDto2 = branch2.get(dataTypeName);
                checkNotEmpty(dataMapDto2          , errs, "Measures for type "+dataTypeName);
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

    private List<BikeDto> readFetchData() throws Exception {
        //Convert station data
        String responseString = getTestData(DATA_FETCH_STATIONS, ServiceCallParam.FUNCTION_NAME_PAGE_NUM, "1");
        BikesharingMoqoPageDto bikesharingMoqoPageDto = converter.convertCarsResponseToInternalDTO(responseString);
        List<BikeDto> data = bikesharingMoqoPageDto.getBikeList();

        //Convert availability data
        for (BikeDto bikeDto : data) {
            String bikeId = bikeDto.getId();
            String responseStringAvail = getTestData(DATA_FETCH_MEASUREMENTS, ServiceCallParam.FUNCTION_NAME_STATION_ID, bikeId);
            if ( DCUtils.paramNotNull(responseStringAvail) ) {
                List<AvailabilityDto> availDtoList = converter.convertAvailabilityResponseToInternalDTO(responseStringAvail);
                bikeDto.setAvailabilityList(availDtoList);
                //Evaluate attributes available, until and from for the bike, looking into the Availability slots
                converter.calculateBikeAvailability_FromUntil(bikeDto, availDtoList);
                converter.calculateBikeAvailability(bikeDto, availDtoList);
            }
        }

        return data;
    }

    public static String getTestData(String dataType, String paramName, String paramValue) {
        StringBuffer retval = new StringBuffer();

        Reader rr = null;
        BufferedReader br = null;

        try {
            LOG.debug("START read test data");
            String fileName =
                    DATA_PUSH_STATIONS.equals(dataType)      ? TEST_FILE_PUSH_STATIONS :
                    DATA_PUSH_MEASUREMENTS.equals(dataType)  ? TEST_FILE_PUSH_MEASUREMENTS :
                    DATA_FETCH_STATIONS.equals(dataType)     ? TEST_FILE_FETCH_STATIONS :
                    DATA_FETCH_MEASUREMENTS.equals(dataType) ? TEST_FILE_FETCH_MEASUREMENTS :
                    TEST_FILE_FETCH_STATIONS;
            if ( DCUtils.paramNotNull(paramName) && paramValue != null ) {
                fileName = fileName.replace(paramName, paramValue);
            }
            URL url = BikesharingMoqoDataRetrieverTestIT.class.getResource(fileName);
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
