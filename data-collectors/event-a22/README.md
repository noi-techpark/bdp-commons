# A22 Events

Application which takes A22 event data from a web service parses it and sends it
to the [Open Data Hub](https://opendatahub.bz.it).

**Table of contents**
- [A22 Events](#a22-events)
  - [Getting started](#getting-started)
    - [Prerequisites](#prerequisites)
    - [Source code](#source-code)
    - [Build](#build)
  - [Running tests](#running-tests)
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
cd bdp-commons/data-collectors/events-a22
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

This is a maven project and will produce a war that can be deployed in any j2ee
container like tomcat or jetty.

Steps:

* change the file
  `src/main/resources/it/bz/noi/a22/events/a22connector.properties`. set the url,
  the username and the password to connect to the A22 API (or configure it
  within a CI tool)

```
url=
user=
password=
```

* optionally change the origin, the provenance, the station type, the last timestamp and the scan window (in seconds) in
  the file `src/main/resources/it/bz/noi/a22/events/a22events.properties`. (or configure it within a CI tool)

```
origin=A22
integreenTypology=Events
categoryPrefix=A22
uuidNamescpace=8b1a7848-c436-44e5-9123-e221496a7769
lastTimestamp=2018-01-01 00:00:00
scanWindowSeconds=2592000
```

* optionally change the metadata values mapping in the file
  src/main/resources/it/bz/noi/a22/events/a22eventsmetadatamapping.properties.
  (or configure it within a CI tool)

```
a22_events.metadata.iddirezione.1=Sud
a22_events.metadata.iddirezione.2=Nord
a22_events.metadata.iddirezione.3=Entrambe
a22_events.metadata.iddirezione.*=Non definito
...
```

* configure the log4j.properties file as desidered

* create the war executing the following command

```
mvn clean package
```

* deploy the war to a j2ee container like tomcat or jetty

## Mapping of Tipi and SottoTipi Event

The mapping of the events Tipi are saved in a properties file which is located
in `src/main/resources/it/bz/noi/a22/events/a22eventsmetadatamapping.properties`.

Each property is in the following form:
```
a22_events.metadata.idtipoevento.<idtipoevento>=<description>
```

The mapping of The Events SottoTipi is saved in a csv file witch is located in
`src/main/resources/it/bz/noi/a22/events/SottotipiEventi.csv`. The file has to
contain the headers `IdSottotipo` and `Descrizione`.

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
