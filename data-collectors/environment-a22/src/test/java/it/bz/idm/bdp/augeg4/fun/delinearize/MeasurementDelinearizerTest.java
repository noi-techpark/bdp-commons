package it.bz.idm.bdp.augeg4.fun.delinearize;

import it.bz.idm.bdp.augeg4.dto.RawMeasurement;
import it.bz.idm.bdp.augeg4.dto.fromauge.ElaboratedResVal;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;

public class MeasurementDelinearizerTest {

    private static final double ASSERT_EQUALS_DOUBLE_DELTA = 0.001;

    private final MeasurementDelinearizer delinearizer = new MeasurementDelinearizer();

    /**
     * Linear function 1 = (a * x) + b
     */
    @Test
    public void test_linear_function_1() {
        // given
        double a = 1;
        double b = 2;
        double x = 3;
        double y = (a * x) + b;
        ElaboratedResVal elaboratedResVal = new ElaboratedResVal(1, y, MeasurementDelinearizer.LINEAR_FUNCTION_1_LINFUNCID, a, b);

        // when
        Optional<RawMeasurement> delinearizedResVal = delinearizer.delinearize(elaboratedResVal);

        // then
        assertTrue(delinearizedResVal.isPresent());
        //assertEquals(x, delinearizedResVal.get().getValue(), ASSERT_EQUALS_DOUBLE_DELTA);
    }

    @Test
    public void delinearize_value_not_linearized() {
        // given
        double y = 123;
        ElaboratedResVal elaboratedResVal = new ElaboratedResVal(1, y);

        // when
        Optional<RawMeasurement> delinearizedResVal = delinearizer.delinearize(elaboratedResVal);

        // then
        assertTrue(delinearizedResVal.isPresent());
        assertEquals(y, delinearizedResVal.get().getValue(), ASSERT_EQUALS_DOUBLE_DELTA);
    }

    @Test
    public void test_unknown_linear_function() {
        // given
        double a = 1;
        double b = 3;
        double y = 2;
        ElaboratedResVal elaboratedResVal = new ElaboratedResVal(1, y, -123, a, b);

        // when
        Optional<RawMeasurement> delinearizedResVal = delinearizer.delinearize(elaboratedResVal);

        // then
        assertFalse(delinearizedResVal.isPresent());
    }
}
