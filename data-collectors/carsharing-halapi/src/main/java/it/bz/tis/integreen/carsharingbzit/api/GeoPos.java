// Copyright (C) 2015 TIS Innovation Park - Bolzano/Bozen - Italy
// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.tis.integreen.carsharingbzit.api;

import java.io.Serializable;

/**
 * @author Davide Montesin <d@vide.bz>
 */
public class GeoPos implements Serializable
{
   private static final long serialVersionUID = -2858566542786324052L;

   String lat;
   String lon;

   public void setLat(String lat)
   {
      this.lat = lat;
   }

   public void setLon(String lon)
   {
      this.lon = lon;
   }

   public String getLat()
   {
      return this.lat;
   }

   public String getLon()
   {
      return this.lon;
   }
}
