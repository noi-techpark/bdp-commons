// Copyright (C) 2015 TIS Innovation Park - Bolzano/Bozen - Italy
// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.tis.integreen.carsharingbzit.api;

import it.bz.tis.integreen.carsharingbzit.api.ListVehiclesByStationsRequest.ListVehiclesByStationsSubRequest;
import it.bz.tis.integreen.carsharingbzit.api.ServiceRequest.SubRequest;

/**
 * 
 * @author Davide Montesin <d@vide.bz>
 */
public class ListVehiclesByStationsRequest extends ServiceRequest<ListVehiclesByStationsSubRequest>
{
   static class ListVehiclesByStationsSubRequest extends SubRequest
   {
      String[] stationUID;

      public String[] getStationUID()
      {
         return this.stationUID;
      }
   }

   public ListVehiclesByStationsRequest(String... stationUID)
   {
      this.request = new ListVehiclesByStationsSubRequest();
      this.request.stationUID = stationUID;
      this.function = "Api.listVehiclesByStation";
   }

}
