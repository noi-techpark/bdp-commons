package it.bz.idm.bdp.augeg4.dto.toauge;

public class LinearResVal {

    private final int id;

    private final double value;

    public LinearResVal(int id, double value) {
        this.id = id;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public double getValue() {
        return value;
    }
}
