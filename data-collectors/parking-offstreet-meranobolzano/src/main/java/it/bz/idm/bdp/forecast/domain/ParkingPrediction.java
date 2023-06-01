// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.idm.bdp.forecast.domain;


public class ParkingPrediction {
	private ParkingPlace parkingPlace = null;
	private TSPrediction prediction = null;


	public ParkingPlace getParkingPlace() {
		return parkingPlace;
	}

	public TSPrediction getPrediction() {
		return prediction;
	}
	
	

}
