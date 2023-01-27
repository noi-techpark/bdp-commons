package it.fos.noibz.skyalps.dto.json.realtime;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RealtimeDeparureDto {

    @JsonProperty("F")
    String flightCode;

    @JsonProperty("EX")
    String expectedTime;

    @JsonProperty("SC")
    String scheduledTime;

    @JsonProperty("D")
    DestinationsDto destinations;

    @JsonProperty("A")
    String airlineCode;

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
    String statusCode;

    @JsonProperty("C")
    String checkinCode;

    @JsonProperty("G")
    String gateCode;

    @JsonProperty("GI")
    String gateInformationTime;

    @JsonProperty("T")
    String terminal;

    // = Flag of delay. 0 = no flag. 1 = early flight, 2 = delayed flight
    @JsonProperty("DC")
    String delayFlag;

    public RealtimeDeparureDto() {
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

    public String getCheckinCode() {
        return checkinCode;
    }

    public void setCheckinCode(String checkinCode) {
        this.checkinCode = checkinCode;
    }

    public String getGateCode() {
        return gateCode;
    }

    public void setGateCode(String gateCode) {
        this.gateCode = gateCode;
    }

    public String getGateInformationTime() {
        return gateInformationTime;
    }

    public void setGateInformationTime(String gateInformationTime) {
        this.gateInformationTime = gateInformationTime;
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
