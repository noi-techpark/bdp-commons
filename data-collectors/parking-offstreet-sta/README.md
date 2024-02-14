<!--
SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>

SPDX-License-Identifier: CC0-1.0
-->

# Parking off street STA

[![CI](https://github.com/noi-techpark/bdp-commons/actions/workflows/ci-parking-offstreet-sta.yml/badge.svg)](https://github.com/noi-techpark/bdp-commons/actions/workflows/ci-parking-offstreet-sta.yml)

This data collector gathers data about STA's parking facilities provided by Skidata.  
The job runs as a scheduled cron job that periodically interrogates the onecenter REST API.  

See the [requirements](./documentation/230214_SpecificheIntegrazione_NOI_v1.1.pdf) and [API specification](./documentation/230728_SpecificaSkidata.pdf) for more details

See [calls.http](./calls.http) for example API calls to the onecenter REST API.

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

Credentials both for ODH and ONECENTER endpoints have to be obtained separately

After plugging in the relevant credentials, docker-compose up should fire up a local instance.
