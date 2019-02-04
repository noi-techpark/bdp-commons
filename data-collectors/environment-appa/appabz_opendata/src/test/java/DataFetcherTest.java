import com.google.gson.JsonElement;
import info.datatellers.appabz.DataFetcher;
import org.junit.Assert;
import org.junit.Test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;


public class DataFetcherTest {

    private DataFetcher fetcher = new DataFetcher();
    private static final Logger LOG = LogManager.getLogger(DataFetcherTest.class.getName());
    private ResourceBundle rb = ResourceBundle.getBundle("test");

    @Test
    public void fetchStationsTest()
    {
        LOG.debug("Method fetchStations testing started.");
        ArrayList<JsonElement> testStations = fetcher.fetchStations();

        //Verifying 22 stations (JsonElements) have been fetched from endpoint
        Assert.assertEquals(22, testStations.size());

        //Verifying the two json portions are not null
        for (JsonElement testStation : testStations) {
            Assert.assertNotNull(testStation.getAsJsonObject().get("SCODE"));
            Assert.assertNotNull(testStation.getAsJsonObject().get("NAME_I"));
        }
        LOG.debug("FetchStationsTest execution terminated.");
    }

    @Test
    public void fetchPollutersTest()
    {
        LOG.debug("Method fetchPolluters testing started.");
        String micro = rb.getString("odh.test.polluters.micro").replace("Â", "");
        String milli = rb.getString("odh.test.polluters.milli").replace("Â", "");
        String radio = rb.getString("odh.test.polluters.radio").replace("Â", "");

        HashMap<String, String> testPolluters = fetcher.fetchPolluters();

        //Verifying polluters names are not null, verifying unit is a known one; for each polluter.
        for (String polluterID : testPolluters.keySet())
        {
            Assert.assertNotNull(polluterID);
            String unit = testPolluters.get(polluterID).replace("\"" ,"");
            Assert.assertTrue(unit.contains(micro) || unit.contains(milli) || unit.contains(radio));
        }
        LOG.debug("FetchPollutersTest execution terminated.");
    }
}
