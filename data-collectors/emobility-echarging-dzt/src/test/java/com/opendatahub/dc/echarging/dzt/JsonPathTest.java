// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package com.opendatahub.dc.echarging.dzt;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.FileInputStream;
import java.nio.charset.Charset;

import org.junit.jupiter.api.Test;
import org.springframework.util.StreamUtils;

public class JsonPathTest {
    private final static String examplePath = "src/test/resources/example_stations";
    @Test
    void parseCompleteJson() throws Exception {
        String jsonStr = StreamUtils.copyToString(new FileInputStream(examplePath + "/complete.json"),
                Charset.defaultCharset());
        DZTClient.Station station = DZTParser.parseJsonToStation(jsonStr);
        assertEquals("64891004", station.id);
        assertEquals("Stadtwerke Hattingen GmbH - 64891004 - Normalladeeinrichtung", station.name);
        assertEquals(5.1397E1, station.latitude);
        assertEquals(7.164E0, station.longitude);
        assertEquals("Deutschland", station.addressCountry);
        assertEquals("Hattingen", station.addressLocality);
        assertEquals("45525", station.addressPostalCode);
        assertEquals("Weg zum Wasserwerk 23", station.addressStreet);
        assertEquals("Bundesnetzagentur", station.publisher);
        assertEquals("https://www.bundesnetzagentur.de/", station.publisherUrl);

        assertEquals("AC Steckdose Typ 2", station.plugs.get(0).name);
        assertEquals("AC Steckdose Typ 2", station.plugs.get(0).socket);
        assertEquals("kW", station.plugs.get(0).powerUnitCode);
        assertEquals("Kilowatt", station.plugs.get(0).powerUnitText);
        assertEquals(22.0, station.plugs.get(0).powerValue);

        assertEquals("AC Steckdose Typ 2", station.plugs.get(1).name);
        assertEquals("AC Steckdose Typ 2", station.plugs.get(1).socket);
        assertEquals("kW", station.plugs.get(1).powerUnitCode);
        assertEquals("Kilowatt", station.plugs.get(1).powerUnitText);
        assertEquals(22.0, station.plugs.get(1).powerValue);
    }
    @Test
    void parseIncompleteJson() throws Exception {
        String jsonStr = StreamUtils.copyToString(new FileInputStream(examplePath + "/no_data.json"),
                Charset.defaultCharset());
        DZTClient.Station station = DZTParser.parseJsonToStation(jsonStr);
        assertEquals(null, station.id);
        assertEquals(null, station.name);
        assertEquals(null, station.latitude);
        assertEquals(null, station.longitude);
        assertEquals(null, station.addressCountry);
        assertEquals(null, station.addressLocality);
        assertEquals(null, station.addressPostalCode);
        assertEquals(null, station.addressStreet);
        assertEquals(null, station.publisher);
        assertEquals(null, station.publisherUrl);

        assertEquals(null, station.plugs.get(0).name);
        assertEquals(null, station.plugs.get(0).socket);
        assertEquals(null, station.plugs.get(0).powerUnitCode);
        assertEquals(null, station.plugs.get(0).powerUnitText);
        assertEquals(null, station.plugs.get(0).powerValue);

        assertEquals(null, station.plugs.get(1).name);
        assertEquals(null, station.plugs.get(1).socket);
        assertEquals(null, station.plugs.get(1).powerUnitCode);
        assertEquals(null, station.plugs.get(1).powerUnitText);
        assertEquals(null, station.plugs.get(1).powerValue);
    }
    @Test
    void parseSingleChargerJson() throws Exception {
        String jsonStr = StreamUtils.copyToString(new FileInputStream(examplePath + "/single_charger.json"),
                Charset.defaultCharset());
        DZTClient.Station station = DZTParser.parseJsonToStation(jsonStr);
        assertEquals(1, station.plugs.size());
        assertEquals("AC Steckdose Typ 2", station.plugs.get(0).name);
    }
}
