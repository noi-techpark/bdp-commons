// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.idm.bdp.augeg4.face;

import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.StationList;

import java.util.List;

/**
 * Sends converted data to Open Data Hub.
 */
public interface DataPusherHubFace {
    void pushData();

    Object syncStations(StationList stationList);

    Object syncDataTypes(List<DataTypeDto> dataTypeList);

    <T> DataMapDto<RecordDtoImpl> mapData(T data);

    StationList getSyncedStations();

    String getStationType();

    String getOrigin();
}
