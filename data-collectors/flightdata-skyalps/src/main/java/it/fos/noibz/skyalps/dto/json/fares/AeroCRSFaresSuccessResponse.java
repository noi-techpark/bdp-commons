package it.fos.noibz.skyalps.dto.json.fares;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AeroCRSFaresSuccessResponse {

    private AeroCRSFaresSuccess fares;

    public AeroCRSFaresSuccessResponse() {
    }

    public AeroCRSFaresSuccessResponse(AeroCRSFaresSuccess fares) {
        this.fares = fares;
    }

    public AeroCRSFaresSuccess getFares() {
        return fares;
    }

    public void setFares(AeroCRSFaresSuccess fares) {
        this.fares = fares;
    }

}
