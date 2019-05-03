package it.bz.idm.bdp.augeg4.fun.retrieve;

import it.bz.idm.bdp.augeg4.dto.fromauge.AugeG4FromAlgorabDataDto;
import it.bz.idm.bdp.augeg4.dto.fromauge.RawResVal;
import it.bz.idm.bdp.augeg4.face.DataRetrieverFace;
import it.bz.idm.bdp.augeg4.face.DataServiceFace;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Date;

@Component
public class DataRetrieverMock implements DataRetrieverFace {

    private DataServiceFace dataServiceFace;

    public void setDataService(DataServiceFace dataServiceFace) {
        this.dataServiceFace = dataServiceFace;
    }

    public void mockDataRetrievedFromAlgorab() {
        AugeG4FromAlgorabDataDto dto = mockDtoFromAlgorab();
        dataServiceFace.addDataFromAlgorab(Collections.singletonList(dto));
    }

    private AugeG4FromAlgorabDataDto mockDtoFromAlgorab () {
        AugeG4FromAlgorabDataDto dto = new AugeG4FromAlgorabDataDto();
        dto.setDateTimeAcquisition(new Date());
        dto.setControlUnitId("mock");
        dto.setResVal(Collections.singletonList(
                new RawResVal(1, 10, 1, 1, 1)
        ));
        return dto;
    }


}
