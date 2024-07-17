#!/bin/bash

# SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
#
# SPDX-License-Identifier: CC0-1.0


echo "https://www.altoadigepedala.bz.it/dashboard/api/opendata/challenges"
ACTIVE=true

curl "https://www.altoadigepedala.bz.it/dashboard/api/opendata/challenges" | jq
echo "###################"



echo "https://www.suedtirolradelt.bz.it/dashboard/api/opendata/organisations"
CHALLENGE_ID=459
TYPE=""
QUERY=""

curl "https://www.suedtirolradelt.bz.it/dashboard/api/opendata/organisations?challengeId=$CHALLENGE_ID&type=$TYPE&query=$QUERY" | jq
echo "###################"
