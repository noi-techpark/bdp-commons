<!--
SPDX-FileCopyrightText: NOI Techpark <digital@noi.com>

SPDX-License-Identifier: CC0-1.0
-->

# Bikesharing BZ Data Collector

[![CI bikesharing-bz](https://github.com/noi-techpark/bdp-commons/actions/workflows/ci-bikesharing-bz.yml/badge.svg)](https://github.com/noi-techpark/bdp-commons/actions/workflows/ci-bikesharing-bz.yml)

Datacollector providing bike sharing realtime data to the Open Data Hub.
Data is provided by Ecospazio.

**Table Of Contents**
- [Bikesharing BZ Data Collector](#bikesharing-bz-data-collector)
	- [Analysis](#analysis)
	- [Configuration](#configuration)
	- [Tests](#tests)
	- [Build and deploy](#build-and-deploy)
	- [Packages and important classes](#packages-and-important-classes)
	- [Information](#information)
		- [Support](#support)
		- [Contributing](#contributing)
		- [Documentation](#documentation)
		- [License](#license)


## Analysis

For details regarding mapping from data provided by the service and the data used by the opendatahub, please see this analysis document:
[documentation/200115_SpecificheIntegrazioneODH.pdf](documentation/200115_SpecificheIntegrazioneODH.pdf)

## Configuration
  - See `src/main/resources/META-INF/spring/applicationContext.xml`

    here you can configure the scheduler for the tasks "pushData" and
    "pushDataTypes". The scheduler is implemented in class
    it.bz.idm.bdp.dcbikesharingbz.BikesharingBzJobScheduler and uses Spring
    framework provided services. PushData is called every 5 minutes.

  - See `src/main/resources/META-INF/spring/application.properties`

    please refer to the comments provided in the file, here you can configure the parameters for:
    - the service endpoints (credentials must be provided setting the parameter `app.auth.token`);
    - other parameters like `app.origin` (origin of the data), `app.period` (period parameter given in the SimpleRecordDto);


## Tests

In the Datacollector there are two types of test cases:

 - Integration Tests: this tests interact with the opendatahub. To execute them
   the writer and reader module of the opendatahub must be up and running, also
   ECOSPAZIO service endpoints must be reachable;

 - Unit Tests: this tests perform checks to the logic that converts data coming
   from the source service into the DTOs used by the opendatahub. To execute
   this tests it is not necessary that an instance of the opendatahub is
   running. Logic is checked against a set of predefined test data stored if
   folder `src/test/resources/test_data`.


## Build and deploy

Java JDK version 7+ is required.

Go to the folder where the datacollector is saved:
`bdp-commons/data-collectors/bikesharing-bz`

Use Maven build system to package the war: `mvn clean package`

A war file is located under the target folder: `target/dc-bikesharing-bz.war`
This file can be deployed in the servlet container of your choice (for example
Tomcat 8).

To set up the Open Data Hub environment containing Tomcat, PostgreSQL and all
required database objects see the detailed guide provided at
[https://github.com/noi-techpark/bdp-core](https://github.com/noi-techpark/bdp-core)
and run the setup script.


## Packages and important classes

package **it.bz.idm.bdp.dcbikesharingbz**

This package contains the Pusher, the Retriever, the Scheduler and Converter classes:
 - BikesharingBzDataRetriever class: provides functionalities to get data from the external service;
 - BikesharingBzDataConverter class: provides methods to convert data provided by the external service in a more practical internal representation;
 - BikesharingBzDataPusher class: provides functionality to push data to the Open Data Hub platform;
 - BikesharingBzJobScheduler class: provides functionality to schedule retrieve and push operations, for Stations, DataTypes and Measurements.

package **it.bz.idm.bdp.dcbikesharingbz.dto**

## Information

### Support

For support, please contact [help@opendatahub.com](mailto:help@opendatahub.com).

### Contributing

If you'd like to contribute, please follow our [Getting
Started](https://github.com/noi-techpark/odh-docs/wiki/Contributor-Guidelines:-Getting-started)
instructions.

### Documentation

More documentation can be found at
[https://docs.opendatahub.com](https://docs.opendatahub.com).

### License

The code in this project is licensed under the GNU AFFERO GENERAL PUBLIC LICENSE
Version 3 license. See the [LICENSE](../../LICENSE) file for more information.
