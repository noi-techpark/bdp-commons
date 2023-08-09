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
    usageState("usageState", "Usage state", true, "state"),
    freeSpotsRegularBikes("freeSpotsRegularBike", "Free parking spots (regular bikes)", true, "count"),
    freeSpotsElectricBikes("freeSpotsElectricBike", "Free parking spots (electric bikes)", true, "count"),
    free("free", "Free parking spots", false, "count");

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
    public final String rtype = "Instantaneous";

    public DataTypeDto toDataTypeDto() {
        return new DataTypeDto(key, unit, description, rtype);
    }
}
