<!--
SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>

SPDX-License-Identifier: CC0-1.0
-->

# A22 traffic forecast


Application which takes A22 traffic forecast data from a web service parses it and sends it to the Open Data Hub.

[![CI traffic-a22-forecast](https://github.com/noi-techpark/bdp-commons/actions/workflows/ci-traffic-a22-forecast.yml/badge.svg)](https://github.com/noi-techpark/bdp-commons/actions/workflows/ci-traffic-a22-forecast.yml)

## Table of contents

- [A22 traffic forecast](#a22-traffic-forecast)
	- [Table of contents](#table-of-contents)
	- [Getting started](#getting-started)
		- [Prerequisites](#prerequisites)
		- [Source code](#source-code)
		- [Build](#build)
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

- Java JDK 17 or higher (e.g. [OpenJDK](https://openjdk.java.net/))
- [Maven](https://maven.apache.org/) 3.x
- A postgres database with the schema intimev2 and elaboration (bdp) already installed

### Source code

Get a copy of the repository:

```bash
git clone git clone https://github.com/noi-techpark/bdp-commons
```

Change directory:

```bash
cd bdp-commons/data-collectors/traffic-a22-forecast
```

### Build

Build the project:

```bash
mvn clean package
```

## Information

### Support

For support, please contact [info@opendatahub.com](mailto:info@opendatahub.com).

### Contributing

If you'd like to contribute, please follow the following instructions:

- Fork the repository.

- Checkout a topic branch from the `main` branch.

- Make sure the tests are passing.

- Create a pull request against the `main` branch.

### Documentation

More documentation can be found at [https://opendatahub.readthedocs.io/en/latest/index.html](https://opendatahub.readthedocs.io/en/latest/index.html).

### License

The code in this project is licensed under the GNU AFFERO GENERAL PUBLIC LICENSE Version 3 license. See the [LICENSE.md](LICENSE.md) file for more information.
