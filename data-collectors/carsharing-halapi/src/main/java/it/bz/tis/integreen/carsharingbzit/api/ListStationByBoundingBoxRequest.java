// Copyright (C) 2015 TIS Innovation Park - Bolzano/Bozen - Italy
// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.tis.integreen.carsharingbzit.api;

import it.bz.tis.integreen.carsharingbzit.api.ListStationByBoundingBoxRequest.ListStationByBoundingBoxSubRequest;
import it.bz.tis.integreen.carsharingbzit.api.ServiceRequest.SubRequest;

/**
 * 
 * @author Patrick Bertolla
 */
public class ListStationByBoundingBoxRequest extends ServiceRequest<ListStationByBoundingBoxSubRequest>
{
   static class ListStationByBoundingBoxSubRequest extends SubRequest
   {
      BoundingBox boundingBox;

      public BoundingBox getBoundingBox() {
		return boundingBox;
      }

      public void setBoundingBox(BoundingBox boundingBox) {
		this.boundingBox = boundingBox;
      }


   }

   public ListStationByBoundingBoxRequest(BoundingBox box)
   {
      this.request = new ListStationByBoundingBoxSubRequest();
      this.request.boundingBox = box; 
      this.function = "Api.listStationsByGeoPos";
   }

}
