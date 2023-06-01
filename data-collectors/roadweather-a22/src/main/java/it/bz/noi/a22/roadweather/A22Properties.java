// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.noi.a22.roadweather;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Properties;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class A22Properties extends Properties {

    private static Logger log = LoggerFactory.getLogger(A22Properties.class);

    public A22Properties(String propertiesFile) {
        try {
            try (Reader in = new InputStreamReader(getClass().getResourceAsStream(propertiesFile)))
            {
                this.load(in);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
