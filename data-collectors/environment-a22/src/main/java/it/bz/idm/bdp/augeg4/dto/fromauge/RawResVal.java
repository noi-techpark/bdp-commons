package it.bz.idm.bdp.augeg4.dto.fromauge;

public class RawResVal {

    private final int id;
    private final double value;
    private final int linFuncId;
    private final double paramA;
    private final double paramB;

    public RawResVal(int id, double value, int linFuncId, double paramA, double paramB) {
        this.id = id;
        this.value = value;
        this.linFuncId = linFuncId;
        this.paramA = paramA;
        this.paramB = paramB;
    }

    public int getId() {
        return id;
    }

    public double getValue() {
        return value;
    }

    public int getLinFuncId() {
        return linFuncId;
    }

    public double getParamA() {
        return paramA;
    }

    public double getParamB() {
        return paramB;
    }
}
