
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
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

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
        return new ToStringBuilder(this).append("registeredUsers", registeredUsers).append("confirmedTrips", confirmedTrips).append("tripsLast30", tripsLast30).append("drivers", drivers).append("passengers", passengers).append("distance", distance).append("co2", co2).append("additionalProperties", additionalProperties).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(passengers).append(distance).append(tripsLast30).append(registeredUsers).append(co2).append(additionalProperties).append(drivers).append(confirmedTrips).toHashCode();
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
        return new EqualsBuilder().append(passengers, rhs.passengers).append(distance, rhs.distance).append(tripsLast30, rhs.tripsLast30).append(registeredUsers, rhs.registeredUsers).append(co2, rhs.co2).append(additionalProperties, rhs.additionalProperties).append(drivers, rhs.drivers).append(confirmedTrips, rhs.confirmedTrips).isEquals();
    }

}
