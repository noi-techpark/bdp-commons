#!/bin/bash

echo "<settings><localRepository>$PWD/tmp/.m2</localRepository></settings>" > /settings.xml
chmod 755 /settings.xml

/bin/bash -c "$@"
