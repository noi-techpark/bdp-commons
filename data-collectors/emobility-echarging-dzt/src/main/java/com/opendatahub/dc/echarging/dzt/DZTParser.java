// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package com.opendatahub.dc.echarging.dzt;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;

public class DZTParser {
    public static DZTClient.Station parseJsonToStation(String json) throws Exception {
        DZTClient.Station s = new DZTClient.Station();

        // Data quality is hit or miss, we just ignore missing stuff an validate later
        Configuration conf = Configuration.defaultConfiguration()
            .addOptions(Option.DEFAULT_PATH_LEAF_TO_NULL, Option.SUPPRESS_EXCEPTIONS);
        var jp = JsonPath.using(conf).parse(json);
        
        s.id = jp.read("$.[0]['https://schema.org/identifier']['https://schema.org/value']['@value']");
        s.name = jp.read("$.[0]['https://schema.org/name']");
        s.latitude = jp.read("$.[0]['https://schema.org/geo']['https://schema.org/latitude']['@value']", Double.class);
        s.longitude = jp.read("$.[0]['https://schema.org/geo']['https://schema.org/longitude']['@value']", Double.class);
        s.addressCountry = jp.read("$.[0]['https://schema.org/address']['https://schema.org/addressCountry']");
        s.addressLocality = jp.read("$.[0]['https://schema.org/address']['https://schema.org/addressLocality']");
        s.addressPostalCode = jp.read("$.[0]['https://schema.org/address']['https://schema.org/postalCode']");
        s.addressStreet = jp.read("$.[0]['https://schema.org/address']['https://schema.org/streetAddress']");
        s.publisher = jp.read("$.[0]['https://schema.org/sdPublisher']['https://schema.org/name']");
        s.publisherUrl = jp.read("$.[0]['https://schema.org/sdPublisher']['https://schema.org/url']['@value']");

        var dztPlugs = jp.read("$.[0]['https://odta.io/voc/hasCharger']");

        // plugs may be empty, a single object or a list
        if (!(dztPlugs instanceof List)) {
            var ar = new ArrayList<>();
            if (dztPlugs != null) {
                ar.add(dztPlugs);
            }
            dztPlugs = ar;
        }

        for (var dztPlug : (List<?>) dztPlugs) {
            var jpp = JsonPath.using(conf).parse(dztPlug);
            DZTClient.Plug plug = new DZTClient.Plug();
            plug.name = jpp.read("$.['https://schema.org/name']");
            plug.socket = jpp.read("$.['https://odta.io/voc/socket']");
            plug.powerUnitCode = jpp.read("$.['https://odta.io/voc/power']['https://schema.org/unitCode']");
            plug.powerUnitText = jpp.read("$.['https://odta.io/voc/power']['https://schema.org/unitText']");
            plug.powerValue = jpp.read("$.['https://odta.io/voc/power']['https://schema.org/value']['@value']", Double.class);
            s.plugs.add(plug);
        }

        return s;
    }

    private static final Map<String, String> sockets = Map.of(
        "AC Steckdose Typ 2", "Type2Mennekes"
    );

    public String mapSocketToOdh(String socket){
        return sockets.containsKey(socket) ? sockets.get(socket) : socket;
    }
}
