package it.bz.idm.bdp.dcbikesharingbz.dto;

import java.io.Serializable;

public class BikesharingBzBayDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String  label              ;
    private String  state              ;
    private Boolean charger            ;
    private String  use                ;
    private String  usageState         ;
    private Boolean vehiclePresent     ;
    private String  vehicleBatteryState;
    private String  vehicleCode        ;
    private String  vehicleName        ;
    private Boolean vehicleElectric    ;

    private BikesharingBzStationDto parentStation;

    public BikesharingBzBayDto() {
    }

    public BikesharingBzStationDto getParentStation() {
        return parentStation;
    }

    public void setParentStation(BikesharingBzStationDto parentStation) {
        this.parentStation = parentStation;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
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

    public String getUsageState() {
        return usageState;
    }

    public void setUsageState(String usageState) {
        this.usageState = usageState;
    }

    public Boolean getVehiclePresent() {
        return vehiclePresent;
    }

    public void setVehiclePresent(Boolean vehiclePresent) {
        this.vehiclePresent = vehiclePresent;
    }

    public String getVehicleBatteryState() {
        return vehicleBatteryState;
    }

    public void setVehicleBatteryState(String vehicleBatteryState) {
        this.vehicleBatteryState = vehicleBatteryState;
    }

    public String getVehicleCode() {
        return vehicleCode;
    }

    public void setVehicleCode(String vehicleCode) {
        this.vehicleCode = vehicleCode;
    }

    public String getVehicleName() {
        return vehicleName;
    }

    public void setVehicleName(String vehicleName) {
        this.vehicleName = vehicleName;
    }

    public Boolean getVehicleElectric() {
        return vehicleElectric;
    }

    public void setVehicleElectric(Boolean vehicleElectric) {
        this.vehicleElectric = vehicleElectric;
    }

    @Override
    public String toString() {
        return "BikesharingBzBayDto [label=" + label + ", state=" + state + ", charger=" + charger + ", use=" + use + ", usageState=" + usageState + ", vehiclePresent=" + vehiclePresent
                + ", vehicleBatteryState=" + vehicleBatteryState + ", vehicleCode=" + vehicleCode + ", vehicleName=" + vehicleName + ", vehicleElectric="
                + vehicleElectric + "]";
    }

}
