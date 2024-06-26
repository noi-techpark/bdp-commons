// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package com.opendatahub.bdp.radelt.dto.organisationen;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RadeltChallengeStatisticDto {

	private Long id;
	private Long challenge_id;
	private String challenge_name;
	private double km_total;
	private double height_meters_total;
	private double km_average;
	private double kcal;
	private double co2;
	private double m2_trees;
	private double money_saved;
	private Object physical_activity_percentage; // can be null, so Object type
	private int number_of_people;
	private LocalDateTime created_at;
	private String name;
	private String challenge_type;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getChallenge_id() {
		return challenge_id;
	}

	public void setChallenge_id(Long challenge_id) {
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

	public double getHeight_meters_total() {
		return height_meters_total;
	}

	public void setHeight_meters_total(double height_meters_total) {
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

	public Object getPhysical_activity_percentage() {
		return physical_activity_percentage;
	}

	public void setPhysical_activity_percentage(Object physical_activity_percentage) {
		this.physical_activity_percentage = physical_activity_percentage;
	}

	public int getNumber_of_people() {
		return number_of_people;
	}

	public void setNumber_of_people(int number_of_people) {
		this.number_of_people = number_of_people;
	}

	public LocalDateTime getCreated_at() {
		return created_at;
	}

	public void setCreated_at(String created_at) {

		DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSX");
		LocalDateTime formattedDate = LocalDateTime.parse(created_at, format);
		this.created_at = formattedDate;
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
