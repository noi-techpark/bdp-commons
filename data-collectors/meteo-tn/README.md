Meteo TN datacollector
=========================

Datacollector which takes data regarding meteorology in the Province of Trento, parses it and sends it to the opendatahub.
Data are provided by Meteotrentino (http://dati.meteotrentino.it/service.asmx).


## Analysis:

For details regarding mapping from data provided by the service and the data used by the opendatahub, please see this analysis document:
[181126_SpecificheIntegrazione_IDM.pdf](documentation/181126_SpecificheIntegrazione_IDM.pdf)


## Configuration:
  - See `src/main/resources/META-INF/spring/applicationContext.xml`

    here you can configure the scheduler for the tasks "pushData", "pushDataTypes" and "pushStations". The scheduler is implemented in class it.bz.idm.bdp.dcmeteotn.MeteoTnJobScheduler.java and uses Spring framework provided services.

  - See `src/main/resources/META-INF/spring/application.properties`

    please refer to the comments provided in the file, here you can configure the parameters for:
    - the service endpoint (no credentials are required);
    - Station constants, to fill data not provided by the service;
    
TO BE DONE

  - Logsystem: `src/main/resources/log4j.properties` (Make sure the log-files are writable)


## Tests:

In the Datacollector there are two types of test cases:

 - Integration Tests: this tests interact with the opendatahub. To execute them the writer module of the opendatahub must be up and running;


 - Unit Tests: this tests perform checks to the logic that converts data coming from the source service into the DTOs used by the opendatahub. To execute this tests it is not necessary that an instance of the opendatahub is running.


## Build and deploy:

Java JDK version 7+ is required.

Go to the folder where the datacollector is saved: `bdp-commons/data-collectors/dc-meteo-tn`

Use Maven build system to package the war: `mvn clean package`

A war file is located under the target folder: `target/dc-meteo-tn.war`
This file can be deployed in the servlet container of your choice (for example Tomcat 8).



## Packages and important classes:

TO BE DONE

