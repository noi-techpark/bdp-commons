// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package com.opendatahub.bdp.commons.dc.meteorology.bz.forecast.config;

/**
 * define datatype enums for ODH
 */
import it.bz.idm.bdp.dto.DataTypeDto;

public enum DataTypes {
    airTemperatureMax("air-temperature-max", "Max air temperature", true, "Celcius"),
    airTemperatureMin("air-temperature-min", "Min air temperature", true, "Celcius"),
    precipitationMax("precipitation-max", "Max precipitation", false, "mm"),
    precipitationMin("precipitation-min", "Min precipitation", false, "mm");

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
