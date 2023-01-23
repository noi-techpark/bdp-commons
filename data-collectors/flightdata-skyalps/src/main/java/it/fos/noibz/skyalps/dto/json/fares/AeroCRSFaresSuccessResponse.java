package it.fos.noibz.skyalps.dto.json.fares;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AeroCRSFaresSuccessResponse {

    private AeroCRSFaresSuccess aerocrs;

    public AeroCRSFaresSuccessResponse() {
    }

    public AeroCRSFaresSuccess getAerocrs() {
        return aerocrs;
    }

    public void setAerocrs(AeroCRSFaresSuccess aerocrs) {
        this.aerocrs = aerocrs;
    }

}
