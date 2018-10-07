package traffic_a22;

import java.io.*;
import java.net.*;
import java.util.*;
import org.json.simple.*;

/**
 * A22 connector.
 *
 * @author chris
 */
public class Connector {

    private static final int WS_CONN_TIMEOUT_MSEC = 5000;
    private static final boolean DEBUG = true;

    private String token = null;
    private String url = null;

    /**
     * Get authentication token and store it.
     *
     * @param url - the A22 web service URL
     * @param auth_json - the authentication String with user/pass in JSON
     *
     * @throws java.net.MalformedURLException
     * @throws java.io.IOException
     *
     */
    public Connector(String url, String auth_json) throws MalformedURLException, IOException {

        this.url = url;

        // make authentication request
        HttpURLConnection conn = (HttpURLConnection) (new URL(url + "/token")).openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("User-Agent", "IDM/traffic_a22");
        conn.setRequestProperty("Accept", "*/*");
        conn.setConnectTimeout(WS_CONN_TIMEOUT_MSEC);
        conn.setDoOutput(true);
        OutputStreamWriter os = new OutputStreamWriter(conn.getOutputStream());
        os.write(auth_json + "\n");
        os.flush();
        int status = conn.getResponseCode();
        if (status != 200) {
            throw new RuntimeException("authentication failure (response code was " + status + ")");
        }

        // get response
        BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
        StringBuilder response = new StringBuilder();
        String s;
        while ((s = br.readLine()) != null) {
            response.append(s);
        }
        os.close();
        conn.disconnect();

        // parse response and store authentication token
        String session_id = null;
        try {
            JSONObject response_json = (JSONObject) JSONValue.parse(response.toString());
            JSONObject subscribe_result = (JSONObject) response_json.get("SubscribeResult");
            session_id = (String) subscribe_result.get("sessionId");
        } catch (Exception e) {
            // null pointer or cast exception in case the json hasn't the expected form
            throw new RuntimeException("authentication failure (could not parse response)");
        }

        if (session_id == null) {
            throw new RuntimeException("authentication failure (could not find sessionId in response)");
        }

        token = session_id;

        if (DEBUG) {
            System.out.println("auth    OK, new token = " + token);
        }

    }

    /**
     * Release authentication token.
     *
     * @throws java.net.MalformedURLException, IOException
     */
    public void close() throws MalformedURLException, IOException {

        if (url == null || token == null) {
            throw new RuntimeException("there is no authenticated session");
        }

        // make de-authentication request
        HttpURLConnection conn = (HttpURLConnection) (new URL(url + "/token/" + token)).openConnection();
        conn.setRequestMethod("DELETE");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("User-Agent", "IDM/traffic_a22");
        conn.setRequestProperty("Accept", "*/*");
        conn.setConnectTimeout(WS_CONN_TIMEOUT_MSEC);
        conn.setDoOutput(true);
        OutputStreamWriter os = new OutputStreamWriter(conn.getOutputStream());
        os.write("\n");
        os.flush();
        int status = conn.getResponseCode();
        if (status != 200) {
            throw new RuntimeException("de-authentication failure (response code was " + status + ")");
        }

        // get response
        BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
        StringBuilder response = new StringBuilder();
        String s;
        while ((s = br.readLine()) != null) {
            response.append(s);
        }
        os.close();
        conn.disconnect();

        // parse response 
        Boolean result = null;
        try {
            JSONObject response_json = (JSONObject) JSONValue.parse(response.toString());
            result = (Boolean) response_json.get("RemoveSubscribeResult");
        } catch (Exception e) {
            // null pointer or cast exception in case the json hasn't the expected form
            throw new RuntimeException("de-authentication failure (could not parse response)");
        }
        if (result == null || result != true) {
            throw new RuntimeException("de-authentication failure (de-authentication was not confirmed)");
        }

        if (DEBUG) {
            System.out.println("de-auth OK, old token = " + token);
        }

        token = null;

    }

    /**
     * Retrieve the list of traffic sensors.
     *
     * @return an ArrayList of HashMaps with the sensor info
     *
     * @throws java.net.MalformedURLException, IOException
     */
    public ArrayList<HashMap<String, String>> getTrafficSensors() throws MalformedURLException, IOException {

        if (url == null || token == null) {
            throw new RuntimeException("there is no authenticated session");
        }

        // make de-authentication request
        HttpURLConnection conn = (HttpURLConnection) (new URL(url + "/traffico/anagrafica")).openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("User-Agent", "IDM/traffic_a22");
        conn.setRequestProperty("Accept", "*/*");
        conn.setConnectTimeout(WS_CONN_TIMEOUT_MSEC);
        conn.setDoOutput(true);
        OutputStreamWriter os = new OutputStreamWriter(conn.getOutputStream());
        os.write("{\"sessionId\":\"" + token + "\"}\n");
        os.flush();
        int status = conn.getResponseCode();
        if (status != 200) {
            throw new RuntimeException("could not retrieve traffic sensor list (response code was " + status + ")");
        }

        // get response
        BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
        StringBuilder response = new StringBuilder();
        String s;
        while ((s = br.readLine()) != null) {
            response.append(s);
        }
        os.close();
        conn.disconnect();

        // parse response 
        ArrayList<HashMap<String, String>> output = new ArrayList<>();
        try {
            JSONObject response_json = (JSONObject) JSONValue.parse(response.toString());
            JSONArray coil_list = (JSONArray) response_json.get("Traffico_GetAnagraficaResult");
            int i, j;
            for (i = 0; i < coil_list.size(); i++) {
                JSONObject coil = (JSONObject) coil_list.get(i);
                JSONArray sensor_list = (JSONArray) coil.get("sensori");
                // note sensor_list can be empty (no sensors available)
                for (j = 0; j < sensor_list.size(); j++) {
                    JSONObject sensor = (JSONObject) sensor_list.get(j);
                    HashMap<String, String> h = new HashMap<>();
                    h.put("stationcode", "A22:" + coil.get("idspira") + ":" + sensor.get("idsensore"));
                    h.put("name", coil.get("descrizione") + " (" + getLaneText((Long) sensor.get("idcorsia") + "", (Long) sensor.get("iddirezione") + "") + ")");
                    h.put("pointprojection", "" + coil.get("latitudine") + "," + coil.get("longitudine"));
                    output.add(h);

                }
            }
        } catch (Exception e) {
            // null pointer or cast exception in case the json hasn't the expected form
            throw new RuntimeException("could not parse traffic sensor list");
        }

        if (DEBUG) {
            System.out.println("getTrafficSensors - got list of " + output.size() + " sensors");
        }

        return output;

    }

    /**
     * Retrieve the list of vehicle transit events for all known sensors.
     *
     * @param fromTS search events from this timestamp in the format understood
     * by the A22 server (ex. 1515740400000+0100)
     * @param toTS search events up to this timestamp in the format understood
     * by the A22 server (ex. 1515740400000+0100)
     *
     * @return an ArrayList of HashMaps with the vehicle transit event info
     *
     * @throws java.net.MalformedURLException, IOException
     */
    public ArrayList<HashMap<String, String>> getVehicles(String fromTS, String toTS) throws MalformedURLException, IOException {

        if (url == null || token == null) {
            throw new RuntimeException("there is no authenticated session");
        }

        ArrayList<HashMap<String, String>> output = new ArrayList<>();

        // retrieve the list of sensors and get unique coil IDs
        ArrayList<HashMap<String, String>> sensors = getTrafficSensors();
        HashMap<String, Integer> coils = new HashMap<>();
        int sid;
        for (sid = 0; sid < sensors.size(); sid++) {
            // stationcode = A22:coilid:sensorid
            String split[] = sensors.get(sid).get("stationcode").split(":");
            if (split.length != 3) {
                throw new RuntimeException("stationcode does not have the expected format");
            }
            coils.put(split[1], 1);
        }
        if (coils.isEmpty()) {
            return output;
        }

        // loop over the coil IDs and retrieve transit events for each coil ID, adding to the output list
        for (String coilid : coils.keySet()) {
            if (DEBUG) {
                System.out.println("getVehicles is retrieving vehicle transit events for coil ID " + coilid);
            }
            // make de-authentication request
            HttpURLConnection conn = (HttpURLConnection) (new URL(url + "/traffico/transiti")).openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("User-Agent", "IDM/traffic_a22");
            conn.setRequestProperty("Accept", "*/*");
            conn.setConnectTimeout(WS_CONN_TIMEOUT_MSEC);
            conn.setDoOutput(true);
            OutputStreamWriter os = new OutputStreamWriter(conn.getOutputStream());
            os.write("{\"request\":{\"sessionId\":\"" + token + "\",\"idspira\":" + coilid + ",\"fromData\":\"/Date(" + fromTS + ")/\",\"toData\":\"/Date(" + toTS + ")/\"}}\n");
            os.flush();
            int status = conn.getResponseCode();
            if (status != 200) {
                // as of today (2018-10-07) there are combinations of timestamps and coil IDs that will trigger a 500 response;
                // to work around this problem, we ignore non-200 responses
                if (DEBUG) {
                    System.out.println("    +- skipping (response status was " + status + ")");
                }
                // throw new RuntimeException("could not retrieve vehicle transit events list (response code was " + status + ")");
                continue;
            }

            // get response
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            StringBuilder response = new StringBuilder();
            String s;
            while ((s = br.readLine()) != null) {
                response.append(s);
            }
            os.close();
            conn.disconnect();

            // parse response 
            try {
                JSONObject response_json = (JSONObject) JSONValue.parse(response.toString());
                JSONArray event_list = (JSONArray) response_json.get("Traffico_GetTransitiResult");

                if (DEBUG) {
                    System.out.println("    +- got " + event_list.size() + " events");
                }

                int i, j;
                for (i = 0; i < event_list.size(); i++) {
                    JSONObject event = (JSONObject) event_list.get(i);
                    HashMap<String, String> h = new HashMap<>();
                    h.put("stationcode", "A22:" + event.get("idspira") + ":" + event.get("idsensore"));
                    h.put("distance",    "" + event.get("distanza"));
                    h.put("headway",     "" + event.get("avanzamento"));
                    h.put("speed",       "" + event.get("velocita"));
                    h.put("length",      "" + event.get("lunghezza"));
                    h.put("axles",       "" + event.get("assi"));
                    h.put("class",       "" + event.get("classe"));
                    h.put("direction",   "" + event.get("direzione"));
                    h.put("timestamp",   "" + event.get("data"));
                    h.put("against-traffic", "" + (Boolean) event.get("controsenso"));
                    output.add(h);
                }
            } catch (Exception e) {
                // null pointer or cast exception in case the json hasn't the expected form
                e.printStackTrace();
                throw new RuntimeException("could not parse vehicle transit events");
            }

            try {
                Thread.sleep(100); // sleep a bit to avoid overloading the server
            } catch (InterruptedException ex) {
            }

        } // for coilid 

        if (DEBUG) {
            System.out.println("getVehicles summary - coils: " + coils.keySet().size() + ", sensors: " + sensors.size() + ", transit events: " + output.size());
        }

        return output;

    }

    private String getLaneText(String lane, String orientation) {

        String s = "Corsia di ";

        switch (lane) {
            case "1":
                s += "Emergenza";
                break;
            case "2":
                s += "Marcia";
                break;
            case "3":
                s += "Sorpasso";
                break;
            case "4":
                s += "Sorpasso veloce";
                break;
            case "5":
                s += "Intera carreggiata";
                break;
            case "6":
                s += "Piazzola di sosta";
                break;
            case "7":
                s += "Emergenza e marcia";
                break;
            case "8":
                s += "Marcia e sorpasso";
                break;
            case "9":
                s += "Banchina";
                break;
            default:
                s += "n/a";
        }

        s += ", Direzione ";

        switch (orientation) {
            case "1":
                s += "Sud";
                break;
            case "2":
                s += "Nord";
                break;
            case "3":
                s += "Entrambe";
                break;
            case "4":
                s += "Non definita";
                break;
            default:
                s += "n/a";
        }

        return s;

    }
}
