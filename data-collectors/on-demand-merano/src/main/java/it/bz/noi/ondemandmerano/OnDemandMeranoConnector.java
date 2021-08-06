package it.bz.noi.ondemandmerano;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.bz.noi.ondemandmerano.configuration.ConnectorConfiguration;
import it.bz.noi.ondemandmerano.model.OnDemandMeranoStop;
import it.bz.noi.ondemandmerano.model.OnDemandMeranoVehicle;
import it.bz.noi.ondemandmerano.model.OnDemandServicePositionPoint;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;

/**
 * encapsulates the communication with the A22 web service
 */
@Component
public class OnDemandMeranoConnector {

    private static final Logger LOG = LogManager.getLogger(OnDemandMeranoConnector.class);

    private static final String STOPS_PATH = "/v2.0/stops";
    private static final String ACTIVE_ACTIVITIES_PATH = "/v1.0/activities/active";

    @Autowired
    private ConnectorConfiguration connectorConfiguration;

    public OnDemandMeranoConnector() {
    }

    public List<OnDemandMeranoStop> getMyStops() throws IOException {

        HttpURLConnection conn = (HttpURLConnection) (new URL(connectorConfiguration.getURL() + STOPS_PATH)).openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("Authorization", getBasicAuthorizationHeader());
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

        ArrayList<OnDemandMeranoStop> output = new ArrayList<>();

        JsonArray jsonArray;
        try {
            jsonArray = new Gson().fromJson(response.toString(), JsonArray.class);
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

        int skipped = 0;
        int i;
        for (i = 0; i < jsonArray.size(); i++) {

            try {
                JsonObject jsonRecord = jsonArray.get(i).getAsJsonObject();
                OnDemandMeranoStop record = new OnDemandMeranoStop();

                record.setId(extractLong(jsonRecord, "id"));
                record.setTitle(extractString(jsonRecord, "title"));
                record.setReference(extractString(jsonRecord, "reference"));
                record.setType(extractString(jsonRecord, "type"));

                JsonObject geoAddress = extractJsonObject(jsonRecord, "geoAddress");
                JsonObject position = extractJsonObject(geoAddress, "position");
                JsonObject streetAddress = extractJsonObject(geoAddress, "streetAddress");
                JsonArray groups = extractJsonArray(jsonRecord, "groups");
                JsonObject region = extractJsonObject(jsonRecord, "region");

                record.setPosition(new Gson().fromJson(position, OnDemandServicePositionPoint.class));
                record.setAddress(new Gson().fromJson(streetAddress, HashMap.class));
                record.setGroups(new Gson().fromJson(groups, new TypeToken<ArrayList<HashMap<String, Object>>>() {
                }.getType()));
                record.setRegion(new Gson().fromJson(region, HashMap.class));

                output.add(record);

            } catch (Exception e) {

                // null pointer, cast or number format exception in case the json hasn't the expected form
                // or has incompatible data types: log and skip the record
                LOG.warn("---");
                LOG.warn("getStops() ERROR: skipping unparsable record:");
                LOG.warn("vvv");
                LOG.warn(jsonArray.get(i).toString());
                LOG.warn("^^^");
                LOG.warn(e.getMessage(), e);
                LOG.warn("---");
                skipped++;
                continue;
            }

        }

        LOG.debug("getStops() OK: got data about " + output.size() + " stops, " + skipped + " skipped");

        return output;

    }

    public List<OnDemandMeranoVehicle> getMyVehicles() throws IOException {

        HttpURLConnection conn = (HttpURLConnection) (new URL(connectorConfiguration.getURL() + ACTIVE_ACTIVITIES_PATH)).openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("Authorization", getBasicAuthorizationHeader());
        int status = conn.getResponseCode();
        if (status != 200) {
            throw new RuntimeException("could not get active activities (response code was " + status + ")");
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

        ArrayList<OnDemandMeranoVehicle> output = new ArrayList<>();

        JsonArray jsonArray;
        try {
            jsonArray = new Gson().fromJson(response.toString(), JsonArray.class);
        } catch (Exception e) {
            // throw an error in case not even the top level element cannot be extracted as expected
            LOG.warn("---");
            LOG.warn("getVehicles() ERROR: unparsable response:");
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
                JsonObject jsonRecord = jsonArray.get(i).getAsJsonObject();
                JsonObject vehicleRecord = extractJsonObject(jsonRecord, "bus");
                JsonObject latestPosition = extractJsonObject(jsonRecord, "latestPosition");

                OnDemandMeranoVehicle record = new OnDemandMeranoVehicle();
                record.setLicensePlateNumber(extractString(vehicleRecord, "licensePlateNumber"));
                record.setType(extractString(vehicleRecord, "type"));

                JsonObject operator = extractJsonObject(vehicleRecord, "operator");
                JsonObject capacityMax = extractJsonObject(vehicleRecord, "capacityMax");
                JsonObject capacityUsed = extractJsonObject(vehicleRecord, "capacityUsed");

                record.setOperator(new Gson().fromJson(operator, HashMap.class));
                record.setCapacityMax(new Gson().fromJson(capacityMax, HashMap.class));
                record.setCapacityUsed(new Gson().fromJson(capacityUsed, HashMap.class));

                record.setRecordTime(extractString(latestPosition, "recordTime"));
                JsonObject position = extractJsonObject(latestPosition, "position");
                record.setPosition(new Gson().fromJson(position, OnDemandServicePositionPoint.class));

                output.add(record);

            } catch (Exception e) {

                // null pointer, cast or number format exception in case the json hasn't the expected form
                // or has incompatible data types: log and skip the record
                LOG.warn("---");
                LOG.warn("getVehicles() ERROR: skipping unparsable record:");
                LOG.warn("vvv");
                LOG.warn(jsonArray.get(i).toString());
                LOG.warn("^^^");
                LOG.warn(e.getMessage(), e);
                LOG.warn("---");
                skipped++;
                continue;
            }

        }

        LOG.debug("getVehicles() OK: got data about " + output.size() + " vehicles, " + skipped + " skipped");

        return output;

    }

    private String getBasicAuthorizationHeader() {
        return "Basic " + Base64.getEncoder().encodeToString((connectorConfiguration.getUsername() + ":" + connectorConfiguration.getPassword()).getBytes(StandardCharsets.UTF_8));
    }

    public String extractString(JsonObject obj, String key) throws IllegalArgumentException {
        JsonElement prop = obj.get(key);
        if (prop == null || prop.isJsonNull()) {
            return null;
        }
        if (prop.isJsonPrimitive())
            return prop.getAsString();
        throw new IllegalArgumentException("value cannot be interpreted as String");
    }

    public Long extractLong(JsonObject obj, String key) throws IllegalArgumentException {
        JsonElement prop = obj.get(key);
        if (prop == null || prop.isJsonNull())
            return null;
        if (prop.isJsonPrimitive())
            return prop.getAsLong();
        throw new IllegalArgumentException("value cannot be interpreted as Long");
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
