package it.bz.idm.bdp.augeg4.fun.convert;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ConverterMappingsTest {

    @Test
    public void test_mapping_from_a_known_linearized_id () {
        // given
        ConverterMappings mapper = new ConverterMappings();

        // when
        String dataType = mapper.mapLinearizedIdToDataType(101);

        // then
        assertEquals("temperature", dataType);
    }

}
