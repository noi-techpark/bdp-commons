// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.idm.bdp.augeg4.fun.convert.tohub;

public class StationMapping {

    private final String controlUnitId;
    private final String name;
    private final Double latitude, longitude;

    public StationMapping(String controlUnitId, String name, Double latitude, Double longitude) {
        this.controlUnitId = controlUnitId;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getControlUnitId() {
        return controlUnitId;
    }

    public String getName() {
        return name;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    @Override
    public String toString() {
        return "StationMapping{" +
                "controlUnitId='" + controlUnitId + '\'' +
                ", name='" + name + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
