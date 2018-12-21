package info.datatellers.appabz;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.HashMap;

/**
 * @author Nicolò Molinari, Datatellers.
 *
 * This class handles all the logic necessary to retrieve data from the web-service.
 */

public class DataFetcher {

    private final ResourceBundle rb = ResourceBundle.getBundle("config");
    private static final Logger LOG = LogManager.getLogger(DataFetcher.class.getName());
    private final String stationsEndpoint = rb.getString("odh.url.stations.metadata");
    private final String pollutersEndpoint = rb.getString("odh.url.polluters.metadata");
    private final String measurementsEndpoint = rb.getString("odh.url.polluters.measurements");
    private ArrayList<JsonElement> rawStations = new ArrayList<>();

    public ArrayList<JsonElement> fetchStations()
    {
            LOG.debug("Endpoint requested: " + stationsEndpoint);
            try {
                URL website = new URL(stationsEndpoint);
                URLConnection connection;
                StringBuilder builder = new StringBuilder();
                connection = website.openConnection();
                return fetchStationsHandler(connection, builder);
            } catch (IOException e)
            {
                LOG.fatal("Unable to establish connection.");
                return null;
            }
    }

    public HashMap<String, String> fetchPolluters()
    {
            LOG.debug("Endpoint requested: " + pollutersEndpoint);
            try {
                URL website = new URL(pollutersEndpoint);
                URLConnection connection;
                StringBuilder builder = new StringBuilder();
                connection = website.openConnection();
                return fetchPollutersHandler(connection, builder);
            } catch (IOException e)
            {
                LOG.fatal("Unable to establish connection.");
                return null;
            }
    }

    private ArrayList<JsonElement> fetchStationsHandler(URLConnection connection, StringBuilder builder)
    {
        JsonElement parsedStations;
        if (connection.getHeaderFields().get(null).get(0).contains("200"))
        {
            connect(connection, builder);
            parsedStations = new JsonParser().parse(builder.toString());

            JsonArray stations = parsedStations.getAsJsonObject().get("features").getAsJsonArray();
            LOG.debug("Extracting station metadata...");
            for (JsonElement station: stations)
            {
                rawStations.add(station.getAsJsonObject().get("properties").getAsJsonObject());
            }
            LOG.debug("Stations metadata extracted.");
        } else
            {
            LOG.debug("Problems encountered while connecting to endpoint.");
        }
        return rawStations;
    }

    private HashMap<String,String> fetchPollutersHandler(URLConnection connection, StringBuilder builder)
    {
        ArrayList<String> rawPolluters = new ArrayList<>();
        HashMap<String, String> pollutersMap = new HashMap<>();
        JsonArray parsedPolluters;
        if (connection.getHeaderFields().get(null).get(0).contains("200"))
        {
            connect(connection, builder);
            parsedPolluters = (JsonArray) new JsonParser().parse(builder.toString());

            JsonArray polluters = parsedPolluters.getAsJsonArray();

            LOG.debug("Extracting polluters metadata...");
            for (JsonElement polluter: polluters)
            {
                if (!rawPolluters.contains(polluter.getAsJsonObject().get("MCODE").toString()))
                {
                    rawPolluters.add(polluter.getAsJsonObject().get("MCODE").toString());
                    pollutersMap.put(polluter.getAsJsonObject().get("DESC_I").toString(), polluter.getAsJsonObject().get("UNIT").toString());
                }
            }

            LOG.debug("Stations metadata extracted.");
        } else
        {
            LOG.debug("Problems encountered while connecting to endpoint.");
        }
        return pollutersMap;
    }

    private void connect(URLConnection connection, StringBuilder builder) {
        LOG.debug("Connection established.");
        parseInputJson(connection, builder);
    }

    private void parseInputJson(URLConnection connection, StringBuilder builder) {
        BufferedReader in;
        String inputLine;
        LOG.debug("Parsing JSON.");
        try
        {
            in = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            while ((inputLine = in.readLine()) != null)
            {
                builder.append(inputLine);
            }
            in.close();
        }catch (IOException e)
        {
            LOG.debug("IOException thrown.");
        }
    }

    TreeMap<String, ArrayList<String>> interrogateEndpoint(String polluterID, String stationID)
    {
        String specificEndpoint = measurementsEndpoint + "?meas_code=" + polluterID + "&type=1&station_code=" + stationID.split("[_]")[1];
        TreeMap<String, ArrayList<String>> dataMap;
        LOG.debug("Endpoint requested: " + specificEndpoint);
        try {
            URL website = new URL(specificEndpoint);
            URLConnection connection;
            StringBuilder builder = new StringBuilder();
            connection = website.openConnection();
            JsonArray parsedMeasurements;

            if (connection.getHeaderFields().get(null).get(0).contains("200"))
            {
                dataMap = new TreeMap<>();
                LOG.debug("Connection established for station: " + stationID + " and polluter " + polluterID + ". Parsing JSON...");
                parseInputJson(connection, builder);
                parsedMeasurements = (JsonArray) new JsonParser().parse(builder.toString());
                JsonArray measurements = parsedMeasurements.getAsJsonArray();

                LOG.debug("Extracting measurements...");
                if (measurements.toString().equals("[]") || measurements.toString().equals(""))
                {
                    LOG.debug("No " + polluterID + " data collected at station " + stationID + ".");
                }else {
                    for (JsonElement measurement : measurements)
                    {
                        ArrayList<String> measurementRawData = new ArrayList<>();
                        measurementRawData.add(measurement.getAsJsonObject().get("DATE").toString());
                        measurementRawData.add(measurement.getAsJsonObject().get("VALUE").toString());
                        dataMap.put(measurement.getAsJsonObject().get("DATE").toString().split("[T]")[1].substring(0,2), measurementRawData);
                    }
                    return dataMap;
                }
                LOG.debug("Measurements extracted.");
            } else
            {
                LOG.info("Problems encountered while connecting to endpoint.");
            }
        }catch (IOException e)
        {
            LOG.fatal("Unable to establish connection.");
        }
        return null;
    }
}