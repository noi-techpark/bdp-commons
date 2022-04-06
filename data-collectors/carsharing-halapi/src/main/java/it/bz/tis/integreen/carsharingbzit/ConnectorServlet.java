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

import javax.annotation.PostConstruct;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.bz.idm.bdp.IntegreenPushable;
import it.bz.tis.integreen.carsharingbzit.api.ApiClient;

/**
 * 
 * @author Davide Montesin <d@vide.bz>
 */
@Component
public class ConnectorServlet
{
	static final Logger       logger       = LoggerFactory.getLogger(ConnectorServlet.class);

	boolean                   destroy;

	ApiClient                 apiClient    = null;

	String[]                  cityUIDs;

	@Autowired
	private IntegreenPushable             xmlrpcPusher;

	HashMap<String, String[]> vehicleIdsByStationIds;

	String                    serviceStartedAt;
	ArrayList<ActivityLog>    activityLogs = new ArrayList<ActivityLog>();

	private Properties props = new Properties();

	@PostConstruct
	public void init()
	{
		logger.debug("init(ServletConfig): begin");
		URL resource = getClass().getClassLoader().getResource("application.properties");
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
		}
		String user = props.getProperty("user");
		String password = props.getProperty("password");
		String initCityUIDs = props.getProperty("cityUIDs");

		this.cityUIDs = initCityUIDs.split("\\s*,\\s*");

		this.apiClient = new ApiClient(endpoint, user, password);

		this.vehicleIdsByStationIds = null;

		this.destroy = false;
		logger.debug("init(ServletConfig): end");
	}

	public void run()
	{
		logger.debug("run(): begin");
		this.serviceStartedAt = formatDateTime(System.currentTimeMillis());
		long updateTime = calcLastPastIntervall();
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
				}
				else
				{
					logger.error("run(): InterruptedException received not by a destroy !!! Why?");
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

	static String formatDateTime(long dateTime)
	{
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(dateTime));
	}
}
