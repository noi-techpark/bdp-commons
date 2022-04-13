/*
 *  A22 Properties
 *
 *  (C) 2021 NOI Techpark Südtirol / Alto Adige
 *
 *  changelog:
 *  2021-06-01  1.0 - thomas.nocker@catch-solve.tech
 */

package it.bz.noi.a22.parking;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Properties;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class A22Properties extends Properties {

    private static Logger LOG = LoggerFactory.getLogger(A22Properties.class);

    public A22Properties(String propertiesFile) {
        try {
            try (Reader in = new InputStreamReader(getClass().getResourceAsStream(propertiesFile)))
            {
                this.load(in);
            }
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
    }
}
