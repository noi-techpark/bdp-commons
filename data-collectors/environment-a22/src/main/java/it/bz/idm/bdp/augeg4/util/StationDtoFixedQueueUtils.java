// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.idm.bdp.augeg4.util;

import it.bz.idm.bdp.augeg4.dto.tohub.StationId;
import it.bz.idm.bdp.dto.StationDto;

public class StationDtoFixedQueueUtils {

    public static boolean isStationWithIdAlreadyQueued(FixedQueue<StationDto> queuedStations, StationId id){
        for (StationDto station : queuedStations) {
            if (station.getId().equals(id.getValue())) {
                return true;
            }
        }
        return false;
    }

    public static StationDto getQueuedStationById(FixedQueue<StationDto> queuedStations, StationId id) {
        for (StationDto station : queuedStations) {
            if (station.getId().equals(id.getValue())) {
                return station;
            }
        }
        return null;
    }

}
