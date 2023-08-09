// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package com.opendatahub.bdp.commons.dc.bikeboxes.config;

/**
 * define datatype enums for ODH
 */
import it.bz.idm.bdp.dto.DataTypeDto;

public enum DataTypes {
    /** FREE, OCCUPIED, OUT OF SERVICE */
    usageState("usageState", true, "Usage state"),
    freeSpotsRegularBikes("freeSpotsRegularBike", true, "Free parking spots (regular bikes)"),
    freeSpotsElectricBikes("freeSpotsElectricBike", true, "Free parking spots (electric bikes)"),
    free("free", false, "Free parking spots");

    private DataTypes(String key, boolean sync, String description) {
        this.key = key;
        this.description = description;
        this.syncToOdh = sync;
    }

    public final boolean syncToOdh;
    public final String key;
    public final String unit = "count";
    public final String description;
    public final String rtype = "Instantaneous";

    public DataTypeDto toDataTypeDto() {
        return new DataTypeDto(key, unit, description, rtype);
    }
}
