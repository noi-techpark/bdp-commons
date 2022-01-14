# Mapping files

First, the data files should be inside the `uploads` folder of the FTP server. We will download and process all files,
that end with `.dat`.

Secondly, we need 3 mapping files to explain the contents of the input files, which should be zipped into a single
archive named `mapping.zip` and put in the `uploads` folder of the FTP server as well.

Documentation refers to "BrennerLEC ch4.2.3 - [180117_DA11_Part2.pdf](https://github.com/idm-suedtirol/bdp-commons/blob/master/data-collectors/airquality-appabz/infos/180117_DA11_Part2.pdf)" (DOC).

Files are...
 - All files are csv-files with UTF-8 encoding and a comma as separator
 - All fields are treated as strings
 - Put quotation marks, if you need a comma inside fields
 - First line is the header

## stations.csv
 1) station: ID seen at first of each input row (DOC: field A)
 2) mapping: ID given on [rete-civica:stations](http://dati.retecivica.bz.it/services/airquality/stations) (SCODE from DOC Tab.134 first row)
 3) lat: Latitude in decimal degrees as WGS84/EPSG:4326 of the station (optional)
 4) lon: Longitude in decimal degrees as WGS84/EPSG:4326 of the station (optional)

Coordinates (lat/lon) override data coming from rete-civica.

## parameters.csv
 1) parameter: ID from DOC field C
 2) metric: ID from DOC field D
 3) name (_this name must be unique_):
	- mapping of field C as it is used in [rete-civica:sensors](http://dati.retecivica.bz.it/services/airquality/sensors) (`MCODE`)
	- an underscore (`_`)
	- the metric in lowercase (can be different from point 2)
 4) desc: description of field C as it is in DOC Tab.130 (parantheses) including the description of field D as it is in DOC Tab.131
 5) unit: how is it measured (short)

## errors.csv
 1) error: ID from DOC field R
 2) desc: description of field R as it is in DOC Tab.132 (full text, no abbreviations)



Refers to:
https://github.com/idm-suedtirol/bdp-commons/tree/master/data-collectors/airquality-appabz/infos/mapping-files/README.md
