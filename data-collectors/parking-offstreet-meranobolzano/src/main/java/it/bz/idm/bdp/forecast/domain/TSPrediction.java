// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.idm.bdp.forecast.domain;

public class TSPrediction{

	private Integer predictedFreeSlots;
	private double upperConfidenceLevel;
	private double lowerConfidenceLevel;
	private String status;

	public TSPrediction() {
		super();
	}

	public TSPrediction(Integer predictedFreeSlots, double upperConfidenceLevel, double lowerConfidenceLevel,
			String status) {
		super();
		this.predictedFreeSlots = predictedFreeSlots;
		this.upperConfidenceLevel = upperConfidenceLevel;
		this.lowerConfidenceLevel = lowerConfidenceLevel;
		this.status = status;
	}

	public Integer getPredictedFreeSlots() {
		return predictedFreeSlots;
	}

	public String getStatus() {
		return this.status;
	}

	public double getUpperConfidenceLevel() {
		return upperConfidenceLevel;
	}

	public double getLowerConfidenceLevel() {
		return lowerConfidenceLevel;
	}

}
