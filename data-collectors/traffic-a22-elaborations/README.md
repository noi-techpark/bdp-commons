<!--
SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>

SPDX-License-Identifier: CC0-1.0
-->

# A22 Traffic elaborations

[![CI traffic-a22-elaborations](https://github.com/noi-techpark/bdp-commons/actions/workflows/ci-traffic-a22-elaborations.yml/badge.svg)](https://github.com/noi-techpark/bdp-commons/actions/workflows/ci-traffic-a22-elaborations.yml)

Application which takes vehicular traffic data from the big data platform,
collected by bluetoothboxes around the city of Bolzano, and makes different
elaborations and saving them back to the bdp again.

**Table of contents**
- [A22 Traffic elaborations](#a22-traffic-elaborations)
	- [Getting started](#getting-started)
		- [Prerequisites](#prerequisites)
		- [Source code](#source-code)
		- [Build](#build)
	- [Running tests](#running-tests)
	- [Deployment](#deployment)
	- [Information](#information)
		- [Support](#support)
		- [Contributing](#contributing)
		- [Documentation](#documentation)
		- [License](#license)

## Getting started

These instructions will get you a copy of the project up and running
on your local machine for development and testing purposes.

### Prerequisites

To build the project, the following prerequisites must be met:

- Java JDK 1.8 or higher (e.g. [OpenJDK](https://openjdk.java.net/))
- [Maven](https://maven.apache.org/) 3.x
- The [Open Data Hub Mobility Writer](https://github.com/noi-techpark/bdp-core)
  (aka Big Data Platform Core) installed, or alternative a Postgres database
  with the schema `intimev2` and `elaboration` (bdp) already installed
- [Credentials](https://github.com/noi-techpark/odh-docs/wiki/Contributor-Guidelines:-Credentials)
  - to get the ODH writer token

### Source code

Get a copy of the repository:

```bash
git clone https://github.com/noi-techpark/bdp-commons
```

Change directory:

```bash
cd bdp-commons/data-collectors/traffic-a22-elaborations
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

## Deployment

This is a maven project and will produce a war that can be deployed in any j2ee container like tomcat or jetty.

Steps:

* change the file `src/main/resources/it/bz/noi/a22elaborations/db.properties`.
  set the postgres variables to connect to the database

```ini
USER=
PASSWORD=
HOST=localhost
PORT=5432
DBNAME=
```

* optionally change frequence of elaborations in the file `src/main/resources/it/bz/noi/a22scheduler/cron.properties`.

```ini
schedule=0 0/10 * * * ?
```

* optionally change the window and the step of elaborations in the file `src/main/resources/it/bz/noi/a22elaborations/elaborations.properties` (numbers are milliseconds).

```ini
windowLength=600000
step=600000
```

* configure the `logback.xml` file as desidered

* create the war executing the following command

```
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
