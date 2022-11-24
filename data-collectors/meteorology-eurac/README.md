# Eurac meteorology Data Collector

[![CI](https://github.com/noi-techpark/bdp-commons/actions/workflows/ci-meteorology-eurac.yml/badge.svg)](https://github.com/noi-techpark/bdp-commons/actions/workflows/ci-meteorology-eurac.yml)

Data collector for taking data from the Eurac Weather API and pushing it to Open Data Hub.

**Table of Contents**
- [Eurac meteorology Data Collector](#eurac-meteorology-data-collector)
	- [Getting started](#getting-started)
		- [Analysis](#analysis)
		- [Prerequisites](#prerequisites)
		- [Configuration](#configuration)
	- [Packages and important classes:](#packages-and-important-classes)
	- [Implementation details:](#implementation-details)


## Getting started

General instructions can be found inside the [Open Data Hub Mobility - Data
Collectors README](../../README.md). Please read that first. The following
chapters will only contain specific configuration and setup steps.

These instructions will get you a copy of the project up and running on your
local machine for development and testing purposes.

### Analysis

For details regarding mapping from data provided by the service and the data used by the opendatahub, please see this analysis document:
[documentation/220718_SpecificheIntegrazioneODH_NOI.pdf](documentation/220718_SpecificheIntegrazioneODH_NOI.pdf)

### Prerequisites

To build the project, the following prerequisites must be met:
- Everything inside [Open Data Hub Mobility - Data Collectors README](../../README.md#prerequisites)

### Configuration

  - See [src/main/resources/application.properties](src/main/resources/application.properties)

    please refer to the comments provided in the file, here you can configure the parameters for:

    - CRON expressions for job intervals;
    - the service endpoints (no credentials are required);
    - request params required by the services (climate daily endpoint needs station id);

## Packages and important classes:

package **it.bz.idm.bdp.dcmeteoeurac**

 - EuracClient class: provides functionality to get data from the Eurac API;
 - OdhClient class: provides functionality to push data to the Open Data Hub platform;
 - SyncScheduler class: provides functionality to schedule retrieve and push operations for Stations, DataTypes and Measurements.


package **it.bz.idm.bdp.dcmeteoeurac.dto**

 - MetadataDto: used to convert JSON string provided by `/metadata` service into Java objects.
 - ClimatologyDto: used to convert JSON string provided by `/climatologies` service into Java objects.
 - ClimateDailyDto: used to convert JSON string provided by `/climate_daily` service into Java objects.


## Implementation details:

There are 3 scheduled jobs:

- syncJobStations: synchronizes stations and data types. Stations are provided by EuracClient class as Metadata, meanwhile data types are described in the code based on the documentation (see [Analysis](#analysis));
- syncJobClimatologies: synchronizes monthly climatology data. For detailed information about data mapping between Eurac API and Open Data Hub, see documentation ([Analysis](#analysis));
- syncJobClimateDaily: synchronizes daily climate data. First, all Eurac stations are retrieved, then a separate request is made for each station to retrieve its daily climate data. This is because the `/climate_daily` endpoint returns too much data if no filter parameters are provided. For detailed information about data mapping between Eurac API and Open Data Hub, see documentation ([Analysis](#analysis));
