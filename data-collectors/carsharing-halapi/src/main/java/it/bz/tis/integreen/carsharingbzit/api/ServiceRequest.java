// Copyright (C) 2015 TIS Innovation Park - Bolzano/Bozen - Italy
// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.tis.integreen.carsharingbzit.api;

import it.bz.tis.integreen.carsharingbzit.api.ServiceRequest.SubRequest;

/**
 * 
 * @author Davide Montesin <d@vide.bz>
 */
public class ServiceRequest<S extends SubRequest>
{
   public static class SubRequest
   {
      TechnicalUser technicalUser = new TechnicalUser();

      public TechnicalUser getTechnicalUser()
      {
         return this.technicalUser;
      }
   }

   S      request;
   String function = "";

   public S getRequest()
   {
      return this.request;
   }

   public String getFunction()
   {
      return this.function;
   }
}
