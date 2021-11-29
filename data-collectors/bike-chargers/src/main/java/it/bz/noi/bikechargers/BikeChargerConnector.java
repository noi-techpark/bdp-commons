package it.bz.noi.bikechargers;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.bz.noi.bikechargers.configuration.ConnectorConfiguration;
import it.bz.noi.bikechargers.model.BikeChargerBayStation;
import it.bz.noi.bikechargers.model.BikeChargerStation;
import it.bz.noi.bikechargers.model.BikeChargerStationDetails;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * encapsulates the communication with the A22 web service
 */
@Component
public class BikeChargerConnector {

    private static final Logger LOG = LogManager.getLogger(BikeChargerConnector.class);

    private static final String PATH_STATIONS = "/v1/stations";

    @Autowired
    private ConnectorConfiguration connectorConfiguration;

    public List<BikeChargerStation> getStations() throws IOException {

        HttpURLConnection conn = (HttpURLConnection) (new URL(connectorConfiguration.getURL() + PATH_STATIONS)).openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty(connectorConfiguration.getApiKeyName(), connectorConfiguration.getApiKeyValue());
        int status = conn.getResponseCode();
        if (status != 200) {
            throw new RuntimeException("could not get stops (response code was " + status + ")");
        }

        // get response
        BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
        StringBuilder response = new StringBuilder();
        String s;
        while ((s = br.readLine()) != null) {
            response.append(s);
        }
        conn.disconnect();

        // parse response

        ArrayList<BikeChargerStation> output = new ArrayList<>();

        JsonArray jsonArray;
        try {
            jsonArray = new Gson().fromJson(response.toString(), JsonArray.class);
        } catch (Exception e) {
            // throw an error in case not even the top level element cannot be extracted as expected
            LOG.warn("---");
            LOG.warn("getStations() ERROR: unparsable response:");
            LOG.warn("vvv");
            LOG.warn(response.toString());
            LOG.warn("^^^");
            LOG.warn(e.getMessage(), e);
            LOG.warn("---");
            throw new IOException("ERROR: unparsable response");
        }

        int skipped = 0;
        int i;
        for (i = 0; i < jsonArray.size(); i++) {

            try {
                JsonObject stationJson = jsonArray.get(i).getAsJsonObject();

                BikeChargerStation station = new BikeChargerStation();
                station.setId(extractString(stationJson, "id"));
                station.setAddress(extractString(stationJson, "address"));
                station.setName(extractString(stationJson, "name"));
                station.setState(extractString(stationJson, "state"));
                station.setLat(extractDouble(stationJson, "lat"));
                station.setLng(extractDouble(stationJson, "lng"));

                output.add(station);

            } catch (Exception e) {

                // null pointer, cast or number format exception in case the json hasn't the expected form
                // or has incompatible data types: log and skip the record
                LOG.warn("---");
                LOG.warn("getStations() ERROR: skipping unparsable record:");
                LOG.warn("vvv");
                LOG.warn(jsonArray.get(i).toString());
                LOG.warn("^^^");
                LOG.warn(e.getMessage(), e);
                LOG.warn("---");
                skipped++;
                continue;
            }

        }

        LOG.debug("getStations() OK: got data about " + output.size() + " stations, " + skipped + " skipped");

        return output;
    }

    public BikeChargerStationDetails getStationDetails(BikeChargerStation station) throws IOException {

        HttpURLConnection conn = (HttpURLConnection) (new URL(connectorConfiguration.getURL() + PATH_STATIONS + "/" + station.getId())).openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty(connectorConfiguration.getApiKeyName(), connectorConfiguration.getApiKeyValue());
        int status = conn.getResponseCode();
        if (status != 200) {
            throw new RuntimeException("could not get stops (response code was " + status + ")");
        }

        // get response
        BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
        StringBuilder response = new StringBuilder();
        String s;
        while ((s = br.readLine()) != null) {
            response.append(s);
        }
        conn.disconnect();

        // parse response
        try {
            JsonObject stationDetailsJson = new Gson().fromJson(response.toString(), JsonObject.class);

            BikeChargerStationDetails stationDetails = new BikeChargerStationDetails();
            stationDetails.setTotalBays(extractInteger(stationDetailsJson, "totalBays"));
            stationDetails.setFreeBay(extractInteger(stationDetailsJson, "freeBay"));
            stationDetails.setAvailableVehicles(extractInteger(stationDetailsJson, "availableVehicles"));

            JsonObject stationBaysJson = extractJsonObject(stationDetailsJson, "bays");
            JsonArray stationBaysNodesJsonArray = extractJsonArray(stationBaysJson, "nodes");

            int skipped = 0;
            int i;
            for (i = 0; i < stationBaysNodesJsonArray.size(); i++) {

                try {
                    JsonObject stationBaysNodeJson = stationBaysNodesJsonArray.get(i).getAsJsonObject();

                    BikeChargerBayStation bayStation = new BikeChargerBayStation();
                    bayStation.setLabel(extractString(stationBaysNodeJson, "label"));
                    bayStation.setState(extractString(stationBaysNodeJson, "state"));
                    bayStation.setCharger(extractBoolean(stationBaysNodeJson, "charger"));

                    JsonArray bayStationUseJsonArray = extractJsonArray(stationBaysNodeJson, "use");
                    LOG.debug("bayStationUseJsonArray: {}", bayStationUseJsonArray.size());
                    if(bayStationUseJsonArray != null && bayStationUseJsonArray.size() > 0)
                        bayStation.setUse(extractString(bayStationUseJsonArray.get(0)));

                    JsonArray bayStationUsageStateJsonArray = extractJsonArray(stationBaysNodeJson, "usageState");
                    LOG.debug("bayStationUsageStateJsonArray: {}", bayStationUsageStateJsonArray.size());
                    if(bayStationUsageStateJsonArray != null && bayStationUsageStateJsonArray.size() > 0)
                        bayStation.setUsageState(extractString(bayStationUsageStateJsonArray.get(0)));

                    stationDetails.addBayStation(bayStation);

                } catch (Exception e) {

                    // null pointer, cast or number format exception in case the json hasn't the expected form
                    // or has incompatible data types: log and skip the record
                    LOG.warn("---");
                    LOG.warn("getStations() ERROR: skipping unparsable bay record:");
                    LOG.warn("vvv");
                    LOG.warn(stationBaysNodesJsonArray.get(i).toString());
                    LOG.warn("^^^");
                    LOG.warn(e.getMessage(), e);
                    LOG.warn("---");
                    skipped++;
                    continue;
                }

            }

            LOG.debug("getStationDetails() OK: got data about " + stationDetails.getBayStations().size() + " bay stations, " + skipped + " skipped");

            return stationDetails;
        } catch (Exception e) {
            // throw an error in case not even the top level element cannot be extracted as expected
            LOG.warn("---");
            LOG.warn("getStops() ERROR: unparsable response:");
            LOG.warn("vvv");
            LOG.warn(response.toString());
            LOG.warn("^^^");
            LOG.warn(e.getMessage(), e);
            LOG.warn("---");
            throw new IOException("ERROR: unparsable response");
        }
    }


    public String extractString(JsonObject obj, String key) throws IllegalArgumentException {
        return extractString(obj.get(key));
    }

    public String extractString(JsonElement prop) throws IllegalArgumentException {
        if (prop == null || prop.isJsonNull()) {
            return null;
        }
        if (prop.isJsonPrimitive())
            return prop.getAsString();
        throw new IllegalArgumentException("value cannot be interpreted as String");
    }

    public Double extractDouble(JsonObject obj, String key) throws IllegalArgumentException {
        JsonElement prop = obj.get(key);
        if (prop == null || prop.isJsonNull()) {
            return null;
        }
        if (prop.isJsonPrimitive())
            return prop.getAsDouble();
        throw new IllegalArgumentException("value cannot be interpreted as Double");
    }

    public Integer extractInteger(JsonObject obj, String key) throws IllegalArgumentException {
        JsonElement prop = obj.get(key);
        if (prop == null || prop.isJsonNull())
            return null;
        if (prop.isJsonPrimitive())
            return prop.getAsInt();
        throw new IllegalArgumentException("value cannot be interpreted as Integer");
    }

    public Boolean extractBoolean(JsonObject obj, String key) throws IllegalArgumentException {
        JsonElement prop = obj.get(key);
        if (prop == null || prop.isJsonNull())
            return null;
        if (prop.isJsonPrimitive())
            return prop.getAsBoolean();
        throw new IllegalArgumentException("value cannot be interpreted as Boolean");
    }

    public JsonObject extractJsonObject(JsonObject obj, String key) throws IllegalArgumentException {
        JsonElement prop = obj.get(key);
        if (prop == null || prop.isJsonNull())
            return null;
        if (prop.isJsonObject())
            return prop.getAsJsonObject();
        throw new IllegalArgumentException("value cannot be interpreted as JsonObject");
    }

    public JsonArray extractJsonArray(JsonObject obj, String key) throws IllegalArgumentException {
        JsonElement prop = obj.get(key);
        if (prop == null || prop.isJsonNull())
            return null;
        if (prop.isJsonArray())
            return prop.getAsJsonArray();
        throw new IllegalArgumentException("value cannot be interpreted as JsonArray");
    }
}
