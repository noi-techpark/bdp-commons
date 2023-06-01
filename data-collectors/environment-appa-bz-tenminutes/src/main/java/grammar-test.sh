#!/bin/bash

# SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
#
# SPDX-License-Identifier: CC0-1.0

antlr4-grun it.bz.idm.bdp.airquality.AirQuality row -tree test.txt

exit $?
