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
    usageState("usageState", "usageState"),
    availableMuscularBikes("availableMuscularBikes", "availableMuscularBikes"),
    availableAssistedBikes("availableAssistedBikes", "availableAssistedBikes"),
    availableVehicles("availableVehicles", "availableVehicles");

    private DataTypes(String key, String description) {
        this.key = key;
        this.description = description;
    }

    public final String key;
    public final String unit = "";
    public final String description;
    public final String rtype = "Instantaneous";

    public DataTypeDto toDataTypeDto() {
        return new DataTypeDto(key, unit, description, rtype);
    }
}
