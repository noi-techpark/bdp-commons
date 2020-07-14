package it.bz.idm.bdp.augeg4.fun.convert;

import it.bz.idm.bdp.augeg4.dto.MeasurementId;
import org.junit.Test;

import java.util.Optional;

import static it.bz.idm.bdp.augeg4.fun.process.MeasurementProcessor.MEASUREMENT_ID_TEMPERATURA;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class MeasurementMappingsTest {

    @Test
    public void test_mapping_from_a_known_id() {
        // given
        MeasurementMappings mapper = new MeasurementMappings();

        // when
        Optional<MeasurementMapping> mappingContainer = mapper.getMapping(MEASUREMENT_ID_TEMPERATURA);

        // then
        assertTrue(mappingContainer.isPresent());

        MeasurementMapping mapping = mappingContainer.get();
        assertEquals("temperature-internal", mapping.getDataType());
    }

    @Test
    public void test_mapping_from_a_unknown_id() {
        // given
        MeasurementMappings mapper = new MeasurementMappings();

        // when
        Optional<MeasurementMapping> mappingContainer = mapper.getMapping(new MeasurementId(-123));

        // then
        assertFalse(mappingContainer.isPresent());
    }

}
