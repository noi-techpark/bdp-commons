
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
    "name",
    "address",
    "cap",
    "city",
    "country",
    "latitude",
    "longitude",
    "is_event",
    "availability",
    "users"
})
public class Hub implements Serializable
{

    @JsonProperty("id")
    private Integer id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("address")
    private String address;
    @JsonProperty("cap")
    private Integer cap;
    @JsonProperty("city")
    private String city;
    @JsonProperty("country")
    private String country;
    @JsonProperty("latitude")
    private Double latitude;
    @JsonProperty("longitude")
    private Double longitude;
    @JsonProperty("is_event")
    private String isEvent;
    @JsonProperty("availability")
    private String availability;
    @JsonProperty("users")
    private Integer users;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    private final static long serialVersionUID = 8600691433377306948L;

    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Integer id) {
        this.id = id;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("address")
    public String getAddress() {
        return address;
    }

    @JsonProperty("address")
    public void setAddress(String address) {
        this.address = address;
    }

    @JsonProperty("cap")
    public Integer getCap() {
        return cap;
    }

    @JsonProperty("cap")
    public void setCap(Integer cap) {
        this.cap = cap;
    }

    @JsonProperty("city")
    public String getCity() {
        return city;
    }

    @JsonProperty("city")
    public void setCity(String city) {
        this.city = city;
    }

    @JsonProperty("country")
    public String getCountry() {
        return country;
    }

    @JsonProperty("country")
    public void setCountry(String country) {
        this.country = country;
    }

    @JsonProperty("latitude")
    public Double getLatitude() {
        return latitude;
    }

    @JsonProperty("latitude")
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    @JsonProperty("longitude")
    public Double getLongitude() {
        return longitude;
    }

    @JsonProperty("longitude")
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    @JsonProperty("is_event")
    public String getIsEvent() {
        return isEvent;
    }

    @JsonProperty("is_event")
    public void setIsEvent(String isEvent) {
        this.isEvent = isEvent;
    }

    @JsonProperty("availability")
    public String getAvailability() {
        return availability;
    }

    @JsonProperty("availability")
    public void setAvailability(String availability) {
        this.availability = availability;
    }

    @JsonProperty("users")
    public Integer getUsers() {
        return users;
    }

    @JsonProperty("users")
    public void setUsers(Integer users) {
        this.users = users;
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
        return new ToStringBuilder(this).append("id", id).append("name", name).append("address", address).append("cap", cap).append("city", city).append("country", country).append("latitude", latitude).append("longitude", longitude).append("isEvent", isEvent).append("availability", availability).append("users", users).append("additionalProperties", additionalProperties).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(country).append(address).append(city).append(latitude).append(availability).append(users).append(cap).append(name).append(id).append(additionalProperties).append(longitude).append(isEvent).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Hub) == false) {
            return false;
        }
        Hub rhs = ((Hub) other);
        return new EqualsBuilder().append(country, rhs.country).append(address, rhs.address).append(city, rhs.city).append(latitude, rhs.latitude).append(availability, rhs.availability).append(users, rhs.users).append(cap, rhs.cap).append(name, rhs.name).append(id, rhs.id).append(additionalProperties, rhs.additionalProperties).append(longitude, rhs.longitude).append(isEvent, rhs.isEvent).isEquals();
    }

}
