// Copyright (C) 2015 TIS Innovation Park - Bolzano/Bozen - Italy
// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.tis.integreen.carsharingbzit.api;

/**
 * 
 * @author Davide Montesin <d@vide.bz>
 */
public class GetVehicleResponse
{

	CarsharingVehicleDto[] carsharingVehicleDto;

   public void setVehicle(CarsharingVehicleDto[] vehicle)
   {
      this.carsharingVehicleDto = vehicle;
   }

   public CarsharingVehicleDto[] getVehicle()
   {
      return this.carsharingVehicleDto;
   }
}
