package it.bz.idm.bdp.augeg4.mock;

import it.bz.idm.bdp.augeg4.dto.toauge.AugeG4LinearizedDataDto;
import it.bz.idm.bdp.augeg4.dto.tohub.AugeG4ToHubDataDto;
import it.bz.idm.bdp.augeg4.dto.tohub.Measurement;
import it.bz.idm.bdp.augeg4.dto.tohub.StationId;
import it.bz.idm.bdp.augeg4.face.DataConverterFace;

import java.util.Collections;
import java.util.Date;
import java.util.List;

public class DataConverterMock implements DataConverterFace {

    public final static double MOCKED_VALUE_RAW = 1.0;
    public final static double MOCKED_VALUE_PROCESSED = 1.1;

    @Override
    public List<AugeG4ToHubDataDto> convert(List<AugeG4LinearizedDataDto> data) {
        Date acquisitionDate = new Date();
        List<Measurement> measurements = Collections.singletonList(
                new Measurement("temperature", MOCKED_VALUE_RAW, MOCKED_VALUE_PROCESSED)
        );
        AugeG4ToHubDataDto station = new AugeG4ToHubDataDto(new StationId("STATION A"), acquisitionDate, measurements);

        return Collections.singletonList(station);
    }

}
