// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.fos.noibz.skyalps.dto.string;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 *
 * @author Thierry BODHUIN, bodhuin@gmail.com
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AeroCRSGetScheduleSuccessResponseString {

	private AeroCRSGetScheduleSuccessString aerocrs;

	public AeroCRSGetScheduleSuccessResponseString() {
	}

	public AeroCRSGetScheduleSuccessResponseString(AeroCRSGetScheduleSuccessString aerocrs) {
		this.aerocrs = aerocrs;
	}

	public AeroCRSGetScheduleSuccessString getAerocrs() {
		return aerocrs;
	}

	public void setAerocrs(AeroCRSGetScheduleSuccessString aerocrs) {
		this.aerocrs = aerocrs;
	}

}
