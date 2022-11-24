# Car Pooling Data Collector

[![CI](https://github.com/noi-techpark/bdp-commons/actions/workflows/ci-helloworld.yml/badge.svg)](https://github.com/noi-techpark/bdp-commons/actions/workflows/ci-helloworld.yml)

Application which takes carpool data from the service provided [ummadum](car pooling), parses it and sends it to
the [Open Data Hub](https://opendatahub.bz.it). The data is made available via a publicly accessible Google Drive file.

**Table of Contents**

- [Helloworld Data Collector](#helloworld-data-collector)
	- [Getting started](#getting-started)
		- [Prerequisites](#prerequisites)
		- [Configuration](#configuration)

## Getting started

These instructions will get you a copy of the project up and running on your
local machine for development and testing purposes.

### Prerequisites

To build the project, the following prerequisites must be met:

- Everything inside [Open Data Hub Mobility - Data Collectors README](../../README.md#prerequisites)
- To access to the Google Drive file, you need to create a Google service account and enable the "Drive API". After that
  you have to create the credentials for a service account and export them as JSON.
  For more information on how to create the credentials json file, see the following Google documentations:
	- [Create a Google Cloud project](https://developers.google.com/workspace/guides/create-project)
	- [Create access credentials](https://developers.google.com/workspace/guides/create-credentials)
	- [https://developers.google.com/drive/api/quickstart/java](https://developers.google.com/drive/api/quickstart/java)

### Configuration

To build the project, the following prerequisites must be met:

- You have to store the credentials of your Google service account to
  `/src/main/resources/google-api-service-account.json`. You can also place the file at a different path. But for this
  you have to set the environmental variable `GOOGLE_API_CREANTIALS_FILE` to this path.

