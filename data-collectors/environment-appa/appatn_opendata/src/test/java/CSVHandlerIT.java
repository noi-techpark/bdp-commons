import info.datatellers.appatn.helpers.CSVHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

/**
 * @author Nicolò Molinari, Datatellers.
 * Test class for ../src/main/java/helpers/CSVHandler.java
 * Detailed information can be found inside comments and log.
 */
public class CSVHandlerIT {

    private ResourceBundle rb = ResourceBundle.getBundle("test");
    private static final Logger LOG = LogManager.getLogger(CSVHandler.class.getName());

    @Test
    public void parseStationsCSVTest()
    {
        LOG.debug("parseStationCSVTest execution started.");
        HashMap<String, ArrayList<String>> stationMeta = new CSVHandler().parseStationsCSV();
        String[] stationsIDs = rb.getString("odh.station.test.IDs").replace("{", "").replace("}", "").split(",");

        LOG.debug("Checking CSV parsing outputs as expected.");
        for (String stationID : stationsIDs)
        {
            Assert.assertTrue(stationMeta.keySet().contains(stationID));
            Assert.assertTrue(stationMeta.get(stationID).size() <= 5);
        }
        LOG.debug("parseStationCSVTest execution terminated.");
    }

    @Test
    public void parseTypesCSVTest()
    {
        LOG.debug("parseTypesCSVTest execution started.");
        String unity = rb.getString("odh.csv.unity").replace("{","").replace("}","");
        ArrayList<String> typesMeta = new CSVHandler().parseTypesCSV();

        LOG.debug("Checking CSV parsing outputs as expected.");
        for (String meta: typesMeta)
        {
            if (meta.split(",")[0].equals("6"))
            {
                Assert.assertNotEquals(unity, meta.split(",")[3]);
            }else{
                Assert.assertEquals(unity, meta.split(",")[3]);
            }
            Assert.assertTrue(meta.split(",").length <= 4);
            LOG.debug("parseTypesCSVTest execution terminated.");
        }
    }
}