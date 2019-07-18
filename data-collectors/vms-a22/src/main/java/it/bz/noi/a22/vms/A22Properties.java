package it.bz.noi.a22.vms;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Properties;

public class A22Properties extends Properties {

    public A22Properties(String propertiesFile) {
        try {
            try (Reader in = new InputStreamReader(getClass().getResourceAsStream(propertiesFile)))
            {
                this.load(in);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
