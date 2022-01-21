# A22 Traffic elaborations

Application which takes vehicular traffic data from the big data platform, collected by bluetoothboxes around the city of Bolzano, 
and makes different elaborations and saving them back to the bdp again.

[![CI traffic-a22-elaborations](https://github.com/noi-techpark/bdp-commons/actions/workflows/ci-traffic-a22-elaborations.yml/badge.svg)](https://github.com/noi-techpark/bdp-commons/actions/workflows/ci-traffic-a22-elaborations.yml)

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
cd bdp-commons/data-collectors/traffic-a22/a22elaborations
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

* change the file src/main/resources/it/bz/noi/a22elaborations/db.properties. set the postgres variables to connect to the
  database (or configure it within a CI tool)
  
```
USER=
PASSWORD=
HOST=localhost
PORT=5432
DBNAME=
```

* optionally change frequence of elaborations in the file src/main/resources/it/bz/noi/a22scheduler/cron.properties.
  (or configure it within a CI tool)
  
```
schedule=0 0/10 * * * ?
```

* optionally change the window and the step of elaborations in the file src/main/resources/it/bz/noi/a22elaborations/elaborations.properties (numbers are milliseconds).
  (or configure it within a CI tool)
  
```
windowLength=600000
step=600000
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
