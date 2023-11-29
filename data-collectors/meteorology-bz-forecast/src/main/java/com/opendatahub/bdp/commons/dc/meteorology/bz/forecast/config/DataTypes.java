// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package com.opendatahub.bdp.commons.dc.meteorology.bz.forecast.config;

/**
 * define datatype enums for ODH
 */
import it.bz.idm.bdp.dto.DataTypeDto;

public enum DataTypes {
    airTemperatureMax("forecast-air-temperature-max", "Forecast of max air temperature during a day", true, "Celcius"),
    airTemperatureMin("forecast-air-temperature-min", "Forecast of min air temperature during a day", true, "Celcius"),
    airTemperature("forecast-air-temperature", "Forecast of air temperature at a specific timestamp", true, "Celcius"),
    windDirection("forecast-wind-direction", "Forecast of wind direction", true, "\\u00b0"),
    windSpeed("forecast-wind-speed", "Forecast of wind speed", true, "m/s"),
    sunshineDuration("forecast-sunshine-duration", "Forecast of sun shine duration", true, "h"),
    precipitationProbability("forecast-precipitation-probability", "Forecast of precipitation probability", true, "%"),
    qualitativeForecast("qualitative-forecast", "Forecast of overall weather condition. Example: sunny", true, ""),
    precipitationSum("forecast-precipitation-sum", "Forecast of cumulated precipitation", true, "mm");

    private DataTypes(String key, String description, boolean sync, String unit) {
        this.key = key;
        this.description = description;
        this.syncToOdh = sync;
        this.unit = unit;
    }

    public final boolean syncToOdh;
    public final String key;
    public final String unit;
    public final String description;
    public final String rtype = "Forecast";

    public DataTypeDto toDataTypeDto() {
        return new DataTypeDto(key, unit, description, rtype);
    }
}
