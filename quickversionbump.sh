#!/bin/bash

# SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
#
# SPDX-License-Identifier: CC0-1.0

#
# Will update all pom.xml dc-interface dependency of this folder, means upgrade the dc-interface dependency for all data-collectors
# Do not forget to use quotes around arguments you pass to the script and you need still to escape parantheses when using  
# version ranges for example [1.2.4,2.0.0\)
#
set -euo pipefail

VERSION="$1"
XMLNS=http://maven.apache.org/POM/4.0.0

find ./ -name pom.xml -exec sh -c "xmlstarlet ed -P -L -N pom=$XMLNS -u '/pom:project/pom:dependencies/pom:dependency[pom:groupId=\"it.bz.idm.bdp\"][pom:artifactId=\"dc-interface\"]/pom:version' -v $VERSION {}" \;

exit 0
