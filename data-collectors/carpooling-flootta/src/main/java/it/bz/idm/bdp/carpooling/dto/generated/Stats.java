
package it.bz.idm.bdp.carpooling.dto.generated;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "registered_users",
    "confirmed_trips",
    "trips_last_30",
    "drivers",
    "passengers",
    "distance",
    "co2"
})
public class Stats implements Serializable
{

    @JsonProperty("registered_users")
    private Integer registeredUsers;
    @JsonProperty("confirmed_trips")
    private Integer confirmedTrips;
    @JsonProperty("trips_last_30")
    private Integer tripsLast30;
    @JsonProperty("drivers")
    private Integer drivers;
    @JsonProperty("passengers")
    private Integer passengers;
    @JsonProperty("distance")
    private Double distance;
    @JsonProperty("co2")
    private Double co2;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    private final static long serialVersionUID = -5488574664419190561L;

    @JsonProperty("registered_users")
    public Integer getRegisteredUsers() {
        return registeredUsers;
    }

    @JsonProperty("registered_users")
    public void setRegisteredUsers(Integer registeredUsers) {
        this.registeredUsers = registeredUsers;
    }

    @JsonProperty("confirmed_trips")
    public Integer getConfirmedTrips() {
        return confirmedTrips;
    }

    @JsonProperty("confirmed_trips")
    public void setConfirmedTrips(Integer confirmedTrips) {
        this.confirmedTrips = confirmedTrips;
    }

    @JsonProperty("trips_last_30")
    public Integer getTripsLast30() {
        return tripsLast30;
    }

    @JsonProperty("trips_last_30")
    public void setTripsLast30(Integer tripsLast30) {
        this.tripsLast30 = tripsLast30;
    }

    @JsonProperty("drivers")
    public Integer getDrivers() {
        return drivers;
    }

    @JsonProperty("drivers")
    public void setDrivers(Integer drivers) {
        this.drivers = drivers;
    }

    @JsonProperty("passengers")
    public Integer getPassengers() {
        return passengers;
    }

    @JsonProperty("passengers")
    public void setPassengers(Integer passengers) {
        this.passengers = passengers;
    }

    @JsonProperty("distance")
    public Double getDistance() {
        return distance;
    }

    @JsonProperty("distance")
    public void setDistance(Double distance) {
        this.distance = distance;
    }

    @JsonProperty("co2")
    public Double getCo2() {
        return co2;
    }

    @JsonProperty("co2")
    public void setCo2(Double co2) {
        this.co2 = co2;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Stats.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("registeredUsers");
        sb.append('=');
        sb.append(((this.registeredUsers == null)?"<null>":this.registeredUsers));
        sb.append(',');
        sb.append("confirmedTrips");
        sb.append('=');
        sb.append(((this.confirmedTrips == null)?"<null>":this.confirmedTrips));
        sb.append(',');
        sb.append("tripsLast30");
        sb.append('=');
        sb.append(((this.tripsLast30 == null)?"<null>":this.tripsLast30));
        sb.append(',');
        sb.append("drivers");
        sb.append('=');
        sb.append(((this.drivers == null)?"<null>":this.drivers));
        sb.append(',');
        sb.append("passengers");
        sb.append('=');
        sb.append(((this.passengers == null)?"<null>":this.passengers));
        sb.append(',');
        sb.append("distance");
        sb.append('=');
        sb.append(((this.distance == null)?"<null>":this.distance));
        sb.append(',');
        sb.append("co2");
        sb.append('=');
        sb.append(((this.co2 == null)?"<null>":this.co2));
        sb.append(',');
        sb.append("additionalProperties");
        sb.append('=');
        sb.append(((this.additionalProperties == null)?"<null>":this.additionalProperties));
        sb.append(',');
        if (sb.charAt((sb.length()- 1)) == ',') {
            sb.setCharAt((sb.length()- 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = ((result* 31)+((this.passengers == null)? 0 :this.passengers.hashCode()));
        result = ((result* 31)+((this.distance == null)? 0 :this.distance.hashCode()));
        result = ((result* 31)+((this.tripsLast30 == null)? 0 :this.tripsLast30 .hashCode()));
        result = ((result* 31)+((this.registeredUsers == null)? 0 :this.registeredUsers.hashCode()));
        result = ((result* 31)+((this.co2 == null)? 0 :this.co2 .hashCode()));
        result = ((result* 31)+((this.additionalProperties == null)? 0 :this.additionalProperties.hashCode()));
        result = ((result* 31)+((this.drivers == null)? 0 :this.drivers.hashCode()));
        result = ((result* 31)+((this.confirmedTrips == null)? 0 :this.confirmedTrips.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Stats) == false) {
            return false;
        }
        Stats rhs = ((Stats) other);
        return (((((((((this.passengers == rhs.passengers)||((this.passengers!= null)&&this.passengers.equals(rhs.passengers)))&&((this.distance == rhs.distance)||((this.distance!= null)&&this.distance.equals(rhs.distance))))&&((this.tripsLast30 == rhs.tripsLast30)||((this.tripsLast30 != null)&&this.tripsLast30 .equals(rhs.tripsLast30))))&&((this.registeredUsers == rhs.registeredUsers)||((this.registeredUsers!= null)&&this.registeredUsers.equals(rhs.registeredUsers))))&&((this.co2 == rhs.co2)||((this.co2 != null)&&this.co2 .equals(rhs.co2))))&&((this.additionalProperties == rhs.additionalProperties)||((this.additionalProperties!= null)&&this.additionalProperties.equals(rhs.additionalProperties))))&&((this.drivers == rhs.drivers)||((this.drivers!= null)&&this.drivers.equals(rhs.drivers))))&&((this.confirmedTrips == rhs.confirmedTrips)||((this.confirmedTrips!= null)&&this.confirmedTrips.equals(rhs.confirmedTrips))));
    }

}
