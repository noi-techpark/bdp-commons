// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package com.opendatahub.bdp.radelt.dto.organisationen;

import java.util.List;

public class RadeltDataDto {
	private List<RadeltOrganisationenDto> organisations;

	public List<RadeltOrganisationenDto> getOrganisations() {
		return organisations;
	}

	public void setOrganisations(List<RadeltOrganisationenDto> organisations) {
		this.organisations = organisations;
	}
}
