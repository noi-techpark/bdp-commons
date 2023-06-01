<!--
SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>

SPDX-License-Identifier: CC0-1.0
-->

# Sky Alps Data Collector 

The following Data Collector is used to take data from the Sky Alps API and pushing them to the Open Data Hub.

**Table of contents**
- [Sky Alps - Data Collectors](#Sky-Alps-Data-Collector)
	- [General information](#General-information)
		- [Analysis](#Analysis)
		- [API Request](#API-Request)
		- [Pre-requisites](#Pre-requisites)
		- [Packages and important classes](#Packages-and-important-classes)
		- [Implementation details](#Implementation-details)
		- [Configuration](#Configuration)
		- [env](#env)
		
## General information: ##

General instructions can be found at the [Open Data Hub Mobility - Data
Collectors README](../../README.md).

The instructions in the following paper will get you a copy of the project up and running on your local machine for development and testing purposes. 

## Analysis: ##

SkyAlps is an Italian airline operator managing flights at the Bolzano airport in South Tyrol. Thanks to the support of NOI, SkyAlps has initiated an innovation process that aims to share the data of the air services offered. 
The first set of data which is shared is related to the planned timetable of the flights offered, which is made available through a machine-readable API, i.e. through the AeroCRS hub, to which SkyAlps is connected. The reference method that will be used is the following: [AeroCRS](https://docs.aerocrs.com/reference/getschedule) 

The access credentials have been made available by SkyAlps and will be shared for the development purposes. An example of data record is the following:

{"fltnumber":"BN1900","airlinename":"Sky Alps","airlinedesignator":"BN","airlineid":423,"fromdestination":"BZO","todestination":"CAG","std":"17:15","sta":"19:15","weekdaysun":true,"weekdaymon":false,"weekdaytue":false,"weekdaywed":false,"weekdaythu":false,"weekdayfri":false,"weekdaysat":false,"accode":"DH4","fltsfromperiod":"2022\/05\/29","fltstoperiod":"2022\/09\/25"}
The following specifications have been considered:

•	the ODH field origin is to set as SKYALPS (See the [ODHClient](src/main/java/it/fos/noibz/skyalps/service/ODHClient.java) ).

•	the ODH field stationtype is to set as Flight (See the [ODHClient](src/main/java/it/fos/noibz/skyalps/service/ODHClient.java) ).

o	latitude: 46.46248 (See the [SyncScheduler](src/main/java/it/fos/noibz/skyalps/service/SyncScheduler.java)).

o	longitude: 11.32985 (See the [SyncScheduler](src/main/java/it/fos/noibz/skyalps/service/SyncScheduler.java)).

•	the other fields provided by the web-service (i.e.: airlinename, airlinedesignator, airlineid, std, sta, weekdaysun, weekdaymon, weekdaytue, weekdaywed, weekdaythu, weekdayfri, weekdaysat, accode, fltsfromperiod, fltstoperiod)  have to be considered as metadata (i.e. will be stored in the “metadata” table of the ODH database).

Those values have been retrieved and pushed in the ODH Database. Please see the [SyncScheduler -> syncJobStations](src/main/java/it/fos/noibz/skyalps/service/SyncScheduler.java). 

## API Request: ##

After a more detailed evaluation of the data retrieved by the service, it has been noted that in case of changes in the schedules these are visible only through the SSIM format. Therefore, the data should be requested in this way and not in the JSON format. (see [AeroCRSRest](src/main/java/it/fos/noibz/skyalps/rest/AeroCRSRest.java) ). 
Ultimately, data can be retrieved both in the JSON format (see [AeroCRSGetScheduleSuccessResponse](src/main/java/it/fos/noibz/skyalps/dto/json/AeroCRSGetScheduleSuccessResponse.java) )  and in the SSIM format. (see [AeroCRSGetScheduleSuccessResponseString](src/main/java/it/fos/noibz/skyalps/dto/string/AeroCRSGetScheduleSuccessResponseString.java) )

## Pre-requisites: ##

To build the project, the following prerequisites must be met: 
-	Everything inside: [Open Data Hub Mobility - Data Collectors README](../../README.md#prerequisites)

## Packages and important classes: ##

Package [it.fos.noibz](src/main/java/it/fos/noibz) 
Package used for the implementation of [noi-tech-park methods it.bz.idm.bdp](noi-techpark/bdp-core/dto/src/main/java/it/bz/idm/bdp/dto/)
-     ODHClient class: provides functionality to push data to the Open Data Hub platform.
-       SyncScheduler Class: provides functionality to schedule data retrieving and push operations for Stations, DataType, and MetaData. 
-       AeroCRSConst Class: provides constant variables used in the SynScheduler class 
-       The Classes inside the it.fos.noibz.dto.json package: have been used to convert the String provided into Java Objects. 
-       The Classes inside the it.fos.noibz.dto.string package: have been used to convert the SSIM string file provided into Java Objects. Within the [AeroCRSGetScheduleSuccessResponseString -> decodeFlights()](src/main/java/it/fos/noibz/skyalps/dto/string/AeroCRSGetScheduleSuccessResponseString.java it has been converted the SSIM string retrieved into various values used to populate the Flight object subsequently parsed into a JSON. 
-       AeroCRSRest Class: creates a request for Sky Alps API and get the response back either in JSON or SSIM format. 

## Implementation details: #

The command line runner interface executes the Spring Boot application once started. 
In the [SkyAlpsconf Class](src/main/java/it/fos/noibz/skyalps/conf/SkyAlpsconf.java) 
, we inject the RestTemplate, the [SyncScheduler class](src/main/java/it/fos/noibz/skyalps/service/SyncScheduler.java)
, and two Beans methods are used to return beans managed by the Spring context. 
Once Spring boot starts running, users can decide: 
-       Retrieving data in SSIM or JSON format: by setting the boolean ssim as true in the case of the SSIM or by setting it as false in the case of JSON format.
-       The dates range to retrieve flight data. As a consideration of what has just been said we need to clarify that: 
Flights are retrieved between a range of days declared in the application.properties file (see code comments). This range of flights can be retrieved either from the current date or a specific date entered when the application is running. 
If the boolean ssim will be set as true, the request handled by the AeroCRSRest class will return a response a String that will be converted into Java objects by the classes present in it.fos.noibz.skyalps.dto.services and parsed into a JSON format. 
The syncJobStations method inside the SyncScheduler class synchronizes and pushes both stations and data types. Stations are provided as Metadata, while DataType objects represent the following values (unique name identifier, descriptions, data). See the code for comments.

We suggest to run the app through the [SpringBootApp](src/main/java/it/fos/noibz/skyalps/SpringBootApp.java) to correctly get all the implementations and configurations explained in this paper.

## Configuration: ##

For further information about the configuration being used, you can see the [application.properties file](src/main/resources/application.properties) where appropriate comments have been made to make sense of what has been used. The parameters set in this file are: 
-       CRON expressions used for the syncJobStations intervals.
-       Params required by the services classes.
-       Logging level and style.
-       Open Data Hub configuration for testing and development purposes. 
-       0-Auth KeyCloack configuration with standard values for testing and development purposes.
-       Debugging configurations. 

## env: ##
See env files for connection information.
