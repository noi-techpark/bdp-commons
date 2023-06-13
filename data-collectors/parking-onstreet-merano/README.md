<!--
SPDX-FileCopyrightText: NOI Techpark <digital@noi.com>

SPDX-License-Identifier: CC0-1.0
-->

# Parking On-Street Merano

[//]: # ([![CI on-street-parking]&#40;https://github.com/noi-techpark/bdp-commons/actions/workflows/on-street-parking.yml/badge.svg&#41;]&#40;https://github.com/noi-techpark/bdp-commons/actions/workflows/on-street-parking.yml&#41;)

Application which connects to systems mqqt server to receive all parking sensor messages and sends them
to the [Open Data Hub](https://opendatahub.com).

**Table of contents**

- [Parking On-Street Merano](#parking-on-street-merano)
	- [Getting started](#getting-started)
		- [Prerequisites](#prerequisites)
		- [Source code](#source-code)
		- [Build](#build)
	- [Running tests](#running-tests)
	- [Local execution with an Embedded Tomcat](#local-execution-with-an-embedded-tomcat)
	- [Local execution with Docker](#local-execution-with-docker)
	- [VSCode: Start a debug session with Docker](#vscode-start-a-debug-session-with-docker)
	- [Deployment](#deployment)
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
cd bdp-commons/data-collectors/parking-onstreet-merano
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

## Local execution with Docker

- Inside the corresponding data collector folder, copy `.env.example` to `.env`
  and configure it
- Run `docker-compose up -d`
- You can follow the output with `docker-compose logs -f`

Credentials needed? See
[here](https://github.com/noi-techpark/odh-docs/wiki/Contributor-Guidelines:-Credentials).

## VSCode: Start a debug session with Docker

Copy this file to `.vsode/launch.json`:
```json
{
    "version": "0.2.0",
    "configurations": [
		{
			"type": "java",
			"name": "Attach",
			"request": "attach",
			"hostName": "0.0.0.0",
			"port": "9000",
			"justMyCode": false
		}
    ]
}
```

Run `docker-compose up -d` inside the data-collector folder of your choice, and
then launch `Attach` from VSCode. You are now ready to set breakpoints and debug.

## Deployment

This is a maven project and will produce a war that can be deployed in any j2ee
container like tomcat or jetty.

Steps:

* change the file
  `src/main/resources/mqttclient.properties`. Set the server uri, username, the password, the clientId and the topic to
  subscribe of your mqtt connection

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

* create the war executing the following command

```bash
mvn clean package
```

* deploy the war to a j2ee container like tomcat or jetty

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
