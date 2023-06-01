// Copyright (C) 2015 TIS Innovation Park - Bolzano/Bozen - Italy
// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.tis.integreen.carsharingbzit.api;

/**
 * 
 * @author Davide Montesin <d@vide.bz>
 */
public class GetStationResponse
{

	CarsharingStationDto[] carsharingStationDto;

   public void setStation(CarsharingStationDto[] station)
   {
      this.carsharingStationDto = station;
   }

   public CarsharingStationDto[] getStation()
   {
      return this.carsharingStationDto;
   }

}
