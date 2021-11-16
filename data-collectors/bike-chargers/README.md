# E-bike / E-car chargers Ecospazio Data Collector

Application which takes data of the MENTOR project from the API provided by "ecospazio", parses it and sends it to the opendatahub.

## Table of contents

- [Gettings started](#getting-started)
- [Running tests](#running-tests)
- [Deployment](#deployment)
- [Information](#information)

## Getting started

These instructions will get you a copy of the project up and running on your local machine for development and testing
purposes.

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
cd bdp-commons/data-collectors/bike-charger
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

* change the file src/main/resources/connector.properties. set the url, the api key name and the
  api key value to connect to the "ecospazio" API (or configure it within a CI tool)

```
connector.url=
connector.apiKey.name=
connector.apiKey.value=
```

* optionally change the origin, the station types, periods (in seconds) in
  the file src/main/resources/bikecharger.properties. (or configure it within a CI tool)

```
origin=ECOSPAZIO
bikeCharger.stationtype=BIKE_CHARGER
bikeChargerBay.stationtype=BIKE_CHARGER_BAY
period=300
```

* optionally change the data types in the file src/main/resources/datatypes.properties.
  (or configure it within a CI tool)

```state={key: 'state', unit: '', description: 'state', rtype: 'Instantaneous'}
freebay={key: 'freebay', unit: '', description: 'freebay', rtype: 'Instantaneous'}
availableVehicles={key: 'availableVehicles', unit: '', description: 'availableVehicles', rtype: 'Instantaneous'}
usageState={key: 'usageState', unit: '', description: 'usageState', rtype: 'Instantaneous'}
...
```

* configure the log4j.properties file as desidered

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

More documentation can be found
at [https://opendatahub.readthedocs.io/en/latest/index.html](https://opendatahub.readthedocs.io/en/latest/index.html).

### License

The code in this project is licensed under the GNU AFFERO GENERAL PUBLIC LICENSE Version 3 license. See
the [LICENSE.md](LICENSE.md) file for more information.
