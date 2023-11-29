// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package com.opendatahub.bdp.commons.dc.meteorology.bz.forecast.dto;

public class LocationDto {
    public LocationDto(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public double lat;
    public double lon;
}
