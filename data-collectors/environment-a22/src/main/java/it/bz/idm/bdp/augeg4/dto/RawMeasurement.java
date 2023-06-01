// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.idm.bdp.augeg4.dto;

public class RawMeasurement {
   private final MeasurementId id;
   private final double value;

    public RawMeasurement(MeasurementId id, double value) {
        this.id = id;
        this.value = value;
    }

    public MeasurementId getId() {
        return id;
    }

    public double getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "RawMeasurement{" +
                "id=" + id +
                ", value=" + value +
                '}';
    }
}
