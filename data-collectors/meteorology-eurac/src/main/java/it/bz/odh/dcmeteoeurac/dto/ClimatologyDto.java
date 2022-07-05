package it.bz.odh.dcmeteoeurac.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ClimatologyDto {
    
    private int month;
    
    private double tmin;
    
    private double tmax;

    private double tmean;
    
    private double prec;
    
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

    public double getTmin() {
        return tmin;
    }

    public void setTmin(double tmin) {
        this.tmin = tmin;
    }

    public double getTmax() {
        return tmax;
    }

    public void setTmax(double tmax) {
        this.tmax = tmax;
    }

    public double getTmean() {
        return tmean;
    }

    public void setTmean(double tmean) {
        this.tmean = tmean;
    }

    public double getPrec() {
        return prec;
    }

    public void setPrec(double prec) {
        this.prec = prec;
    }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }
}
