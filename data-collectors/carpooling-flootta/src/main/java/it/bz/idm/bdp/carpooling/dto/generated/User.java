
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
        StringBuilder sb = new StringBuilder();
        sb.append(User.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("id");
        sb.append('=');
        sb.append(((this.id == null)?"<null>":this.id));
        sb.append(',');
        sb.append("added");
        sb.append('=');
        sb.append(((this.added == null)?"<null>":this.added));
        sb.append(',');
        sb.append("userName");
        sb.append('=');
        sb.append(((this.userName == null)?"<null>":this.userName));
        sb.append(',');
        sb.append("userRating");
        sb.append('=');
        sb.append(((this.userRating == null)?"<null>":this.userRating));
        sb.append(',');
        sb.append("userType");
        sb.append('=');
        sb.append(((this.userType == null)?"<null>":this.userType));
        sb.append(',');
        sb.append("userGender");
        sb.append('=');
        sb.append(((this.userGender == null)?"<null>":this.userGender));
        sb.append(',');
        sb.append("userPendular");
        sb.append('=');
        sb.append(((this.userPendular == null)?"<null>":this.userPendular));
        sb.append(',');
        sb.append("userAvailability");
        sb.append('=');
        sb.append(((this.userAvailability == null)?"<null>":this.userAvailability));
        sb.append(',');
        sb.append("tripFrom");
        sb.append('=');
        sb.append(((this.tripFrom == null)?"<null>":this.tripFrom));
        sb.append(',');
        sb.append("tripToId");
        sb.append('=');
        sb.append(((this.tripToId == null)?"<null>":this.tripToId));
        sb.append(',');
        sb.append("tripToName");
        sb.append('=');
        sb.append(((this.tripToName == null)?"<null>":this.tripToName));
        sb.append(',');
        sb.append("tripArrival");
        sb.append('=');
        sb.append(((this.tripArrival == null)?"<null>":this.tripArrival));
        sb.append(',');
        sb.append("tripDeparture");
        sb.append('=');
        sb.append(((this.tripDeparture == null)?"<null>":this.tripDeparture));
        sb.append(',');
        sb.append("tripLatitude");
        sb.append('=');
        sb.append(((this.tripLatitude == null)?"<null>":this.tripLatitude));
        sb.append(',');
        sb.append("tripLongitude");
        sb.append('=');
        sb.append(((this.tripLongitude == null)?"<null>":this.tripLongitude));
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
        result = ((result* 31)+((this.tripLongitude == null)? 0 :this.tripLongitude.hashCode()));
        result = ((result* 31)+((this.added == null)? 0 :this.added.hashCode()));
        result = ((result* 31)+((this.tripToName == null)? 0 :this.tripToName.hashCode()));
        result = ((result* 31)+((this.tripDeparture == null)? 0 :this.tripDeparture.hashCode()));
        result = ((result* 31)+((this.tripFrom == null)? 0 :this.tripFrom.hashCode()));
        result = ((result* 31)+((this.userAvailability == null)? 0 :this.userAvailability.hashCode()));
        result = ((result* 31)+((this.userName == null)? 0 :this.userName.hashCode()));
        result = ((result* 31)+((this.userRating == null)? 0 :this.userRating.hashCode()));
        result = ((result* 31)+((this.tripArrival == null)? 0 :this.tripArrival.hashCode()));
        result = ((result* 31)+((this.tripToId == null)? 0 :this.tripToId.hashCode()));
        result = ((result* 31)+((this.userGender == null)? 0 :this.userGender.hashCode()));
        result = ((result* 31)+((this.id == null)? 0 :this.id.hashCode()));
        result = ((result* 31)+((this.userType == null)? 0 :this.userType.hashCode()));
        result = ((result* 31)+((this.additionalProperties == null)? 0 :this.additionalProperties.hashCode()));
        result = ((result* 31)+((this.tripLatitude == null)? 0 :this.tripLatitude.hashCode()));
        result = ((result* 31)+((this.userPendular == null)? 0 :this.userPendular.hashCode()));
        return result;
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
        return (((((((((((((((((this.tripLongitude == rhs.tripLongitude)||((this.tripLongitude!= null)&&this.tripLongitude.equals(rhs.tripLongitude)))&&((this.added == rhs.added)||((this.added!= null)&&this.added.equals(rhs.added))))&&((this.tripToName == rhs.tripToName)||((this.tripToName!= null)&&this.tripToName.equals(rhs.tripToName))))&&((this.tripDeparture == rhs.tripDeparture)||((this.tripDeparture!= null)&&this.tripDeparture.equals(rhs.tripDeparture))))&&((this.tripFrom == rhs.tripFrom)||((this.tripFrom!= null)&&this.tripFrom.equals(rhs.tripFrom))))&&((this.userAvailability == rhs.userAvailability)||((this.userAvailability!= null)&&this.userAvailability.equals(rhs.userAvailability))))&&((this.userName == rhs.userName)||((this.userName!= null)&&this.userName.equals(rhs.userName))))&&((this.userRating == rhs.userRating)||((this.userRating!= null)&&this.userRating.equals(rhs.userRating))))&&((this.tripArrival == rhs.tripArrival)||((this.tripArrival!= null)&&this.tripArrival.equals(rhs.tripArrival))))&&((this.tripToId == rhs.tripToId)||((this.tripToId!= null)&&this.tripToId.equals(rhs.tripToId))))&&((this.userGender == rhs.userGender)||((this.userGender!= null)&&this.userGender.equals(rhs.userGender))))&&((this.id == rhs.id)||((this.id!= null)&&this.id.equals(rhs.id))))&&((this.userType == rhs.userType)||((this.userType!= null)&&this.userType.equals(rhs.userType))))&&((this.additionalProperties == rhs.additionalProperties)||((this.additionalProperties!= null)&&this.additionalProperties.equals(rhs.additionalProperties))))&&((this.tripLatitude == rhs.tripLatitude)||((this.tripLatitude!= null)&&this.tripLatitude.equals(rhs.tripLatitude))))&&((this.userPendular == rhs.userPendular)||((this.userPendular!= null)&&this.userPendular.equals(rhs.userPendular))));
    }

}
