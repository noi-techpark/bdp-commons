Parking TN datacollector
=========================

Datacollector which takes data regarding parking areas in the Province of Trento, parses it and sends it to the opendatahub.
Data are provided by FBK (tn.smartcommunitylab.it).


## Analysis:

For details regarding mapping from data provided by the service and the data used by the opendatahub, please see this analysis document:
[181025_SpecificheIntegrazione_IDM.pdf](documentation/181025_SpecificheIntegrazione_IDM.pdf)


## Configuration:
  - See `src/main/resources/META-INF/spring/applicationContext.xml`

    here you can configure the scheduler for the tasks "pushData" and "pushStations". The scheduler is implemented in class it.bz.idm.bdp.dcparkingtn.ParkingTnJobScheduler.java and uses Spring framework provided services.

  - See `src/main/resources/META-INF/spring/application.properties`

    please refer to the comments provided in the file, here you can configure the parameters for:
    - the service endpoint (no credentials are required);
    - Station constants, to fill data not provided by the service;
    - the list of cities to be considered (parameters "endpoint.city.X.code" and "endpoint.city.X.code-prefix");
    

  - Logsystem: `src/main/resources/log4j.properties` (Make sure the log-files are writable)


## Tests:

In the Datacollector there are two types of test cases:

 - Integration Tests: this tests interact with the opendatahub. To execute them the writer module of the opendatahub must be up and running;


 - Unit Tests: this tests perform checks to the logic that converts data coming from the source service into the DTOs used by the opendatahub. To execute this tests it is not necessary that an instance of the opendatahub is running.


## Build and deploy:

Java JDK version 7+ is required.

Go to the folder where the datacollector is saved: `bdp-commons/data-collectors/dc-parking-tn`

Use Maven build system to package the war: `mvn clean package`

A war file is located under the target folder: `target/dc-parking-tn.war`
This file can be deployed in the servlet container of your choice (for example Tomcat 8).



## Packages and important classes:

package **it.bz.idm.bdp.dcparkingtn**

This package contains the Pusher, the Retiever, the Scheduler and Converter classes:
 - ParkingTnDataRetriever class: provides functionalities to get data from the external service;
 - ParkingTnDataConverter class: provides methods to convert data provided by the external service in a more practical internal representation;
 - ParkingTnDataPusher class: provides functionality to push data to the Open Data Hub platform;
 - ParkingTnJobScheduler class: provides functionality to schedule retrieve and push operations, for Stations and Measurements.

package **it.bz.idm.bdp.dcparkingtn.dto**  
This package contains the following DTOs:
 - ParkingAreaServiceDto and ExtraServiceDto classes: Java classes used to map the JSON string provided by the external service. Jackson ObjectMapper is used for the conversion.
 - ParkingTnDto class: used to store the data after conversion, the data are sent to the OpenDataHub as ParkingStationDto.



