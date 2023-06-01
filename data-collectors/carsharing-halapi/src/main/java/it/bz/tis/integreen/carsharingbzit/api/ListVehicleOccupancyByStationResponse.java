// Copyright (C) 2015 TIS Innovation Park - Bolzano/Bozen - Italy
// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.tis.integreen.carsharingbzit.api;

/**
 * 
 * @author Davide Montesin <d@vide.bz>
 */
public class ListVehicleOccupancyByStationResponse
{
   public static class VehicleAndOccupancies
   {
      CarsharingVehicleDto     vehicle;
      Occupancy[] occupancy;

      public CarsharingVehicleDto getVehicle()
      {
         return this.vehicle;
      }

      public Occupancy[] getOccupancy()
      {
         return this.occupancy;
      }
   }

   VehicleAndOccupancies[] vehicleAndOccupancies;

   public VehicleAndOccupancies[] getVehicleAndOccupancies()
   {
      return this.vehicleAndOccupancies;
   }
}
