package it.bz.idm.bdp.dcmeteorologybz;

public class ServiceCallParam {

    public static final String TYPE_FIXED_VALUE   = "fixed";
    public static final String TYPE_STATION_VALUE = "station";
    public static final String TYPE_SENSOR_VALUE  = "sensor";
    public static final String TYPE_FUNCTION      = "funct";

    public static final String FUNCTION_NAME_LAST_DATE = "LAST_DATE";
    public static final String FUNCTION_NAME_CURR_DATE = "CURR_DATE";

    public String name;
    public String type;
    public String value;

    public ServiceCallParam(String name) {
        super();
        this.name = name;
    }

}
