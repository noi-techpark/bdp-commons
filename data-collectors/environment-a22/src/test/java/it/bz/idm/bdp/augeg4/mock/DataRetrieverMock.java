// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.idm.bdp.augeg4.mock;

import it.bz.idm.bdp.augeg4.dto.fromauge.AugeG4ElaboratedDataDto;
import it.bz.idm.bdp.augeg4.dto.fromauge.ElaboratedResVal;
import it.bz.idm.bdp.augeg4.face.DataRetrieverFace;
import it.bz.idm.bdp.augeg4.util.FixedQueue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static it.bz.idm.bdp.augeg4.fun.process.MeasurementProcessor.MEASUREMENT_ID_O3;
import static it.bz.idm.bdp.augeg4.fun.process.MeasurementProcessor.MEASUREMENT_ID_TEMPERATURA;

//@Component
public class DataRetrieverMock implements DataRetrieverFace {

    FixedQueue<AugeG4ElaboratedDataDto> buffer = new FixedQueue<>(100);

    @Override
    public List<AugeG4ElaboratedDataDto> fetchData() {
        AugeG4ElaboratedDataDto dto = mockDtoFromAlgorab();
        buffer.add(dto);
        List<AugeG4ElaboratedDataDto> fetchedAugeG4ElaboratedDataDto = new ArrayList<>();
        buffer.drainTo(fetchedAugeG4ElaboratedDataDto);
        return fetchedAugeG4ElaboratedDataDto;
    }

    @Override
    public void stop() {

    }

    private AugeG4ElaboratedDataDto mockDtoFromAlgorab () {
        AugeG4ElaboratedDataDto dto = new AugeG4ElaboratedDataDto();
        dto.setDateTimeAcquisition(new Date());
        dto.setControlUnitId("AIRQ01");
        dto.setResVal(Arrays.asList(
                new ElaboratedResVal(MEASUREMENT_ID_O3.getValue(), 10.0, 1, 1.0, 1.0),
                new ElaboratedResVal(MEASUREMENT_ID_TEMPERATURA.getValue(), 10.0, 1, 1.0, 1.0)
        ));
        return dto;
    }


}
