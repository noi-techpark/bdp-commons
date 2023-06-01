// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.noi.sta.parkingforecast.configuration;

public class DatatypeConfiguration {

    private String key;

    private String unit;

    private String description;

    private String rtype;

	private Integer period;

	private String property;

    public String getKey() {
        return key;
    }

    public String getUnit() {
        return unit;
    }

    public String getDescription() {
        return description;
    }

    public String getRtype() {
        return rtype;
    }

	public Integer getPeriod() {
		return period;
	}

	public String getProperty() {
		return property;
	}
}
