// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.fos.noibz.skyalps.dto.json.fares;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AeroCRSFares {

    private JsonNode fare;
    private int count;

    public AeroCRSFares() {
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public JsonNode getFare() {
        return fare;
    }

    public void setFare(JsonNode fare) {
        this.fare = fare;
    }

    public List<AeroCRSFare> decodeFare() throws JsonMappingException, JsonProcessingException {
        List<AeroCRSFare> list = new ArrayList<>();
        
        if (count == 0)
            return list;

        ObjectMapper objectMapper = new ObjectMapper();
        Iterator<JsonNode> elements = fare.elements();

        while (elements.hasNext()) {
            JsonNode next = elements.next();

            AeroCRSFare convertValue = objectMapper.convertValue(next, AeroCRSFare.class);

            list.add(convertValue);
        }
        return list;
    }

}
