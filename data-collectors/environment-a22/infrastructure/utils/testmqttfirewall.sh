#!/bin/bash

# SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
#
# SPDX-License-Identifier: CC0-1.0

CMD="host myip.opendns.com resolver1.opendns.com|tail -n1 && nc -nvz 213.21.183.12 61616 && echo ------------------------"


for server in dockerprod1 dockerprod2 dockerprod3 dockerprod4 dockertest1 dockertest2; do
	ssh $server "$CMD"
done


eb ssh test-tomcat-mqtt-dc --force -c "$CMD"
