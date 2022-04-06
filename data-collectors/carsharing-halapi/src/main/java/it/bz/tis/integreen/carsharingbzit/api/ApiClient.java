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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import it.bz.tis.integreen.carsharingbzit.ListStationsByBoundingBoxResponse;

/**
 *
 * @author Davide Montesin <d@vide.bz>
 */
public class ApiClient
{
   static final Logger logger = LoggerFactory.getLogger(ApiClient.class);

   String              endpoint;
   String              user;
   String              password;

   public ApiClient(String endpoint, String user, String password)
   {
      this.endpoint = endpoint;
      this.user = user;
      this.password = password;
   }

   public <T> T callWebService(ServiceRequest<?> request, Class<T> clazz) throws IOException
   {

      request.request.technicalUser.username = this.user;
      request.request.technicalUser.password = this.password;

      ObjectMapper mapper = new ObjectMapper();
      mapper.setVisibility(PropertyAccessor.FIELD, Visibility.NONE).setVisibility(PropertyAccessor.IS_GETTER,
                                                                                  Visibility.PUBLIC_ONLY).setVisibility(PropertyAccessor.GETTER,
                                                                                                                        Visibility.PUBLIC_ONLY).setVisibility(PropertyAccessor.SETTER,
                                                                                                                                                              Visibility.PUBLIC_ONLY);
      mapper.enable(SerializationFeature.INDENT_OUTPUT);

      StringWriter sw = new StringWriter();
      mapper.writeValue(sw, request);

      String requestJson = sw.getBuffer().toString();

      //Avoid logging credentials
      //logger.debug("callWebService(): jsonRequest:" + requestJson);

      URL url = new URL(this.endpoint);
      HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
      conn.setRequestMethod("POST");
      conn.setDoOutput(true);
      OutputStream out = conn.getOutputStream();
      out.write(requestJson.getBytes("UTF-8"));
      out.flush();
      int responseCode = conn.getResponseCode();

      InputStream input = conn.getInputStream();

      ByteArrayOutputStream data = new ByteArrayOutputStream();
      int len;
      byte[] buf = new byte[50000];
      while ((len = input.read(buf)) > 0)
      {
         data.write(buf, 0, len);
      }
      conn.disconnect();
      String jsonResponse = new String(data.toByteArray(), "UTF-8");
      if (responseCode != 200)
      {
         throw new IOException(jsonResponse);
      }

      logger.debug("callWebService(): jsonResponse:" + jsonResponse);

      mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      if ((clazz.isInstance(new ListStationsByBoundingBoxResponse()))&& ("[]").equals(jsonResponse))
    	  return null;
      T response = mapper.readValue(new StringReader(jsonResponse), clazz);

      mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
      sw = new StringWriter();
      mapper.writeValue(sw, response);
      logger.debug("callWebService(): parsed response into " + response.getClass().getName() + ":" + sw.toString());

      return response;
   }
}
