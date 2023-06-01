// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.idm.bdp.augeg4.dto.tohub;

import java.util.Date;
import java.util.List;

public class AugeG4ProcessedDataToHubDto {

    private final StationId stationId;

    private final Date acquisition;

    private final List<ProcessedMeasurementToHub> processedMeasurementsToHub;

    public AugeG4ProcessedDataToHubDto(
            StationId stationId,
            Date acquisition,
            List<ProcessedMeasurementToHub> processedMeasurementsToHub
    ) {
        this.stationId = stationId;
        this.acquisition = acquisition;
        this.processedMeasurementsToHub = processedMeasurementsToHub;
    }


    public StationId getStationId() {
        return stationId;
    }

    public Date getAcquisition() {
        return acquisition;
    }

    public List<ProcessedMeasurementToHub> getProcessedMeasurementsToHub() {
        return processedMeasurementsToHub;
    }

    @Override
    public String toString() {
        return "AugeG4ProcessedDataToHubDto{" +
                "stationId=" + stationId +
                ", acquisition=" + acquisition +
                ", processedMeasurementsToHub=" + processedMeasurementsToHub +
                '}';
    }
}
