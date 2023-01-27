package it.fos.noibz.skyalps.dto.json.realtime;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RealtimeDto {

    @JsonProperty("DEP")
    List<RealtimeDeparureDto> departures;
    @JsonProperty("ARR")
    List<RealtimeArrivalDto> arrivals;

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
