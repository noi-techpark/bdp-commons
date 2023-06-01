<!--
SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>

SPDX-License-Identifier: CC0-1.0
-->

On Street Parking BZ datacollector
=========================

Datacollector providing bikesharing realtimedata to opendatahub. 
The parking device is installed on the bike. This device permit locking and unlocking of the bike in specific parking areas.

Data is provided by the Comune di Bolzano trough the platform Axians.

[![CI onstreet-parking-bz](https://github.com/noi-techpark/bdp-commons/actions/workflows/ci-onstreet-parking-bz.yml/badge.svg)](https://github.com/noi-techpark/bdp-commons/actions/workflows/ci-onstreet-parking-bz.yml)

## Analysis:

For details regarding mapping from data provided by the service and the data used by the opendatahub, please see this analysis document:
[200303_SpecificheIntegrazioneODH_NOI_v2.pdf](documentation/200303_SpecificheIntegrazioneODH_NOI_v2.pdf)

As explained in the analysis document, there are two different data sources managed by the datacollector:
  - the anagraphic data of the sensors installed on each bike: the list of sensors is provided trough a google spreadsheet, each sensor is stored in the Open Data Hub as a StationDto of type ParkingSensor. The data collector reads periodically the spreadsheet and updates the Stations in the Open Data Hub;
  - the measurements coming from the sensors: in this case the Axians platform sends messages with the availability status of the bike. The data collector exposes a REST service that is invoked by the Axians platform. The method responds to GET  and POST calls and consumes a JSON message in the payload. The method is called each time a sensor changes status, therefore it is not possible to define a fixed period.


## Configuration:
  - See `src/main/resources/META-INF/spring/applicationContext.xml`

    here you can configure the scheduler for the tasks "pushStations" and "pushDataTypes". The scheduler is implemented in class it.bz.idm.bdp.dconstreetparkingbz.OnstreetParkingBzJobScheduler and uses Spring framework provided services. As exposed before, data is pushed by the Axians platform, there is non need to schedule a pushData job.

  - See `src/main/resources/META-INF/spring/application.properties`

    please refer to the comments provided in the file, here you can configure the parameters for:
    - the parameters to access the google spreadsheet, in particular the spreadsheetId and the folder where the credentials are stored;
    - the names of the spreadsheet columns and how to map them to StationDto attributes;
    - other parameters like `app.origin` (origin of the data), `app.period` (period parameter given in the SimpleRecordDto, this is set to 1 since the measurements can arrive in any time);

  - See `src/main/resources/META-INF/spring/client_secret.json`
    To access the spreadsheet is also necessary to provide credentials of a user granted to read the spreadsheet. The attributes `client_id` and `client_secret` must be set. A useful guide explaining how to configure access to a google spreadsheet using java and how to obtain the values to set in client_secret.json file can be found here: [https://developers.google.com/sheets/api/quickstart/java](https://developers.google.com/sheets/api/quickstart/java)

  - Logsystem: `src/main/resources/log4j.properties` (Make sure the log-files are writable)


## Tests:

In the Datacollector there are two types of test cases:

 - Integration Tests: this tests interact with the opendatahub. To execute them the writer and reader module of the opendatahub must be up and running, also the google spreadsheet must be reachable. The test OnstreetParkingBzDataPusherIT#testPushController tests the Controller class, simulating an incoming call from the Axians platform;


 - Unit Tests: this tests perform checks to the logic that converts data coming from the source service into the DTOs used by the opendatahub. To execute this tests it is not necessary that an instance of the opendatahub is running. Logic is checked against a set of predefined test data stored if folder `src/test/resources/test_data`.


## Build and deploy:

Java JDK version 7+ is required.

Go to the folder where the datacollector is saved: `bdp-commons/data-collectors/onstreet-parking-bz`

Use Maven build system to package the war: `mvn clean package`

A war file is located under the target folder: `target/dc-onstreet-parking-bz.war`
This file can be deployed in the servlet container of your choice (for example Tomcat 8).

To set up the Open Data Hub environment containing Tomcat, PostgreSQL and all required database objects see the detailed guide provided at [https://github.com/noi-techpark/bdp-core](https://github.com/noi-techpark/bdp-core) and run the setup script.


## Packages and important classes:

package **it.bz.idm.bdp.dconstreetparkingbz**

This package contains the Pusher, the Retriever, the Scheduler, the Controller and Converter classes:
 - OnstreetParkingBzDataRetriever class: provides functionalities to coordinate reading and converting data coming from the google spreadsheet;
 - OnstreetParkingBzSpreadsheetReader class: provides functionalities to get data from the google spreadsheet;
 - OnstreetParkingBzController class: provides functionalities to expose a REST method that collects measurement information;
 - OnstreetParkingBzDataConverter class: provides methods to convert data provided by the external service in a more practical internal representation;
 - OnstreetParkingBzDataPusher class: provides functionality to push data to the Open Data Hub platform;
 - OnstreetParkingBzJobScheduler class: provides functionality to schedule retrieve and push operations, for Stations and DataTypes.

package **it.bz.idm.bdp.dconstreetparkingbz.dto**

This package contains the following DTOs:
 - OnstreetParkingBzSensorDto: used to store the data after conversion, it is used to collect the availability status of a ParkingSensor;


## Implementation details:

DataType list is fixed, as explained in the analysis section. There is only the "occupied" data type that indicates the status of the parking sensor.

For the anagraphic data of the stations, the datasource is the google spreadsheet. The datacollector reads the content of the spreadsheet using the Google APIs fo Java. 
Each line of the spreadsheet is converted to a StationDto, mapping each column value to an attribute of the StationDto. 
The classes involved are OnstreetParkingBzJobScheduler, OnstreetParkingBzDataRetriever that reads content from the spreadsheet trough OnstreetParkingBzSpreadsheetReader class, 
OnstreetParkingBzDataConverter that converts a spreadsheet row in a StationDto, OnstreetParkingBzDataPusher that sends data to the Open data Hub.


For the measurements, the class OnstreetParkingBzController exposes a REST method. This class is a Spring Controller, the method exposes the method pushRecords that consumes a JSON message,
converts it to a OnstreetParkingBzSensorDto object, converts it to a DataMapDto<RecordDtoImpl> object and sends date to the Open Data Hub.


