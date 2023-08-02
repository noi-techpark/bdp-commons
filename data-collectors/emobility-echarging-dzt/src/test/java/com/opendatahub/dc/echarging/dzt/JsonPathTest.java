package com.opendatahub.dc.echarging.dzt;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.FileInputStream;
import java.nio.charset.Charset;

import org.junit.jupiter.api.Test;
import org.springframework.util.StreamUtils;

import com.opendatahub.dc.echarging.dzt.DZTClient.Station;

public class JsonPathTest {
    /**
     * Validates how the json is parsed. Mainly to verify all the jsonPaths
     * @throws Exception
     */
    @Test
    void validateJsonPaths() throws Exception {
        String jsonStr = StreamUtils.copyToString(new FileInputStream("src/test/resources/station.json"),
                Charset.defaultCharset());
        Station station = DZTClient.parseJsonToStation(jsonStr);
        assertEquals("64891004", station.id);
        assertEquals("Stadtwerke Hattingen GmbH - 64891004 - Normalladeeinrichtung", station.name);
        assertEquals(5.1397E1, station.latitude);
        assertEquals(7.164E0, station.longitude);
        assertEquals("Deutschland", station.addressCountry);
        assertEquals("Hattingen", station.addressLocality);
        assertEquals("45525", station.addressPostalCode);
        assertEquals("Weg zum Wasserwerk 23", station.addressStreet);
    }
}
