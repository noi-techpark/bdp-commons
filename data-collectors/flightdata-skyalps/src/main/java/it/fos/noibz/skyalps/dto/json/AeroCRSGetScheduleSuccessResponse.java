package it.fos.noibz.skyalps.dto.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 *
 * @author Thierry BODHUIN, bodhuin@gmail.com
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AeroCRSGetScheduleSuccessResponse {

	private AeroCRSGetScheduleSuccess aerocrs;

	public AeroCRSGetScheduleSuccessResponse() {
	}

	public AeroCRSGetScheduleSuccessResponse(AeroCRSGetScheduleSuccess aerocrs) {
		this.aerocrs = aerocrs;
	}

	public AeroCRSGetScheduleSuccess getAerocrs() {
		return aerocrs;
	}

	public void setAerocrs(AeroCRSGetScheduleSuccess aerocrs) {
		this.aerocrs = aerocrs;
	}

}
