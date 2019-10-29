Vehicletraffic-Bluetooth
========================

This datacollector is meant to be the endpoint for all bluetooth-boxes spread through the city and along the "strada statale" in proximity of the A22 highway. It removes invalid records anonymizes the collected MAC-addresses, adds missing metadata information and forwards it to the writer module of bdp-core.

## Prerequisists
- maven
- JDK 8

## Encryption
All mac addresses send to this data collector are getting hashed with a secrect key (HMAC) to avoid persisting the MAC-addresse, which could result in a security/privacy issue. Don't forget to set your secret key for encryption or your data collector won't work.

## Metadata
The bluetooth boxes are missing some information like their actual position. To add this information a google spreadsheet was created which in case of changes triggers the update mechanism. This will retrieve all metadata of all stations in the spreadsheet associate it with the box and synchronize with odh. 

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

