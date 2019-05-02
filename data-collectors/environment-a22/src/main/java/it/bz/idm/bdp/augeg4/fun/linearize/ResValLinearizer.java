package it.bz.idm.bdp.augeg4.fun.linearize;

import it.bz.idm.bdp.augeg4.dto.fromauge.RawResVal;
import it.bz.idm.bdp.augeg4.dto.toauge.LinearResVal;

import java.util.HashMap;
import java.util.Map;

/**
 * Linearizes RawResVal to LinearResVal
 */
class ResValLinearizer {

    static final int LINEAR_FUNCTION_1_LINFUNCID = 1;
    static final int LINEAR_FUNCTION_2_LINFUNCID = 2;
    static final int LINEAR_FUNCTION_3_LINFUNCID = 3;
    static final int LINEAR_FUNCTION_4_LINFUNCID = 4;

    private final LinearizerMappings linearizerMappings = new LinearizerMappings();

    private interface LinearFunction {
        double apply(double a, double b, double x);
    }

    private final Map<Integer, LinearFunction> functionByLinFuncId = new HashMap<>();

    ResValLinearizer() {
        initFunctionByLinFuncIdMap();
    }

    private void initFunctionByLinFuncIdMap() {
        functionByLinFuncId.put(LINEAR_FUNCTION_1_LINFUNCID, (a, b, x) -> (a * x) + b);
        functionByLinFuncId.put(LINEAR_FUNCTION_2_LINFUNCID, (a, b, x) -> (a * Math.log(x)) + b);
        functionByLinFuncId.put(LINEAR_FUNCTION_3_LINFUNCID, (a, b, x) -> a * (Math.pow(x, b)));
        functionByLinFuncId.put(LINEAR_FUNCTION_4_LINFUNCID, (a, b, x) -> a * (Math.exp(x * b)));
        //  TODO: Update with the new functions
    }

    /**
     * Applies the linearization function and map the raw id to the linear id
     * @param rawResVal
     * @return
     */
    LinearResVal linearize(RawResVal rawResVal) {
        int dataTypeId = rawResVal.getId();
        int linearizedDataTypeId = linearizerMappings.mapRawIdToLinearized(dataTypeId);
        double linearizedValue = getLinearizedValue(rawResVal);
        return new LinearResVal(linearizedDataTypeId, linearizedValue);
    }

    private double getLinearizedValue(RawResVal rawResVal) {
        int linFuncId = rawResVal.getLinFuncId();
        double a = rawResVal.getParamA();
        double b = rawResVal.getParamB();
        double x = rawResVal.getValue();
        LinearFunction linearFunction = getLinearFunction(linFuncId);
        return linearFunction.apply(a, b, x);
    }

    private LinearFunction getLinearFunction(int linFuncId) {
        if (!functionByLinFuncId.containsKey(linFuncId)) {
            throw new IllegalArgumentException("Unknown linear function with id " + linFuncId);
        }
        return functionByLinFuncId.get(linFuncId);
    }
}
