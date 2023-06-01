<!--
SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>

SPDX-License-Identifier: CC0-1.0
-->

# Bike Boxes Data Collector

[![CI](https://github.com/noi-techpark/bdp-commons/actions/workflows/ci-bike-boxes.yml/badge.svg)](https://github.com/noi-techpark/bdp-commons/actions/workflows/ci-bike-boxes.yml)

This data collector gathers data about bicincitta bike parking / sharing boxes.  
The job runs as a scheduled cron job that periodically interrogates the bicincitta REST API.  
Bike boxes are pushed to the Open Data Hub as stations, and their real time status as measurements

See the [requirements](./documentation/230214_SpecificheIntegrazione_NOI_v1.1.pdf) and [API specification](./documentation/API%20Parking.xlsx) for more details

Relevant endpoints:
- `/connect/token` for authentication
- `/resources/services` to get all cities in which bike boxes exist
- `/resources/stations` to get a list of parking stations
- `/resources/station` to get details and real time data for a specific station

Authentication token is obtained via `client_credentials` flow and then passed as Bearer header

## Getting started

General instructions can be found inside the [Open Data Hub Mobility - Data
Collectors README](../../README.md). Please read that first. The following
chapters will only contain specific configuration and setup steps.

These instructions will get you a copy of the project up and running on your
local machine for development and testing purposes.

### Prerequisites

To build the project, the following prerequisites must be met:
- Everything inside [Open Data Hub Mobility - Data Collectors README](../../README.md#prerequisites)

### Configuration

Create a local .env file by copying the [example .env](.env.example)

Credentials both for ODH and BICINCITTA endpoints have to be obtained separately

See .env and [application.properties](./src/main/resources/application.properties) for configuration options

After plugging in the relevant credentials, docker-compose up should fire up a local instance.
