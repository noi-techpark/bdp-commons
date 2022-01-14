#!/bin/bash

antlr4 -no-listener -package it.bz.idm.bdp.airquality it/bz/idm/bdp/airquality/AirQuality.g4
antlr4-javac -d . it/bz/idm/bdp/airquality/AirQuality*.java

exit 0
