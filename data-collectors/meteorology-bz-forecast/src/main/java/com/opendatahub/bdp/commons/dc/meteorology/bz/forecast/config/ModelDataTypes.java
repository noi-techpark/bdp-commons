// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package com.opendatahub.bdp.commons.dc.meteorology.bz.forecast.config;

/**
 * define datatype enums for ODH
 */
import it.bz.idm.bdp.dto.DataTypeDto;

public enum ModelDataTypes {
    airTemperatureMax("forecast-air-temperature-max", "Forecast of max air temperature", true, "Celcius"),
    airTemperatureMin("forecast-air-temperature-min", "Forecast of min air temperature", true, "Celcius"),
    precipitationMax("forecast-precipitation-max", "Forecast of max precipitation", true, "mm"),
    precipitationMin("forecast-precipitation-min", "Forecast of min precipitation", true, "mm");

    private ModelDataTypes(String key, String description, boolean sync, String unit) {
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
