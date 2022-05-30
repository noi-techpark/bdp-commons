# On-Street Parking

[//]: # ([![CI on-street-parking]&#40;https://github.com/noi-techpark/bdp-commons/actions/workflows/on-street-parking.yml/badge.svg&#41;]&#40;https://github.com/noi-techpark/bdp-commons/actions/workflows/on-street-parking.yml&#41;)

Application which connects to systems mqqt server to receive all parking sensor messages and sends them
to the [Open Data Hub](https://opendatahub.bz.it).

**Table of contents**

- [A22 Events](#a22-events)
	- [Getting started](#getting-started)
		- [Prerequisites](#prerequisites)
		- [Source code](#source-code)
		- [Build](#build)
	- [Running tests](#running-tests)
	- [Local execution with an Embedded Tomcat](#local-execution-with-an-embedded-tomcat)
	- [Deployment](#deployment)
	- [Mapping of Tipi and SottoTipi Event](#mapping-of-tipi-and-sottotipi-event)
	- [Information](#information)
		- [Support](#support)
		- [Contributing](#contributing)
		- [Documentation](#documentation)
		- [License](#license)

## Getting started

These instructions will get you a copy of the project up and running on your
local machine for development and testing purposes.

### Prerequisites

To build the project, the following prerequisites must be met:

- Java JDK 1.8 or higher (e.g. [OpenJDK](https://openjdk.java.net/))
- [Maven](https://maven.apache.org/) 3.x
- The [Open Data Hub Mobility Writer](https://github.com/noi-techpark/bdp-core)
  (aka Big Data Platform Core) installed

### Source code

Get a copy of the repository:

```bash
git clone git clone https://github.com/noi-techpark/bdp-commons
```

Change directory:

```bash
cd bdp-commons/data-collectors/on-street-parking
```

### Build

Build the project:

```bash
mvn clean package
```

## Running tests

The unit tests can be executed with the following command:

```bash
mvn clean test
```

## Local execution with an Embedded Tomcat

You can set the parameters directly as environmental variables, as follows:

```bash
mvn tomcat:run \
  -DMQQT_CLIENT_USERNAME=... \
  -DMQQT_CLIENT_PASSWORD=... \
  -DMQQT_CLIENT_ID=... \
  -DODH_WRITER_SECRET=...
```

...or, set them inside the relevant `.properties` files directly (see [Deployment](#deployment)), and run:

```bash
mvn tomcat:run
```

Credentials needed? See
[here](https://github.com/noi-techpark/odh-docs/wiki/Contributor-Guidelines:-Credentials).

## Deployment

This is a maven project and will produce a war that can be deployed in any j2ee
container like tomcat or jetty.

Steps:

* change the file
  `src/main/resources/mqttclient.properties`. Set the server uri, username, the password, the clientId and the topic to
  subscribe of your mqqt connection

```ini
mqttclient.serverURI=ssl://mqtt.kamote.io:8883
mqttclient.username=
mqttclient.password=
mqttclient.clientId=
mqttclient.topic=parking_space_occupancy/#
```

* optionally change the origin, the station type, the origin, the maximum time without message until a station is set as
  inactive(in seconds) in the file
  `src/main/resources/onstreetparking.properties`.

```ini
origin=systems
stationtype=ParkingSensor
period=300
maxTimeSinceLastMeasurementSeconds=604800
```

The property `maxTimeSinceLastMeasurementSeconds` defines the maximum number of seconds that a sensor can remain
activated without a new message before it is classified as inactive.

* configure the `log4j.properties` file as desidered

* create the war executing the following command

```bash
mvn clean package
```

* deploy the war to a j2ee container like tomcat or jetty

## Information

### Support

For support, please contact [help@opendatahub.bz.it](mailto:help@opendatahub.bz.it).

### Contributing

If you'd like to contribute, please follow our [Getting
Started](https://github.com/noi-techpark/odh-docs/wiki/Contributor-Guidelines:-Getting-started)
instructions.

### Documentation

More documentation can be found at
[https://docs.opendatahub.bz.it](https://docs.opendatahub.bz.it).

### License

The code in this project is licensed under the GNU AFFERO GENERAL PUBLIC LICENSE
Version 3 license. See the [LICENSE](../../LICENSE) file for more information.
