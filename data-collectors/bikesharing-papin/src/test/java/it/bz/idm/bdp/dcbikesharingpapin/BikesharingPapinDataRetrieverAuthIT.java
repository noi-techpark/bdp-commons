package it.bz.idm.bdp.dcbikesharingpapin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLDecoder;
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

import it.bz.idm.bdp.dcbikesharingpapin.dto.BikesharingPapinDto;
import it.bz.idm.bdp.dcbikesharingpapin.dto.BikesharingPapinStationDto;
import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.StationList;

@ContextConfiguration(locations = { "classpath:/META-INF/spring/applicationContext.xml" })
public class BikesharingPapinDataRetrieverAuthIT extends AbstractJUnit4SpringContextTests {

    private static final Logger LOG = LoggerFactory.getLogger(BikesharingPapinDataRetrieverAuthIT.class.getName());

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

            //Check that station list contains a station with ID=TEST_IDX and that input data is converted correctly
            StringBuffer errs = new StringBuffer();
            StationDto station = findStation(stations, "1");
            if ( station != null ) {
                Assert.assertEquals("1", station.getId());
            }
            if ( station == null ) {
                Assert.fail("No station 1 found with id: " + 1);
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

            //Get data of first Station
            List<BikesharingPapinStationDto> data = readFetchData();

            //Convert in Data Hub data structure
            DataMapDto<RecordDtoImpl> stationRec = mappingUtil.mapData(data);

            //Check there is a branch for TEST_STATION_ID_1 station
            Map<String, DataMapDto<RecordDtoImpl>> branch1 = stationRec.getBranch();
            assertNotNull("StationMeasurements: Branch Level 1 is null", branch1);

            //Check there is a data map for TEST_STATION_ID_1 station
            DataMapDto<RecordDtoImpl> dataMapDto1 = branch1.get("1");
            assertNotNull("StationMeasurements: DataMapDTO for station "+1+" is null", dataMapDto1);

            //Check there is data measurements in the map
            StringBuffer errs = new StringBuffer();
            Map<String, DataMapDto<RecordDtoImpl>> branch2 = dataMapDto1.getBranch();
            assertNotNull("StationMeasurements: Branch Level 2 is null", branch2);

            Object valueAvailability = findMeasure(branch2, BikesharingPapinDataConverter.DATA_TYPE_STATION_AVAILABILITY);
            Object valueIsClose      = findMeasure(branch2, BikesharingPapinDataConverter.DATA_TYPE_STATION_IS_CLOSE);

            Assert.assertEquals(1.0, valueAvailability);
            Assert.assertEquals(0.0, valueIsClose);

            if ( errs.length() > 0 ) {
                Assert.fail("Station converter failure: " + errs);
            }

        } catch (Exception e) {
            String msg = "Exception in testConvertStationMeasurements: " + e;
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

    private List<BikesharingPapinStationDto> readFetchData() throws Exception {
        //Convert station data
        String responseString = getTestData(DATA_FETCH_STATIONS, null, null);
        BikesharingPapinDto BikesharingPapinDto = converter.convertStationsResponseToInternalDTO(responseString);
        List<BikesharingPapinStationDto> data = BikesharingPapinDto.getStationList();

        List<BikesharingPapinStationDto> retval = new ArrayList<>();
        Long tsNow = System.currentTimeMillis();

        //Convert availability data
        for (BikesharingPapinStationDto stationDto : data) {
            stationDto.setMeasurementTimestamp(tsNow);
            retval.add(stationDto);
        }

        return retval;
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
            testFilePath = URLDecoder.decode(testFilePath, "UTF-8");
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
