#!/bin/bash

# SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
#
# SPDX-License-Identifier: CC0-1.0

# load .env
if [ -f .env ]; then
	set -a
	source .env
	set +a
	printf ".env loaded\n"
else
	printf "create .env first and fill needed vars\n"
	printf "cp .env.example .env\n"
	exit 1
fi

BASE_URL=https://www.onecenter.info
AUTH_URL=/oauth/token
FACILITY_PATH=/api/Facility/GetFacilities
FREE_PLACES_PATH=/api/Facility/GetFreePlaces

# auth
printf "\nauth\n"
curl -H 'Content-Type:application/x-www-form-urlencoded' \
	"$API_OAUTH_TOKEN_URI?grant_type=password&username=$API_OAUTH_USERNAME&client_id=STA&client_secret=$API_OAUTH_PASSWORD&password=$API_OAUTH_PASSWORD"
printf "\n###############\n"


