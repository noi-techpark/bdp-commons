package traffic_a22;

import java.net.*;
import java.io.*;
import java.util.*;

/**
 * A22 traffic API connector integration test.
 *
 * @author chris@1006.org
 */
public class Main {

    /**
     * Test the traffic_a22.Connector API.
     *
     * Please note that the web service URL and the authentication JSON string
     * must be provided in a properties file $HOME/A22Connector.properties:
     *
     * URL=xxx AUTH_JSON={"request":{"username":"xxx","password":"xxx"}}
     *
     * @param args - ignored
     * @throws MalformedURLException, IOException
     */
    public static void main(String[] args) throws MalformedURLException, IOException {

        Properties secrets = new Properties();
        secrets.load(new FileReader(System.getProperty("user.home") + File.separator + "A22Connector.properties"));

        String url = secrets.getProperty("URL");
        String auth_json = secrets.getProperty("AUTH_JSON");

        if (url == null || auth_json == null) {
            System.err.println("missing properties, expected were URL and AUTH_JSON");
            return;
        }

        ArrayList<HashMap<String, String>> res;

        Connector conn = new Connector(url, auth_json);
        // 1 minute from Fri Nov 23 07:00:00 UTC 2018
        res = conn.getVehicles("1542956400", "1542956460");
        int i;

        for (i = 0; i < res.size(); i++) {
            System.out.println(res.get(i));
        }

        conn.close();

    }

}
