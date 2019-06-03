package it.bz.idm.bdp.augeg4.dto.fromauge;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Objects;

/**
 * Contains the value elaborated by Auge. That it, the value is the result of function linFuncId to the
 * PreElaborated value (we don't have the PreElaborated, only Auge has it).
 */
public class ElaboratedResVal {

    private Integer id;
    private Double value;
    private Integer linFuncId;
    private Double paramA;
    private Double paramB;

    public ElaboratedResVal() {}

    public ElaboratedResVal(Integer id, Double value) {
        this.id = id;
        this.value = value;
        this.linFuncId = null;
        this.paramA = null;
        this.paramB = null;
    }

    public ElaboratedResVal(Integer id, Double value, Integer linFuncId, Double paramA, Double paramB) {
        this.id = id;
        this.value = value;
        this.linFuncId = linFuncId;
        this.paramA = paramA;
        this.paramB = paramB;
    }

    public Integer getId() {
        return id;
    }

    public Double getValue() {
        return value;
    }

    public Integer getLinFuncId() {
        return linFuncId;
    }

    public Double getParamA() {
        return paramA;
    }

    public Double getParamB() {
        return paramB;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public void setLinFuncId(Integer linFuncId) {
        this.linFuncId = linFuncId;
    }

    public void setParamA(Double paramA) {
        this.paramA = paramA;
    }

    public void setParamB(Double paramB) {
        this.paramB = paramB;
    }

    @JsonIgnore
    public boolean isLinearized () {
        return this.getLinFuncId() != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ElaboratedResVal elaboratedResVal = (ElaboratedResVal) o;
        return id == elaboratedResVal.id &&
                Double.compare(elaboratedResVal.value, value) == 0 &&
                Objects.equals(linFuncId, elaboratedResVal.linFuncId) &&
                Objects.equals(paramA, elaboratedResVal.paramA) &&
                Objects.equals(paramB, elaboratedResVal.paramB);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, value, linFuncId, paramA, paramB);
    }

    @Override
    public String toString() {
        return "ElaboratedResVal{" +
                "id=" + id +
                ", value=" + value +
                ", linFuncId=" + linFuncId +
                ", paramA=" + paramA +
                ", paramB=" + paramB +
                '}';
    }
}
