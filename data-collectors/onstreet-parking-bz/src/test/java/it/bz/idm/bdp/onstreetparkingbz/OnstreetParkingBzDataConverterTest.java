package it.bz.idm.bdp.onstreetparkingbz;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.lang.reflect.Method;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import it.bz.idm.bdp.dconstreetparkingbz.DCUtils;
import it.bz.idm.bdp.dconstreetparkingbz.OnstreetParkingBzDataConverter;

@ContextConfiguration(locations = { "classpath:/META-INF/spring/applicationContext.xml" })
public class OnstreetParkingBzDataConverterTest extends AbstractJUnit4SpringContextTests {

    private static final Logger LOG = LogManager.getLogger(OnstreetParkingBzDataConverterTest.class.getName());

//    @Autowired
//    private OnstreetParkingBzDataPusher pusher;

//    @Autowired
//    private OnstreetParkingBzDataConverter converter;

//    @Autowired
//    private BikesharingMoqoDataRetriever reader;

    private static final String TEST_FILE_PUSH_MEASUREMENTS_1  = "/test_data/test_data_onstreet-parking_1.json";
    private static final String TEST_FILE_PUSH_MEASUREMENTS_2  = "/test_data/test_data_onstreet-parking_2.json";

    public static final String DATA_PUSH_MEASUREMENTS_2  = "PUSH_MEASUREMENTS_1";
    public static final String DATA_PUSH_MEASUREMENTS_1  = "PUSH_MEASUREMENTS_2";

    public static final String CONTROLLER_URL = "http://localhost:8080/osp/json/pushRecords";

    public static final String TEST_STATION_ID_1 = "sensor-1";
    public static final String TEST_STATION_ID_2 = "sensor-2";
    public static final String TEST_STATION_TYPE = OnstreetParkingBzDataConverter.STATION_TYPE_SENSOR;

    @Test
    public void testConvertDate() {
        LOG.setLevel(org.apache.log4j.Level.DEBUG);

        String str1 = "2020-02-24T13:37:00.436096605Z";
        String str2 = "2019-06-01T13:20:00CEST";
        String str3 = "2019-01-12T20:50:00CET";
        String str4 = "2019-10-23T08:09:28+02:00";
        Date d1 = DCUtils.convertStringTimezoneToDate(str1);
        Date d2 = DCUtils.convertStringTimezoneToDate(str2);
        Date d3 = DCUtils.convertStringTimezoneToDate(str3);
        Date d4 = DCUtils.convertStringTimezoneToDate(str4);
        LOG.debug(str1+" ==> "+d1);
        LOG.debug(str2+" ==> "+d2);
        LOG.debug(str3+" ==> "+d3);
        LOG.debug(str4+" ==> "+d4);

        String format = "yyyy-MM-dd'T'HH:mm:ss";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            Date dt = sdf.parse(str1);
            LOG.debug(str1 + " ==> " + dt);
        } catch (Exception e) {
            LOG.debug("Unable to parse date '"+str1+"'  with format '"+format+"': "+e);
        }

        Date d5 = new Date(1582552162);
        String str5_1 = DCUtils.convertDateToString(d5, "yyyy-MM-dd'T'HH:mm:ssX");
        String str5_2 = DCUtils.convertDateToString(d5, "yyyy-MM-dd'T'HH:mm:ssZ");
        String str5_3 = DCUtils.convertDateToString(d5, "yyyy-MM-dd'T'HH:mm:ss:SSSSSSSSS");
        LOG.debug("str9_1="+str5_1);
        LOG.debug("str9_2="+str5_2);
        LOG.debug("str9_3="+str5_3);

        Date d9 = new Date();
        String str9_1 = DCUtils.convertDateToString(d9, "yyyy-MM-dd'T'HH:mm:ssX");
        String str9_2 = DCUtils.convertDateToString(d9, "yyyy-MM-dd'T'HH:mm:ssZ");
        String str9_3 = DCUtils.convertDateToString(d9, "yyyy-MM-dd'T'HH:mm:ss:SSSSSSSSS");
        LOG.debug("str9_1="+str9_1);
        LOG.debug("str9_2="+str9_2);
        LOG.debug("str9_3="+str9_3);

    }

    public static String getTestData(String dataType, String paramName, String paramValue) {
        StringBuffer retval = new StringBuffer();

        Reader rr = null;
        BufferedReader br = null;

        try {
            LOG.debug("START read test data");
            String fileName =
                    DATA_PUSH_MEASUREMENTS_1.equals(dataType)  ? TEST_FILE_PUSH_MEASUREMENTS_1 :
                    DATA_PUSH_MEASUREMENTS_2.equals(dataType)  ? TEST_FILE_PUSH_MEASUREMENTS_2 :
                    DATA_PUSH_MEASUREMENTS_1;
            if ( DCUtils.paramNotNull(paramName) && paramValue != null ) {
                fileName = fileName.replace(paramName, paramValue);
            }
            URL url = OnstreetParkingBzDataConverterTest.class.getResource(fileName);
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
