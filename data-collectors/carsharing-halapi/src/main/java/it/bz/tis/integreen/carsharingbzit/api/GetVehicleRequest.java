// Copyright (C) 2015 TIS Innovation Park - Bolzano/Bozen - Italy
// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.tis.integreen.carsharingbzit.api;

import it.bz.tis.integreen.carsharingbzit.api.GetVehicleRequest.GetVehicleSubRequest;
import it.bz.tis.integreen.carsharingbzit.api.ServiceRequest.SubRequest;

/**
 * 
 * @author Davide Montesin <d@vide.bz>
 */
public class GetVehicleRequest extends ServiceRequest<GetVehicleSubRequest>
{
   static class GetVehicleSubRequest extends SubRequest
   {
      String[] vehicleUID;

      public String[] getVehicleUID()
      {
         return this.vehicleUID;
      }
   }

   public GetVehicleRequest(String... vehicleUID)
   {
      this.request = new GetVehicleSubRequest();
      this.request.vehicleUID = vehicleUID;
      this.function = "Api.getVehicle";
   }

}
