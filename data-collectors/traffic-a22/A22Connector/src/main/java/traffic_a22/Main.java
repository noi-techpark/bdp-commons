package traffic_a22;

import java.net.*;
import java.io.*;
import java.util.*;

/**
 * A22 connector integration test.
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
     * URL=xxx 
     * AUTH_JSON={"request":{"username":"xxx","password":"xxx"}}
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

        res = conn.getTrafficSensors();
        
        System.out.println("response size was " + res.size());
        System.out.println("first hashmap was " + res.get(0));
        
        /*

        the A22 server expect date Strings as what appears to be unix time stamps times 1000
        with an added time zone
        
        the following dates in UTC would be given as
        
        date --date='2018-09-01 12:00:00' +"%s"
        1535803200
        # becomes 1535803200000+0000
        date --date='2018-09-01 12:10:00' +"%s"
        1535803800
        # becomes 1535803800000+0000
        
        */
        
        res = conn.getVehicles("1535803200000+0000", "1535803800000+0000");

        System.out.println("response size was " + res.size());
        System.out.println("first hashmap was " + res.get(0));

        conn.close();

    }

}
