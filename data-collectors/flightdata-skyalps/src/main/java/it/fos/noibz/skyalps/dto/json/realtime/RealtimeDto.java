// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.fos.noibz.skyalps.dto.json.realtime;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonAlias;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RealtimeDto {

    @JsonAlias("DEP")
    private List<RealtimeDeparureDto> departures;
    @JsonAlias("ARR")
    private List<RealtimeArrivalDto> arrivals;

    public RealtimeDto() {

    }

    public List<RealtimeDeparureDto> getDepartures() {
        return departures;
    }

    public void setDepartures(List<RealtimeDeparureDto> departures) {
        this.departures = departures;
    }

    public List<RealtimeArrivalDto> getArrivals() {
        return arrivals;
    }

    public void setArrivals(List<RealtimeArrivalDto> arrivals) {
        this.arrivals = arrivals;
    }

}
