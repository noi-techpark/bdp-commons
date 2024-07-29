#!/bin/bash

# SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
#
# SPDX-License-Identifier: CC0-1.0

######################
# load .env
######################

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

######################
# define variables
######################

BASE_URL=https://online.onecenter.info
FACILITY_PATH=/api/Facility/GetFacilities
FREE_PLACES_PATH=/api/Facility/GetFreePlaces

######################
# auth
######################

printf "\nauth\n"

RESPONSE=$(curl --request POST \
			--url "$API_OAUTH_TOKEN_URI" \
			--header "Content-Type: application/x-www-form-urlencoded" \
			--data grant_type=password \
			--data username=$API_OAUTH_USERNAME \
			--data client_id=STA \
			--data client_secret=$API_OAUTH_CLIENT_SECRET \
			--data password=$API_OAUTH_PASSWORD
		)

printf "\n$RESPONSE\n"

TOKEN=$(echo $RESPONSE | jq --raw-output '.access_token')

# printf "Token: $TOKEN"

printf "\n###############\n"



######################
# facilities
######################

printf "\nfacilities\n"

curl --url "$BASE_URL$FACILITY_PATH" \
	 --header "Authorization: Bearer $TOKEN" | jq


printf "\n###############\n"

