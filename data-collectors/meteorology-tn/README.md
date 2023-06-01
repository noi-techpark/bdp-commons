<!--
SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>

SPDX-License-Identifier: CC0-1.0
-->

Meteorology TN datacollector
=========================

Datacollector which takes data regarding meteorology in the Province of Trento, parses it and sends it to the opendatahub.
Data are provided by Meteotrentino (http://dati.meteotrentino.it/service.asmx).

[![CI meteorology-tn](https://github.com/noi-techpark/bdp-commons/actions/workflows/ci-meteorology-tn.yml/badge.svg)](https://github.com/noi-techpark/bdp-commons/actions/workflows/ci-meteorology-tn.yml)

## Analysis:

For details regarding mapping from data provided by the service and the data used by the opendatahub, please see this analysis document:
[190118_SpecificheIntegrazione_IDM.pdf](documentation/190118_SpecificheIntegrazione_IDM.pdf)

Meteotrentino provides two services:
  - getListOfMeteoStations: provides anagrafic data for available stations
  - getLastDataOfMeteoStation: provides measurements of last 24 hours, refreshed every 15 minutes. Data are normally available from the service after 15-20 minutes (i.e. at 15:30 we can see data measured at 15:00, sometimes also measured at 15:15).

## Configuration:
  - See `src/main/resources/META-INF/spring/applicationContext.xml`

    here you can configure the scheduler for the tasks "pushData", "pushDataTypes" and "pushStations". The scheduler is implemented in class it.bz.idm.bdp.dcmeteotn.MeteoTnJobScheduler.java and uses Spring framework provided services. Since the data is refreshed every 15 minutes, pushData is called every 15 minutes. 

  - See `src/main/resources/META-INF/spring/application.properties`

    please refer to the comments provided in the file, here you can configure the parameters for:
    - the service endpoints (no credentials are required);
    - request params required by the services (getLastDataOfMeteoStation needs the code of the station as input parameter);
    - Station constants, to fill data not provided by the service;


  - Logsystem: `src/main/resources/log4j.properties` (Make sure the log-files are writable)


## Tests:

In the Datacollector there are two types of test cases:

 - Integration Tests: this tests interact with the opendatahub. To execute them the writer and reader module of the opendatahub must be up and running, also Meteotrentino service endpoints must be reachable;


 - Unit Tests: this tests perform checks to the logic that converts data coming from the source service into the DTOs used by the opendatahub. To execute this tests it is not necessary that an instance of the opendatahub is running. Logic is checked against a set of predefined test data.


## Build and deploy:

Java JDK version 7+ is required.

Go to the folder where the datacollector is saved: `bdp-commons/data-collectors/dc-meteo-tn`

Use Maven build system to package the war: `mvn clean package`

A war file is located under the target folder: `target/dc-meteo-tn.war`
This file can be deployed in the servlet container of your choice (for example Tomcat 8).



## Packages and important classes:

package **it.bz.idm.bdp.dcmeteotn**

This package contains the Pusher, the Retiever, the Scheduler and Converter classes:
 - MeteoTnDataRetriever class: provides functionalities to get data from the external service;
 - MeteoTnDataConverter class: provides methods to convert data provided by the external service in a more practical internal representation;
 - MeteoTnDataPusher class: provides functionality to push data to the Open Data Hub platform;
 - MeteoTnJobScheduler class: provides functionality to schedule retrieve and push operations, for Stations, DataTypes and Measurements.

package **it.bz.idm.bdp.dcmeteotn.dto**  
This package contains the following DTOs:
 - MeteoTnDto, MeteoTnMeasurementDto, MeteoTnMeasurementListDto classes: used to store the data after conversion, the data are sent to the OpenDataHub as MeteoStationDto.

