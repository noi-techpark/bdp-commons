#!/bin/bash

# SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
#
# SPDX-License-Identifier: CC0-1.0

set -euo pipefail

DC="$1"
TYPE="$2"
VERSION="$3"
if [[ ${4-} ]]; then
    COREVERSION="$4"
else
    COREVERSION=""
fi

test "$TYPE" = "release" -o "$TYPE" = "snapshot" || {
    echo "ERROR: \$1 must be either 'release' or 'snapshot'"
    exit 1
}

# UPDATING pom.xml files...
test "$TYPE" = "snapshot" && VERSION="$VERSION-SNAPSHOT"

REP="maven-repo.opendatahub.bz.com"
REP_ID="$REP"
XMLNS=http://maven.apache.org/POM/4.0.0
CMD="xmlstarlet ed -P -L -N pom=$XMLNS"

find ./ -name pom.xml|grep -v target | while read POM; do
    if [ `xmlstarlet sel -N pom=$XMLNS -t -v '/pom:project/pom:artifactId' "$POM"` == "$DC" ]; then
        $CMD -u "/pom:project/pom:version" -v $VERSION $POM
        if [ -n "$COREVERSION" ]; then
            $CMD -u "/pom:project/pom:dependencies/pom:dependency[pom:groupId='it.bz.idm.bdp'][pom:artifactId='dc-interface']/pom:version" -v $COREVERSION $POM
            if [[ "$COREVERSION" == *SNAPSHOT*  ]]; then
                REP_ID="$REP_ID"-"SNAPSHOT"
                REP_URL="http://it.bz.opendatahub.s3-website-eu-west-1.amazonaws.com/snapshot"
            else 
                REP_URL="http://it.bz.opendatahub.s3-website-eu-west-1.amazonaws.com/release"
            fi
            $CMD -u "/pom:project/pom:repositories/pom:repository[starts-with(pom:id,'$REP')]/pom:url" -v $REP_URL $POM
            $CMD -u "/pom:project/pom:repositories/pom:repository[starts-with(pom:id,'$REP')]/pom:id" -v $REP_ID $POM
        fi
    fi
done


exit 0
