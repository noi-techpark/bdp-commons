package it.bz.idm.bdp.augeg4.dto.tohub;

import java.util.Date;
import java.util.List;

public class AugeG4ToHubDataDto {

    private final StationId stationId;

    private final Date acquisition;

    private final List<Measurement> measurements;

    public AugeG4ToHubDataDto(StationId stationId, Date acquisition, List<Measurement> measurements) {
        this.stationId = stationId;
        this.acquisition = acquisition;
        this.measurements = measurements;
    }

    public StationId getStationId() {
        return stationId;
    }

    public Date getAcquisition() {
        return acquisition;
    }

    public List<Measurement> getMeasurements() {
        return measurements;
    }

    @Override
    public String toString() {
        return "AugeG4ToHubDataDto{" +
                "stationId='" + stationId + '\'' +
                ", acquisition=" + acquisition +
                ", measurements=" + measurements +
                '}';
    }
}
