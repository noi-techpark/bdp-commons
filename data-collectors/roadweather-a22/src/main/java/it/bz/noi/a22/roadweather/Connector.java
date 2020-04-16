/*
 *  A22 road weather API connector
 *
 *  (C) 2029 NOI Techpark Südtirol / Alto Adige
 *  
 *  
 *  changelog:
 *  	2020-02-28 chris@1006.org  
 *  
 *  
 *  upstream API reference:
 *   	2017-002 Autostrada del Brennero S.p.A. - Manutenzione ordinaria e straordinaria sistema di supervisione CAU - anno 2017
 *  	No doc: 2017-0046
 *  	Revisione: 3
 *  
 */
package it.bz.noi.a22.roadweather;

import java.io.*;
import java.net.*;
import java.util.*;

import org.apache.log4j.Logger;
import org.json.simple.*;

/**
 * encapsulates the A22 API to query road weather conditions
 */
public class Connector {

    private static Logger log = Logger.getLogger(Connector.class);

    private static final String user_agent = "NOI/A22RoadWeatherConnector";
    private static final int WS_CONN_TIMEOUT_MSEC = 30000;

    private String token = null;
    private String url = null;

    /**
     * get a new authentication token and store it in the instance
     *
     * @param url
     * @param username
     * @param password
     *
     * @throws java.io.IOException
     *
     */
    public Connector(String url, String username, String password) throws IOException {

        String auth_json = "{\"request\":{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}}";

        // make authentication request
        HttpURLConnection conn = (HttpURLConnection) (new URL(url + "/token")).openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("User-Agent", user_agent);
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
            // null pointer or cast exception in case the JSON hasn't the expected form
            e.printStackTrace();
            throw new RuntimeException("authentication failure (could not parse response)");
        }

        if (session_id == null) {
            throw new RuntimeException("authentication failure (could not find sessionId in response)");
        }

        this.url = url;
        this.token = session_id;

        log.debug("authentication    OK new token = " + token);

    }

    /**
     * release the authentication token
     *
     * @throws IOException
     */
    public void close() throws IOException {

        if (url == null || token == null) {
            throw new RuntimeException("de-authentication failure (currently not authenticated)");
        }

        // make de-authentication request
        HttpURLConnection conn = (HttpURLConnection) (new URL(url + "/token/" + token)).openConnection();
        conn.setRequestMethod("DELETE");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("User-Agent", user_agent);
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
            // null pointer or cast exception in case the JSON hasn't the expected form
            e.printStackTrace();
            throw new RuntimeException("de-authentication failure (could not parse response)");
        }
        if (result == null || result != true) {
            throw new RuntimeException("de-authentication failure (de-authentication was not confirmed)");
        }

        log.debug("de-authentication OK old token = " + token);

        this.url = null;
        this.token = null;

    }

    /**
     * get info about the weather stations
     *
     * @return an ArrayList of HashMaps with the info about all stations
     *
     * @throws IOException
     */
    public ArrayList<HashMap<String, String>> getStations() throws IOException {

        if (url == null || token == null) {
            throw new RuntimeException("not authenticated");
        }

        HttpURLConnection conn = (HttpURLConnection) (new URL(url + "/meteo/anagrafica")).openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("User-Agent", user_agent);
        conn.setRequestProperty("Accept", "*/*");
        conn.setConnectTimeout(WS_CONN_TIMEOUT_MSEC);
        conn.setDoOutput(true);
        OutputStreamWriter os = new OutputStreamWriter(conn.getOutputStream());
        os.write("{\"sessionId\":\"" + token + "\"}\n");
        os.flush();
        int status = conn.getResponseCode();
        if (status != 200) {
            throw new RuntimeException("could not get weather stations (response code was " + status + ")");
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
            JSONArray station_list = (JSONArray) response_json.get("MeteoAnagraficaResult");
            int i;
            for (i = 0; i < station_list.size(); i++) {
                JSONObject station = (JSONObject) station_list.get(i);
                // note we return all fields as strings, but will type check the numbers
                HashMap<String, String> h = new HashMap<>();
                h.put("autostrada", (String)station.get("autostrada"));
                h.put("idcabina", String.valueOf((Long)station.get("idcabina")));
                h.put("descrizione", (String)station.get("descrizione"));
                h.put("longitudine", String.valueOf((Double) station.get("longitudine")));
                h.put("latitudine", String.valueOf((Double) station.get("latitudine")));
                h.put("iddirezione", String.valueOf((Long) station.get("iddirezione")));
                h.put("metro", String.valueOf((Long) station.get("metro")));
                output.add(h);
            }
         } catch (Exception e) {
            // null pointer, cast or number format exception in case the JSON hasn't the expected form or data types
            e.printStackTrace();
            throw new RuntimeException("could not parse list of stations");
        }

        log.debug("getStations() OK: got " + output.size() + " stations");

        return output;

    }

    /**
     * get the weather data for the given station ("idcabina") within the given time interval
     *
     * @param fr - Unix epoch in UTC indicating the lower bound of the interval
     *
     * @param to - Unix epoch in UTC indicating the upper bound of the interval (the interval *includes* this time stamp)
     *
     * @param id - station ID ("idcabina")
     *
     * @return an ArrayList of HashMaps with the weather data for the given station in the given interval
     *
     * @throws IOException
     */
    public ArrayList<HashMap<String, String>> getWeatherData(long fr, long to, long id) throws IOException {

        if (url == null || token == null) {
            throw new RuntimeException("not authenticated");
        }

        ArrayList<HashMap<String, String>> output = new ArrayList<>();

        HashMap<Integer, Integer> http_codes = new HashMap<>();

        // convert to format used by A22
        // (see the comment "Reverse engineering the A22 time stamp format" at the end of the file)
        String frTS = fr + "000+0000";
        String toTS = to + "999+0000";

        // retrieve events
        HttpURLConnection conn = (HttpURLConnection) (new URL(url + "/meteo/misure")).openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("User-Agent", user_agent);
        conn.setRequestProperty("Accept", "*/*");
        conn.setConnectTimeout(WS_CONN_TIMEOUT_MSEC);
        conn.setDoOutput(true);
        OutputStreamWriter os = new OutputStreamWriter(conn.getOutputStream());
        os.write("{\"request\":{\"sessionId\":\"" + token + "\",\"idcabina\":" + id + ",\"fromData\":\"/Date(" + frTS + ")/\",\"toData\":\"/Date(" + toTS + ")/\"}}\n");
        os.flush();
        int status = conn.getResponseCode();
        if (http_codes.containsKey(status)) {
            http_codes.put(status, http_codes.get(status) + 1);
        } else {
            http_codes.put(status, 1);
        }
        if (status != 200) {
            throw new RuntimeException("could not get weather data (response code was " + status + ")");
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
            JSONArray weatherdata_list = (JSONArray) response_json.get("MeteoMisureResult");
            int i;
            for (i = 0; i < weatherdata_list.size(); i++) {
                JSONObject weatherdata = (JSONObject) weatherdata_list.get(i);
                
                // note we return all fields as strings, but will type check the numbers
                
                HashMap<String, String> h = new HashMap<>();
                
                h.put("idcabina", String.valueOf((Long)weatherdata.get("idcabina")));
                
                // convert from format used by A22
                // (see the comment "Reverse engineering the A22 time stamp format" at the end of the file)
                h.put("data", String.valueOf(Long.valueOf(((String)weatherdata.get("data")).substring(6, 16))));
                
                // note all the following values can be null (JSON allows null), so when we cast
                // to Long, null will be correctly recognized and store in the HashMap as "null";
                // however, a double value might come as Long or Double from the JSON API,
                // we cannot cast it to Double right away, we need to cast it to String and then 
                // parse it as Double, paying attention to handle null correctly
                h.put("allarme_frana", String.valueOf((Long) weatherdata.get("allarme_frana")));
                h.put("prec_abs", getDoubleFromJSON(weatherdata.get("prec_abs")));
                h.put("prec_diff", getDoubleFromJSON(weatherdata.get("prec_diff")));
                h.put("prec_qta", getDoubleFromJSON(weatherdata.get("prec_qta")));
                h.put("prec_tipo", String.valueOf((Long) weatherdata.get("prec_tipo")));
                h.put("press_abs", getDoubleFromJSON(weatherdata.get("press_abs")));
                h.put("qta_salina", getDoubleFromJSON(weatherdata.get("qta_salina")));
                h.put("qta_salina_2", getDoubleFromJSON(weatherdata.get("qta_salina_2")));
                h.put("raffica_vel", getDoubleFromJSON(weatherdata.get("raffica_vel")));
                h.put("ssuolo_temp1", getDoubleFromJSON(weatherdata.get("ssuolo_temp1")));
                h.put("ssuolo_temp1_2", getDoubleFromJSON(weatherdata.get("ssuolo_temp1_2")));
                h.put("ssuolo_temp2", getDoubleFromJSON(weatherdata.get("ssuolo_temp2")));
                h.put("ssuolo_temp2_2", getDoubleFromJSON(weatherdata.get("ssuolo_temp2_2")));
                h.put("stato_meteo", String.valueOf((Long) weatherdata.get("stato_meteo")));
                h.put("strato_h2o", getDoubleFromJSON(weatherdata.get("strato_h2o")));
                h.put("strato_h2o_2", getDoubleFromJSON(weatherdata.get("strato_h2o_2")));
                h.put("temp_aria", getDoubleFromJSON(weatherdata.get("temp_aria")));
                h.put("temp_rugiada", getDoubleFromJSON(weatherdata.get("temp_rugiada")));
                h.put("temp_suolo", getDoubleFromJSON(weatherdata.get("temp_suolo")));
                h.put("temp_suolo_2", getDoubleFromJSON(weatherdata.get("temp_suolo_2")));
                h.put("umidita_abs", getDoubleFromJSON(weatherdata.get("umidita_abs")));
                h.put("umidita_rel", getDoubleFromJSON(weatherdata.get("umidita_rel")));
                h.put("vento_dir", getDoubleFromJSON(weatherdata.get("vento_dir")));
                h.put("vento_vel", getDoubleFromJSON(weatherdata.get("vento_vel")));
                
                output.add(h);
            }
        } catch (Exception e) {
            // null pointer, cast or number format exception in case the JSON hasn't the expected form or data types
            e.printStackTrace();
            throw new RuntimeException("could not parse list of weather data");
        }

        log.debug("getWeatherData() OK: got " + output.size() + " weather data records");

        return output;

    }

    private String getDoubleFromJSON(Object val) {
    	String str = String.valueOf(val);
    	if (str.equals("null")) {
    		return str;
    	}
    	Double d = Double.parseDouble(str);
    	return String.valueOf(d);
    }
    
    

    /*
    
    Reverse engineering the A22 time stamp format
    --------------------------------------------

    In CET we have daylight saving change from +2 to +1
    on 28 oct 2018 (the clock goes from 3:00 to 2:00).

    These are events from one sensor around the daylight change:

    [...]
    /Date(1540688331000+0200)/
    /Date(1540688331000+0200)/
    /Date(1540688358000+0200)/
    /Date(1540688358000+0200)/
    /Date(1540688390000+0200)/
    /Date(1540688560000+0100)/
    /Date(1540688645000+0100)/
    /Date(1540688645000+0100)/
    /Date(1540688648000+0100)/
    /Date(1540688656000+0100)/
    [...]
    
    /Date(1540688390000+0200)/

        $ date --date='@1540688390'
        Sun Oct 28 00:59:50 UTC 2018

        00:59 UTC would be 02:59 CET ~ 03 CET


    /Date(1540688560000+0100)/

        $ date --date='@1540688560'
        Sun Oct 28 01:02:40 UTC 2018

        01:02 UTC would be 02:02 CET ~ 02 CET

    That means the first part of this string *is* the correct time stamp 
    in unix epoch / UTC. 

     */
}
