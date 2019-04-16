package it.bz.idm.bdp.augeg4.mock;

import it.bz.idm.bdp.augeg4.dto.toauge.AugeG4LinearizedDataDto;
import it.bz.idm.bdp.augeg4.dto.tohub.AugeG4ToHubDataDto;
import it.bz.idm.bdp.augeg4.dto.tohub.Measurement;
import it.bz.idm.bdp.augeg4.face.DataConverterFace;

import java.util.Collections;
import java.util.Date;
import java.util.List;

public class DataConverterMock implements DataConverterFace {

    @Override
    public List<AugeG4ToHubDataDto> convert(List<AugeG4LinearizedDataDto> data) {
        Date acquisitionDate = new Date();
        List<Measurement> measurements = Collections.singletonList(
                new Measurement("temperature", 1.0)
        );
        AugeG4ToHubDataDto station = new AugeG4ToHubDataDto("STATION A", acquisitionDate, measurements);

        return Collections.singletonList(station);
    }

}
