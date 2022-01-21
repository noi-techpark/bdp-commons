# APPABZ Retecivica Medie Orarie Data Collector

[![CI environment-appa-bz-opendata](https://github.com/noi-techpark/bdp-commons/actions/workflows/ci-environment-appa-bz-opendata.yml/badge.svg)](https://github.com/noi-techpark/bdp-commons/actions/workflows/ci-environment-appa-bz-opendata.yml)

## Table of contents

1. [Project overview](#Project-overview)
2. [Installation instructions](#Installation-instructions)


## Project overview

The purpose of this project is to provide the [Open Data Hub](https://github.com/idm-suedtirol/bdp-core) with data from the BrennerLEC environment stations.
It is to be distributed in form of a WAR package for Tomcat and it consists of five classes, three dealing with data gathering, transformation and transmission (**JobScheduler**, **DataFetcher** and **DataPusher**) and one helper classes **DateHelper**). Should it be necessary, detailed JavaDoc is present and can be generated.

---

#### JobScheduler

It is the entry point of the project, and has the purpose of calling the components in the specific order. It is managed by the Spring Scheduler and can be configured in `src/main/resources/spring/applicationContext.xml` using CRON-like timing syntax: by defult it is executed every day at 10 AM.
It also structures the map in which data will be inserted.

#### DataFetcher

This is the class that takes care of managing API requests to the source of the data. Strongly relies on the ResourceBundle configuration file that can be found in `src/main/resources/config.properties`.

#### DataPusher

Relying on [Google Gson](https://github.com/google/gson) on input, it creates the required format for the data to be pushed to the [Open Data Hub](https://github.com/idm-suedtirol/bdp-core). As DataFetcher, it strongly relies on the ResourceBundle configuration file that can be found in `src/main/resources/config.properties`.

#### DateHelper

This helper class contains only one method that given a date, return its timestamp. As this class is marginal to the aim of this project, see related JavaDocs for more information.


---

## Installation instructions

As mentioned before, this project is thought to be packaged in a WAR file and deployed to a Tomcat8 instance.

Dependencies are managed through Maven, as you can see from the root pom.xml file.

Testing is done with the [JUnit](https://junit.org/junit4/) framework and is executed automatically when Maven compiles and packages the project.

Before compiling and packaging a few steps must be executed:


1. Edit Log4j config file

	Log4j manages the logging of the whole data collector, being it information, error or debug messages. Thus, it should be correctly configured. To do so, edit `src/main/resources/log4j2.properties` to match your desired configuration.

    Minimal configuration is provided, given that you set `property.logFileFolder` (use absolute paths) and `property.logFileName`.

2. Edit ResourceBundle config file

	The one file that contains variables that are actively used at runtime from the data collector itself is `src/main/resources/config.properties`. Such file contains both sensitive and non-sensitive information. A list follows:

    ##### Already set (do not changhe this, unless you know what you are changing)
    - `odh.url.stations.metadata=http://dati.retecivica.bz.it/services/airquality/stations`
    - `odh.url.polluters.metadata=http://dati.retecivica.bz.it/services/airquality/sensors`
    - `odh.url.polluters.measurements=http://dati.retecivica.bz.it/services/airquality/timeseries`
    - `odh.station.type=Environmentstation`
    - `odh.station.origin=APPABZ`
    - `odh.station.projection=EPSG:4326`
    - `odh.station.rtype=Mean`
    
3. Check Scheduling CRON repetition

    Although a value has already been set, it may be that it has to be changed, based on how frequently you want the data collector to look for data. By default the CRON expression is 0 0 10 * * ?, which means everyday at 10 AM.

    To edit that, you may wanna go and edit src/main/resources/META-INF/spring/applicationContext.xml, precisely in the <task-scheduled> tag, in the cron attribute. [Here](https://www.freeformatter.com/cron-expression-generator-quartz.html) you can generate your own CRON string online.


4. Prepare the package

    As already mentioned, dependency handling and packaging happen through Maven. In a terminal, type:
    
    ```
    cd root_folder_of_this_project
    mvn test
    ```
    
    It is advised to run tests separately so that it is easier to distinguish between the output log if more configuration has to be done based on errors in the tests. If no errors are raised, we can procede:
    
    ```
    mvn clean package
    ```
    
    Once finished, a new WAR package will appear in target/ folder.


5. Deploy to Tomcat8

    Depending on one's personal preferences, the WAR package can be deployed to Tomcat8 either by copying it to Tomcat webapps/ folder, or using Tomcat8 Manager.
    
