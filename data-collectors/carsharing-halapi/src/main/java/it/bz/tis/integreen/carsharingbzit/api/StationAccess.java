// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.tis.integreen.carsharingbzit.api;

import java.io.Serializable;

public class StationAccess implements Serializable{
	private static final long serialVersionUID = -6882191851531656942L;
	String locationNote;
	   String parking;

	   public String getParking()
	   {
	      return this.parking;
	   }

	   public void setParking(String parking)
	   {
	      this.parking = parking;
	   }

	   public String getLocationNote()
	   {
	      return this.locationNote;
	   }

	   public void setLocationNote(String locationNote)
	   {
	      this.locationNote = locationNote;
	   }
}
