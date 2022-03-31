package info.datatellers.appabz;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.TreeMap;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

/**
 * @author Nicolò Molinari, Datatellers.
 *
 * This class handles all the logic necessary to retrieve data from the web-service.
 */

public class DataFetcher {

    private final ResourceBundle rb = ResourceBundle.getBundle("config");
    private static final Logger LOG = LoggerFactory.getLogger(DataFetcher.class.getName());
    private final String stationsEndpoint = rb.getString("odh.url.stations.metadata");
    private final String pollutersEndpoint = rb.getString("odh.url.polluters.metadata");
    private final String measurementsEndpoint = rb.getString("odh.url.polluters.measurements");
    private ArrayList<JsonElement> rawStations = new ArrayList<>();

    /**
     * This method connects to the endpoint for station metadata retrieval, using the value stored inside
     * the config.properties file. The main logic of data retrieval is handled inside fetchStationsHandler
     * and is pretty trivial. See code for more info.
     * @return the return value of fetchStationsHandler, which is an ArrayList filled with Json Elements.
     */
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
                LOG.error("Unable to establish connection.");
                return null;
            }
    }

    /**
     * This method connects to the endpoint for polluters metadata retrieval, using the value stored inside
     * the config.properties file. The main logic of data retrieval is handled inside fetchPollutersHandler
     * and is pretty trivial. See code for more info.
     * @return the return value of fetchPollutersHandler, which is an HashMap with ID's equals to endpoint's
     * DESC_I and values consisting in endpoint's UNIT.
     */
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
                LOG.error("Unable to establish connection.");
                return null;
            }
    }

    /**
     * @param connection already initialized URLConnection object, connected to station's metadata endpoint.
     * @param builder already initialized StringBuilder object.
     * @return an ArrayList filled with Json Elements.
     */
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

    /**
     * @param connection already initialized URLConnection object, connected to polluters' metadata endpoint.
     * @param builder already initialized StringBuilder object.
     * @return an HashMap with ID's equals to endpoint's DESC_I and values consisting in endpoint's UNIT.
     */
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

    /**
     * This method parses the json returned when the measurement request is made,
     * and isolates the field containing the measurement's date.
     * @return the said date, in form of a String.
     */
    public String getDateOfRecord()
    {
        try {
            URL website = new URL(measurementsEndpoint);
            URLConnection connection;
            StringBuilder builder = new StringBuilder();
            connection = website.openConnection();
            JsonArray parsed;

            if (connection.getHeaderFields().get(null).get(0).contains("200"))
            {
                connect(connection, builder);

                parsed = new JsonParser().parse(builder.toString()).getAsJsonArray();

                LOG.debug("Extracting polluters metadata...");

                JsonElement polluter = parsed.get(0);
                return polluter.getAsJsonObject().get("DATE").getAsString().split("T")[0];
            } else {
                LOG.debug("Problems encountered while connecting to endpoint.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Helper method used to partially avoid code duplication.
     * @param connection already initialized URLConnection object.
     * @param builder already initialized StringBuilder object.
     */
    private void connect(URLConnection connection, StringBuilder builder) {
        LOG.debug("Connection established.");
        parseInputJson(connection, builder);
    }

    /**
     * Helper method used to partially avoid code duplication.
     * @param connection already initialized URLConnection object.
     * @param builder already initialized StringBuilder object.
     */
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

    /**
     * This method interrogates the endpoints in the way specified inside the requirements PDF file at pp.4:
     *
     * "[...] è possibile fare richieste mirate per stazione e tipo misurato, ad es.:
     * http://dati.retecivica.bz.it/services/airquality/timeseries?meas_code=PM10&type=1&station_code=BZ6
     * ritorna i valori di PM10 rilevati nelle ultime 24 ore dalla stazione Bolzano 6 – Via Amba Alagi
     * Probabilmente conviene interrogare il servizio con questa modalità per semplicità e leggibilità nel
     * codice del data collector. In questo caso il web-service non consente di accedere ai dati storici. [...]"
     *
     * that is, every day for every polluter of every station a request is made to the endpoint.
     * @param polluterID used to correctly struct the url used for the request.
     * @param stationID used to correctly struct the url used for the request.
     * @return A TreeMap, containing the actual data measurements, with keys equals
     * to two-digits hours (e.g. midnight --> "00", 1 A.M. --> "01" etc.) containing
     * an ArrayList of String of length 2, with the date (e.g. "2019-01-11T00:00:00")
     * and the value (e.g. "0.1343"), with the following structure:
     * -- key (00)
     *     |--> date (2019-01-11T00:00:00)
     *     |--> value (0.1343)
     * -- key (01)
     *     |--> date (...)
     *     |--> value(...)
     * and so on.
     */
    TreeMap<String, ArrayList<String>> interrogateEndpoint(String polluterID, String stationID)
    {
        String stationCodeSplit[] = stationID.split("[_]");
        String stationCode = stationCodeSplit.length == 1 ? stationCodeSplit[0] : stationCodeSplit[1];
        String specificEndpoint = measurementsEndpoint + "?meas_code=" + polluterID + "&type=1&station_code=" + stationCode;
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
            LOG.error("Unable to establish connection.");
        }
        return null;
    }
}