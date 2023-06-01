// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.idm.bdp.service.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import it.bz.idm.bdp.dto.ChildDto;

@JsonInclude(value=Include.NON_EMPTY)
public class ChildPlugDto extends ChildDto{
	/**
	 *
	 */
	private static final long serialVersionUID = -7467402590346607912L;
	private List<OutletDtoV2> outlets = new ArrayList<>();
	private boolean available;
	public List<OutletDtoV2> getOutlets() {
		return outlets;
	}
	public void setOutlets(List<OutletDtoV2> outlets) {
		this.outlets = outlets;
	}
	public boolean isAvailable() {
		return available;
	}
	public void setAvailable(boolean available) {
		this.available = available;
	}
}
