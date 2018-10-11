package it.bz.idm.bdp.dcemobilityh2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
import it.bz.idm.bdp.dto.StationList;
import it.bz.idm.bdp.dto.emobility.EchargingPlugDto;
import it.bz.idm.bdp.dto.emobility.EchargingStationDto;
import it.bz.idm.bdp.dto.emobility.OutletDtoV2;

@ContextConfiguration(locations = { "classpath:/META-INF/spring/applicationContext.xml" })
public class HydrogenDataRetrieverTest extends AbstractJUnit4SpringContextTests {

    private static final Logger LOG = LogManager.getLogger(HydrogenDataRetrieverTest.class.getName());

    @Autowired
    private HydrogenDataPusher pusher;

    @Autowired
    private HydrogenDataRetriever reader;

    private static final String TEST_FILE_FETCH = "/test_data/test_data_fetch.xml";
    private static final String TEST_FILE_PUSH  = "/test_data/test_data_push.xml";

    private static final String TEST_STATION_ID = "TEST_IDX";
    private static final String TEST_PLUG_ID    = "TEST_IDX-1";

    public static final String DATA_FETCH = "FETCH";
    public static final String DATA_PUSH  = "PUSH";

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

    @Test
    public void testConvertStationData() {

        try {
            String responseString = getTestData(DATA_FETCH);

            List<HydrogenDto> data = reader.convertResponseToInternalDTO(responseString);

            StationList stations = pusher.mapStations2Bdp(data);
//            StationList plugs    = pusher.mapPlugs2Bdp(data);
//            DataMapDto<RecordDtoImpl> map = pusher.mapData(data);
//            DataMapDto<RecordDtoImpl> plugRec = pusher.mapPlugData2Bdp(data);

            //Test data contains 2 stations
            assertEquals(2, stations.size());

            //Check that station list contains a station with ID=TEST_IDX and that input data is converted correctly 
            StringBuffer errs = new StringBuffer();
            boolean stationFound = false;
            for ( int i=0 ; !stationFound && i<stations.size() ; i++ ) {
                EchargingStationDto station = (EchargingStationDto) stations.get(i);
                String id = station.getId();
                if ( TEST_STATION_ID.equals(id) ) {
                    stationFound = true;
                    checkEquals(TEST_STATION_ID   , station.getId()            , errs, "ID is INCORRECT");
                    checkEquals("TEST_NAME"       , station.getName()          , errs, "NAME is INCORRECT");
                    checkEquals(11D               , station.getLongitude()     , errs, "LONGITUDE is INCORRECT");
                    checkEquals(46D               , station.getLatitude()      , errs, "LATITUDE is INCORRECT");
                    checkEquals("TEST_CITY"       , station.getMunicipality()  , errs, "MUNICIPALITY is INCORRECT");
                    checkEquals("TEST_HOSTNAME"   , station.getProvider()      , errs, "PROVIDER is INCORRECT");
                    checkEquals("TEST_CITY"       , station.getCity()          , errs, "CITY is INCORRECT");
                    checkEquals("AVAILABLE"       , station.getState()         , errs, "STATE is INCORRECT");
                    checkEquals("TEST_COMMENTS"   , station.getAccessInfo()    , errs, "ACCESS_INFO is INCORRECT");
                    checkEquals("IIT"             , station.getOrigin()        , errs, "ORIGIN is INCORRECT");
                    checkEquals("EChargingStation", station.getStationType()   , errs, "STATION_TYPE is INCORRECT");
                    checkEquals(Boolean.TRUE      , station.getReservable()    , errs, "RESERVABLE is INCORRECT");
                    checkEquals("PUBLIC"          , station.getAccessType()    , errs, "ACCESS_TYPE is INCORRECT");
                }
            }
            if ( !stationFound ) {
                Assert.fail("No station found with id: " + TEST_STATION_ID);
            }
            if ( errs.length() > 0 ) {
                Assert.fail("Station converter failure: " + errs);
            }

        } catch (Exception e) {
            String msg = "Exception in testConvertData: " + e;
            LOG.error(msg, e);
            Assert.fail(msg);
        }

    }

    @Test
    public void testConvertPlugData() {

        try {
            String responseString = getTestData(DATA_FETCH);

            List<HydrogenDto> data = reader.convertResponseToInternalDTO(responseString);

            StationList plugs    = pusher.mapPlugs2Bdp(data);

            //Test data contains 2 plugs
            assertEquals(2, plugs.size());

            //Check that plug list contains a plug with ID=TEST_IDX-1 and that input data is converted correctly 
            StringBuffer errs = new StringBuffer();
            boolean plugFound = false;
            for ( int i=0 ; !plugFound && i<plugs.size() ; i++ ) {
                EchargingPlugDto plug = (EchargingPlugDto) plugs.get(i);
                String id = plug.getId();
                if ( TEST_PLUG_ID.equals(id) ) {
                    plugFound = true;
                    checkEquals(TEST_PLUG_ID      , plug.getId()            , errs, "ID is INCORRECT");
                    checkEquals("TEST_NAME - Punto di rifornimento", plug.getName(), errs, "NAME is INCORRECT");
                    checkEquals(11D               , plug.getLongitude()     , errs, "LONGITUDE is INCORRECT");
                    checkEquals(46D               , plug.getLatitude()      , errs, "LATITUDE is INCORRECT");
                    checkEquals(TEST_STATION_ID   , plug.getParentStation() , errs, "PARENT_STATION is INCORRECT");
                    checkEquals("IIT"             , plug.getOrigin()        , errs, "ORIGIN is INCORRECT");
                    checkEquals("EChargingPlug"   , plug.getStationType()   , errs, "PLUG_TYPE is INCORRECT");

                    List<OutletDtoV2> outlets = plug.getOutlets();
                    if ( outlets!=null && outlets.size()>0 ) {
                        OutletDtoV2 outlet = outlets.get(0);
                        checkEquals(TEST_PLUG_ID              , outlet.getId()            , errs, "OUTLET ID is INCORRECT");
                        checkEquals("700 bar, small vehicles" , outlet.getOutletTypeCode(), errs, "OUTLET_TYPE_CODE is INCORRECT");
                    } else {
                        errs.append(" - Plug with EMPTY OUTLET LIST");
                    }
                }
            }
            if ( !plugFound ) {
                Assert.fail("No plug found with id: " + TEST_PLUG_ID);
            }
            if ( errs.length() > 0 ) {
                Assert.fail("Plug converter failure: " + errs);
            }

        } catch (Exception e) {
            String msg = "Exception in testConvertData: " + e;
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
            String URL = HydrogenDataRetrieverTest.class.getResource(fileName).getFile();
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
