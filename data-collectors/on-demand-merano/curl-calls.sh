#!/bin/bash

# load USERNAME/PASSWORD from .env first

API_ENDPOINT=https://badiabus.app.savvy.mobi/api/external

STOPS_PATH=/v2.0/stops
ACTIVE_ACTIVITIES_PATH=/v1.0/activities/active
POLYGONS_PATH=/v1.0/polygons

# stops
printf "\nstops\n"
curl -u $USERNAME:$PASSWORD -H 'Accept:application/json' $API_ENDPOINT$STOPS_PATH | jq
printf "\n###############\n"

# activities
printf "\nactivities\n"
curl -u $USERNAME:$PASSWORD -H 'Accept:application/json' $API_ENDPOINT$ACTIVE_ACTIVITIES_PATH | jq
printf "\n###############\n"

# polygon path
printf "\npolygons\n"
curl -u $USERNAME:$PASSWORD -H 'Accept:application/json' $API_ENDPOINT$POLYGONS_PATH | jq
printf "\n###############\n"
