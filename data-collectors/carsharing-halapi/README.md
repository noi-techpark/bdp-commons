# Car sharing data source

[![CI carsharing-halapi](https://github.com/noi-techpark/bdp-commons/actions/workflows/ci-carsharing-halapi.yml/badge.svg)](https://github.com/noi-techpark/bdp-commons/actions/workflows/ci-carsharing-halapi.yml)

Table of content

*   1\. Introduction
*   2\. Compiling & running
*   3\. Configuration
*   4\. Scheduler
*   5\. Packages and classes
*   6\. Activity report

## 1\. Introduction

This software collects real time data about car sharing stations and vehicles (currently of South Tyrol) and stores the data into the integreen platform. The integreen platform is a central database where many real time data are collected. Data sources are components of the integreen system that collects data from specific APIs and writes to a common database structure.

## 2\. Compiling & running

This is a java dynamic web (j2ee) project. It uses maven as uniform build system. The pom.xml file contains all specs about compiling, dependencies and running this project.

*   run & debug the project using an embedded tomcat: `mvn tomcat7:run`
*   compile and create war for deploy: `mvn package`

## 3\. Configuration

This project uses only one java servlet for scheduling tasks and for the simple report.

*   it.bz.tis.integreen.carsharingbzit.ConnectorServlet

The servlet is configured in the web.xml file. Parameters for the servlet are:

*   **endpoint**: the url for the car sharing web service
*   **user/password**: authentication
*   **cityUIDs**: a comma separated list of ID of the city the servlet must get the real time data

**Important**: the servlet must be configured with the tag " <load-on-startup>1</load-on-startup>" because the servlet starts the scheduler thread, otherwise the scheduler will not start immediately.

The project uses log4j as logging library. To configure log4j edit the file

*   src/main/resources/log4j2.xml

## 4\. Scheduler

The main java class is `it.bz.tis.integreen.carsharingbzit.ConnectorServlet` . This servlet, during the init process, creates a new background thread. The background thread polls the car sharing API each 10 minute. At the moment this time is stored into this constant:

<pre>it.bz.tis.integreen.carsharingbzit.ConnectorLogic

   final static long INTERVALL = 10L * 60L * 1000L;
</pre>

The `INTERVALL` constant can be changed as desired, but please remember that it must fit one hour. Example of valid values are: 1 minute, 2 minute, 3 minute, 4 minute, 5 minute, 6 minute, 10 minute. 7, 8, 9 minute are examples of not valid values.

This connector makes two kind of synchronization. The first is about "static" data and the second is about real time data. During the "static" synchronization values, that do not change frequently, are copied: car sharing station list and vehicle list. This data is synchronized only once a day. The time when this is done is defined in the main Servlet in the following method:

<pre>it.bz.tis.integreen.carsharingbzit.ConnectorLogic

   static boolean isTimeForAFullSync(long time)
   {
      Calendar cal = Calendar.getInstance();
      cal.setTimeInMillis(time);
      return cal.get(Calendar.HOUR_OF_DAY) == 12 && cal.get(Calendar.MINUTE) == 00;
   }

</pre>

At the moment the servlet is configured to run a synchronization of "static" data at 12:00 pm each day.

**Important:** if you change the minute, please pay attention that this must match the INTERVALL constants

Each INTERVALL (10 minute) real time data are synchronized. Real time data contains the following informations:

*   How much vehicles are free/occupied in a station now and forecast
*   for each vehicle if is free/occupied now and forecast

## 5\. Packages and important classes

package **it.bz.tis.integreen.carsharingbzit**

This package contains the core classes: the servlet ( `ConnectorServlet` ) that starts the scheduler and the business logic ( `ConnectorLogic` ). The business logic contains all the code to read, convert and write data from car sharing system to integreen.

package **it.bz.tis.integreen.carsharingbzit.api**

This package contains all model classes for the car sharing api. Examples are: City, Vehicle and so on.

The class `ApiClient` is the "main" class used to send request to the car sharing system and read responses.

package **it.bz.tis.integreen.carsharingbzit.tis**

This package contains integreen extended classes to write data into the integreen database. It reuses integreen DTO classes.

## 6\. Activity report

If you open the web application with a browser (like http://www.ddddddd.xyz/carsharing-ds), a report is returned. The report shows the last 1000 activities with timestamp, what was done and status. In case of problems you can look at log4j files too.

![](report-example.png)
