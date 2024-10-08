// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.idm.bdp.augeg4.fun.convert.tohub;

import org.junit.Test;

import java.util.Optional;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class StationMappingsTest {

    @Test
    public void test_mapping_from_a_known_control_unit_id() {
        // given
        StationMappings mapper = new StationMappings();

        // when
        Optional<StationMapping> mappingContainer = mapper.getMapping("AIRQ01");

        // then
        assertTrue(mappingContainer.isPresent());

        StationMapping mapping = mappingContainer.get();
        assertEquals(mapping.getName(), "Stazione_KM177-600");
    }

    @Test
    public void test_mapping_from_a_unknown_id() {
        // given
        StationMappings mapper = new StationMappings();

        // when
        Optional<StationMapping> mappingContainer = mapper.getMapping("abc");

        // then
        assertFalse(mappingContainer.isPresent());
    }
}
