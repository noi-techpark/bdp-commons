#!/bin/bash

export MAVEN_CONFIG="$HOME"

mkdir -p ~/.m2

cat > ~/.m2/settings.xml << EOF
<settings>
    <localRepository>$PWD/docker/.m2</localRepository>
</settings>
EOF

/bin/bash -c "/usr/local/bin/mvn-entrypoint.sh $@"
