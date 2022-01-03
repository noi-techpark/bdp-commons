#!/bin/bash
#
# Will update all pom.xml log4j dependency of this folder, means upgrade the dc-interface dependency for all data-collectors
# Do not forget to use quotes around arguments you pass to the script and you need still to escape parantheses when using  
# version ranges for example [1.2.4,2.0.0\)
#


if [ ! $# -eq 1 ]
  then
    echo "1 argument needed: version"
    echo "example to update to 2.17.1:"
    echo "./quickversionbump-log4j.sh  2.17.1"
    exit 1
fi

set -euo pipefail

VERSION="$1"
XMLNS=http://maven.apache.org/POM/4.0.0

# log4j-core
find ./ -name pom.xml -exec sh -c "xmlstarlet ed -P -L -N pom=$XMLNS -u '/pom:project/pom:dependencies/pom:dependency[pom:groupId=\"org.apache.logging.log4j\"][pom:artifactId=\"log4j-core\"]/pom:version' -v \"$VERSION\" {}" \;
echo "log4j-core updated to $VERSION" 

# log4j-api
find ./ -name pom.xml -exec sh -c "xmlstarlet ed -P -L -N pom=$XMLNS -u '/pom:project/pom:dependencies/pom:dependency[pom:groupId=\"org.apache.logging.log4j\"][pom:artifactId=\"log4j-api\"]/pom:version' -v \"$VERSION\" {}" \;
echo "log4j-api updated to $VERSION" 

exit 0
