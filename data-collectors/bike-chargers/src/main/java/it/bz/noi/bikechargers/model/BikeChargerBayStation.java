package it.bz.noi.bikechargers.model;

public class BikeChargerBayStation {

    private String label;
    private Boolean charger;
    private String use;
    private String state;
    private String usageState;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Boolean getCharger() {
        return charger;
    }

    public void setCharger(Boolean charger) {
        this.charger = charger;
    }

    public String getUse() {
        return use;
    }

    public void setUse(String use) {
        this.use = use;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getUsageState() {
        return usageState;
    }

    public void setUsageState(String usageState) {
        this.usageState = usageState;
    }

    @Override
    public String toString() {
        return "BikeChargerBayStation{" +
                "label='" + label + '\'' +
                ", charger=" + charger +
                ", use='" + use + '\'' +
                ", state='" + state + '\'' +
                ", usageState='" + usageState + '\'' +
                '}';
    }
}
