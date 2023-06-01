// Copyright (C) 2015 TIS Innovation Park - Bolzano/Bozen - Italy
// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.tis.integreen.carsharingbzit.api;

import it.bz.tis.integreen.carsharingbzit.api.ListVehicleOccupancyByStationRequest.ListVehicleOccupancyByStationSubRequest;
import it.bz.tis.integreen.carsharingbzit.api.ServiceRequest.SubRequest;

/**
 * 
 * @author Davide Montesin <d@vide.bz>
 */
public class ListVehicleOccupancyByStationRequest extends ServiceRequest<ListVehicleOccupancyByStationSubRequest>
{
   static class ListVehicleOccupancyByStationSubRequest extends SubRequest
   {
      String   stationUID;
      String[] vehicleUID;
      String   begin;
      String   end;

      public String getStationUID()
      {
         return this.stationUID;
      }

      public String[] getVehicleUID()
      {
         return this.vehicleUID;
      }

      public String getBegin()
      {
         return this.begin;
      }

      public String getEnd()
      {
         return this.end;
      }
   }

   public ListVehicleOccupancyByStationRequest(String begin, String end, String stationUID, String... vehicleUID)
   {
      this.request = new ListVehicleOccupancyByStationSubRequest();
      this.request.stationUID = stationUID;
      this.request.vehicleUID = vehicleUID;
      this.request.begin = begin;
      this.request.end = end;
      this.function = "Api.listVehicleOccupancyByStation";
   }

}
