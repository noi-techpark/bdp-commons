package it.bz.idm.bdp.augeg4.fun.linearize;

import it.bz.idm.bdp.augeg4.dto.fromauge.RawResVal;
import it.bz.idm.bdp.augeg4.dto.toauge.LinearResVal;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ResValLinearizerTest {

    private static final double ASSERT_EQUALS_DOUBLE_DELTA = 0.001;

    private final ResValLinearizer linearizer = new ResValLinearizer();

    /**
     * Linear function 1 = (a * x) + b
     */
    @Test
    public void test_linear_function_1 () {
        // given
        double a = 1;
        double b = 3;
        double x = 2;
        RawResVal rawResVal = new RawResVal(1, x, ResValLinearizer.LINEAR_FUNCTION_1_LINFUNCID, a, b);

        // when
        LinearResVal linearResVal = linearizer.linearize(rawResVal);

        // then
        double expectedLinearizedValue = (a * x) + b;
        assertEquals(expectedLinearizedValue, linearResVal.getValue(), ASSERT_EQUALS_DOUBLE_DELTA);
        // TODO: Should we also test the mapping between the ids?
    }

    // TODO: Test other linear functions
}
