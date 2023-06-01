// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

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
    "NAME_D",
    "NAME_I",
    "NAME_L",
    "NAME_E",
    "ALT",
    "LONG",
    "LAT"
})
public class Properties {

    @JsonProperty("SCODE")
    private String sCODE;
    @JsonProperty("NAME_D")
    private String nAMED;
    @JsonProperty("NAME_I")
    private String nAMEI;
    @JsonProperty("NAME_L")
    private String nAMEL;
    @JsonProperty("NAME_E")
    private String nAMEE;
    @JsonProperty("ALT")
    private Integer aLT;
    @JsonProperty("LONG")
    private Double lONG;
    @JsonProperty("LAT")
    private Double lAT;
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

    @JsonProperty("NAME_D")
    public String getNAMED() {
        return nAMED;
    }

    @JsonProperty("NAME_D")
    public void setNAMED(String nAMED) {
        this.nAMED = nAMED;
    }

    @JsonProperty("NAME_I")
    public String getNAMEI() {
        return nAMEI;
    }

    @JsonProperty("NAME_I")
    public void setNAMEI(String nAMEI) {
        this.nAMEI = nAMEI;
    }

    @JsonProperty("NAME_L")
    public String getNAMEL() {
        return nAMEL;
    }

    @JsonProperty("NAME_L")
    public void setNAMEL(String nAMEL) {
        this.nAMEL = nAMEL;
    }

    @JsonProperty("NAME_E")
    public String getNAMEE() {
        return nAMEE;
    }

    @JsonProperty("NAME_E")
    public void setNAMEE(String nAMEE) {
        this.nAMEE = nAMEE;
    }

    @JsonProperty("ALT")
    public Integer getALT() {
        return aLT;
    }

    @JsonProperty("ALT")
    public void setALT(Integer aLT) {
        this.aLT = aLT;
    }

    @JsonProperty("LONG")
    public Double getLONG() {
        return lONG;
    }

    @JsonProperty("LONG")
    public void setLONG(Double lONG) {
        this.lONG = lONG;
    }

    @JsonProperty("LAT")
    public Double getLAT() {
        return lAT;
    }

    @JsonProperty("LAT")
    public void setLAT(Double lAT) {
        this.lAT = lAT;
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
        return new ToStringBuilder(this).append("sCODE", sCODE).append("nAMED", nAMED).append("nAMEI", nAMEI).append("nAMEL", nAMEL).append("nAMEE", nAMEE).append("aLT", aLT).append("lONG", lONG).append("lAT", lAT).append("additionalProperties", additionalProperties).toString();
    }

}
