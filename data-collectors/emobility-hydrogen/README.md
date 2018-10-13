Hydrogen datacollector
=========================

Datacollector which takes data from IIT, parses it and sends it to the opendatahub.

## Configuration:
  - See `src/main/resources/META-INF/spring/applicationContext.xml`

    here you can configure the scheduler for the tasks "pushData" and "pushStations"

  - See `src/main/resources/META-INF/spring/application.properties`

    here you can configure the parameters for:
    - the IIT endpoint (you must have a valid TOKEN to fetch data from the service)
    - Station and Plug constants, to fill data not provided by the service
    

  - Logsystem: `src/main/resources/log4j.properties` (Make sure the log-files are writable)

## Tests:

In the Datacollector there are two types of test cases:

 - Integration Tests: this tests interact with che opendatahub. To execute them the writer module of the opendatahub must be up and running;


 - Unit Tests: this tests perform checks to the logic that converts data coming from the source service into the DTOs used by the opendatahub. To execute this tests it is not necessary that an instance of the opendatahub is running.


## Build and deploy:

Java JDK version 7+ is required.

Go to the folder where the datacollector is saved: `bdp-commons/data-collectors/emobility-hydrogen`

Use Maven build system to package the war: `mvn clean package`

A war file is located under the target folder: `target/dc-emobility-hydrogen.war`
This file can be deployed in the servlet container of your choice (for example Tomcat 8).

