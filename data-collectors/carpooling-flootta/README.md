CARPOOLING DATASOURCE
======================

This datasource retrieves carpooling data from the webservice FLOOTA and converts it, to be usable in the big data platform.


# FLOOTA webservice
The floota webservice exposes the data as json

## Table of contents

- [Gettings started](#getting-started)
- [Running tests](#running-tests)
- [Deployment](#deployment)
- [Information](#information)

## Getting started


- check out the carpooling module
- go to the root of the project
> 
- create your war file
> mvn package

### Prerequisites

To build the project, the following prerequisites must be met:
- Java JDK 1.8 or higher (e.g. [OpenJDK](https://openjdk.java.net/))
- [Maven](https://maven.apache.org/) 3.x

### Source code

Get a copy of the repository:

```bash
git clone git@github.com:noi-techpark/bdp-commons.git
```

Change directory:

```bash

cd data-collectors/carpooling-flootta
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
To check if the FLOOTTA endpoint is working you can run integration tests

```bash
mvn clean verify
```

## Deployment

Take your generated artifact and deploy it on any java webserver.

## Information

### Support

ToDo: For support, please contact [info@opendatahub.bz.it](mailto:info@opendatahub.bz.it).

### Contributing

If you'd like to contribute, please follow the following instructions:

- Fork the repository.

- Checkout a topic branch from the `development` branch.

- Make sure the tests are passing.

- Create a pull request against the `development` branch.

### Documentation

More documentation can be found at [https://opendatahub.readthedocs.io/en/latest/index.html](https://opendatahub.readthedocs.io/en/latest/index.html).

