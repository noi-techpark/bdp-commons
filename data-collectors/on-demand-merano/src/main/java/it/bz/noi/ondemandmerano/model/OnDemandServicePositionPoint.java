package it.bz.noi.ondemandmerano.model;

import java.util.List;

public class OnDemandServicePositionPoint {

    private String type;
    private List<Double> coordinates;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Double> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<Double> coordinates) {
        this.coordinates = coordinates;
    }

    public Double getLongitude() {
        if(coordinates != null && coordinates.size() == 2)
            return coordinates.get(0);
        return null;
    }

    public Double getLatitude() {
        if(coordinates != null && coordinates.size() == 2)
            return coordinates.get(1);
        return null;
    }

    @Override
    public String toString() {
        return "OnDemandServicePositionPoint{" +
                "type='" + type + '\'' +
                ", coordinates=" + coordinates +
                '}';
    }
}
