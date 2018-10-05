package traffic_a22;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import org.json.simple.*;

/**
 *
 * @author chris@1006.org
 */
public class Main {

    /**
     * Test the traffic_a22.Connector API.
     *
     * Please note that the web service URL and the authentication JSON string
     * must be provided as system properties:
     *
     * -DURL="..." -DAUTH_JSON="..."
     *
     * @param args - ignored
     * @throws java.lang.Exception
     */
    public static void main(String[] args) throws Exception {

        String url = System.getProperty("URL");
        String auth_json = System.getProperty("AUTH_JSON");

        if (url == null || auth_json == null) {
            System.err.println("missing system properties: -DURL=\"...\" -DAUTH_JSON=\"...\"");
            return;
        }

        ArrayList<HashMap<String, String>> res;
        
        Connector conn = new Connector(url, auth_json);

        res = conn.getTrafficSensors();
        System.out.println(res);

        res = conn.getVehicles("1483268800000+0000", "1483269400000+0000");

        System.out.println(res);
        
        conn.close();
        
    }

}
