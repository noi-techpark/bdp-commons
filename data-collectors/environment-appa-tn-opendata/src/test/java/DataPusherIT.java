import java.util.HashMap;
import java.util.ResourceBundle;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import info.datatellers.appatn.opendata.DataPusher;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.StationList;

/**
 * @author Nicolò Molinari, Datatellers.
 * Test class for ../src/main/java/info/datatellers/appatn/DataPusher.java
 * Detailed information can be found in comments and log.
 */
public class DataPusherIT
{
    private DataPusher dataPusher = new DataPusher();
    private ResourceBundle rb = ResourceBundle.getBundle("test");
    private static final Logger LOG = LoggerFactory.getLogger(DataPusherIT.class.getName());

    @Test
    public void mapStationTest()
    {
        LOG.debug("mapStationTest execution started.");
        LOG.debug("Initializing two partially complete stations.");
        StationDto testStationOne = new StationDto("22", "PIANA ROTALIANA", 46.196839, 11.11343);
        testStationOne.getMetaData().put("municipality", "Mezzolombardo");
        StationDto testStationTwo = new StationDto("15", "MONTE GAZA", 46.082531, 10.958041);
        testStationTwo.getMetaData().put("municipality", "Vallelaghi");

        LOG.debug("Fetching real stations.");
        StationList stationList = dataPusher.mapStations();

        LOG.debug("Checking presence or each parameter for every station.");
        for (StationDto station : stationList)
        {
            Assert.assertNotNull(station.getId());
            Assert.assertNotNull(station.getName());
            Assert.assertNotNull(station.getLatitude());
            Assert.assertNotNull(station.getLongitude());
            Assert.assertNotNull(station.getOrigin());
            Assert.assertNotNull(station.getStationType());
        }

        LOG.debug("Checking parameters consistency on real stations compared to sample ones.");
        Assert.assertEquals(testStationOne.getName(), stationList.get(0).getName());
        Assert.assertNotEquals(testStationOne, stationList.get(2));
        Assert.assertEquals(testStationTwo.getLatitude(), stationList.get(4).getLatitude());
        Assert.assertNotEquals(testStationTwo, stationList.get(6));
        LOG.debug("mapStationTest execution terminated.");
    }

    @Test
    public void mapTypesTest()
    {
        LOG.debug("mapTypesTest execution started.");
        LOG.debug("Initializing two sample typeDto.");
        DataTypeDto testTypeDtoOne = new DataTypeDto("sulphur dioxide", "ug/mc", "Medie orarie", "Mean", 3600);
        DataTypeDto testTypeDtoTwo = new DataTypeDto("particulate-matter10", "ug/mc", "Medie orarie", "Mean", 3600);

        LOG.debug("Fetching real polluters.");
        HashMap<String, DataTypeDto> typesMap = dataPusher.mapTypes();

        LOG.debug("Checking correct polluters mapping: polluters are mapped by their acronym, not 0,1,...,n.");
        for (int looper = 0; looper < typesMap.keySet().size(); looper++)
        {
            Assert.assertNull(typesMap.get(String.valueOf(looper)));
        }

        LOG.debug("Checking parameters validity on each parameter for every polluter.");
        LOG.debug("Checking that \"rType\" and \"period\" are the same in every station.");
        String[] pollutersIDs = {"so2", "o3", "co", "no2", "pm10", "pm25"};
        for (int looper = 0; looper < typesMap.keySet().size(); looper++)
        {
            Assert.assertNotNull(typesMap.get(pollutersIDs[looper]).getName());
            Assert.assertNotNull(typesMap.get(pollutersIDs[looper]).getUnit());
            Assert.assertNotNull(typesMap.get(pollutersIDs[looper]).getDescription());
            Assert.assertNotNull(typesMap.get(pollutersIDs[looper]).getRtype());
            Assert.assertNotNull(typesMap.get(pollutersIDs[looper]).getPeriod());

            if(looper == 0)
            {
                Assert.assertEquals(typesMap.get(pollutersIDs[looper]).getDescription(), typesMap.get(pollutersIDs[looper + 1]).getDescription());
                Assert.assertEquals(typesMap.get(pollutersIDs[looper]).getDescription(), typesMap.get(pollutersIDs[looper + 2]).getDescription());
                Assert.assertEquals(typesMap.get(pollutersIDs[looper]).getDescription(), typesMap.get(pollutersIDs[looper + 3]).getDescription());
                Assert.assertEquals(typesMap.get(pollutersIDs[looper]).getDescription(), typesMap.get(pollutersIDs[looper + 4]).getDescription());
                Assert.assertEquals(typesMap.get(pollutersIDs[looper]).getDescription(), typesMap.get(pollutersIDs[looper + 5]).getDescription());

                Assert.assertEquals(typesMap.get(pollutersIDs[looper]).getPeriod(), typesMap.get(pollutersIDs[looper + 1]).getPeriod());
                Assert.assertEquals(typesMap.get(pollutersIDs[looper]).getPeriod(), typesMap.get(pollutersIDs[looper + 2]).getPeriod());
                Assert.assertEquals(typesMap.get(pollutersIDs[looper]).getPeriod(), typesMap.get(pollutersIDs[looper + 3]).getPeriod());
                Assert.assertEquals(typesMap.get(pollutersIDs[looper]).getPeriod(), typesMap.get(pollutersIDs[looper + 4]).getPeriod());
                Assert.assertEquals(typesMap.get(pollutersIDs[looper]).getPeriod(), typesMap.get(pollutersIDs[looper + 5]).getPeriod());

                Assert.assertEquals(typesMap.get(pollutersIDs[looper]).getRtype(), typesMap.get(pollutersIDs[looper + 1]).getRtype());
                Assert.assertEquals(typesMap.get(pollutersIDs[looper]).getRtype(), typesMap.get(pollutersIDs[looper + 2]).getRtype());
                Assert.assertEquals(typesMap.get(pollutersIDs[looper]).getRtype(), typesMap.get(pollutersIDs[looper + 3]).getRtype());
                Assert.assertEquals(typesMap.get(pollutersIDs[looper]).getRtype(), typesMap.get(pollutersIDs[looper + 4]).getRtype());
                Assert.assertEquals(typesMap.get(pollutersIDs[looper]).getRtype(), typesMap.get(pollutersIDs[looper + 5]).getRtype());
            }
        }

        LOG.debug("Checking parameters consistency on real polluters compared to sample ones.");
        Assert.assertEquals(testTypeDtoOne.getName(), typesMap.get(pollutersIDs[0]).getName());
        Assert.assertNotEquals(testTypeDtoTwo.getName(), typesMap.get(pollutersIDs[2]).getName());
        LOG.debug("mapTypesTest execution terminated.");
    }

    @Test
    public void fillRootMapTest()
    {
        LOG.debug("fillRootMapTest execution started.");
        /*Constructing and filling rootMap, with the following structure:
        --rootMap
            |-- Station (key = stationId e.g. "2")
                |-- Polluter (key = polluterId e.g."no2")
                    |-- Record (timestamp = __; value =__; period = ____;)
        */

//        DataMapDto<RecordDtoImpl> rootMap = Tester.constructRootMap();
//        dataPusher.mapData(rootMap, new DateHelper().getTestDate(), new DateHelper().getTestDate(), true);
//
//        LOG.debug("Checking that each polluter branch contains 1 element, as the mapping method calling specifies only one day.");
//        String[] stationsIDs = rb.getString("odh.station.test.IDs").replace("{", "").replace("}", "").split(",");
//        for (int looper = 0; looper < rootMap.getBranch().keySet().size(); looper++)
//        {
//            String[] specificStationPolluters = rb.getString("odh.station.test." + stationsIDs[looper]).
//                    replace("{", "").replace("}", "").split(",");
//
//            for (String specificStationPolluter : specificStationPolluters)
//            {
//                try {
                    //Checking that each station branch contains between 3 and 6 elements.
//                    Assert.assertFalse(rootMap.getBranch().get("APPATN_" + stationsIDs[looper]).getBranch().size() < 3);
//                    Assert.assertFalse(rootMap.getBranch().get("APPATN_" + stationsIDs[looper]).getBranch().size() > 5);
                    //Checking that each polluter branch contain not-null data.
//                    Assert.assertNotNull(rootMap.getBranch().get("APPATN_" + stationsIDs[looper]).getBranch().get(specificStationPolluter.trim()).getData());
//
//                }catch (NullPointerException | AssertionError e)
//                {
                    /*
                    Some measurements are not collected in some hours, for some unknown reason.
                    I guess there's a minimum tolerance to be satisfied in order for the measurement
                    to be collected. Same in DataFetcherTest, see lines 92-96.
                     */
//                    if (e.getClass().getName().equals("java.lang.AssertionError"))
//                    {
//                        LOG.info(e.getClass().getName() + " thrown. There are less than expected minimum polluters branches on date: " + new DateHelper().getTestDate() + " at station: "
//                        + stationsIDs[looper] + ".");
//                    }else{
//                    LOG.info("NullPointerException thrown. Polluter: " + "--> \"" + specificStationPolluter + "\": [missing data] <--" + " field in json is missing: value was not collected on date: "
//                            + new DateHelper().getTestDate() + " at station: " + stationsIDs[looper] + ".");
//                    }
//                }
//            }
//        }
//        LOG.debug("fillRootMapTest execution terminated.");
    }
}