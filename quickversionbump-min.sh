#!/bin/bash

# SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
#
# SPDX-License-Identifier: CC0-1.0

#
# Will set the version of a dependency to a minimal version in the proerties of all data-collectors
#
# 2 arguments needed: name version"
# example to update log4j2-version to 2.17.1"
#
# ./quickversionbump-min.sh log4j2.version 2.17.1"
#

if [ ! $# -eq 2 ]
  then
    echo "2 arguments needed: name version"
    echo "example to update log4j2.version to 2.17.1:"
    echo "./quickversionbump-generic.sh log4j2.version 2.17.1"
    exit 1
fi

set -euo pipefail

NAME="$1"
VERSION="$2"
XMLNS=http://maven.apache.org/POM/4.0.0

find ./ -name pom.xml -exec sh -c "xmlstarlet ed -P -L -N pom=$XMLNS -u '/pom:project/pom:properties/pom:$NAME' -v \"$VERSION\" {}" \;
echo "Set $NAME to minimal version $VERSION" 

exit 0
