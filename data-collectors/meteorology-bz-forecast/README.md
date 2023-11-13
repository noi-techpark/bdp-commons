<!--
SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>

SPDX-License-Identifier: CC0-1.0
-->

# Meteorology province BZ forecast Data Collector

[![CI](https://github.com/noi-techpark/bdp-commons/actions/workflows/ci-meteorology-bz-forecast.yml/badge.svg)](https://github.com/noi-techpark/bdp-commons/actions/workflows/ci-meteorology-bz-forecast.yml)

This data collector gathers meteorology forecast data about the municipalities of the province of Bolzano.

See the [requirements](./documentation/231019_SpecificheIntegrazione_NOI_v1.1.pdf) for more details.

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

See .env and [application.properties](./src/main/resources/application.properties) for configuration options
