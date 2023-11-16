package com.opendatahub.bdp.commons.dc.meteorology.bz.forecast.dto;

public class LocationDto {
    public LocationDto(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public double lat;
    public double lon;
}
