/*
 *  A22 sign API connector
 *
 *  (C) 2019 NOI Techpark Südtirol / Alto Adige
 *
 *  author: chris@1006.org  
 */

package it.bz.noi.a22.vms;

import java.io.*;
import java.net.*;
import java.util.*;

import org.json.simple.*;

/**
 * encapsulates the A22 API to query historic values of variable road signs
 */
public class Connector {

    private static final String user_agent = "NOI/A22SignConnector";
    private static final int WS_CONN_TIMEOUT_MSEC = 30000;
    private static final boolean DEBUG = false;

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
            // null pointer or cast exception in case the json hasn't the expected form
            e.printStackTrace();
            throw new RuntimeException("authentication failure (could not parse response)");
        }

        if (session_id == null) {
            throw new RuntimeException("authentication failure (could not find sessionId in response)");
        }

        this.url = url;
        this.token = session_id;

        if (DEBUG) {
            System.err.println("authentication    OK new token = " + token);
        }

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
            // null pointer or cast exception in case the json hasn't the expected form
            e.printStackTrace();
            throw new RuntimeException("de-authentication failure (could not parse response)");
        }
        if (result == null || result != true) {
            throw new RuntimeException("de-authentication failure (de-authentication was not confirmed)");
        }

        if (DEBUG) {
            System.err.println("de-authentication OK old token = " + token);
        }

        this.url = null;
        this.token = null;

    }

    /**
     * get info about variable road signs
     *
     * @return an ArrayList of HashMaps with the info about all variable road
     * signs
     *
     * @throws IOException
     */
    public ArrayList<HashMap<String, String>> getSigns() throws IOException {

        if (url == null || token == null) {
            throw new RuntimeException("not authenticated");
        }

        HttpURLConnection conn = (HttpURLConnection) (new URL(url + "/infoutenza/anagrafica")).openConnection();
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
            throw new RuntimeException("could not get signs (response code was " + status + ")");
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
            JSONArray pmv_list = (JSONArray) response_json.get("Infoutenza_GetPMVResult");
            int i;
            for (i = 0; i < pmv_list.size(); i++) {
                JSONObject pmv = (JSONObject) pmv_list.get(i);
                // note we return Strings, but will type check the numbers
                HashMap<String, String> h = new HashMap<>();
                h.put("id", String.valueOf((Long) pmv.get("idpmv")));
                h.put("pmv_type", String.valueOf((Long) pmv.get("tipopmv")));
                h.put("road", (String) pmv.get("autostrada"));
                h.put("descr", (String) pmv.get("descrizione"));
                h.put("segment_end", String.valueOf((Long) pmv.get("finetratto")));       // appears to always contain "0"
                h.put("segment_start", String.valueOf((Long) pmv.get("iniziotratto")));   // idem
                h.put("direction_id", String.valueOf((Long) pmv.get("iddirezione")));
                h.put("position_m", String.valueOf((Long) pmv.get("metro")));
                h.put("lat", String.valueOf((Double) pmv.get("latitudine")));
                h.put("long", String.valueOf((Double) pmv.get("longitudine")));
                output.add(h);
            }
        } catch (Exception e) {
            // null pointer, cast or number format exception in case the json hasn't the expected form or data types
            e.printStackTrace();
            throw new RuntimeException("could not parse list of signs");
        }

        if (DEBUG) {
            System.err.println("getSigns() OK: got " + output.size() + " signs");
        }

        return output;

    }

    /**
     * get display change events for the given sign within the given time
     * interval
     *
     * @param fr search events from this timestamp (Unix epoch in UTC)
     *
     * @param to search events up to *and* *including* this timestamp (Unix
     * epoch in UTC)
     *
     * @param id variable road sign id ('idpmv')
     *
     * @return a nested data structure with the events
     *
     * @throws IOException
     */
    public ArrayList<HashMap<String, Object>> getEvents(long fr, long to, long id) throws IOException {

        if (url == null || token == null) {
            throw new RuntimeException("not authenticated");
        }

        ArrayList<HashMap<String, Object>> output = new ArrayList<>();

        HashMap<Integer, Integer> http_codes = new HashMap<>();

        // convert to format used by A22
        // (see the comment "Reverse engineering the A22 timestamp format" at the end of the file)
        String frTS = fr + "000+0000";
        String toTS = to + "999+0000";

        // retrieve events
        HttpURLConnection conn = (HttpURLConnection) (new URL(url + "/infoutenza/esposizioni")).openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("User-Agent", user_agent);
        conn.setRequestProperty("Accept", "*/*");
        conn.setConnectTimeout(WS_CONN_TIMEOUT_MSEC);
        conn.setDoOutput(true);
        OutputStreamWriter os = new OutputStreamWriter(conn.getOutputStream());
        os.write("{\"request\":{\"sessionId\":\"" + token + "\",\"idpmv\":" + id + ",\"fromData\":\"/Date(" + frTS + ")/\",\"toData\":\"/Date(" + toTS + ")/\"}}\n");
        os.flush();
        int status = conn.getResponseCode();
        if (http_codes.containsKey(status)) {
            http_codes.put(status, http_codes.get(status) + 1);
        } else {
            http_codes.put(status, 1);
        }
        if (status != 200) {
            throw new RuntimeException("could not get events (response code was " + status + ")");

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
            JSONArray event_list = (JSONArray) response_json.get("Infoutenza_GetEsposizioneResult");
            int i;
            for (i = 0; i < event_list.size(); i++) {
                JSONObject event = (JSONObject) event_list.get(i);
                HashMap<String, Object> h = new HashMap<>();
                h.put("id", String.valueOf((Long) event.get("idpmv")));
                // convert from format used by A22
                // (see the comment "Reverse engineering the A22 timestamp format" at the end of the file)
                h.put("timestamp", String.valueOf(event.get("data")).substring(6, 16));
                JSONArray component_list = (JSONArray) event.get("componenti");
                ArrayList<HashMap<String, String>> output_nest = new ArrayList<>();
                int j;
                for (j = 0; j < component_list.size(); j++) {
                    JSONObject component = (JSONObject) component_list.get(j);
                    HashMap<String, String> c = new HashMap<>();
                    c.put("data", String.valueOf(component.get("dati")));
                    c.put("component_id", String.valueOf((Long) component.get("idcomponente")));
                    c.put("page_id", String.valueOf((Long) component.get("idpagina")));
                    c.put("status", String.valueOf((Long) component.get("stato")));
                    output_nest.add(c);
                }
                h.put("component", output_nest);
                output.add(h);
            }
        } catch (Exception e) {
            // null pointer, cast or number format exception in case the json hasn't the expected form or data types
            e.printStackTrace();
            throw new RuntimeException("could not parse list of events");
        }

        if (DEBUG) {
            System.err.println("getEvents() OK: got " + output.size() + " events");
        }

        return output;

    }

    /*
    
    Reverse engineering the A22 timestamp format
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

    That means the first part of this string *is* the correct timestamp 
    in unix epoch / UTC. 

     */
}
