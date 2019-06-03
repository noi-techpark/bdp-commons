package it.bz.idm.bdp.augeg4.fun.process;

import org.junit.Test;

import java.util.Optional;

import static it.bz.idm.bdp.augeg4.fun.process.MeasurementProcessor.MEASUREMENT_ID_O3;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class MeasurementProcessorParametersTest {

    @Test
    public void get_parameters_of_existing_station_and_res_val_id () {
        // given
        String controlUnitId = "AIRQ01";
        MeasurementProcessorParameters processorParameters = new MeasurementProcessorParameters();

        // when
        Optional<MeasurementParameters> parameters = processorParameters.getMeasurementParameters(controlUnitId, MEASUREMENT_ID_O3);

        // expect
        assertTrue(parameters.isPresent());
        assertEquals(1.0, parameters.get().getA());
    }

}
