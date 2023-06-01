// Copyright (C) 2015 TIS Innovation Park - Bolzano/Bozen - Italy
// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.tis.integreen.carsharingbzit;

/**
 * 
 * @author Davide Montesin <d@vide.bz>
 */
public class ActivityLog
{
   public String  timestamp   = "YYYY-MM-DD HH:MM:SS";
   public String  requesttime = "YYYY-MM-DD HH:MM:SS";
   public boolean full        = false;
   public String  report      = "";
   public int     durationSec = -1;          // -1 means in progress
   public String  error       = null;

   @Override
   public ActivityLog clone()
   {
      ActivityLog log = new ActivityLog();
      log.timestamp = this.timestamp;
      log.requesttime = this.requesttime;
      log.full = this.full;
      log.report = this.report;
      log.durationSec = this.durationSec;
      log.error = this.error;
      return log;
   }
}
