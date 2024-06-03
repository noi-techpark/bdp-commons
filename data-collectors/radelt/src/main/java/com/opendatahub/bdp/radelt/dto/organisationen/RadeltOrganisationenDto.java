// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package com.opendatahub.bdp.radelt.dto.organisationen;


public class RadeltOrganisationenDto {
	private int id;
	private String name;
	private String type;
	private String logo;
	private String website;
	private int peopleTotal;
	private RadeltStatisticsDto statistics;

	// Getters and Setters
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public int getPeopleTotal() {
		return peopleTotal;
	}

	public void setPeopleTotal(int peopleTotal) {
		this.peopleTotal = peopleTotal;
	}

	public RadeltStatisticsDto getStatistics() {
		return statistics;
	}

	public void setStatistics(RadeltStatisticsDto statistics) {
		this.statistics = statistics;
	}
}
