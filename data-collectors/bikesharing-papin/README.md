<!--
SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>

SPDX-License-Identifier: CC0-1.0
-->

Bikesharing Papin Data Collector
=========================

Datacollector providing bikesharing realtimedata to opendatahub.

Data is provided by Papin Sport.

[![CI bikesharing-papin](https://github.com/noi-techpark/bdp-commons/actions/workflows/ci-bikesharing-papin.yml/badge.svg)](https://github.com/noi-techpark/bdp-commons/actions/workflows/ci-bikesharing-papin.yml)

## Configuration:
  - See `src/main/resources/META-INF/spring/applicationContext.xml`

    here you can configure the scheduler for the tasks "pushData". The scheduler is implemented in class it.bz.idm.bdp.dcbikesharingpapin.BikesharingPapinJobScheduler and uses Spring framework provided services. PushData is called every day. 

  - See `src/main/resources/META-INF/spring/application.properties`

    please refer to the comments provided in the file, here you can configure the parameters for:
    - the service endpoints (credentials must be provided setting the parameter `app_auth_token`);
    - other parameters like `app.origin` (origin of the data), `app.period` (period parameter given in the SimpleRecordDto);


  - Logsystem: `src/main/resources/log4j.properties` (Make sure the log-files are writable)


## Tests:

In the Datacollector there are two types of test cases:

 - Integration Tests: this tests interact with the opendatahub. To execute them the writer and reader module of the opendatahub must be up and running, also service endpoints must be reachable;


 - Unit Tests: this tests perform checks to the logic that converts data coming from the source service into the DTOs used by the opendatahub. To execute this tests it is not necessary that an instance of the opendatahub is running. Logic is checked against a set of predefined test data stored if folder `src/test/resources/test_data`.


## Build and deploy:

Java JDK version 7+ is required.

Go to the folder where the datacollector is saved: `bdp-commons/data-collectors/bikesharing-papin`

Use Maven build system to package the war: `mvn clean package`

A war file is located under the target folder: `target/dc-bikesharing-papin.war`
This file can be deployed in the servlet container of your choice (for example Tomcat 8).

To set up the Open Data Hub environment containing Tomcat, PostgreSQL and all required database objects see the detailed guide provided at [https://github.com/noi-techpark/bdp-core](https://github.com/noi-techpark/bdp-core) and run the setup script.
