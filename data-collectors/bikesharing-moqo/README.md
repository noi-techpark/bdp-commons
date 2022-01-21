Bikesharing MOQO datacollector
=========================

Datacollector providing bikesharing realtimedata to opendatahub.

Data is provided by Bike Sharing Merano trough the MOQO Platform (https://portal.moqo.de).

[![CI bikesharing-moqo](https://github.com/noi-techpark/bdp-commons/actions/workflows/ci-bikesharing-moqo.yml/badge.svg)](https://github.com/noi-techpark/bdp-commons/actions/workflows/ci-bikesharing-moqo.yml)

## Analysis:

For details regarding mapping from data provided by the service and the data used by the opendatahub, please see this analysis document:
[191023_SpecificheIntegrationeODH.pdf](documentation/191023_SpecificheIntegrationeODH.pdf)

Specification of the api is described in detail in this documents:
[API_merano.zip](documentation/API_merano.zip)

Bike Sharing Merano provides one service at endpoint https://portal.moqo.de/api:
  - `/cars`: provides anagrafic data for available stations. The data is paginated, to obtain the data of all bikes it is necessary to make different calls, see file [test_data_fetch_page_2.json](src/test/resources/test_data/test_data_fetch_page_2.json) for an example;
  - `/cars/{car_id}/availability`: provides availability slots for a particular bike, see file [test_data_fetch_availab_825813160.json](src/test/resources/test_data/test_data_fetch_availab_825813160.json) for an example;

To access the service the required credentials must be provided (Authorization token and X-Selected-Team parameter).
Here are two examples of calls using the curl command:
  - `curl -H "Authorization: Bearer STRING_TOKEN" -H "X-Selected-Team: STRING_TEAM" -v https://portal.moqo.de/api/cars?include_unavailable_cars=true&page=2`
  - `curl -H "Authorization: Bearer STRING_TOKEN" -H "X-Selected-Team: STRING_TEAM" -v https://portal.moqo.de/api/cars/1359037427/availability`

Each bike is stored in the Open Data Hub as a StationDto, with its anagraphical data. The location of the bike can be one of the Parking Locations provided by the sharing service. The location of the Station (bike) can change during the day. The measurements for each bike are based on the following data types:
  - `availability`: a boolean that indicates if the bike is currently free for usage;
  - `future-availability`: a boolean that indicates if the availability will change in next period (the period is defined setting the parameter "app.station.future_availability.minutes");
  - `in-maitenance`: a boolean that indicates if the bike is currently out of service.

## Configuration:
  - See `src/main/resources/META-INF/spring/applicationContext.xml`

    here you can configure the scheduler for the tasks "pushData" and "pushDataTypes". The scheduler is implemented in class it.bz.idm.bdp.dcbikesharingmoqo.BikesharingMoqoJobScheduler and uses Spring framework provided services. PushData is called every 5 minutes. 

  - See `src/main/resources/META-INF/spring/application.properties`

    please refer to the comments provided in the file, here you can configure the parameters for:
    - the service endpoints (credentials must be provided setting the two parameters `app.auth.token` and `app.auth.selectedTeam`);
    - request params required by the services;
    - other parameters like `app.origin` (origin of the data), `app.period` (period parameter given in the SimpleRecordDto), `app.station.future_availability.minutes` (used to calculate future-availability);


  - Logsystem: `src/main/resources/log4j.properties` (Make sure the log-files are writable)


## Tests:

In the Datacollector there are two types of test cases:

 - Integration Tests: this tests interact with the opendatahub. To execute them the writer and reader module of the opendatahub must be up and running, also MOQO service endpoints must be reachable;


 - Unit Tests: this tests perform checks to the logic that converts data coming from the source service into the DTOs used by the opendatahub. To execute this tests it is not necessary that an instance of the opendatahub is running. Logic is checked against a set of predefined test data stored if folder `src/test/resources/test_data`.


## Build and deploy:

Java JDK version 7+ is required.

Go to the folder where the datacollector is saved: `bdp-commons/data-collectors/bikesharing-moqo`

Use Maven build system to package the war: `mvn clean package`

A war file is located under the target folder: `target/dc-bikesharing-moqo.war`
This file can be deployed in the servlet container of your choice (for example Tomcat 8).

To set up the Open Data Hub environment containing Tomcat, PostgreSQL and all required database objects see the detailed guide provided at [https://github.com/noi-techpark/bdp-core](https://github.com/noi-techpark/bdp-core) and run the setup script.


## Packages and important classes:

package **it.bz.idm.bdp.dcbikesharingmoqo**

This package contains the Pusher, the Retriever, the Scheduler and Converter classes:
 - BikesharingMoqoDataRetriever class: provides functionalities to get data from the external service;
 - BikesharingMoqoDataConverter class: provides methods to convert data provided by the external service in a more practical internal representation;
 - BikesharingMoqoDataPusher class: provides functionality to push data to the Open Data Hub platform;
 - BikesharingMoqoJobScheduler class: provides functionality to schedule retrieve and push operations, for Stations, DataTypes and Measurements.

package **it.bz.idm.bdp.dcbikesharingmoqo.dto**

This package contains the following DTOs:
 - BikesharingMoqoDto: used to store the data after conversion, it is used to collect the full list of bikes;
 - BikesharingMoqoPageDto: used to store data for each call to the external service (data can be accessed at pages of 10 bikes per call);
 - BikeDto: used to store the data for one Bike, each bike is sent to the OpenDataHub as StationDto;
 - LocationDto, AvailabilityDto, PaginationDto: convenient beans to store partial information of the bike.


## Implementation details:

Mainly for each service an HTTP call is performed. Returned JSON string is converted to Java objects using the org.json library. We do not use Jackson because the service is based on "cars" and returns a lot information that is not relevant for a "bike". We need to get only few data from the JSON string provided by the service and org.json library is much more faster in this scenario.

DataType list is fixed, as explained in the analysis section.

For station and measurement fetch / convert / sync process, some explanation is necessary. The process consists of three steps:
  1. get all station data calling the service /cars in a loop;

  1.1. get n-th page of station data (service `/cars?page=n`) until returned parameter `next_page` is null;

  2. with this information, the system performs a loop for all stations. 
     For each station the system fetches the data provided by the service `/cars/{car_id}/availability`. 
     This service returns a list of availability slots, telling if the bike is booked now or in the future. 
     With this information the system calculates the values of current availability and future availability.
     After retrieving all available data for a particular station, data is sent to the Open data Hub. 




