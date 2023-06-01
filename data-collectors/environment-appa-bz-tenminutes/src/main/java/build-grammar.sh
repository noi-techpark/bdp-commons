#!/bin/bash

# SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
#
# SPDX-License-Identifier: CC0-1.0

antlr4 -no-listener -package it.bz.idm.bdp.airquality it/bz/idm/bdp/airquality/AirQuality.g4
antlr4-javac -d . it/bz/idm/bdp/airquality/AirQuality*.java

exit 0
