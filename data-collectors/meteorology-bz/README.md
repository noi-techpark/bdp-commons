Meteorology BZ datacollector
=========================

Datacollector which takes data regarding meteorology in the Province of Bolzano, parses it and sends it to the opendatahub.
Data are provided by Open Data Alto Adige / Suedtirol (in italian language: "http://dati.retecivica.bz.it/it/dataset/misure-meteo-e-idrografiche", in german language: "http://dati.retecivica.bz.it/de/dataset/misure-meteo-e-idrografiche").


## Analysis:

For details regarding mapping from data provided by the service and the data used by the opendatahub, please see this analysis document:
[190429_SpecificheIntegrazione_NOI.pdf](documentation/190429_SpecificheIntegrazione_NOI.pdf)

Open Data Alto Adige provides three services at endpoint http://dati.retecivica.bz.it/services/meteo/v1:
  - `/stations`: provides anagrafic data for available stations;
  - `/sensors`: provides last measurements for all sensors and all stations;
  - `/timeseries`: provides a list of measurements given the station code, the data type code, the interval (from-date, to-date).

## Configuration:
  - See `src/main/resources/META-INF/spring/applicationContext.xml`

    here you can configure the scheduler for the tasks "pushData", "pushDataTypes" and "pushStations". The scheduler is implemented in class it.bz.idm.bdp.dcmeteorologybz.MeteorologyBzJobScheduler.java and uses Spring framework provided services. PushData is called every 10 minutes. 

  - See `src/main/resources/META-INF/spring/application.properties`

    please refer to the comments provided in the file, here you can configure the parameters for:
    - the service endpoints (no credentials are required);
    - request params required by the services (timeseries needs four different params);
    - Station constants, to fill data not provided by the service;


  - Logsystem: `src/main/resources/log4j.properties` (Make sure the log-files are writable)


## Tests:

In the Datacollector there are two types of test cases:

 - Integration Tests: this tests interact with the opendatahub. To execute them the writer and reader module of the opendatahub must be up and running, also Open Data Alto Adige service endpoints must be reachable;


 - Unit Tests: this tests perform checks to the logic that converts data coming from the source service into the DTOs used by the opendatahub. To execute this tests it is not necessary that an instance of the opendatahub is running. Logic is checked against a set of predefined test data stored if folder `src/test/resources/test_data`.


## Build and deploy:

Java JDK version 7+ is required.

Go to the folder where the datacollector is saved: `bdp-commons/data-collectors/dc-meteorology-bz`

Use Maven build system to package the war: `mvn clean package`

A war file is located under the target folder: `target/dc-meteorology-bz.war`
This file can be deployed in the servlet container of your choice (for example Tomcat 8).


## Packages and important classes:

package **it.bz.idm.bdp.dcmeteorologybz**

This package contains the Pusher, the Retiever, the Scheduler and Converter classes:
 - MeteorologyBzDataRetriever class: provides functionalities to get data from the external service;
 - MeteorologyBzDataConverter class: provides methods to convert data provided by the external service in a more practical internal representation;
 - MeteorologyBzDataPusher class: provides functionality to push data to the Open Data Hub platform;
 - MeteorologyBzJobScheduler class: provides functionality to schedule retrieve and push operations, for Stations, DataTypes and Measurements.


package **it.bz.idm.bdp.dcmeteorologybz.dto**  
This package contains the following DTOs:
 - MeteorologyBzDto class: used to store the data after conversion, the data are sent to the OpenDataHub as StationDto.
 - FeaturesDto, Feature, Crs, Geometry, Properties: used to convert JSON string provided by `/stations` service into Java objects.
 - SensorDto: used to convert JSON string provided by `/sensors` service into Java objects.
 - TimeSerieDto: used to convert JSON string provided by `/timeseries` service into Java objects.


## Implementation details:

Mainly for each service an HTTP call is performed. Returned JSON string is converted to Java objects using the com.fasterxml.jackson.databind.ObjectMapper provided by the Jackson library.

For Station and DataType the fetch, conversion and synchronization process is simple and no particular explanation is needed.

For measurement fetch / convert / sync process, some explanation is necessary. The process consists of three steps:
1. get all station data (service /stations);
2. get last measurements for all stations (service /sensors);
3. with this information, the system performs a loop for all stations. In the system fetches the data for all sensors available for the station using the `/timeseries` service. After retrieving all available data for a particular station, data is sent to the Open data Hub. To prevent loss of data, before calling the `/timeseries` service the system checks `getDateOfLastRecord` for each particular station and data type. In this way it is possible to retrieve data that for some reason are not stored in the Open data Hub (for example malfunctions in open data Hub or Open Data Alto Adige services). Missing data is retrieved at chunks of 1 day maximum (period configurable using `app.fetch_period` parameter) in order to avoid too long operations.



