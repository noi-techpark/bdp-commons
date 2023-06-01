#!/bin/bash

# SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
#
# SPDX-License-Identifier: CC0-1.0

set -xeuo pipefail

openssl genrsa -out private_key.pem 2048
openssl pkcs8 -topk8 -inform PEM -outform DER -in private_key.pem -nocrypt > pkcs8_key

openssl req -new -key private_key.pem -out cert.csr

openssl x509 -req -days 365 -in cert.csr -signkey private_key.pem -out cert.crt

exit 0

