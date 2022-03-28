package it.bz.noi.a22.traveltimes;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Properties;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class A22Properties extends Properties {

    private static final Logger LOG = LoggerFactory.getLogger(A22Properties.class);

    public A22Properties(String propertiesFile) {
        try {
            try (Reader in = new InputStreamReader(getClass().getResourceAsStream(propertiesFile)))
            {
                this.load(in);
            }
        } catch (Exception e) {
            LOG.error("{}", e);
        }
    }
}
