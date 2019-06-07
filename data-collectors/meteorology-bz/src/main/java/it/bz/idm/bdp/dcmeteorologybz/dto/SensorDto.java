
package it.bz.idm.bdp.dcmeteorologybz.dto;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "SCODE",
    "TYPE",
    "DESC_D",
    "DESC_I",
    "DESC_L",
    "UNIT",
    "DATE",
    "VALUE"
})
public class SensorDto {

    @JsonProperty("SCODE")
    private String sCODE;
    @JsonProperty("TYPE")
    private String tYPE;
    @JsonProperty("DESC_D")
    private String dESCD;
    @JsonProperty("DESC_I")
    private String dESCI;
    @JsonProperty("DESC_L")
    private String dESCL;
    @JsonProperty("UNIT")
    private String uNIT;
    @JsonProperty("DATE")
    private String dATE;
    @JsonProperty("VALUE")
    private Double vALUE;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("SCODE")
    public String getSCODE() {
        return sCODE;
    }

    @JsonProperty("SCODE")
    public void setSCODE(String sCODE) {
        this.sCODE = sCODE;
    }

    @JsonProperty("TYPE")
    public String getTYPE() {
        return tYPE;
    }

    @JsonProperty("TYPE")
    public void setTYPE(String tYPE) {
        this.tYPE = tYPE;
    }

    @JsonProperty("DESC_D")
    public String getDESCD() {
        return dESCD;
    }

    @JsonProperty("DESC_D")
    public void setDESCD(String dESCD) {
        this.dESCD = dESCD;
    }

    @JsonProperty("DESC_I")
    public String getDESCI() {
        return dESCI;
    }

    @JsonProperty("DESC_I")
    public void setDESCI(String dESCI) {
        this.dESCI = dESCI;
    }

    @JsonProperty("DESC_L")
    public String getDESCL() {
        return dESCL;
    }

    @JsonProperty("DESC_L")
    public void setDESCL(String dESCL) {
        this.dESCL = dESCL;
    }

    @JsonProperty("UNIT")
    public String getUNIT() {
        return uNIT;
    }

    @JsonProperty("UNIT")
    public void setUNIT(String uNIT) {
        this.uNIT = uNIT;
    }

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
        return new ToStringBuilder(this).append("sCODE", sCODE).append("tYPE", tYPE).append("dESCD", dESCD).append("dESCI", dESCI).append("dESCL", dESCL).append("uNIT", uNIT).append("dATE", dATE).append("vALUE", vALUE).append("additionalProperties", additionalProperties).toString();
    }

}
