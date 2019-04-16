package it.bz.idm.bdp.augeg4.mock;

import it.bz.idm.bdp.augeg4.face.DataRetrieverFace;
import it.bz.idm.bdp.augeg4.dto.fromauge.AugeG4FromAlgorabDataDto;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class DataRetrieverMock implements DataRetrieverFace {

    private static final Logger LOG = LogManager.getLogger(DataRetrieverMock.class.getName());

    private boolean retrieved = true;

    @Override
    public List<AugeG4FromAlgorabDataDto> fetchData() throws Exception {
        LOG.info("fetchData()");
        System.out.println(this);
        this.retrieved = true;
        return mockedResult();
    }

    private List<AugeG4FromAlgorabDataDto> mockedResult() {
        List<AugeG4FromAlgorabDataDto> result = new ArrayList<AugeG4FromAlgorabDataDto>();
        return result;
    }

    public boolean getRetrieved() {
        System.out.println(this);
        return this.retrieved;
    }
}

