package it.fos.noibz.skyalps.dto.json.realtime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAlias;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RealtimeDeparureDto {

    @JsonProperty(value = "F", access = JsonProperty.Access.WRITE_ONLY)
    private String flightCode;

    @JsonAlias("EX")
    private String expectedTime;

    @JsonAlias("SC")
    private String scheduledTime;

    @JsonAlias("D")
    private DestinationsDto destinations;

    @JsonAlias("A")
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
    @JsonAlias("S")
    private String statusCode;

    @JsonAlias("C")
    private String checkinCode;

    @JsonAlias("G")
    private String gateCode;

    @JsonAlias("GI")
    private String gateInformationTime;

    @JsonAlias("T")
    private String terminal;

    // = Flag of delay. 0 = no flag. 1 = early flight, 2 = delayed flight
    @JsonAlias("DC")
    private String delayFlag;

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
