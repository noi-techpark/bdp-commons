// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.idm.bdp.augeg4.mock;

import it.bz.idm.bdp.augeg4.face.DataPusherHubFace;
import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.StationList;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.List;

public class DataPusherHubMock implements DataPusherHubFace {

    private static final Logger LOG = LoggerFactory.getLogger(DataPusherHubMock.class.getName());

    private boolean pushed = false;

    @Override
    public void pushData() {
        LOG.info("pushDataToHub()");
        pushed=true;
    }

    @Override
    public Object syncStations(StationList stationList) {
        LOG.info("syncStations()");
        return null;
    }

    @Override
    public Object syncDataTypes(List<DataTypeDto> dataTypeList) {
        LOG.info("syncDataTypes():");
        for (DataTypeDto data: dataTypeList) {
            LOG.info(data.toString());
        }
        return null;
    }

    @Override
    public <T> DataMapDto<RecordDtoImpl> mapData(T data) {
        LOG.info("mapData()");
        return null;
    }

    @Override
    public StationList getSyncedStations() {
        return new StationList();
    }

    @Override
    public String getStationType() {
        return "Teststation";
    }

    @Override
    public String getOrigin() {
        return "test";
    }

    public boolean getPushed() {
        return pushed;
    }
}
