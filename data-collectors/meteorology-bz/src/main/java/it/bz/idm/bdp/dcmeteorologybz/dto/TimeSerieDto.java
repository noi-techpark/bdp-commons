package it.bz.idm.bdp.dcmeteorologybz.dto;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "DATE",
    "VALUE"
})
public class TimeSerieDto {

    @JsonProperty("DATE")
    private String dATE;
    @JsonProperty("VALUE")
    private Double vALUE;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("DATE")
    public String getDATE() {
        return dATE;
    }

    @JsonProperty("DATE")
    public void setDATE(String dATE) {
        this.dATE = dATE;
    }

    @JsonProperty("VALUE")
    public Double getVALUE() {
        return vALUE;
    }

    @JsonProperty("VALUE")
    public void setVALUE(Double vALUE) {
        this.vALUE = vALUE;
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
        return new ToStringBuilder(this).append("dATE", dATE).append("vALUE", vALUE).append("additionalProperties", additionalProperties).toString();
    }

}
