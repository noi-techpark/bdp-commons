package com.opendatahub.bdp.commons.dc.meteorology.bz.forecast.dto;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.opendatahub.bdp.commons.dc.meteorology.bz.forecast.dto.ForecastDto.Municipality;

import lombok.ToString;

@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize
public class ForecastDto {

    public Info info;
    public List<Municipality> municipalities;

    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Info {
        public String model;
        public String currentModelRun;
        public String nextModelRun;
        public String fileName;
        public String fileCreationDate;

        public int absTempMin;
        public int absTempMax;
        public int absPrecMin;
        public int absPrecMax;
    }

    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Municipality {
        public String code;
        public String nameDe;
        public String nameIt;
        public String nameEn;
        public String nameRm;

        public ForecastDoubleSet tempMin24;
        public ForecastDoubleSet tempMax24;
        public ForecastDoubleSet temp3;
        public ForecastDoubleSet ssd24;
        public ForecastDoubleSet precProb3;
        public ForecastDoubleSet precProb24;
        public ForecastDoubleSet precSum3;
        public ForecastDoubleSet precSum24;
        public ForecastStringSet symbols3;
        public ForecastStringSet symbols24;
        public ForecastDoubleSet windDir3;
        public ForecastDoubleSet windSpd3;
    }

    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ForecastDoubleSet {
        public String nameDe;
        public String nameIt;
        public String nameEn;
        public String nameRm;
        public String unit;

        public List<ForecastDouble> data;
    }

    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ForecastStringSet {
        public String nameDe;
        public String nameIt;
        public String nameEn;
        public String nameRm;
        public String unit;

        public List<ForecastString> data;
    }

    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ForecastDouble {
        public String date;
        public Double value;
    }

    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ForecastString {
        public String date;
        public String value;
    }
}
