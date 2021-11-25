# Open Data Hub Mobility - Data Collectors

The Open Data Hub Mobility Data Collectors, historically called also Big Data
Platform data collectors, and also data providers where contained in this repo,
therefore it is called common.

This repository contains the source code of all data collectors, that are Java
workers that connect to a remote data pool, such as an API, MQTT broker, FTP
server or their like, and download data, aggregate and enriches that, and
finally send it to the [Big Data Platform
writer](https://github.com/noi-techpark/bdp-core), which stores it inside a
Postgres DB.

**Table of contents**
- [Open Data Hub Mobility - Data Collectors](#open-data-hub-mobility---data-collectors)
	- [Getting started](#getting-started)
		- [Prerequisites](#prerequisites)
		- [Source code](#source-code)
		- [Build](#build)
		- [Running tests](#running-tests)
		- [Test the code with Docker](#test-the-code-with-docker)
		- [Execute without Docker](#execute-without-docker)
	- [Write a new data collector](#write-a-new-data-collector)
	- [Deploy a new data collector](#deploy-a-new-data-collector)
	- [Information](#information)
		- [Support](#support)
		- [Contributing](#contributing)
		- [Documentation](#documentation)
		- [License](#license)


## Getting started

These instructions will get you a copy of the project up and running on your
local machine for development and testing purposes. These are *just general
guidelines*, for specific details, refer to the `README.md` file in each folder,
or if missing, get an idea of how it works from the
`infrastructure/Jenkinsfile*` files.

### Prerequisites

To build the data collector project, the following prerequisites must be met:

- Java JDK 1.8 or higher (e.g. [OpenJDK](https://openjdk.java.net/))
- [Maven](https://maven.apache.org/) 3.x
- The [Open Data Hub Mobility Writer](https://github.com/noi-techpark/bdp-core)
  (aka Big Data Platform Core) installed

If you want to run the application using [Docker](https://www.docker.com/), the
environment is already set up with all dependencies for you. You only have to
install [Docker](https://www.docker.com/) and [Docker
Compose](https://docs.docker.com/compose/) and follow the instruction in the
[dedicated section](#execute-with-docker).

Hint: To be sure to have the correct Java version and build environment equal to our
infrastructure use the provided docker configuration.

### Source code

Get a copy of the repository:

```bash
git clone https://github.com/noi-techpark/bdp-commons.git
```

Change directory:

```bash
cd bdp-commons/data-collectors/[your-collector]
```

### Build

Build the project:

```bash
mvn clean package
```

### Running tests

The unit tests can be executed with the following command:

```bash
mvn clean test
```

### Test the code with Docker

- Inside the corresponding data collector folder, copy `.env.example` to `.env`
  and configure it
- Change to the root of this repository
- Copy `.env.example` to `.env` and configure it
- Run `docker-compose up -d`
- You can follow the output with `docker-compose logs -f`

Please note, if that command does not work it might be that the data collector
was not configured to allow `mvn tomcat:run`. Please, refer to the `README.md`
inside that folder for further details, and report the incidence to
`help@opendatahub.bz.it`.

### Execute without Docker

Change directory into the data collector you want.

You can set the parameters directly as environmental variables (see
`.env.example`) and run ), as follows:

```bash
mvn tomcat:run \
  -DPARAM1=... \
  -DPARAM2=... \
  -DPARAM3=...
```

...or, set them inside the relevant `.properties` files directly (see the
corresponding `README.md` for details), and run:

```bash
mvn tomcat:run
```

Credentials needed? See
[here](https://github.com/noi-techpark/odh-docs/wiki/Contributor-Guidelines:-Credentials).

## Write a new data collector

If you want to write a new Data Collector, mimic the code present in this
repository and follow our [Contributing](#contributing) guidelines.

One detail, which is also present in the guidelines, but very important, make
sure your data collector can be configured with environmental variables. That
is, `.properties` files must allow injection of ENV VARS as follows:

```ini
MY_PARAM_1=${ENV_VAR_FOR_MY_PARAM_1}
MY_PARAM_2=${ENV_VAR_FOR_MY_PARAM_2:or-this-default-if-missing}
```

## Deploy a new data collector

- Copy `skeleton/infrastructure` to the data collector folder
  - Complete the `Jenkinsfile-Test.groovy` and `Jenkinsfile-Prod.groovy` where the
    `PROJECT` variable must be set to the folder name
  - Check which docker servers have the least load recently, and choose them for
    testing and production inside the `infrastructure/ansible/hosts` file
- Go to
  [AWS/ECR](https://eu-west-1.console.aws.amazon.com/ecr/create-repository?region=eu-west-1)
  and create a new repository called like the value of `PROJECT`
- Push all new files to a new branch for testing purposes
- Go to our [Jenkins CD server/Data Collectors
  folder](https://jenkins.testingmachine.eu/job/it.bz.opendatahub.bigdataplatform/job/data-collectors/)
- Create two new items there:
  - Staging pipeline: item name = value of `PROJECT` ; choose "Copy from" and type
    `bike-chargers` for instance
  - Production pipeline: item name = `PROJECT` + `.prod` ; choose "Copy from" and type
    `bike-chargers.prod` for instance
  - For both of them:
    - Set your test branch you've created before, for example
      `*/pm-bike-chargers-cd`. You need to change this afterward your initial
      tests to `*/development` inside your staging pipeline, and to `*/master`
      inside your production pipeline.
    - Adapt the "Script path"
- If you need to inject credentials, go to [Jenkins CD server/Big Data Platform
  folder](https://jenkins.testingmachine.eu/job/it.bz.opendatahub.bigdataplatform)
- Create new Credentials under "Credentials>Folder>Global credentials". You can
  inject them in your Jenkinsfiles with `credentials('your-credential-id')`.
  Please try to always use secret texts, not files or there like (if possible).


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
