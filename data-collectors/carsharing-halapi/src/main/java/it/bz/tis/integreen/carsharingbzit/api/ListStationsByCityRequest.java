// Copyright (C) 2015 TIS Innovation Park - Bolzano/Bozen - Italy
// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.tis.integreen.carsharingbzit.api;

import it.bz.tis.integreen.carsharingbzit.api.ListStationsByCityRequest.ListStationsByCitySubRequest;
import it.bz.tis.integreen.carsharingbzit.api.ServiceRequest.SubRequest;

/**
 * 
 * @author Davide Montesin <d@vide.bz>
 */
public class ListStationsByCityRequest extends ServiceRequest<ListStationsByCitySubRequest>
{
   static class ListStationsByCitySubRequest extends SubRequest
   {
      String[] cityUID;

      public String[] getCityUID()
      {
         return this.cityUID;
      }
   }

   public ListStationsByCityRequest(String... cityUID)
   {
      this.request = new ListStationsByCitySubRequest();
      this.request.cityUID = cityUID;
      this.function = "Api.listStationsByCity";
   }

}
