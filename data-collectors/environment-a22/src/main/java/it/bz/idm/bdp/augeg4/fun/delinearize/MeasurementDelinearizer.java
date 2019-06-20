package it.bz.idm.bdp.augeg4.fun.delinearize;

import it.bz.idm.bdp.augeg4.dto.MeasurementId;
import it.bz.idm.bdp.augeg4.dto.RawMeasurement;
import it.bz.idm.bdp.augeg4.dto.fromauge.ElaboratedResVal;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MeasurementDelinearizer {
    private static final Logger LOG = LogManager.getLogger(MeasurementDelinearizer.class.getName());

    static final int LINEAR_FUNCTION_1_LINFUNCID = 1;
    static final int LINEAR_FUNCTION_2_LINFUNCID = 2;
    static final int LINEAR_FUNCTION_3_LINFUNCID = 3;
    static final int LINEAR_FUNCTION_4_LINFUNCID = 4;

    private interface LinearFunction {
        double apply(double a, double b, double x);
    }

    private final Map<Integer, LinearFunction> inverseFunctionByLinFuncId = new HashMap<>();

    MeasurementDelinearizer() {
        initFunctionByLinFuncIdMap();
    }

    private void initFunctionByLinFuncIdMap() {
        inverseFunctionByLinFuncId.put(LINEAR_FUNCTION_1_LINFUNCID, (a, b, y) -> (y - b) / a); // y = (a * x) + b
        inverseFunctionByLinFuncId.put(LINEAR_FUNCTION_2_LINFUNCID, (a, b, y) -> Math.exp((y - b) / a)); // (a * ln(x)) + b
        inverseFunctionByLinFuncId.put(LINEAR_FUNCTION_3_LINFUNCID, (a, b, y) -> Math.log10(y / a) / Math.log10(b)); // a * (x^b)
        inverseFunctionByLinFuncId.put(LINEAR_FUNCTION_4_LINFUNCID, (a, b, y) -> Math.log(y / a) / b); // a * (e^(x * b))
    }

    Optional<RawMeasurement> delinearize(ElaboratedResVal elaboratedResVal) {
        if (!elaboratedResVal.isLinearized()) {
            return delinearizeNotLinearizedResVal(elaboratedResVal);
        }
        return delinearizeLinearizedResVal(elaboratedResVal);
    }

    private Optional<RawMeasurement> delinearizeNotLinearizedResVal(ElaboratedResVal elaboratedResVal) {
        return Optional.of(new RawMeasurement(
                getMeasurementId(elaboratedResVal),
                elaboratedResVal.getValue()
        ));
    }

    private Optional<RawMeasurement> delinearizeLinearizedResVal(ElaboratedResVal elaboratedResVal) {
        return getLinearFunctionOrNull(elaboratedResVal.getLinFuncId())
                .map(linearFunction -> new RawMeasurement(
                        getMeasurementId(elaboratedResVal),
                        delinearizeValue(linearFunction, elaboratedResVal)
                ));
    }

    private MeasurementId getMeasurementId(ElaboratedResVal elaboratedResVal) {
        return new MeasurementId(elaboratedResVal.getId());
    }


    private double delinearizeValue(LinearFunction linearFunction, ElaboratedResVal elaboratedResVal) {
        double a = elaboratedResVal.getParamA();
        double b = elaboratedResVal.getParamB();
        double y = elaboratedResVal.getValue();
        return linearFunction.apply(a, b, y);
    }

    private Optional<LinearFunction> getLinearFunctionOrNull(int linFuncId) {
        LinearFunction function = inverseFunctionByLinFuncId.get(linFuncId);
        if (function == null) {
            LOG.warn("getLinearFunctionOrNull() called with an unknown linear function id: {}", linFuncId);
        }
        return Optional.ofNullable(function);
    }
}
