// Copyright (C) 2015 TIS Innovation Park - Bolzano/Bozen - Italy
// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.tis.integreen.carsharingbzit.api;

/**
 * 
 * @author Davide Montesin <d@vide.bz>
 */
public class Occupancy
{
   String begin;
   String end;
   String occupancyKind;

   public String getBegin()
   {
      return this.begin;
   }

   public String getEnd()
   {
      return this.end;
   }

   public String getOccupancyKind()
   {
      return this.occupancyKind;
   }
}
