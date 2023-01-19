package it.fos.noibz.skyalps.dto.json.fares;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AeroCRSFares {

	private List<AeroCRSFare> fare;
    private int count;

    public AeroCRSFares() {
    }

    public List<AeroCRSFare> getFare() {
        return fare;
    }

    public void setFare(List<AeroCRSFare> fare) {
        this.fare = fare;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    
}
