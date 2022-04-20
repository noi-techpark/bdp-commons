# Traffic events + roadworks Province BZ

Application which takes traffic events and roadworks data from a web service
provided by Province BZ, parses it and sends it to the opendatahub.

[![CI traffic-event-roadworks-bz](https://github.com/noi-techpark/bdp-commons/actions/workflows/ci-traffic-event-roadworks-bz.yml/badge.svg)](https://github.com/noi-techpark/bdp-commons/actions/workflows/ci-traffic-event-roadworks-bz.yml)

**Table of contents**
- [Traffic events + roadworks Province BZ](#traffic-events--roadworks-province-bz)
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

These instructions will get you a copy of the project up and running on your
local machine for development and testing purposes.

### Prerequisites

To build the project, the following prerequisites must be met:

- Java JDK 1.8 or higher (e.g. [OpenJDK](https://openjdk.java.net/))
- [Maven](https://maven.apache.org/) 3.x
- A postgres database with the schema intimev2 and elaboration (bdp) already installed

### Source code

Get a copy of the repository:

```bash
git clone https://github.com/noi-techpark/bdp-commons
```

Change directory:

```bash
cd bdp-commons/data-collectors/traffic-event-roadworks-bz
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

* optionally change the origin, the provenance, the integreen namespace and the uuid namespace in the file
  src/main/resources/trafficevents_roadworks.properties

```
app.origin=PROVINCE_BZ
app.provenance.name=this-data-collectors-name
app.provenance.version=this-data-collectors-version
integreenTypology=Events
uuidNamescpace=61c5e315-bf03-407d-a029-4032be31273c
```

* create the war executing the following command

```
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
Version 3 license. See the [LICENSE](LICENSE) file for more information.
