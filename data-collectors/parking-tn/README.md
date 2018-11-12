Parking TN datacollector
=========================

Datacollector which takes data from ..., parses it and sends it to the opendatahub.

## Analysis:



## Configuration:
  - See `src/main/resources/META-INF/spring/applicationContext.xml`

    here you can configure the scheduler for the tasks "pushData" and "pushStations"

  - See `src/main/resources/META-INF/spring/application.properties`

    here you can configure the parameters for:
    

  - Logsystem: `src/main/resources/log4j.properties` (Make sure the log-files are writable)

## Tests:

In the Datacollector there are two types of test cases:

 - Integration Tests: this tests interact with che opendatahub. To execute them the writer module of the opendatahub must be up and running;


 - Unit Tests: this tests perform checks to the logic that converts data coming from the source service into the DTOs used by the opendatahub. To execute this tests it is not necessary that an instance of the opendatahub is running.


## Build and deploy:

Java JDK version 7+ is required.


