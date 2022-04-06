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

package it.bz.tis.integreen.carsharingbzit.tis;

import java.io.IOException;
import java.io.StringWriter;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.StationList;

/**
 *
 * @author Davide Montesin <d@vide.bz>
 */
public class FakeConnector implements IXMLRPCPusher
{
   static final Logger logger = LoggerFactory.getLogger(FakeConnector.class);

   ObjectMapper        mapper;

   public FakeConnector()
   {
      this.mapper = new ObjectMapper();
      this.mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
      this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
   }

   @Override
   public Object syncStations(String datasourceName, StationList data)
   {

      StringWriter sw = new StringWriter();
      try
      {
         this.mapper.writeValue(sw, data);
      }
      catch (IOException e)
      {
         throw new IllegalStateException("Why???");
      }

      String txt = sw.getBuffer().toString();

      logger.debug("FakeConnector.syncStations: " + datasourceName + " - " + txt);
      return null;
   }

   @Override
   public Object pushData(String datasourceName, @SuppressWarnings("rawtypes") DataMapDto data)
   {
      StringWriter sw = new StringWriter();
      try
      {
         this.mapper.writeValue(sw, data);
      }
      catch (IOException e)
      {
         throw new IllegalStateException("Why???");
      }

      String txt = sw.getBuffer().toString();

      logger.debug("FakeConnector.pushData: " + datasourceName + " - " + txt);
      return null;
   }
}
