<!--
SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>

SPDX-License-Identifier: CC0-1.0
-->

# Parking Forecast

[//]: <> (\[\!\[CI parking-forecast\]\(https://github.com/noi-techpark/bdp-commons/actions/workflows/ci-parking-forecast.yml/badge.svg\)\]\(https://github.com/noi-techpark/bdp-commons/actions/workflows/ci-parking-forecast.yml\))

Application which takes [parking forecast data computed by STA](https://web01.sta.bz.it/parking-forecast/readme.md) from
a web service parses it and sends it to the [Open Data Hub](https://opendatahub.bz.it). The computed forecasts of the
API are based on the data of the ODH station of the types ParkingStation and ParkingSensor, the parking station spaces
already available in the Open Data Hub.

**Table of contents**

- [Parking Forecast](#parking-forecast)
	- [Getting started](#getting-started)
		- [Prerequisites](#prerequisites)
		- [Source code](#source-code)
		- [Build](#build)
	- [Running tests](#running-tests)
	- [Local execution with an Embedded Tomcat](#local-execution-with-an-embedded-tomcat)
	- [Deployment](#deployment)
	- [Information](#information)
		- [Support](#support)
		- [Contributing](#contributing)
		- [Documentation](#documentation)
		- [License](#license)

## Getting started

These instructions will get you a copy of the project up and running on your local machine for development and testing
purposes.

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
cd bdp-commons/data-collectors/parking-forecast
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
  -DODH_WRITER_SECRET=...
```

...or, set them inside the relevant `.properties` files directly (see [Deployment](#deployment)), and run:

```bash
mvn tomcat:run
```

Credentials needed? See
[here](https://github.com/noi-techpark/odh-docs/wiki/Contributor-Guidelines:-Credentials).

## Deployment

This is a maven project and will produce a war that can be deployed in any j2ee container like tomcat or jetty.

Steps:

* optionally change the api endpoint, the origin, and the station types in the file
  `src/main/resources/parkingforecast.properties`.

```ini
connector.endpoint=https://web01.sta.bz.it/parking-forecast/result.json
origin=STA
stationtype.parkingStation=ParkingStation
stationtype.parkingSensor=ParkingSensor
```

* optionally change the data types in the file `src/main/resources/datatypes.json`.

```json
[
	{
		"key": "PARKING-FORECAST-30",
		"unit": "",
		"description": "30 minutes forecast",
		"rtype": "Forecast",
		"period": 1800,
		"property": "mean"
	},
	{
		"key": "PARKING-FORECAST-60",
		"unit": "",
		"description": "60 minutes forecast",
		"rtype": "Forecast",
		"period": 3600,
		"property": "mean"
	},
	{
		"key": "PARKING-FORECAST-90",
		"unit": "",
		"description": "90 minutes forecast",
		"rtype": "Forecast",
		"period": 5400,
		"property": "mean"
	},
	...
]
```

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

If you'd like to contribute, please follow
our [Getting Started](https://github.com/noi-techpark/odh-docs/wiki/Contributor-Guidelines:-Getting-started)
instructions.

### Documentation

More documentation can be found at
[https://docs.opendatahub.bz.it](https://docs.opendatahub.bz.it).

### License

The code in this project is licensed under the GNU AFFERO GENERAL PUBLIC LICENSE Version 3 license. See
the [LICENSE](../../LICENSE) file for more information.
