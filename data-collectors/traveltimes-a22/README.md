# A22 Travel times


Application which takes A22 travel time data from a web service parses it and sends it to the opendatahub.

## Table of contents

- [Gettings started](#getting-started)
- [Running tests](#running-tests)
- [Deployment](#deployment)
- [Information](#information)

## Getting started

These instructions will get you a copy of the project up and running
on your local machine for development and testing purposes.

### Prerequisites

To build the project, the following prerequisites must be met:

- Java JDK 1.8 or higher (e.g. [OpenJDK](https://openjdk.java.net/))
- [Maven](https://maven.apache.org/) 3.x
- A postgres database with the schema intimev2 and elaboration (bdp) already installed

### Source code

Get a copy of the repository:

```bash
git clone git clone https://github.com/noi-techpark/bdp-commons
```

Change directory:

```bash
cd bdp-commons/data-collectors/traveltimes-a22
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

* change the file src/main/resources/it/bz/noi/a22/traveltimes/a22connector.properties. set the url, the username and the 
  password to connect to the A22 API to query historic values of travel time (or configure it within a CI tool)
  
```
url=
user=
password=
```

* optionally change frequence of elaborations in the file src/main/resources/it/bz/noi/a22/traveltimes/cron.properties.
  (or configure it within a CI tool)
  
```
schedule=0 0/10 * * * ?
```

* optionally change the origin, the provenance, the station type, the last timestamp and the scan window (in seconds) in the file 
src/main/resources/it/bz/noi/a22/traveltimes/a22traveltimes.properties. (or configure it within a CI tool)
  
```
origin=A22
provenance.name=dc-
provenance.version=1.0.0-SNAPSHOT
stationtype=RWISstation
lastTimestamp=2017-01-01 00:00:00
scanWindowSeconds=604800
```

* optionally change the data types in the file src/main/resources/it/bz/noi/a22/traveltimes/a22traveltimesdatatypes.propertiess.
  (or configure it within a CI tool)
  
```
a22traveltimes.datatype.lds.key=lds
a22traveltimes.datatype.lds.unit=
a22traveltimes.datatype.lds.description=Livello di servizio
a22traveltimes.datatype.lds.rtype=Mean
...
```

* configure the log4j.properties file as desidered

* create the war executing the following command

```
mvn clean package
```

* deploy the war to a j2ee container like tomcat or jetty


## Information

### Support

For support, please contact [info@opendatahub.bz.it](mailto:info@opendatahub.bz.it).

### Contributing

If you'd like to contribute, please follow the following instructions:

- Fork the repository.

- Checkout a topic branch from the `development` branch.

- Make sure the tests are passing.

- Create a pull request against the `development` branch.

### Documentation

More documentation can be found at [https://opendatahub.readthedocs.io/en/latest/index.html](https://opendatahub.readthedocs.io/en/latest/index.html).

### License

The code in this project is licensed under the GNU AFFERO GENERAL PUBLIC LICENSE Version 3 license. See the [LICENSE.md](LICENSE.md) file for more information.