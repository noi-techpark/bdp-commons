package it.bz.idm.bdp.augeg4.dto.tohub;

import java.util.Date;
import java.util.List;

/**
 * // TODO: Document
 */
public class AugeG4ToHubDataDto {

    private final String station;

    private final Date acquisition;

    private final List<Measurement> measurements;

    public AugeG4ToHubDataDto(String station, Date acquisition, List<Measurement> measurements) {
        this.station = station;
        this.acquisition = acquisition;
        this.measurements = measurements;
    }

    public String getStation() {
        return station;
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
                "station='" + station + '\'' +
                ", acquisition=" + acquisition +
                ", measurements=" + measurements +
                '}';
    }
}
