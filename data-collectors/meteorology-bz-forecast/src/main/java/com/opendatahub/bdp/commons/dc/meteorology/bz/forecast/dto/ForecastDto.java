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

    public List<Municipality> municipalities;

    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Municipality {
        public String code;
        public String nameDe;
        public String nameIt;
        public String nameEn;
        public String nameRm;

        public ForecastDataSet tempMin24;
        public ForecastDataSet tempMax24;
        public ForecastDataSet temp3;
        public ForecastDataSet ssd24;
        public ForecastDataSet precProb3;
        public ForecastDataSet precProb24;
        public ForecastDataSet precSum3;
        public ForecastDataSet precSum24;
        // public ForecastDataSet symbols3;
        // public ForecastDataSet symbols24;
        public ForecastDataSet windDir3;
        public ForecastDataSet windSpd3;
    }

    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ForecastDataSet {
        public String nameDe;
        public String nameIt;
        public String nameEn;
        public String nameRm;
        public String unit;

        public List<ForecastData> data;
    }

    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ForecastData {
        public String date;
        public Double value;
    }
}
