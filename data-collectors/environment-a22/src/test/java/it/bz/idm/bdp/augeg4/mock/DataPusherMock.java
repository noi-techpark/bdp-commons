package it.bz.idm.bdp.augeg4.mock;

import it.bz.idm.bdp.augeg4.face.DataPusherFace;
import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.StationList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class DataPusherMock implements DataPusherFace {

    private static final Logger LOG = LogManager.getLogger(DataPusherMock.class.getName());

    private boolean pushed = false;

    @Override
    public void pushData() {
        LOG.info("pushData()");
        pushed=true;
    }

    @Override
    public Object syncStations(StationList stationList) {
        LOG.info("syncStations()");
        return null;
    }

    @Override
    public Object syncDataTypes(List<DataTypeDto> dataTypeList) {
        LOG.info("syncDataTypes()");
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
