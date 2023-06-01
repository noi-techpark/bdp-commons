// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.idm.bdp.augeg4.mock;

import it.bz.idm.bdp.augeg4.dto.AugeG4ProcessedData;
import it.bz.idm.bdp.augeg4.dto.tohub.AugeG4ProcessedDataToHubDto;
import it.bz.idm.bdp.augeg4.dto.tohub.ProcessedMeasurementToHub;
import it.bz.idm.bdp.augeg4.dto.tohub.StationId;
import it.bz.idm.bdp.augeg4.face.DataConverterHubFace;

import java.util.Collections;
import java.util.Date;
import java.util.List;

public class DataConverterHubMock implements DataConverterHubFace {

    public final static double MOCKED_VALUE_RAW = 1.0;
    public final static double MOCKED_VALUE_PROCESSED = 1.1;

    @Override
    public List<AugeG4ProcessedDataToHubDto> convert(List<AugeG4ProcessedData> data) {
        Date acquisitionDate = new Date();
        List<ProcessedMeasurementToHub> processedMeasurementsToHub = Collections.singletonList(
                new ProcessedMeasurementToHub("temperature-external", MOCKED_VALUE_RAW, MOCKED_VALUE_PROCESSED)
        );
        AugeG4ProcessedDataToHubDto station = new AugeG4ProcessedDataToHubDto(
                new StationId("AUGEG4_", "STATION A"),
                acquisitionDate,
                processedMeasurementsToHub
        );

        return Collections.singletonList(station);
    }

}
