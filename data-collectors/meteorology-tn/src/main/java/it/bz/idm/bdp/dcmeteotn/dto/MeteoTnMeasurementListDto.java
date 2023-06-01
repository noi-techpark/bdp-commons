// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.idm.bdp.dcmeteotn.dto;

import java.util.ArrayList;
import java.util.List;

public class MeteoTnMeasurementListDto {

    private String name;
    private List<MeteoTnMeasurementDto> measurements;

    public MeteoTnMeasurementListDto(String name) {
        this.name = name;
        this.measurements = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<MeteoTnMeasurementDto> getMeasurements() {
        return measurements;
    }

    public void setMeasurements(List<MeteoTnMeasurementDto> measurements) {
        this.measurements = measurements;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName()+" [name=" + name + ", measurements=" + measurements.size() + "]";
    }


}
