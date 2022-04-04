Vehicletraffic-Bluetooth
========================

This datacollector is meant to be the endpoint for all bluetooth-boxes spread through the city and along the "strada statale" in proximity of the A22 highway. It removes invalid records anonymizes the collected MAC-addresses, adds missing metadata information and forwards it to the writer module of bdp-core.

[![CI vehicletraffic-bluetooth](https://github.com/noi-techpark/bdp-commons/actions/workflows/ci-vehicletraffic-bluetooth.yml/badge.svg)](https://github.com/noi-techpark/bdp-commons/actions/workflows/ci-vehicletraffic-bluetooth.yml)

## Prerequisists
- maven
- JDK 8

## Encryption
All mac addresses send to this data collector are getting hashed with a secrect key (HMAC) to avoid persisting the MAC-addresse, which could result in a security/privacy issue. Don't forget to set your secret key for encryption or your data collector won't work.

## Metadata through Google Spreadsheet
The bluetooth boxes are missing some information like their actual position. To add this information a google spreadsheet was created which in case of changes triggers the update mechanism. This will retrieve all metadata of all stations in the spreadsheet associate it with the box and synchronize with odh.
### How does the spreadsheet work
The spreadsheet must have these 3 columns(id,longitude,latitude).
- id: must be unique and correspond the identifer(stationcode) of a BluetoothStation
- longitude,latitude: need to be valid EPSG:4326 cooridnates

Every additional column that gets created will be treated as metadata and added to the metadata field.

Changes to the file will trigger an automatic synchronization of data.

To alow push notifications modify for other domains follow this guide https://developers.google.com/drive/api/v3/push

## Installation
To get a first version of the datacollector running, you download the code, configure the environment with the given property file, package the app and deploy it on a java application server of your choice.


## Setup

```
git clone git@github.com:noi-techpark/bdp-commons.git
cd data-collectors/vehicletraffic-bluetooth/
mvn clean
vim src/main/resources/META-INF/spring/application.properties	//set an encryption secret of your choice and change the other props if needed
mvn package
cp target/[app].war [TOMCAT_HOME]/webapps
```

## Run Tests
To run tests just go to the project home folder and run `mvn test`. To also run integration tests:`mvn verify`

## Information

### Support

For support, please contact [help@opendatahub.bz.it](mailto:help@opendatahub.bz.it).

### Contributing

If you'd like to contribute, please follow our [Getting
Started](https://github.com/noi-techpark/odh-docs/wiki/Contributor-Guidelines:-Getting-started)
instructions.

### Documentation

More documentation can be found at
[https://docs.opendatahub.bz.it](https://docs.opendatahub.bz.it).

### License

The code in this project is licensed under the GNU AFFERO GENERAL PUBLIC LICENSE
Version 3 license. See the [LICENSE](../../LICENSE) file for more information.
