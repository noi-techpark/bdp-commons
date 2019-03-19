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

package it.bz.tis.integreen.carsharingbzit;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import it.bz.idm.bdp.IntegreenPushable;
import it.bz.tis.integreen.carsharingbzit.api.ApiClient;
import it.bz.tis.integreen.carsharingbzit.tis.CarSharingPusher;

/**
 * 
 * @author Davide Montesin <d@vide.bz>
 */
public class ConnectorServlet extends HttpServlet implements Runnable
{
   private static final long serialVersionUID = 2317521315923271535L;
   static final Logger       logger       = LogManager.getLogger(ConnectorServlet.class);

   Thread                    backgroundTask;
   boolean                   destroy;

   ApiClient                 apiClient    = null;

   String[]                  cityUIDs;

   IntegreenPushable             xmlrpcPusher;

   HashMap<String, String[]> vehicleIdsByStationIds;

   String                    serviceStartedAt;
   ArrayList<ActivityLog>    activityLogs = new ArrayList<ActivityLog>();

	private Properties props = new Properties();

   @Override
   public void init(ServletConfig config) throws ServletException
   {
      logger.debug("init(ServletConfig): begin");
      super.init(config);
      URL resource = getClass().getClassLoader().getResource("app.properties");
	  try {
		props.load(new FileInputStream(resource.getFile()));
	} catch (FileNotFoundException e) {
		e.printStackTrace();
	} catch (IOException e) {
		e.printStackTrace();
	}
      String endpoint = props.getProperty("endpoint");
      if (endpoint == null || endpoint.trim().length() == 0)
      {
         String msg = "endpoint not configured. Please configure it in the properties file";
         logger.error(msg);
         throw new ServletException(msg);
      }
      String user = props.getProperty("user");
      String password = props.getProperty("password");
      String initCityUIDs = props.getProperty("cityUIDs");
      this.cityUIDs = initCityUIDs.split("\\s*,\\s*");

      this.apiClient = new ApiClient(endpoint, user, password);
      String xmlrpcPusherParam = config.getInitParameter("xmlrpcpusher");
      if (xmlrpcPusherParam != null && xmlrpcPusherParam.equals("fake"))
      {
         this.xmlrpcPusher = null;
      }
      else
      {
         this.xmlrpcPusher = new CarSharingPusher();
      }

      this.vehicleIdsByStationIds = null;

      this.destroy = false;
      this.backgroundTask = new Thread(this);
      this.backgroundTask.setName("background-task");
      this.backgroundTask.start();
      logger.debug("init(ServletConfig): end");
   }

   @Override
   public void run()
   {
      logger.debug("run(): begin");
      this.serviceStartedAt = formatDateTime(System.currentTimeMillis());
      long updateTime = calcLastPastIntervall();
      while (true)
      {
         long start = System.currentTimeMillis();
         ActivityLog activityLog = new ActivityLog();
         try
         {
            logger.debug("run(): iteration begin");
            if (isTimeForAFullSync(updateTime))
            {
               // Trash previous informations
               this.vehicleIdsByStationIds = null;
               activityLog.full = true;
            }
            activityLog.timestamp = formatDateTime(System.currentTimeMillis());
            activityLog.requesttime = formatDateTime(updateTime);
            synchronized (this.activityLogs)
            {
               this.activityLogs.add(activityLog);
               while (this.activityLogs.size() > 1000)
               {
                  this.activityLogs.remove(0);
               }
            }
            this.vehicleIdsByStationIds = ConnectorLogic.process(this.apiClient,
                                                                 this.cityUIDs,
                                                                 this.xmlrpcPusher,
                                                                 this.vehicleIdsByStationIds,
                                                                 updateTime,
                                                                 activityLog,
                                                                 this.activityLogs);
         }
         catch (Throwable exxx)
         {
            activityLog.error = exxx.getClass().getName() + ": " + exxx.getMessage();
            logger.debug("run(): exception executing task (catched)", exxx);
         }
         finally
         {
            long stop = System.currentTimeMillis();
            activityLog.durationSec = (int) ((stop - start) / 1000);
            logger.debug(String.format("run(): iteration end in %08d millis!\n", stop - start));
         }
         try
         {
            updateTime += ConnectorLogic.INTERVALL;
            long sleep = updateTime - System.currentTimeMillis();
            if (sleep > 0)
            {
               Thread.sleep(sleep);
            }
         }
         catch (InterruptedException ixxx)
         {
            synchronized (this)
            {
               if (this.destroy)
               {
                  logger.debug("run(): InterruptedException received for destroy, exiting");
                  break;
               }
               else
               {
                  logger.error("run(): InterruptedException received not by a destroy !!! Why?");
               }
            }

         }
      }
      logger.debug("run(): end");
   }

   static long calcLastPastIntervall()
   {
      long now = System.currentTimeMillis();
      Calendar cal = Calendar.getInstance();
      cal.setTimeInMillis(now);
      cal.set(Calendar.HOUR_OF_DAY, 0);
      cal.set(Calendar.MINUTE, 0);
      cal.set(Calendar.SECOND, 0);
      cal.set(Calendar.MILLISECOND, 0);
      long midnight = cal.getTimeInMillis();
      long daytime = now - midnight;
      long alreadyCompleteIntervalls = daytime / ConnectorLogic.INTERVALL;
      return midnight + alreadyCompleteIntervalls * ConnectorLogic.INTERVALL;
   }

   static boolean isTimeForAFullSync(long time)
   {
      Calendar cal = Calendar.getInstance();
      cal.setTimeInMillis(time);
      return cal.get(Calendar.HOUR_OF_DAY) == 12 && cal.get(Calendar.MINUTE) == 00;
   }

   @Override
   protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
   {
      String from;
      ActivityLog[] al;
      synchronized (this.activityLogs)
      {
         from = this.serviceStartedAt;
         al = new ActivityLog[this.activityLogs.size()];
         for (int i = 0; i < al.length; i++)
         {
            al[i] = this.activityLogs.get(i).clone();
         }
      }
      HashMap<String, Object> data = new HashMap<String, Object>();
      data.put("from", from);
      data.put("logs", al);
      req.setAttribute("data", data);
      req.getRequestDispatcher("/report.jsp").forward(req, resp);
   }

   @Override
   public void destroy()
   {
      logger.debug("destroy(): begin");
      super.destroy();
      synchronized (this)
      {
         this.destroy = true;
      }
      this.backgroundTask.interrupt();
      try
      {
         this.backgroundTask.join();
      }
      catch (InterruptedException e)
      {
         logger.error("destroy(): join interrupted !!! Why?");
      }
      finally
      {
         logger.debug("destroy(); end");
      }
   }

   static String formatDateTime(long dateTime)
   {
      return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(dateTime));
   }
}
