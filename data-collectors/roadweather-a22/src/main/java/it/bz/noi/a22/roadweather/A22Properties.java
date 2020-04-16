package it.bz.noi.a22.roadweather;

import org.apache.log4j.Logger;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Properties;

public class A22Properties extends Properties {

    private static Logger log = Logger.getLogger(A22Properties.class);

    public A22Properties(String propertiesFile) {
        try {
            try (Reader in = new InputStreamReader(getClass().getResourceAsStream(propertiesFile)))
            {
                this.load(in);
            }
        } catch (Exception e) {
            log.error(e);
        }
    }
}
