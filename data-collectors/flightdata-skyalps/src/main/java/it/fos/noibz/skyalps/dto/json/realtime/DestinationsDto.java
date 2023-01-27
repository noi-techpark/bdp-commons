package it.fos.noibz.skyalps.dto.json.realtime;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DestinationsDto {

    @JsonAlias("dest")
    private String destination1;

    @JsonAlias("dest2")
    private String destination2;

    @JsonAlias("dest3")
    private String destination3;

    @JsonAlias("dest4")
    private String destination4;

    @JsonAlias("dest5")
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
