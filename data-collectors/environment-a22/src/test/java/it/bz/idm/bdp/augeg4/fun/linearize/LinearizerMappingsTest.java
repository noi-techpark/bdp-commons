package it.bz.idm.bdp.augeg4.fun.linearize;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LinearizerMappingsTest {

    @Test
    public void test_mapping_from_a_known_linearized_id () {
        // given
        LinearizerMappings mapper = new LinearizerMappings();

        // when
        int linearizedId = mapper.mapRawIdToLinearized(1);

        // then
        assertEquals(101, linearizedId);
    }
}
