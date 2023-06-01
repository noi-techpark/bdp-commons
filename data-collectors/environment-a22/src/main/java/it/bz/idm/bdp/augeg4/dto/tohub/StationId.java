// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.idm.bdp.augeg4.dto.tohub;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.Objects;
import java.util.Optional;

public class StationId {

    private static final Logger LOG = LoggerFactory.getLogger(StationId.class.getName());

    private String prefix;
    private final String controlUnitId;

    public static Optional<StationId> fromValue(String value, String prefix) {
        if (!value.startsWith(prefix)) {
            LOG.warn("fromValue() called with a 'value' that doesn't start with 'prefix'");
            return Optional.empty();
        }
        String controlUnitId = value.substring(prefix.length());
        return Optional.of(new StationId(prefix, controlUnitId));
    }

    public StationId(String prefix, String controlUnitId) {
        this.prefix = prefix;
        this.controlUnitId = controlUnitId;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getControlUnitId() {
        return controlUnitId;
    }

    public String getValue() {
        return prefix + controlUnitId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StationId)) return false;
        StationId stationId = (StationId) o;
        return getPrefix().equals(stationId.getPrefix()) &&
                getValue().equals(stationId.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getValue());
    }

    @Override
    public String toString() {
        return "StationId{" +
                "prefix='" + prefix + '\'' +
                ", controlUnitId='" + controlUnitId + '\'' +
                ", value='" + getValue() + '\'' +
                '}';
    }
}
