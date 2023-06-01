// Copyright (C) 2015 TIS Innovation Park - Bolzano/Bozen - Italy
// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.tis.integreen.carsharingbzit.api;

import it.bz.idm.bdp.dto.StationDto;

/**
 * 
 * @author Davide Montesin <d@vide.bz>
 */
public class ListStationsByCityResponse
{
   public static class CityAndStations
   {
      City      city = new City();
      StationDto[] station;

      public City getCity()
      {
         return this.city;
      }

      public StationDto[] getStation()
      {
         return this.station;
      }
   }

   CityAndStations[] cityAndStations;

   public CityAndStations[] getCityAndStations()
   {
      return this.cityAndStations;
   }

}
