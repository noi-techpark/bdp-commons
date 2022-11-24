package it.fos.noibz.skyalps.dto.json;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 *
 * @author Thierry BODHUIN, bodhuin@gmail.com
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AeroCRSGetScheduleSuccess {

	private boolean success;
	private List<AeroCRSFlight> flight;

	public AeroCRSGetScheduleSuccess() {
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public List<AeroCRSFlight> getFlight() {
		return flight;
	}

	public void setFlight(List<AeroCRSFlight> flight) {
		this.flight = flight;
	}

}
