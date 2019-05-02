package it.bz.idm.bdp.augeg4.dto.tohub;

import java.util.Objects;

public class StationId {
    private final String value;

    public StationId(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StationId)) return false;
        StationId stationId = (StationId) o;
        return getValue().equals(stationId.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getValue());
    }

    @Override
    public String toString() {
        return "StationId{" +
                "value='" + value + '\'' +
                '}';
    }
}
