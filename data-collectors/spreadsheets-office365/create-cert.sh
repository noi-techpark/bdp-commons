#!/bin/bash
# Creates a valid key, certificate etc. for 10 years

openssl genrsa -out private_key.pem 2048
openssl pkcs8 -topk8 -inform PEM -outform DER -in private_key.pem -nocrypt > pkcs8_key
openssl req -new -key private_key.pem -out cert.csr
openssl x509 -req -days 3650 -in cert.csr -signkey private_key.pem -out cert.crt