// Copyright (C) 2015 TIS Innovation Park - Bolzano/Bozen - Italy
// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.tis.integreen.carsharingbzit.api;

/**
 * 
 * @author Davide Montesin <d@vide.bz>
 */
public class BookMode
{
   boolean canBookAhead;
   boolean spontaneously;
   boolean hasCompanyPreferredVehicle;

   public boolean isCanBookAhead()
   {
      return this.canBookAhead;
   }

   public void setCanBookAhead(boolean canBookAhead)
   {
      this.canBookAhead = canBookAhead;
   }

   public boolean isSpontaneously()
   {
      return this.spontaneously;
   }

   public void setSpontaneously(boolean spontaneously)
   {
      this.spontaneously = spontaneously;
   }

   public boolean isHasCompanyPreferredVehicle()
   {
      return this.hasCompanyPreferredVehicle;
   }

   public void setHasCompanyPreferredVehicle(boolean hasCompanyPreferredVehicle)
   {
      this.hasCompanyPreferredVehicle = hasCompanyPreferredVehicle;
   }

}
