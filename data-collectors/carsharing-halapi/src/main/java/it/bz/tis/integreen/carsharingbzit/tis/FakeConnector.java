// Copyright (C) 2015 TIS Innovation Park - Bolzano/Bozen - Italy
// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

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
