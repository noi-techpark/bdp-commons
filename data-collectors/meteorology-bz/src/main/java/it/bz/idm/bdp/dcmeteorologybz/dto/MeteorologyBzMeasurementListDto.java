package it.bz.idm.bdp.dcmeteorologybz.dto;

import java.util.ArrayList;
import java.util.List;

public class MeteorologyBzMeasurementListDto {

    private String name;
    private List<MeteorologyBzMeasurementDto> measurements;

    public MeteorologyBzMeasurementListDto(String name) {
        this.name = name;
        this.measurements = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<MeteorologyBzMeasurementDto> getMeasurements() {
        return measurements;
    }

    public void setMeasurements(List<MeteorologyBzMeasurementDto> measurements) {
        this.measurements = measurements;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName()+" [name=" + name + ", measurements=" + measurements.size() + "]";
    }


}
