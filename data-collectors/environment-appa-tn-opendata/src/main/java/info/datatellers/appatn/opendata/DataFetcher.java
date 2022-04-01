package info.datatellers.appatn.opendata;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import info.datatellers.appatn.helpers.CSVHandler;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

/**
 * @author Nicolò Molinari, Datatellers.
 *
 * This class handles all the logic necessary to retrieve data from the web-service.
 */

public class DataFetcher {

    private final ResourceBundle rb = ResourceBundle.getBundle("config");
    private static final Logger LOG = LoggerFactory.getLogger(DataFetcher.class.getName());
    private ArrayList<JsonElement> rawStations = new ArrayList<>();

    /*
    As different stations have different URLs, the common String is stored into the config.properties file.
    The last digit, a number, specifies the single station (e.g. https://appa.alpz.it/aria/opendata/json/last/2
    is the URL of the station in Parco Santa Chiara, Trento).
     */
    private final String endpointFinal = rb.getString("odp.url.stations.trentino.historic");
    private String endpoint = endpointFinal;


    /**
     * This method is used in order to retrieve all the stations at one time.
     * @param endpoint A String used to make the call to the endpoint.
     * @param date A String appended to endpoint, necessary to make
     *             the correct call to the so specified endpoint.
     * @return An ArrayList rawStations, now filled with one
     * JsonElement (containing a single station data) for each call.
     */
    private ArrayList<JsonElement> fetch(String endpoint, String date) {
        URL website;
        URLConnection connection;
        StringBuilder response = new StringBuilder();
        endpoint = endpoint + date;
        try {
            for (int index = 0; index < new CSVHandler().getStationsIDsSize(); index++)
            {
                website = new URL(appendStationsIDs(endpoint, index));
                LOG.debug("Station ID appended. Endpoint requested: {}", website);
                connection = website.openConnection();
                fetchingHandler(connection, response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rawStations;
    }

    /**
     * This method handles the connection to the endpoint, fills the rawStations
     * ArrayList with one JsonElement for each endpoint call,
     * corresponding to a json containing data related to a single station.
     * @param connection An opened URLConnection.
     * @param response A StringBuilder used to fetch data from the retrieved json.
     */
    private void fetchingHandler(URLConnection connection, StringBuilder response)
    {
        BufferedReader in;
        String inputLine;
        JsonElement station;

        LOG.debug("Establishing connection with " + connection.toString());

        if(connection.getHeaderFields().get(null).get(0).contains("200")) {
            LOG.debug("Connection established. Parsing content...");
            try {
                in = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                /*
                As fetchHandler() is iterated inside fetch() no loop is needed here
                 */
                if ((inputLine = in.readLine()) != null){
                    response.append(inputLine);
                    station = new JsonParser().parse(inputLine);
                    rawStations.add(station);
                    LOG.debug("Stations parsed correctly. Starting to pair stations...");
                }

                in.close();
            } catch (IOException e) {
                LOG.error("Unable to establish connection.");
                e.printStackTrace();
            }
        } else {
            try {
                in = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                while ((inputLine = in.readLine()) != null)
                    response.append(inputLine);
                LOG.debug(response.toString());
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This method, if no date is specified as input parameter, appends
     * "last/" to the String which is later used for the endpoint call: in such
     * a way the last measured data is retrieved.
     * @param date If specified, is appended to the String use to make the
     *             endpoint call.
     * @return An ArrayList containing one json for each station contained inside
     * the provided csv file.
     */
    public ArrayList<JsonElement> fetchStations(String date)
    {
        //If date parameter is null, the retrieval is automatically done with the last entries retrieved from the sensor.
        if (date == null)
        {
            date = "last";
        }
        return fetch(endpoint, date + "/");
    }

    /**
     * This method appends the stationIDs contained in the csv file.
     * @param endpoint A String specifying part of the needed URl.
     * @param looper An int which points to a specific station ID.
     * @return A String response, containing the necessary URL for the endpoint
     * call.
     */
    private String appendStationsIDs(String endpoint, int looper)
    {
        CSVHandler csvHandler = new CSVHandler();
        String response;
        ArrayList<String> stationsCodes = csvHandler.getStationsIDs();
        int[] sortedStationIDs = new int[stationsCodes.size()];

        //Sorting stations IDs in order to correctly retrieve/map json files to stations
        for (int index = 0; index < stationsCodes.size(); index++)
        {
            sortedStationIDs[index] = Integer.valueOf(stationsCodes.get(index));
        }

        Arrays.sort(sortedStationIDs);
        response = endpoint + sortedStationIDs[looper];
        return response;
    }
}
