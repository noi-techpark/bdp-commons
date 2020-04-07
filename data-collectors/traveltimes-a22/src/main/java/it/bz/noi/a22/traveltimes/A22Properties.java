package it.bz.noi.a22.traveltimes;

import org.apache.log4j.Logger;

import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.Properties;

public class A22Properties extends Properties {

    private static Logger log = Logger.getLogger(A22Properties.class);

    public A22Properties(String propertiesFile) {
        try {
            URL url = getClass().getResource(propertiesFile);
            try (Reader in = new InputStreamReader(getClass().getResourceAsStream(propertiesFile)))
            {
                this.load(in);
            }
        } catch (Exception e) {
            log.error(e);
        }
    }
}
