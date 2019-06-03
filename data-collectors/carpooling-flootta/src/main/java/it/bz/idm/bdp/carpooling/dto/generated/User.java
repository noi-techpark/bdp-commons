
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
    "id",
    "added",
    "user_name",
    "user_rating",
    "user_type",
    "user_gender",
    "user_pendular",
    "user_availability",
    "trip_from",
    "trip_to_id",
    "trip_to_name",
    "trip_arrival",
    "trip_departure",
    "trip_latitude",
    "trip_longitude"
})
public class User implements Serializable
{

    @JsonProperty("id")
    private Integer id;
    @JsonProperty("added")
    private String added;
    @JsonProperty("user_name")
    private String userName;
    @JsonProperty("user_rating")
    private String userRating;
    @JsonProperty("user_type")
    private String userType;
    @JsonProperty("user_gender")
    private String userGender;
    @JsonProperty("user_pendular")
    private String userPendular;
    @JsonProperty("user_availability")
    private String userAvailability;
    @JsonProperty("trip_from")
    private TripFrom tripFrom;
    @JsonProperty("trip_to_id")
    private Integer tripToId;
    @JsonProperty("trip_to_name")
    private String tripToName;
    @JsonProperty("trip_arrival")
    private String tripArrival;
    @JsonProperty("trip_departure")
    private String tripDeparture;
    @JsonProperty("trip_latitude")
    private Double tripLatitude;
    @JsonProperty("trip_longitude")
    private Double tripLongitude;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    private final static long serialVersionUID = 1119434612997166770L;

    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Integer id) {
        this.id = id;
    }

    @JsonProperty("added")
    public String getAdded() {
        return added;
    }

    @JsonProperty("added")
    public void setAdded(String added) {
        this.added = added;
    }

    @JsonProperty("user_name")
    public String getUserName() {
        return userName;
    }

    @JsonProperty("user_name")
    public void setUserName(String userName) {
        this.userName = userName;
    }

    @JsonProperty("user_rating")
    public String getUserRating() {
        return userRating;
    }

    @JsonProperty("user_rating")
    public void setUserRating(String userRating) {
        this.userRating = userRating;
    }

    @JsonProperty("user_type")
    public String getUserType() {
        return userType;
    }

    @JsonProperty("user_type")
    public void setUserType(String userType) {
        this.userType = userType;
    }

    @JsonProperty("user_gender")
    public String getUserGender() {
        return userGender;
    }

    @JsonProperty("user_gender")
    public void setUserGender(String userGender) {
        this.userGender = userGender;
    }

    @JsonProperty("user_pendular")
    public String getUserPendular() {
        return userPendular;
    }

    @JsonProperty("user_pendular")
    public void setUserPendular(String userPendular) {
        this.userPendular = userPendular;
    }

    @JsonProperty("user_availability")
    public String getUserAvailability() {
        return userAvailability;
    }

    @JsonProperty("user_availability")
    public void setUserAvailability(String userAvailability) {
        this.userAvailability = userAvailability;
    }

    @JsonProperty("trip_from")
    public TripFrom getTripFrom() {
        return tripFrom;
    }

    @JsonProperty("trip_from")
    public void setTripFrom(TripFrom tripFrom) {
        this.tripFrom = tripFrom;
    }

    @JsonProperty("trip_to_id")
    public Integer getTripToId() {
        return tripToId;
    }

    @JsonProperty("trip_to_id")
    public void setTripToId(Integer tripToId) {
        this.tripToId = tripToId;
    }

    @JsonProperty("trip_to_name")
    public String getTripToName() {
        return tripToName;
    }

    @JsonProperty("trip_to_name")
    public void setTripToName(String tripToName) {
        this.tripToName = tripToName;
    }

    @JsonProperty("trip_arrival")
    public String getTripArrival() {
        return tripArrival;
    }

    @JsonProperty("trip_arrival")
    public void setTripArrival(String tripArrival) {
        this.tripArrival = tripArrival;
    }

    @JsonProperty("trip_departure")
    public String getTripDeparture() {
        return tripDeparture;
    }

    @JsonProperty("trip_departure")
    public void setTripDeparture(String tripDeparture) {
        this.tripDeparture = tripDeparture;
    }

    @JsonProperty("trip_latitude")
    public Double getTripLatitude() {
        return tripLatitude;
    }

    @JsonProperty("trip_latitude")
    public void setTripLatitude(Double tripLatitude) {
        this.tripLatitude = tripLatitude;
    }

    @JsonProperty("trip_longitude")
    public Double getTripLongitude() {
        return tripLongitude;
    }

    @JsonProperty("trip_longitude")
    public void setTripLongitude(Double tripLongitude) {
        this.tripLongitude = tripLongitude;
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
        return new ToStringBuilder(this).append("id", id).append("added", added).append("userName", userName).append("userRating", userRating).append("userType", userType).append("userGender", userGender).append("userPendular", userPendular).append("userAvailability", userAvailability).append("tripFrom", tripFrom).append("tripToId", tripToId).append("tripToName", tripToName).append("tripArrival", tripArrival).append("tripDeparture", tripDeparture).append("tripLatitude", tripLatitude).append("tripLongitude", tripLongitude).append("additionalProperties", additionalProperties).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(tripLongitude).append(added).append(tripToName).append(tripDeparture).append(tripFrom).append(userAvailability).append(userName).append(userRating).append(tripArrival).append(tripToId).append(userGender).append(id).append(userType).append(additionalProperties).append(tripLatitude).append(userPendular).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof User) == false) {
            return false;
        }
        User rhs = ((User) other);
        return new EqualsBuilder().append(tripLongitude, rhs.tripLongitude).append(added, rhs.added).append(tripToName, rhs.tripToName).append(tripDeparture, rhs.tripDeparture).append(tripFrom, rhs.tripFrom).append(userAvailability, rhs.userAvailability).append(userName, rhs.userName).append(userRating, rhs.userRating).append(tripArrival, rhs.tripArrival).append(tripToId, rhs.tripToId).append(userGender, rhs.userGender).append(id, rhs.id).append(userType, rhs.userType).append(additionalProperties, rhs.additionalProperties).append(tripLatitude, rhs.tripLatitude).append(userPendular, rhs.userPendular).isEquals();
    }

}
