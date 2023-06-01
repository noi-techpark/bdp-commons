<!--
SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>

SPDX-License-Identifier: CC0-1.0
-->

# E-Charging Data Collector

This data collector takes data from different companies which comply to the same
standard, created in the workgroup.

[![CI emobility-echarging](https://github.com/noi-techpark/bdp-commons/actions/workflows/ci-emobility-echarging.yml/badge.svg)](https://github.com/noi-techpark/bdp-commons/actions/workflows/ci-emobility-echarging.yml)

**Table Of Contents**
- [E-Charging Data Collector](#e-charging-data-collector)
	- [Getting Started](#getting-started)
		- [Prerequisites](#prerequisites)
		- [Source code](#source-code)
	- [Build](#build)
	- [Information](#information)
		- [Support](#support)
		- [Contributing](#contributing)
		- [Documentation](#documentation)
		- [License](#license)

## Getting Started

### Prerequisites

To build the project, the following prerequisites must be met:

- Java JDK 1.8 or higher (e.g. [OpenJDK](https://openjdk.java.net/))
- [Maven](https://maven.apache.org/) 3.x
- The [Open Data Hub Mobility Writer](https://github.com/noi-techpark/bdp-core)
  (aka Big Data Platform Core) installed
- [Credentials](https://github.com/noi-techpark/odh-docs/wiki/Contributor-Guidelines:-Credentials)
  - to get the ODH writer token
  - to connect to various E-Charging API endpoint

### Source code

Get a copy of the repository:

```bash
git clone git clone https://github.com/noi-techpark/bdp-commons
```

Change directory:

```bash
cd bdp-commons/data-collectors/emobility-echarging
```


## Build

- go to src/main/resources/META-INF/spring/application.properties and fill it out
- the chron which schedules the tasks can be changed in src/main/resources/META-INF/spring/applicationContext.xml

By now you can already deploy the module.
For further documentation check the core repo.

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
