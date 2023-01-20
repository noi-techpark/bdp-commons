package it.fos.noibz.skyalps.dto.json.fares;

public class ODHFare {

    private double minimumPriceRoundTrip;
    private double minimumPriceOneWay;

    private AeroCRSFare fare;

    public ODHFare(AeroCRSFare fare) {
        if (fare.getAdultFareOW() != null)
            this.minimumPriceRoundTrip = fare.getAdultFareOW();
        if (fare.getAdultFareRT() != null)
            this.minimumPriceOneWay = fare.getAdultFareRT();
        this.fare = fare;
    }

    public double getMinimumPriceRoundTrip() {
        return minimumPriceRoundTrip;
    }

    public void setMinimumPriceRoundTrip(double minimumPriceRoundTrip) {
        this.minimumPriceRoundTrip = minimumPriceRoundTrip;
    }

    public double getMinimumPriceOneWay() {
        return minimumPriceOneWay;
    }

    public void setMinimumPriceOneWay(double minimumPriceOneWay) {
        this.minimumPriceOneWay = minimumPriceOneWay;
    }

    public AeroCRSFare getFare() {
        return fare;
    }

    public void setFare(AeroCRSFare fare) {
        this.fare = fare;
    }

}
