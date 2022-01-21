
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
    "name_de",
    "address_de",
    "city_de",
    "name_it",
    "address_it",
    "city_it",
    "cap",
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
    @JsonProperty("name_de")
    private String nameDe;
    @JsonProperty("address_de")
    private String addressDe;
    @JsonProperty("city_de")
    private String cityDe;
    @JsonProperty("name_it")
    private String nameIt;
    @JsonProperty("address_it")
    private String addressIt;
    @JsonProperty("city_it")
    private String cityIt;
    @JsonProperty("cap")
    private Integer cap;
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
    private final static long serialVersionUID = -4127907172219267243L;

    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Integer id) {
        this.id = id;
    }

    @JsonProperty("name_de")
    public String getNameDe() {
        return nameDe;
    }

    @JsonProperty("name_de")
    public void setNameDe(String nameDe) {
        this.nameDe = nameDe;
    }

    @JsonProperty("address_de")
    public String getAddressDe() {
        return addressDe;
    }

    @JsonProperty("address_de")
    public void setAddressDe(String addressDe) {
        this.addressDe = addressDe;
    }

    @JsonProperty("city_de")
    public String getCityDe() {
        return cityDe;
    }

    @JsonProperty("city_de")
    public void setCityDe(String cityDe) {
        this.cityDe = cityDe;
    }

    @JsonProperty("name_it")
    public String getNameIt() {
        return nameIt;
    }

    @JsonProperty("name_it")
    public void setNameIt(String nameIt) {
        this.nameIt = nameIt;
    }

    @JsonProperty("address_it")
    public String getAddressIt() {
        return addressIt;
    }

    @JsonProperty("address_it")
    public void setAddressIt(String addressIt) {
        this.addressIt = addressIt;
    }

    @JsonProperty("city_it")
    public String getCityIt() {
        return cityIt;
    }

    @JsonProperty("city_it")
    public void setCityIt(String cityIt) {
        this.cityIt = cityIt;
    }

    @JsonProperty("cap")
    public Integer getCap() {
        return cap;
    }

    @JsonProperty("cap")
    public void setCap(Integer cap) {
        this.cap = cap;
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
        StringBuilder sb = new StringBuilder();
        sb.append(Hub.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("id");
        sb.append('=');
        sb.append(((this.id == null)?"<null>":this.id));
        sb.append(',');
        sb.append("nameDe");
        sb.append('=');
        sb.append(((this.nameDe == null)?"<null>":this.nameDe));
        sb.append(',');
        sb.append("addressDe");
        sb.append('=');
        sb.append(((this.addressDe == null)?"<null>":this.addressDe));
        sb.append(',');
        sb.append("cityDe");
        sb.append('=');
        sb.append(((this.cityDe == null)?"<null>":this.cityDe));
        sb.append(',');
        sb.append("nameIt");
        sb.append('=');
        sb.append(((this.nameIt == null)?"<null>":this.nameIt));
        sb.append(',');
        sb.append("addressIt");
        sb.append('=');
        sb.append(((this.addressIt == null)?"<null>":this.addressIt));
        sb.append(',');
        sb.append("cityIt");
        sb.append('=');
        sb.append(((this.cityIt == null)?"<null>":this.cityIt));
        sb.append(',');
        sb.append("cap");
        sb.append('=');
        sb.append(((this.cap == null)?"<null>":this.cap));
        sb.append(',');
        sb.append("country");
        sb.append('=');
        sb.append(((this.country == null)?"<null>":this.country));
        sb.append(',');
        sb.append("latitude");
        sb.append('=');
        sb.append(((this.latitude == null)?"<null>":this.latitude));
        sb.append(',');
        sb.append("longitude");
        sb.append('=');
        sb.append(((this.longitude == null)?"<null>":this.longitude));
        sb.append(',');
        sb.append("isEvent");
        sb.append('=');
        sb.append(((this.isEvent == null)?"<null>":this.isEvent));
        sb.append(',');
        sb.append("availability");
        sb.append('=');
        sb.append(((this.availability == null)?"<null>":this.availability));
        sb.append(',');
        sb.append("users");
        sb.append('=');
        sb.append(((this.users == null)?"<null>":this.users));
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
        result = ((result* 31)+((this.cityDe == null)? 0 :this.cityDe.hashCode()));
        result = ((result* 31)+((this.country == null)? 0 :this.country.hashCode()));
        result = ((result* 31)+((this.latitude == null)? 0 :this.latitude.hashCode()));
        result = ((result* 31)+((this.nameIt == null)? 0 :this.nameIt.hashCode()));
        result = ((result* 31)+((this.availability == null)? 0 :this.availability.hashCode()));
        result = ((result* 31)+((this.users == null)? 0 :this.users.hashCode()));
        result = ((result* 31)+((this.nameDe == null)? 0 :this.nameDe.hashCode()));
        result = ((result* 31)+((this.cap == null)? 0 :this.cap.hashCode()));
        result = ((result* 31)+((this.addressDe == null)? 0 :this.addressDe.hashCode()));
        result = ((result* 31)+((this.id == null)? 0 :this.id.hashCode()));
        result = ((result* 31)+((this.cityIt == null)? 0 :this.cityIt.hashCode()));
        result = ((result* 31)+((this.additionalProperties == null)? 0 :this.additionalProperties.hashCode()));
        result = ((result* 31)+((this.addressIt == null)? 0 :this.addressIt.hashCode()));
        result = ((result* 31)+((this.longitude == null)? 0 :this.longitude.hashCode()));
        result = ((result* 31)+((this.isEvent == null)? 0 :this.isEvent.hashCode()));
        return result;
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
        return ((((((((((((((((this.cityDe == rhs.cityDe)||((this.cityDe!= null)&&this.cityDe.equals(rhs.cityDe)))&&((this.country == rhs.country)||((this.country!= null)&&this.country.equals(rhs.country))))&&((this.latitude == rhs.latitude)||((this.latitude!= null)&&this.latitude.equals(rhs.latitude))))&&((this.nameIt == rhs.nameIt)||((this.nameIt!= null)&&this.nameIt.equals(rhs.nameIt))))&&((this.availability == rhs.availability)||((this.availability!= null)&&this.availability.equals(rhs.availability))))&&((this.users == rhs.users)||((this.users!= null)&&this.users.equals(rhs.users))))&&((this.nameDe == rhs.nameDe)||((this.nameDe!= null)&&this.nameDe.equals(rhs.nameDe))))&&((this.cap == rhs.cap)||((this.cap!= null)&&this.cap.equals(rhs.cap))))&&((this.addressDe == rhs.addressDe)||((this.addressDe!= null)&&this.addressDe.equals(rhs.addressDe))))&&((this.id == rhs.id)||((this.id!= null)&&this.id.equals(rhs.id))))&&((this.cityIt == rhs.cityIt)||((this.cityIt!= null)&&this.cityIt.equals(rhs.cityIt))))&&((this.additionalProperties == rhs.additionalProperties)||((this.additionalProperties!= null)&&this.additionalProperties.equals(rhs.additionalProperties))))&&((this.addressIt == rhs.addressIt)||((this.addressIt!= null)&&this.addressIt.equals(rhs.addressIt))))&&((this.longitude == rhs.longitude)||((this.longitude!= null)&&this.longitude.equals(rhs.longitude))))&&((this.isEvent == rhs.isEvent)||((this.isEvent!= null)&&this.isEvent.equals(rhs.isEvent))));
    }

}
