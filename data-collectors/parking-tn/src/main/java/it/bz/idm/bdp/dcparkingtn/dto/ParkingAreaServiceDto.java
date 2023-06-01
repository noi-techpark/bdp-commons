// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.idm.bdp.dcparkingtn.dto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "name",
    "description",
    "slotsTotal",
    "slotsAvailable",
    "position",
    "monitored",
    "extra"
})
public class ParkingAreaServiceDto {

    @JsonProperty("name")
    private String name;
    @JsonProperty("description")
    private String description;
    @JsonProperty("slotsTotal")
    private Integer slotsTotal;
    @JsonProperty("slotsAvailable")
    private Integer slotsAvailable;
    @JsonProperty("position")
    private List<Double> position = null;
    @JsonProperty("monitored")
    private Boolean monitored;
    @JsonProperty("extra")
    private ExtraServiceDto extra;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("slotsTotal")
    public Integer getSlotsTotal() {
        return slotsTotal;
    }

    @JsonProperty("slotsTotal")
    public void setSlotsTotal(Integer slotsTotal) {
        this.slotsTotal = slotsTotal;
    }

    @JsonProperty("slotsAvailable")
    public Integer getSlotsAvailable() {
        return slotsAvailable;
    }

    @JsonProperty("slotsAvailable")
    public void setSlotsAvailable(Integer slotsAvailable) {
        this.slotsAvailable = slotsAvailable;
    }

    @JsonProperty("position")
    public List<Double> getPosition() {
        return position;
    }

    @JsonProperty("position")
    public void setPosition(List<Double> position) {
        this.position = position;
    }

    @JsonProperty("monitored")
    public Boolean getMonitored() {
        return monitored;
    }

    @JsonProperty("monitored")
    public void setMonitored(Boolean monitored) {
        this.monitored = monitored;
    }

    @JsonProperty("extra")
    public ExtraServiceDto getExtra() {
        return extra;
    }

    @JsonProperty("extra")
    public void setExtra(ExtraServiceDto extra) {
        this.extra = extra;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
