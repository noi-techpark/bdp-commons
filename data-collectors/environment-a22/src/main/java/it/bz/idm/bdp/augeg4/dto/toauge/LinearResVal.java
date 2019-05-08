package it.bz.idm.bdp.augeg4.dto.toauge;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class LinearResVal {

    private final int id;

    @JsonIgnore
    private final double rawValue;

    @JsonProperty("value")
    private final double linearizedValue;

    public LinearResVal(int id, double rawValue, double linearizedValue) {
        this.id = id;
        this.rawValue = rawValue;
        this.linearizedValue = linearizedValue;
    }

    public int getId() {
        return id;
    }

    public double getRawValue() {
        return rawValue;
    }

    public double getLinearizedValue() {
        return linearizedValue;
    }

    @Override
    public String toString() {
        return "LinearResVal{" +
                "id=" + id +
                ", rawValue=" + rawValue +
                ", linearizedValue=" + linearizedValue +
                '}';
    }
}
