#!/bin/bash
#
# Will upgrade the given dependency for all data-collectors
#
# 3 arguments needed: groupId artifactId version"
# example to update log4j-core to 2.17.1"
#
# ./quickversionbump-generic.sh org.apache.logging.log4j log4j-core 2.17.1"
#

if [ ! $# -eq 3 ]
  then
    echo "3 arguments needed: groupId artifactId version"
    echo "example to update log4j-core to 2.17.1:"
    echo "./quickversionbump-generic.sh org.apache.logging.log4j log4j-core 2.17.1"
    exit 1
fi

set -euo pipefail

GROUP_ID="$1"
ARTIFACT_ID="$2"
VERSION="$3"
XMLNS=http://maven.apache.org/POM/4.0.0

find ./ -name pom.xml -exec sh -c "xmlstarlet ed -P -L -N pom=$XMLNS -u '/pom:project/pom:dependencies/pom:dependency[pom:groupId=\"$GROUP_ID\"][pom:artifactId=\"$ARTIFACT_ID\"]/pom:version' -v \"$VERSION\" {}" \;
echo "$GROUP_ID $ARTIFACT_ID  updated to $VERSION" 

exit 0
