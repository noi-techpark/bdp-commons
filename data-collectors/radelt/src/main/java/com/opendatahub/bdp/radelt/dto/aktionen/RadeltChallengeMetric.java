// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package com.opendatahub.bdp.radelt.dto.aktionen;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class RadeltChallengeMetric {
	private long id;
	private long challenge_id;
	private String challenge_name;
	private double km_total;
	private long height_meters_total;
	private double km_average;
	private double kcal;
	private double co2;
	private double m2_trees;
	private double money_saved;
	private Double physical_activity_percentage;
	private int number_of_people;
	private long created_at;
	private int organisation_count;
	private int workplace_count;
	private int school_count;
	private int municipality_count;
	private int association_count;
	private int university_count;
	private String name;
	private String challenge_type;

	// Getters and Setters
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getChallenge_id() {
		return challenge_id;
	}

	public void setChallenge_id(long challenge_id) {
		this.challenge_id = challenge_id;
	}

	public String getChallenge_name() {
		return challenge_name;
	}

	public void setChallenge_name(String challenge_name) {
		this.challenge_name = challenge_name;
	}

	public double getKm_total() {
		return km_total;
	}

	public void setKm_total(double km_total) {
		this.km_total = km_total;
	}

	public long getHeight_meters_total() {
		return height_meters_total;
	}

	public void setHeight_meters_total(long height_meters_total) {
		this.height_meters_total = height_meters_total;
	}

	public double getKm_average() {
		return km_average;
	}

	public void setKm_average(double km_average) {
		this.km_average = km_average;
	}

	public double getKcal() {
		return kcal;
	}

	public void setKcal(double kcal) {
		this.kcal = kcal;
	}

	public double getCo2() {
		return co2;
	}

	public void setCo2(double co2) {
		this.co2 = co2;
	}

	public double getM2_trees() {
		return m2_trees;
	}

	public void setM2_trees(double m2_trees) {
		this.m2_trees = m2_trees;
	}

	public double getMoney_saved() {
		return money_saved;
	}

	public void setMoney_saved(double money_saved) {
		this.money_saved = money_saved;
	}

	public Double getPhysical_activity_percentage() {
		return physical_activity_percentage;
	}

	public void setPhysical_activity_percentage(Double physical_activity_percentage) {
		this.physical_activity_percentage = physical_activity_percentage;
	}

	public int getNumber_of_people() {
		return number_of_people;
	}

	public void setNumber_of_people(int number_of_people) {
		this.number_of_people = number_of_people;
	}

	public long getCreated_at() {
		return created_at;
	}

	public void setCreated_at(String created_at) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");
			sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
			// Parse the date string to a Date object
			Date date = sdf.parse(created_at);
			this.created_at = date.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public int getOrganisation_count() {
		return organisation_count;
	}

	public void setOrganisation_count(int organisation_count) {
		this.organisation_count = organisation_count;
	}

	public int getWorkplace_count() {
		return workplace_count;
	}

	public void setWorkplace_count(int workplace_count) {
		this.workplace_count = workplace_count;
	}

	public int getSchool_count() {
		return school_count;
	}

	public void setSchool_count(int school_count) {
		this.school_count = school_count;
	}

	public int getMunicipality_count() {
		return municipality_count;
	}

	public void setMunicipality_count(int municipality_count) {
		this.municipality_count = municipality_count;
	}

	public int getAssociation_count() {
		return association_count;
	}

	public void setAssociation_count(int association_count) {
		this.association_count = association_count;
	}

	public int getUniversity_count() {
		return university_count;
	}

	public void setUniversity_count(int university_count) {
		this.university_count = university_count;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getChallenge_type() {
		return challenge_type;
	}

	public void setChallenge_type(String challenge_type) {
		this.challenge_type = challenge_type;
	}
}
