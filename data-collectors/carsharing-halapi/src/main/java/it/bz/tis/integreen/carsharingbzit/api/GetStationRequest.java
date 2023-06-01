// Copyright (C) 2015 TIS Innovation Park - Bolzano/Bozen - Italy
// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.tis.integreen.carsharingbzit.api;

import it.bz.tis.integreen.carsharingbzit.api.GetStationRequest.GetStationSubRequest;
import it.bz.tis.integreen.carsharingbzit.api.ServiceRequest.SubRequest;

/**
 * 
 * @author Davide Montesin <d@vide.bz>
 */
public class GetStationRequest extends ServiceRequest<GetStationSubRequest>
{
   static class GetStationSubRequest extends SubRequest
   {
      String[] stationUID;

      public String[] getStationUID()
      {
         return this.stationUID;
      }
   }

   public GetStationRequest(String... stationUID)
   {
      this.request = new GetStationSubRequest();
      this.request.stationUID = stationUID;
      this.function = "Api.getStation";
   }

}
