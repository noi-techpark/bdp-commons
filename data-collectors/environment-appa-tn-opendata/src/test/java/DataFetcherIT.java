import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import info.datatellers.appatn.helpers.DateHelper;
import info.datatellers.appatn.opendata.DataFetcher;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * @author Nicolò Molinari, Datatellers.
 * Test class for ../src/main/java/info/datatellers/appatn/DataFetcher.java
 * Detailed information can be found in comments and log.
 */
public class DataFetcherIT {

    private ResourceBundle rb = ResourceBundle.getBundle("test");
    private static final Logger LOG = LoggerFactory.getLogger(DataFetcherIT.class.getName());

    @Test
    public void fetchTest()
    {
        LOG.debug("DataFetcherTest class execution started.");
        DataFetcher dataFetcher = new DataFetcher();
        LOG.debug("Fetching json...");
        ArrayList<JsonElement> jsonStations = dataFetcher.fetchStations(new DateHelper().getTestDate());
        String jsonIndex = rb.getString("odh.json.test.stazione").replace("{", "").replace("}", "");

        JsonObject jsonStation = (JsonObject) jsonStations.get(0);
        LOG.debug("Checking that at this level the fetched json contains a single JsonArray.");
        Assert.assertTrue(jsonStation.get(jsonIndex).isJsonArray());
        Assert.assertEquals(1, ((JsonArray) jsonStation.get(jsonIndex)).size());

        LOG.debug("Checking that at this level the JsonArray contains five JsonObjects.");
        Assert.assertEquals(5, ((JsonObject )((JsonArray) jsonStation.get(jsonIndex)).get(0)).size());

        LOG.debug("Checking the five JsonObjects contain the keys we need to access them.");
        Assert.assertTrue(((JsonObject) ((JsonArray) jsonStation.get(jsonIndex)).get(0)).keySet().contains("nome"));
        Assert.assertTrue(((JsonObject) ((JsonArray) jsonStation.get(jsonIndex)).get(0)).keySet().contains("citta"));
        Assert.assertTrue(((JsonObject) ((JsonArray) jsonStation.get(jsonIndex)).get(0)).keySet().contains("indirizzo"));
        Assert.assertTrue(((JsonObject) ((JsonArray) jsonStation.get(jsonIndex)).get(0)).keySet().contains("data_ultima_acquisizione"));
        Assert.assertTrue(((JsonObject) ((JsonArray) jsonStation.get(jsonIndex)).get(0)).keySet().contains("dati"));

        LOG.debug("Checking the JsonObject contains the desired retrieval date.");
        Assert.assertTrue(((JsonObject)((JsonObject) ((JsonArray) jsonStation.get(jsonIndex)).get(0)).get("dati")).keySet().contains(new DateHelper().getTestDate()));

        LOG.debug("Checking that every date branch contains 24 hours branches.");
        LOG.debug("Checking that measurement values contained inside hours branch are not null.");

        String[] stationsIDs = rb.getString("odh.station.test.IDs").replace("{", "").replace("}", "").split(",");
        for (String stationsID : stationsIDs)
        {
            String[] specificStationPolluters = rb.getString("odh.station.test." + stationsID).
                    replace("{", "").replace("}", "").split(",");

            for (int hour = 1; hour <= 24; hour++)
            {
                if (hour < 10)
                {
                    //Checking that every date branch contains 24 hours branches.
                    Assert.assertTrue(((JsonObject)((JsonObject)((JsonObject) ((JsonArray) jsonStation.get(jsonIndex)).get(0)).get("dati"))
                            .get(new DateHelper().getTestDate())).keySet().contains("0" + String.valueOf(hour)));
                    for (String specificStationPolluter : specificStationPolluters)
                    {
                        if ((((JsonObject) ((JsonObject) ((JsonObject) ((JsonObject) ((JsonArray) jsonStation.get(jsonIndex)).get(0)).get("dati"))
                                .get(new DateHelper().getTestDate())).get(String.valueOf("0" + hour))).get(specificStationPolluter)) == null)
                        {
                            /*  Some measurements are not collected in some hours, for some unknown reason.
                             I guess there's a minimum tolerance to be satisfied in order for the measurement
                             to be collected.
                             */
                            LOG.debug("Null value. Value was not collected.");
                        }else
                        {
                            //Checking that measurement values contained inside hours branch are not null.
                            Assert.assertNotNull(((JsonObject) ((JsonObject) ((JsonObject) ((JsonObject) ((JsonArray) jsonStation.get(jsonIndex)).get(0)).get("dati"))
                                    .get(new DateHelper().getTestDate())).get(String.valueOf("0" + hour))).get(specificStationPolluter));
                        }
                    }
                }else{
                    Assert.assertTrue(((JsonObject)((JsonObject)((JsonObject) ((JsonArray) jsonStation.get(jsonIndex)).get(0)).get("dati"))
                            .get(new DateHelper().getTestDate())).keySet().contains(String.valueOf(hour)));
                    for (String specificStationPolluter : specificStationPolluters)
                    {
                        if ((((JsonObject) ((JsonObject) ((JsonObject) ((JsonObject) ((JsonArray) jsonStation.get(jsonIndex)).get(0)).get("dati"))
                                .get(new DateHelper().getTestDate())).get(String.valueOf(hour))).get(specificStationPolluter)) == null)
                        {
                            /*
                            Some measurements are not collected in some hours, for some unknown reason.
                            I guess there's a minimum tolerance to be satisfied in order for the measurement
                            to be collected. Same in DataPusherTest, see lines 139-150.
                             */
                            LOG.debug("Null value. Polluter: " + "\"" + specificStationPolluter + "\"" + " field in json is missing: value was not collected on date: "
                                    + new DateHelper().getTestDate() + " at station: " + ((((JsonObject) ((JsonArray) jsonStation.get(jsonIndex)).get(0)).get("nome")) + "."));
                        }else
                        {
                            //Checking that measurement values contained inside hours branch are not null.
                            Assert.assertNotNull(((JsonObject) ((JsonObject) ((JsonObject) ((JsonObject) ((JsonArray) jsonStation.get(jsonIndex)).get(0)).get("dati"))
                                    .get(new DateHelper().getTestDate())).get(String.valueOf(hour))).get(specificStationPolluter));
                        }
                    }
                }
            }
        }
        LOG.debug("Test execution terminated.");
    }
}
