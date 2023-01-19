package it.fos.noibz.skyalps.dto.json.fares;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AeroCRSFaresSuccess {

    private boolean success;
    private AeroCRSFares fares;

    public AeroCRSFaresSuccess() {
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public AeroCRSFares getFares() {
        return fares;
    }

    public void setFares(AeroCRSFares fares) {
        this.fares = fares;
    }

}
