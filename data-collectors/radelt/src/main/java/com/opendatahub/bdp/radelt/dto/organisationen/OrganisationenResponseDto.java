package it.bz.idm.bdp.radelt.dto.organisationen;

import java.util.List;

public class OrganisationenResponseDto {
	private boolean success;
	private RadeltDataDto data;

	// Getters and Setters
	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {

		this.success = success;
	}

	public RadeltDataDto getData() {
		return data;
	}

	public void setData(RadeltDataDto data) {
		this.data = data;
	}
}
