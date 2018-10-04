#!/bin/bash

echo "<settings><localRepository>$PWD/tmp/.m2</localRepository></settings>" > $PWD/tmp/settings.xml
chmod 755 $PWD/tmp/settings.xml

/bin/bash -c "$@"
