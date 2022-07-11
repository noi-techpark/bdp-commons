package it.bz.odh.dcmeteoeurac.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ClimatologyDto {
    
    private int month;
    
    private Double tmin;
    
    private Double tmax;

    private Double tmean;
    
    private Double prec;
    
    private int id;
    
    private String station;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public Double getTmin() {
        return tmin;
    }

    public void setTmin(Double tmin) {
        this.tmin = tmin;
    }

    public Double getTmax() {
        return tmax;
    }

    public void setTmax(Double tmax) {
        this.tmax = tmax;
    }

    public Double getTmean() {
        return tmean;
    }

    public void setTmean(Double tmean) {
        this.tmean = tmean;
    }

    public Double getPrec() {
        return prec;
    }

    public void setPrec(Double prec) {
        this.prec = prec;
    }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }
}
