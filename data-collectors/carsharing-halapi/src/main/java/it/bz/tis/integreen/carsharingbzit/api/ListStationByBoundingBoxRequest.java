/*
carsharing-ds: car sharing datasource for the integreen cloud

Copyright (C) 2015 TIS Innovation Park - Bolzano/Bozen - Italy

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

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
