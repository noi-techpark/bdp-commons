// Copyright (C) 2015 TIS Innovation Park - Bolzano/Bozen - Italy
// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.tis.integreen.carsharingbzit.api;

/**
 * 
 * @author Davide Montesin <d@vide.bz>
 */
public class ListVehiclesByStationsResponse
{
   public static class StationAndVehicles
   {
      CarsharingStationDto station;
      CarsharingVehicleDto[] vehicle;

      public CarsharingStationDto getStation()
      {
         return this.station;
      }

      public CarsharingVehicleDto[] getVehicle()
      {
         return this.vehicle;
      }
   }

   StationAndVehicles[] stationAndVehicles;

   public StationAndVehicles[] getStationAndVehicles()
   {
      return this.stationAndVehicles;
   }

}
