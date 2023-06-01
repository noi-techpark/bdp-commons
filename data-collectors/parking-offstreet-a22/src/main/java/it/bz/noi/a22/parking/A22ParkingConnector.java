// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

/*
 *  A22 Parking Connector
 *
 *  (C) 2021 NOI Techpark Südtirol / Alto Adige
 *
 *  changelog:
 *  2021-05-17  1.0 - chris@1006.org
 */

package it.bz.noi.a22.parking;

import java.io.*;
import java.net.*;
import java.util.*;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.json.simple.*;

/**
 *  encapsulates the communication with the A22 web service
 */
public class A22ParkingConnector {


    private static final String user_agent = "NOI/A22ParkingConnector";
    private static final int WS_CONN_TIMEOUT_MSEC = 30000;
    private static final int WS_READ_TIMEOUT_MSEC = 1800000;

    private static final Logger LOG = LoggerFactory.getLogger(A22ParkingConnector.class);

    private static final boolean DEBUG = true;

    private String token = null;
    private String url = null;


    /**
     * get a new authentication token and store it in the instance
     *
     * @param url       A22 web service URL
     * @param username  A22 web service username
     * @param password  A22 web service password
     *
     * @throws java.io.IOException in case of communication failure
     *
     */
    public A22ParkingConnector(String url, String username, String password) throws IOException {

        if (DEBUG) {
            if ( this.selfTestExtractors()) {
                LOG.debug("selfTestExtractors() success");
            } else {
                LOG.warn("selfTestExtractors() FAILED");
                throw new IOException("self test failed");
            }
        }

        String auth_json = "{\"request\":{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}}";

        // make authentication request
        HttpURLConnection conn = (HttpURLConnection) (new URL(url + "/token")).openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("User-Agent", user_agent);
        conn.setRequestProperty("Accept", "*/*");
        conn.setConnectTimeout(WS_CONN_TIMEOUT_MSEC);
        conn.setReadTimeout(WS_READ_TIMEOUT_MSEC);
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
            LOG.warn(e.getMessage(), e);
            throw new RuntimeException("authentication failure (could not parse response)");
        }

        if (session_id == null) {
            throw new RuntimeException("authentication failure (could not find sessionId in response)");
        }

        this.url = url;
        this.token = session_id;

        if (DEBUG) {
            LOG.debug("authentication OK new token = " + token);
        }

    }


    /**
     * release the authentication token
     *
     * @throws IOException in case of communication failure
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
        conn.setReadTimeout(WS_READ_TIMEOUT_MSEC);
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
            LOG.warn(e.getMessage(), e);
            throw new RuntimeException("de-authentication failure (could not parse response)");
        }
        if (result == null || !result) {
            throw new RuntimeException("de-authentication failure (de-authentication was not confirmed)");
        }

        if (DEBUG) {
            LOG.debug("de-authentication OK old token = " + token);
        }

        this.url = null;
        this.token = null;

    }

    /**
     * fetch car park info ("/parcheggi/anagrafica")
     *
     * @return an ArrayList of A22CarParkInfo objects
     *
     * @throws IOException in case of communication failure
     */
    public ArrayList<A22CarParkInfo> getInfo() throws IOException {

        if (url == null || token == null) {
            throw new RuntimeException("not authenticated");
        }

        String a22path = "/parcheggi/anagrafica";
        String a22name = "Parcheggi_AnagraficaResult";
        String a22request = "{\"request\":{\"sessionId\":\"" + token + "\"}}\n";

        HttpURLConnection conn = (HttpURLConnection) (new URL(url + a22path)).openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("User-Agent", user_agent);
        conn.setRequestProperty("Accept", "*/*");
        conn.setConnectTimeout(WS_CONN_TIMEOUT_MSEC);
        conn.setReadTimeout(WS_READ_TIMEOUT_MSEC);
        conn.setDoOutput(true);
        OutputStreamWriter os = new OutputStreamWriter(conn.getOutputStream());
        os.write(a22request);
        os.flush();
        int status = conn.getResponseCode();
        if (status != 200) {
            throw new RuntimeException("could not get car parks (response code was " + status + ")");
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

        ArrayList<A22CarParkInfo> output = new ArrayList<>();

        JSONArray json_array;
        try {
            json_array = (JSONArray) ((JSONObject) JSONValue.parse(response.toString())).get(a22name);
        } catch (Exception e) {
            // throw an error in case not even the top level element cannot be extracted as expected
            if (DEBUG) {
                LOG.warn("---");
                LOG.warn("getInfo() ERROR: unparsable response:");
                LOG.warn("vvv");
                LOG.warn(response.toString());
                LOG.warn("^^^");
                LOG.warn(e.getMessage(), e);
                LOG.warn("---");
            }
            throw new IOException("ERROR: unparsable response");
        }

        int skipped = 0;
        int i;
        for (i = 0; i < json_array.size(); i++) {

            try {

                JSONObject json_record = (JSONObject) json_array.get(i);
                A22CarParkInfo record = new A22CarParkInfo();

                record.setId(extractLong(json_record, "id"));
                record.setDescrizione(extractString(json_record, "descrizione"));
                record.setAutostrada(extractString(json_record, "autostrada"));
                record.setIddirezione(extractLong(json_record, "iddirezione"));
                record.setMetro(extractLong(json_record, "metro"));
                record.setLatitudine(extractDouble(json_record, "latitudine"));
                record.setLongitudine(extractDouble(json_record, "longitudine"));

                output.add(record);

            } catch (Exception e) {

                // null pointer, cast or number format exception in case the json hasn't the expected form
                // or has incompatible data types: log and skip the record
                if (DEBUG) {
                    LOG.warn("---");
                    LOG.warn("getInfo() ERROR: skipping unparsable record:");
                    LOG.warn("vvv");
                    LOG.warn(json_array.get(i).toString());
                    LOG.warn("^^^");
                    LOG.warn(e.getMessage(), e);
                    LOG.warn("---");
                }
                skipped++;
                continue;
            }

        }

        if (DEBUG) {
            LOG.debug("getInfo() OK: got info about " + output.size() + " car parks, " + skipped + " skipped");
        }

        return output;

    }

    /**
     * fetch car park capacity ("/parcheggi/stato")
     *
     * @return an ArrayList of A22CarParkCapacity objects
     *
     * @throws IOException in case of communication failure
     */
    public ArrayList<A22CarParkCapacity> getCapacity() throws IOException {

        if (url == null || token == null) {
            throw new RuntimeException("not authenticated");
        }

        String a22path = "/parcheggi/stato";
        String a22name = "Parcheggi_OccupazioneResult";
        String a22request = "{\"request\":{\"sessionId\":\"" + token + "\"}}\n";

        HttpURLConnection conn = (HttpURLConnection) (new URL(url + a22path)).openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("User-Agent", user_agent);
        conn.setRequestProperty("Accept", "*/*");
        conn.setConnectTimeout(WS_CONN_TIMEOUT_MSEC);
        conn.setReadTimeout(WS_READ_TIMEOUT_MSEC);
        conn.setDoOutput(true);
        OutputStreamWriter os = new OutputStreamWriter(conn.getOutputStream());
        os.write(a22request);
        os.flush();
        int status = conn.getResponseCode();
        if (status != 200) {
            throw new RuntimeException("could not get car park capacity (response code was " + status + ")");
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

        ArrayList<A22CarParkCapacity> output = new ArrayList<>();

        JSONArray json_array;
        try {
            json_array = (JSONArray) ((JSONObject) JSONValue.parse(response.toString())).get(a22name);
        } catch (Exception e) {
            // throw an error in case not even the top level element cannot be extracted as expected
            if (DEBUG) {
                LOG.warn("---");
                LOG.warn("getCapacity() ERROR: unparsable response:");
                LOG.warn("vvv");
                LOG.warn(response.toString());
                LOG.warn("^^^");
                LOG.warn(e.getMessage(), e);
                LOG.warn("---");
            }
            throw new IOException("ERROR: unparsable response");
        }

        int skipped = 0;
        int i;
        for (i = 0; i < json_array.size(); i++) {

            try {

                JSONObject json_record = (JSONObject) json_array.get(i);
                A22CarParkCapacity record = new A22CarParkCapacity();

                record.setId(extractLong(json_record, "id"));
                record.setStato(extractLong(json_record, "stato"));
                record.setCapienza(extractLong(json_record, "capienza"));
                record.setPosti_liberi(extractLong(json_record, "posti_liberi"));

                output.add(record);

            } catch (Exception e) {

                // null pointer, cast or number format exception in case the json hasn't the expected form
                // or has incompatible data types: log and skip the record
                if (DEBUG) {
                    LOG.warn("---");
                    LOG.warn("getCapacity() ERROR: skipping unparsable record:");
                    LOG.warn("vvv");
                    LOG.warn(json_array.get(i).toString());
                    LOG.warn("^^^");
                    LOG.warn(e.getMessage(), e);
                    LOG.warn("---");
                }
                skipped++;
                continue;
            }

        }

        if (DEBUG) {
            LOG.debug("getCapacity() OK: got capacity for " + output.size() + " car parks, " + skipped + " skipped");
        }

        return output;

    }

    private Boolean selfTestExtractors() {
        return      "str".equals(this.extractString((JSONObject)JSONValue.parse("{\"data\":\"str\"}"), "data"))
                && "true".equals(this.extractString((JSONObject)JSONValue.parse("{\"data\":true}"),    "data"))
                &&    "3".equals(this.extractString((JSONObject)JSONValue.parse("{\"data\":3}"),       "data"))
                && "3.14".equals(this.extractString((JSONObject)JSONValue.parse("{\"data\":3.14}"),    "data"))

                && Long.valueOf(3).equals(this.extractLong((JSONObject)JSONValue.parse("{\"data\":3}"),     "data"))
                && Long.valueOf(3).equals(this.extractLong((JSONObject)JSONValue.parse("{\"data\":\"3\"}"), "data"))

                && Double.valueOf(3.0).equals(this.extractDouble((JSONObject)JSONValue.parse("{\"data\":3}"),        "data"))
                && Double.valueOf(3.0).equals(this.extractDouble((JSONObject)JSONValue.parse("{\"data\":3.0}"),     "data"))
                && Double.valueOf(3.0).equals(this.extractDouble((JSONObject)JSONValue.parse("{\"data\":\"3.0\"}"), "data"));
    }

    private String extractString(JSONObject obj, String key) throws java.lang.NumberFormatException {
        // accept all scalar types from the JSON API (but not objects or arrays)
        Object test = obj.get(key);
        if (test == null) {
            return null;
        }
        if ("String".equals(test.getClass().getSimpleName())) {
            return (String)(test);
        }
        if ("Boolean".equals(test.getClass().getSimpleName())) {
            return String.valueOf((Boolean)(test));
        }
        if ("Long".equals(test.getClass().getSimpleName())) {
            return String.valueOf((Long)(test));
        }
        if ("Double".equals(test.getClass().getSimpleName())) {
            return String.valueOf((Double)(test));
        }
        throw new NumberFormatException("value cannot be interpreted as String");
    }

    private Long extractLong(JSONObject obj, String key) throws java.lang.NumberFormatException {
        // 3 and "3" are both acceptable as Long,
        // the JSON API returns these as Long or String
        Object test = obj.get(key);
        if (test == null) {
            return null;
        }
        if ("Long".equals(test.getClass().getSimpleName())) {
            return (Long)test;
        }
        if ("String".equals(test.getClass().getSimpleName())) {
            return Long.valueOf((String)test); // might throw java.lang.NumberFormatException
        }
        throw new NumberFormatException("value cannot be interpreted as Long");
    }

    private Double extractDouble(JSONObject obj, String key) throws java.lang.NumberFormatException {
        // 3, 3.0 and "3.0" are all acceptable as Double,
        // the JSON API returns these as Long, Double or String
        Object test = obj.get(key);
        if (test == null) {
            return null;
        }
        if ("Long".equals(test.getClass().getSimpleName())) {
            return Double.valueOf((Long)test);
        }
        if ("Double".equals(test.getClass().getSimpleName())) {
            return (Double)test;
        }
        if ("String".equals(test.getClass().getSimpleName())) {
            return Double.valueOf((String)test); // might throw java.lang.NumberFormatException
        }
        throw new NumberFormatException("value cannot be interpreted as Double");
    }

}
