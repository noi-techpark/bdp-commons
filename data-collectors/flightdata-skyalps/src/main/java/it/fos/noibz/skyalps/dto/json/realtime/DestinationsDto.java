package it.fos.noibz.skyalps.dto.json.realtime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DestinationsDto {

    @JsonProperty("dest")
    private String destination1;

    @JsonProperty("dest2")
    private String destination2;

    @JsonProperty("dest3")
    private String destination3;

    @JsonProperty("dest4")
    private String destination4;

    @JsonProperty("dest5")
    private String destination5;

    public DestinationsDto() {
    }

    public String getDestination1() {
        return destination1;
    }

    public void setDestination1(String destination1) {
        this.destination1 = destination1;
    }

    public String getDestination2() {
        return destination2;
    }

    public void setDestination2(String destination2) {
        this.destination2 = destination2;
    }

    public String getDestination3() {
        return destination3;
    }

    public void setDestination3(String destination3) {
        this.destination3 = destination3;
    }

    public String getDestination4() {
        return destination4;
    }

    public void setDestination4(String destination4) {
        this.destination4 = destination4;
    }

    public String getDestination5() {
        return destination5;
    }

    public void setDestination5(String destination5) {
        this.destination5 = destination5;
    }

}
