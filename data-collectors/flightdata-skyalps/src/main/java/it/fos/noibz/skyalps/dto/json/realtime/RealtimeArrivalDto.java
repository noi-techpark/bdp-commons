package it.fos.noibz.skyalps.dto.json.realtime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RealtimeArrivalDto {

    @JsonProperty("F")
    private String flightCode;

    @JsonProperty("EX")
    private String expectedTime;

    @JsonProperty("SC")
    private String scheduledTime;

    @JsonProperty("D")
    private DestinationsDto destinations;

    @JsonProperty("A")
    private String airlineCode;

    // Status Codes:
    // B = Boarding
    // U = Last Call
    // Z = Boarding Closed
    // C = Check-In Opened
    // D = Departed
    // L = Landed
    // G = Gate Number
    // Y = Diverted
    // X = Cancelled
    // R = Baggage claim
    // K = Check-In Closed
    @JsonProperty("S")
    private String statusCode;

    @JsonProperty("B")
    private String beltCode;

    @JsonProperty("T")
    private String terminal;

    // = Flag of delay. 0 = no flag. 1 = early flight, 2 = delayed flight
    @JsonProperty("DC")
    private String delayFlag;

    public RealtimeArrivalDto() {
    }

    public String getFlightCode() {
        return flightCode;
    }

    public void setFlightCode(String flightCode) {
        this.flightCode = flightCode;
    }

    public String getExpectedTime() {
        return expectedTime;
    }

    public void setExpectedTime(String expectedTime) {
        this.expectedTime = expectedTime;
    }

    public String getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(String scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public String getAirlineCode() {
        return airlineCode;
    }

    public void setAirlineCode(String airlineCode) {
        this.airlineCode = airlineCode;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getBeltCode() {
        return beltCode;
    }

    public void setBeltCode(String beltCode) {
        this.beltCode = beltCode;
    }

    public String getTerminal() {
        return terminal;
    }

    public void setTerminal(String terminal) {
        this.terminal = terminal;
    }

    public String getDelayFlag() {
        return delayFlag;
    }

    public void setDelayFlag(String delayFlag) {
        this.delayFlag = delayFlag;
    }

    public DestinationsDto getDestinations() {
        return destinations;
    }

    public void setDestinations(DestinationsDto destinations) {
        this.destinations = destinations;
    }

}
