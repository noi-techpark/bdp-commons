<!--
SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>

SPDX-License-Identifier: CC0-1.0
-->

# Matomo Data Collector

[![CI](https://github.com/noi-techpark/bdp-commons/actions/workflows/ci-matomo.yml/badge.svg)](https://github.com/noi-techpark/bdp-commons/actions/workflows/ci-matomo.yml)

Simple datacollector to get Data from Matomo using the Matomo Reporting API  
https://developer.matomo.org/api-reference/reporting-api

Currently supported API methods are:  
- Actions.getPageUrl
- CustomReports.getCustomReport

**Table of Contents**
- [Matomo Data Collector](#matomo-data-collector)
	- [Getting started](#getting-started)
		- [Prerequisites](#prerequisites)
		- [Configuration](#configuration)

## Getting started

General instructions can be found inside the [Open Data Hub Mobility - Data
Collectors README](../../README.md). Please read that first. The following
chapters will only contain specific configuration and setup steps.

These instructions will get you a copy of the project up and running on your
local machine for development and testing purposes.

### Prerequisites

To build the project, the following prerequisites must be met:
- Everything inside [Open Data Hub Mobility - Data Collectors README](../../README.md#prerequisites)
- Matomo API token https://matomo.org/faq/general/faq_114/

You can try the Matomo API calls with [calls.http](calls.http) using the VSCodium/VSCode rest client 

### Configuration

Create a local .env file by copying the [example .env](.env.example)
Insert your Matomo API token and base URL in your `.env` file.

