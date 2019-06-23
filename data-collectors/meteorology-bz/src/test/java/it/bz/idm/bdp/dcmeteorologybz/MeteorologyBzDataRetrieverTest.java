package it.bz.idm.bdp.dcmeteorologybz;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import it.bz.idm.bdp.dcmeteorologybz.dto.MeteorologyBzDto;
import it.bz.idm.bdp.dcmeteorologybz.dto.TimeSerieDto;
import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.StationList;

@ContextConfiguration(locations = { "classpath:/META-INF/spring/applicationContext.xml" })
public class MeteorologyBzDataRetrieverTest extends AbstractJUnit4SpringContextTests {

    private static final Logger LOG = LogManager.getLogger(MeteorologyBzDataRetrieverTest.class.getName());

    @Autowired
    private MeteorologyBzDataPusher pusher;

    @Autowired
    private MeteorologyBzDataRetriever reader;

    private static final String TEST_FILE_FETCH_STATIONS     = "/test_data/test_data_fetch_stations.json";
    private static final String TEST_FILE_FETCH_DATATYPES    = "/test_data/test_data_fetch_datatypes.json";
    private static final String TEST_FILE_FETCH_MEASUREMENTS = "/test_data/test_data_fetch_measurements.json";

    private static final String TEST_FILE_PUSH_STATIONS      = "/test_data/test_data_push_stations.json";
    private static final String TEST_FILE_PUSH_DATATYPES     = "/test_data/test_data_push_datatypes.json";
    private static final String TEST_FILE_PUSH_MEASUREMENTS  = "/test_data/test_data_push_measurements.json";

    public static final String DATA_FETCH_STATIONS     = "FETCH_STATIONS";
    public static final String DATA_FETCH_DATA_TYPES   = "FETCH_DATA_TYPES";
    public static final String DATA_FETCH_MEASUREMENTS = "FETCH_MEASUREMENTS";

    public static final String DATA_PUSH_STATIONS      = "PUSH_STATIONS";
    public static final String DATA_PUSH_DATA_TYPES    = "PUSH_DATA_TYPES";
    public static final String DATA_PUSH_MEASUREMENTS  = "PUSH_MEASUREMENTS";
//    public static final String DATA_PUSH_STATION_CODE  = "89940PG";
    public static final String[] DATA_PUSH_TYPE_CODES  = new String[] {"Q", "W", "WT"};

    public static final String MUNICIPALITY = "FETCH_MUNICIPALITY";

    private static final String TEST_STATION_ID_1 = "ST_001";
    private static final Integer TEST_PERIOD = new Integer(600);

    private static final String[][] TEST_DATA_TYPE_WS = new String[][] {
            new String [] {"WG"       ,           "wind-speed"                     , "m/s"     , "Velocità del vento"    }
           ,new String [] {"WG.BOE"   ,           "wind-gust-speed"                , "m/s"     , "Velocitá raffica"      }
           ,new String [] {"GS"       ,           "global-radiation"               , "W/mq"    , "[gr desc]"             }
           ,new String [] {"SD"       ,           "sunshine-duration"              , "s"       , "Durata soleggiamento"  }
           ,new String [] {"WR"       ,           "wind-direction"                 , "°"       , "Direzione del vento"   }
           ,new String [] {"LD"       ,           "atmospheric-pressure"           , "[ap]"    , "[ap desc]"             }
           ,new String [] {"LD.RED"   ,           "atmospheric-pressure-reduced"   , "hPa"     , "Pressione atmosferica" }
           ,new String [] {"LT"       ,           "air-temperature"                , "°C"      , "[at desc]"             }
           ,new String [] {"LF"       ,           "air-humidity"                   , "%"       , "Umidità relativa"      }
           ,new String [] {"N"        ,           "precipitation"                  , "mm"      , "Precipitazioni"        }
           ,new String [] {"W"        ,           "water-level"                    , "cm"      , "Livello idrometrico"   }
           ,new String [] {"HS"       ,           "snow-level"                     , "cm"      , "Altezza neve al suolo" }
           ,new String [] {"W.ABST"   ,           "hydrometric-level"              , "m"       , "Altezza freatimetrica" }
           ,new String [] {"WT"       ,           "water-temperature"              , "°C"      , "Temperatura acqua"     }
           ,new String [] {"Q"        ,           "flow-rate"                      , "m³/s"    , "Portata"               }
           ,new String [] {"ND"       ,           "rainfall-duration"              , "[rd]"    , "[rd desc]"             }
           ,new String [] {"LT.050"   ,           "air-temperature-50-cm"          , "[at50]"  , "[at50 desc]"           }
           ,new String [] {"BT.025"   ,           "ground-temperature-25-cm"       , "[gt25]"  , "[gt25 desc]"           }
           ,new String [] {"BT.050"   ,           "ground-temperature-50-cm"       , "[gt50]"  , "[gt50 desc]"           }
           ,new String [] {"BT.010"   ,           "ground-temperature-10-cm"       , "[gt10]"  , "[gt10 desc]"           }
           ,new String [] {"SSTF"     ,           "suspended-solids-in-watercourse", "[ssw]"   , "[ssw desc]"            }
           ,new String [] {"Z.BOE"    ,           "wind-gust-speed-time"           , "m/s"     , "Velocitá raffica"      }
           ,new String [] {"WR.BOE"   ,           "wind-gust-direction"            , "[wgd]"   , "[wgd desc]"            }
           ,new String [] {"TD"       ,           "dew.temperature"                , "[dt]"    , "[dt desc]"             }
           ,new String [] {"U.W.ABST" ,           "groundwater-level"              , "[gw]"    , "[gw desc]"             }
           ,new String [] {"BN"       ,           "foliar-wetting"                 , "[fw]"    , "[fw desc]"             }
    };

    @Test
    public void testConvertDate() {
        String str1 = "2019-06-01T13:20:00CEST";
        String str2 = "2019-03-31T01:40:00CET";
        String str3 = "2019-01-12T20:50:00CET";
        Date d1 = DCUtils.convertStringTimezoneToDate(str1);
        Date d2 = DCUtils.convertStringTimezoneToDate(str2);
        Date d3 = DCUtils.convertStringTimezoneToDate(str3);
        LOG.info("d1="+d1);
        LOG.info("d2="+d2);
        LOG.info("d3="+d3);

        Date d9 = new Date();
        String str9_1 = DCUtils.convertDateToString(d9, "yyyy-MM-dd'T'HH:mm:ssX");
        String str9_2 = DCUtils.convertDateToString(d9, "yyyy-MM-dd'T'HH:mm:ssZ");
        String str9_3 = DCUtils.convertDateToString(d9, "yyyy-MM-dd'T'HH:mm:ss");
        LOG.info("str9_1="+str9_1);
        LOG.info("str9_2="+str9_2);
        LOG.info("str9_3="+str9_3);

    }

    @Test
    public void testConvertStationData() {

        try {
            String responseString = getTestData(DATA_FETCH_STATIONS);

            List<MeteorologyBzDto> data = reader.convertStationsResponseToInternalDTO(responseString);

            StationList stations = pusher.mapStations2Bdp(data);

            //Test data contains 4 records
            assertEquals(4, data.size());

            //Check that station list contains a station with ID=TEST_IDX and that input data is converted correctly
            StringBuffer errs = new StringBuffer();
            boolean station1Found = false;
            for ( int i=0 ; !station1Found && i<stations.size() ; i++ ) {
                StationDto station = stations.get(i);
                String id = station.getId();
                if ( TEST_STATION_ID_1.equals(id) ) {
                    station1Found = true;
                    checkEquals(TEST_STATION_ID_1    , station.getId()            , errs, "STATION_1: ID is INCORRECT");
                    checkEquals("DESC ST_001 IT"     , station.getName()          , errs, "STATION_1: NAME is INCORRECT");
                    checkEquals(11.20262D            , station.getLongitude()     , errs, "STATION_1: LONGITUDE is INCORRECT");
                    checkEquals(46.243333D           , station.getLatitude()      , errs, "STATION_1: LATITUDE is INCORRECT");
                    checkEquals(210D                 , station.getElevation()     , errs, "STATION_1: ELEVATION is INCORRECT");
                    checkEquals("METEO_PAB"          , station.getOrigin()        , errs, "STATION_1: ORIGIN is INCORRECT");
                    checkEquals("Meteostation"       , station.getStationType()   , errs, "STATION_1: STATION_TYPE is INCORRECT");
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
    public void testConvertDataTypeData() {

        try {
            String responseString = getTestData(DATA_FETCH_DATA_TYPES);

            List<DataTypeDto> dataTypes = reader.convertSensorsResponseToInternalDTO(responseString, null);

            //Test data contains 26 records
            assertEquals(TEST_DATA_TYPE_WS.length, dataTypes.size());

            //Check that dataTypes list contains all data types and that input data is converted correctly
            StringBuffer errs = new StringBuffer();
            for ( int i=0 ; i<dataTypes.size() ; i++ ) {
                DataTypeDto dataTypeDto = dataTypes.get(i);

                String name = dataTypeDto.getName();
                String desc = dataTypeDto.getDescription();
                String unit = dataTypeDto.getUnit();
                Integer period = dataTypeDto.getPeriod();
                boolean found = false;

                for ( int j=0 ; !found && j<TEST_DATA_TYPE_WS.length ; j++ ) {
                    String[] attrs = TEST_DATA_TYPE_WS[j];
                    if ( name.equals(attrs[1]) ) {
                        found = true;
                        checkEquals(unit    , attrs[2]            , errs, "DataType["+name+"]: unit   is INCORRECT");
                        checkEquals(desc    , attrs[3]            , errs, "DataType["+name+"]: desc   is INCORRECT");
                        checkEquals(period  , TEST_PERIOD         , errs, "DataType["+name+"]: period is INCORRECT");
                    }
                }
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

            //Create one station
            StationDto stationDto = new StationDto();
            stationDto.setId(TEST_STATION_ID_1);
            MeteorologyBzDto data = new MeteorologyBzDto(stationDto);
            List<MeteorologyBzDto> stationList = new ArrayList<MeteorologyBzDto>();
            stationList.add(data);

            //Add all datatypes to the station
            String responseStringDataTypes = getTestData(DATA_FETCH_DATA_TYPES);
            List<DataTypeDto> dataTypes = reader.convertSensorsResponseToInternalDTO(responseStringDataTypes, stationList);

            //Add all measurements for each data type
            String responseStringMeasurements = getTestData(DATA_FETCH_MEASUREMENTS);
            List<TimeSerieDto> measurementList = reader.convertMeasurementsResponseToInternalDTO(responseStringMeasurements);
            Map<String, List<TimeSerieDto>> timeSeriesMap = data.getTimeSeriesMap();
            for ( int j=0 ; j<TEST_DATA_TYPE_WS.length ; j++ ) {
                String[] attrs = TEST_DATA_TYPE_WS[j];
                String code = attrs[0];
                timeSeriesMap.put(code, measurementList);
            }

            //Convert in Data Hub data structure
            DataMapDto<RecordDtoImpl> stationRec = pusher.mapSingleStationData2Bdp(data);

            //Check there is a branch for TEST_STATION_ID_1 station
            Map<String, DataMapDto<RecordDtoImpl>> branch1 = stationRec.getBranch();
            assertNotNull("StationMeasurements: Branch Level 1 is null", branch1);

            //Check there is a data map for TEST_STATION_ID_1 station
            DataMapDto<RecordDtoImpl> dataMapDto1 = branch1.get(TEST_STATION_ID_1);
            assertNotNull("StationMeasurements: DataMapDTO for station "+TEST_STATION_ID_1+" is null", dataMapDto1);

            //Check there is data measurements in the map
            StringBuffer errs = new StringBuffer();
            Map<String, DataMapDto<RecordDtoImpl>> branch2 = dataMapDto1.getBranch();
            for ( int j=0 ; j<TEST_DATA_TYPE_WS.length ; j++ ) {
                String[] attrs = TEST_DATA_TYPE_WS[j];
                String dataTypeName = attrs[1];
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

    public static String getTestData(String dataType) {
        StringBuffer retval = new StringBuffer();

        Reader rr = null;
        BufferedReader br = null;

        try {
            LOG.debug("START read test data");
            String fileName =
                    DATA_PUSH_STATIONS.equals(dataType)      ? TEST_FILE_PUSH_STATIONS :
                    DATA_PUSH_DATA_TYPES.equals(dataType)    ? TEST_FILE_PUSH_DATATYPES :
                    DATA_PUSH_MEASUREMENTS.equals(dataType)  ? TEST_FILE_PUSH_MEASUREMENTS :
                    DATA_FETCH_STATIONS.equals(dataType)     ? TEST_FILE_FETCH_STATIONS :
                    DATA_FETCH_DATA_TYPES.equals(dataType)   ? TEST_FILE_FETCH_DATATYPES :
                    DATA_FETCH_MEASUREMENTS.equals(dataType) ? TEST_FILE_FETCH_MEASUREMENTS :
                    TEST_FILE_FETCH_STATIONS;
            String URL = MeteorologyBzDataRetrieverTest.class.getResource(fileName).getFile();
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
