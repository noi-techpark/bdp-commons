// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.idm.bdp.augeg4.dto.tohub;

import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;

public class StationIdTest {

    @Test
    public void StationId_from_value_with_prefix() {
        // given
        String controlUnitId = "Station1";
        String prefix = "AUGEG4_";
        String value = prefix + controlUnitId;

        // when
        Optional<StationId> stationIdContainer = StationId.fromValue(value, prefix);

        // then
        assertTrue(stationIdContainer.isPresent());

        StationId stationId = stationIdContainer.get();
        assertEquals(prefix, stationId.getPrefix());
        assertEquals(controlUnitId, stationId.getControlUnitId());
        assertEquals(value, stationId.getValue());
        assertEquals(new StationId(prefix, controlUnitId), stationId);
    }

    @Test
    public void StationId_from_value_with_missing_prefix() {
        // given
        String controlUnitId = "Station1";
        String prefix = "AUGEG4_";

        // when
        Optional<StationId> stationIdContainer = StationId.fromValue(controlUnitId, prefix);

        // then
        assertFalse(stationIdContainer.isPresent());
    }
}
