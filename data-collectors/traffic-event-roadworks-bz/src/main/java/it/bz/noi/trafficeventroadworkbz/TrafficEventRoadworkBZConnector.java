package it.bz.noi.trafficeventroadworkbz;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.bz.noi.trafficeventroadworkbz.model.TrafficEventRoadworkBZModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class TrafficEventRoadworkBZConnector {

    private static final Logger LOG = LogManager.getLogger(TrafficEventRoadworkBZConnector.class);

    private static final String END_POINT_URL = "http://www.provinz.bz.it/vmz/traffic.json";

    public List<TrafficEventRoadworkBZModel> getTrafficEventRoadworksModelList() throws IOException {

        HttpURLConnection conn = (HttpURLConnection) new URL(END_POINT_URL).openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");
        int status = conn.getResponseCode();
        if (status != 200) {
            throw new RuntimeException("could not get traffic events (response code was " + status + ")");
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

        ArrayList<TrafficEventRoadworkBZModel> output = new ArrayList<>();

        JsonArray jsonArray;
        try {
            jsonArray = new Gson().fromJson(response.toString(), JsonArray.class);
        } catch (Exception e) {
            // throw an error in case not even the top level element cannot be extracted as expected
            LOG.warn("---");
            LOG.warn("getTrafficEventRoadworksModelList() ERROR: unparsable response:");
            LOG.warn("vvv");
            LOG.warn(response.toString());
            LOG.warn("^^^");
            LOG.warn(e.getMessage(), e);
            LOG.warn("---");
            throw new IOException("ERROR: unparsable response");
        }

        int skipped = 0;
        for (int i = 0; i < jsonArray.size(); i++) {

            try {
                JsonObject trafficEventRoadworksJson = jsonArray.get(i).getAsJsonObject();

                TrafficEventRoadworkBZModel trafficEventRoadworkBZModel = new TrafficEventRoadworkBZModel();
                trafficEventRoadworkBZModel.setJson_featuretype(extractString(trafficEventRoadworksJson, "json_featuretype"));
                trafficEventRoadworkBZModel.setPublisherDateTime(extractString(trafficEventRoadworksJson, "publishDateTime"));
                trafficEventRoadworkBZModel.setBeginDate(extractLocalDate(trafficEventRoadworksJson, "beginDate"));
                trafficEventRoadworkBZModel.setEndDate(extractLocalDate(trafficEventRoadworksJson, "endDate"));
                trafficEventRoadworkBZModel.setDescriptionDe(extractString(trafficEventRoadworksJson, "descriptionDe"));
                trafficEventRoadworkBZModel.setDescriptionIt(extractString(trafficEventRoadworksJson, "descriptionIt"));
                trafficEventRoadworkBZModel.setTycodeValue(extractString(trafficEventRoadworksJson, "tycodeValue"));
                trafficEventRoadworkBZModel.setTycodeDe(extractString(trafficEventRoadworksJson, "tycodeDe"));
                trafficEventRoadworkBZModel.setTycodeIt(extractString(trafficEventRoadworksJson, "tycodeIt"));
                trafficEventRoadworkBZModel.setSubTycodeValue(extractString(trafficEventRoadworksJson, "subTycodeValue"));
                trafficEventRoadworkBZModel.setSubTycodeDe(extractString(trafficEventRoadworksJson, "subTycodeDe"));
                trafficEventRoadworkBZModel.setSubTycodeIt(extractString(trafficEventRoadworksJson, "subTycodeIt"));
                trafficEventRoadworkBZModel.setPlaceDe(extractString(trafficEventRoadworksJson, "placeDe"));
                trafficEventRoadworkBZModel.setPlaceIt(extractString(trafficEventRoadworksJson, "placeIt"));
                trafficEventRoadworkBZModel.setActualMail(extractInteger(trafficEventRoadworksJson, "actualMail"));
                trafficEventRoadworkBZModel.setMessageId(extractInteger(trafficEventRoadworksJson, "messageId"));
                trafficEventRoadworkBZModel.setMessageStatus(extractInteger(trafficEventRoadworksJson, "messageStatus"));
                trafficEventRoadworkBZModel.setMessageZoneId(extractInteger(trafficEventRoadworksJson, "messageZoneId"));
                trafficEventRoadworkBZModel.setMessageZoneDescDe(extractString(trafficEventRoadworksJson, "messageZoneDescDe"));
                trafficEventRoadworkBZModel.setMessageZoneDescIt(extractString(trafficEventRoadworksJson, "messageZoneDescIt"));
                trafficEventRoadworkBZModel.setMessageGradId(extractInteger(trafficEventRoadworksJson, "messageGradId"));
                trafficEventRoadworkBZModel.setMessageGradDescDe(extractString(trafficEventRoadworksJson, "messageGradDescDe"));
                trafficEventRoadworkBZModel.setMessageGradDescIt(extractString(trafficEventRoadworksJson, "messageGradDescIt"));
                trafficEventRoadworkBZModel.setMessageStreetId(extractInteger(trafficEventRoadworksJson, "messageStreetId"));
                trafficEventRoadworkBZModel.setMessageStreetWapDescDe(extractString(trafficEventRoadworksJson, "messageStreetWapDescDe"));
                trafficEventRoadworkBZModel.setMessageStreetWapDescIt(extractString(trafficEventRoadworksJson, "messageStreetWapDescIt"));
                trafficEventRoadworkBZModel.setMessageStreetInternetDescDe(extractString(trafficEventRoadworksJson, "messageStreetInternetDescDe"));
                trafficEventRoadworkBZModel.setMessageStreetInternetDescIt(extractString(trafficEventRoadworksJson, "messageStreetInternetDescIt"));
                trafficEventRoadworkBZModel.setMessageStreetNr(extractString(trafficEventRoadworksJson, "messageStreetNr"));
                trafficEventRoadworkBZModel.setMessageStreetHierarchie(extractInteger(trafficEventRoadworksJson, "messageStreetHierarchie"));
                trafficEventRoadworkBZModel.setMessageTypeId(extractInteger(trafficEventRoadworksJson, "messageTypeId"));
                trafficEventRoadworkBZModel.setMessageTypeDescDe(extractString(trafficEventRoadworksJson, "messageTypeDescDe"));
                trafficEventRoadworkBZModel.setMessageTypeDescIt(extractString(trafficEventRoadworksJson, "messageTypeDescIt"));
                trafficEventRoadworkBZModel.setX(extractDouble(trafficEventRoadworksJson, "X"));
                trafficEventRoadworkBZModel.setY(extractDouble(trafficEventRoadworksJson, "Y"));

                output.add(trafficEventRoadworkBZModel);

            } catch (Exception e) {

                // null pointer, cast or number format exception in case the json hasn't the expected form
                // or has incompatible data types: log and skip the record
                LOG.warn("---");
                LOG.warn("getTrafficEventRoadworksModelList() ERROR: skipping unparsable record:");
                LOG.warn("vvv");
                LOG.warn(jsonArray.get(i).toString());
                LOG.warn("^^^");
                LOG.warn(e.getMessage(), e);
                LOG.warn("---");
                skipped++;
                continue;
            }

        }

        LOG.debug("getTrafficEventRoadworksModelList() OK: got data about " + output.size() + " traffic events, " + skipped + " skipped");

        return output;
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

    public LocalDate extractLocalDate(JsonObject obj, String key) throws IllegalArgumentException {
        JsonElement prop = obj.get(key);
        if (prop == null || prop.isJsonNull()) {
            return null;
        }
        if (prop.isJsonPrimitive()) {
            String dateString = prop.getAsString();
            if(dateString.isEmpty())
                return null;
            try {
                return LocalDate.parse(dateString);
            } catch (Exception e) {
                throw new IllegalArgumentException("value cannot be interpreted as LocalDate", e);
            }
        }
        throw new IllegalArgumentException("value cannot be interpreted as LocalDate");
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
}
