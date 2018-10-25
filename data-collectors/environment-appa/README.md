# APPATN 10 minuti Data Collector

## Table of contents

1. [Project overview](#Project-overview)
2. [Installation instructions](#Installation-instructions)

## Project overview

The purpose of this project is to provide the [Open Data Hub](https://github.com/idm-suedtirol/bdp-core) with data from the BrennerLEC environment station.

It is to be distributed in form of a WAR package for Tomcat and it consists of four classes, three dealing with data gathering, transformation and transmission (**JobScheduler**, **DataFetcher** and **DataPusher**) and one helper class (**CoordinateHelper**).

---

#### JobScheduler

It is the entry point of the project, and has the purpose of calling the components in the specific order. It is managed by the Spring Scheduler and can be configured in `src/main/resources/META-INF/spring/applicationContext.xml` using CRON-like timing syntax.

#### DataFetcher

This is the class that takes care of managing API requests to the source of the data. Strongly relies on the ResourceBundle configuration file that can be found in `src/main/resources/config.properties`.

#### DataPusher

Relying on [Google Gson](https://github.com/google/gson) on input, it creates the required format for the data to be pushed to the [Open Data Hub](https://github.com/idm-suedtirol/bdp-core). As the DataFetcher, it strongly relies on the ResourceBundle configuration file that can be found in `src/main/resources/config.properties`.

#### CoordinateHelper

This is a helper class that has the sole purpose of converting **UTM – ETRS89** coordinates into **WSG84** standard. It's method *UTMtoDecimal* accepts three or four parameters:

1. *int* zone
2. *double* easting
3. *double* northing
4. *boolean* north (optional parameter, default value true)

## Installation instructions

As mentioned before, this project is thought to be packaged in a WAR file and deployed to a Tomcat8 instance.

Dependencies are managed through Maven, as you can see from the root `pom.xml` file.

Testing is done with the JUnit framework and is executed automatically when Maven compiles and packages the project.

Before compiling and packaging a few steps must be executed:

1. Edit Log4j config file

	Log4j manages the logging of the whole data collector, being it information, error or debug messages. Thus, it should be correctly configured. To do so, edit `src/main/resources/log4j2.properties` to match your desired configuration.

    Minimal configuration is provided, given that you set `property.basePath` (use absolute paths).
2. Edit ResourceBundle config file

	The one file that contains variables that are actively used at runtime from the data collector itself is `src/main/resources/config.properties`. Such file contains both sensitive and non-sensitive information. A list follows:

	##### to be set
	- `odp.url.stations.10minuti=` endpoint of the BrennerLEC API (ending in brennerlec/)
	- `odp.url.stations.10minuti.key=` the API key
	##### already set, leave as-is or know what you're changing
	- `odh.station.type=Environmentstation`
	- `odh.station.origin=APPATN`
	- `odh.station.projection=EPSG:4326`
	- `odp.unit.description.10minuti=Valori a 10 minuti`
	- `odp.unit.rtype.10minuti=Mean`
	- `odp.unit.availability.10minuti=\ - invalidazione`
	- `odp.unit.description.10minuti.availability=Nr. di misure valide / Nr. di misure totali`
	- `odp.unit.rtype.10minuti.availability=Flag`

3. Check Scheduling CRON repetition

	Although a value has already been set, it may be that it has to be changed, based on how frequently you want the data collector to look for data. By default the CRON expression is `0 0 * * * *`, which means once an hour.

    To edit that, you may wanna go and edit `src/main/resources/META-INF/spring/applicationContext.xml`, precisely in the `<task-scheduled>` tag, in the `cron` attribute. [Here](https://www.freeformatter.com/cron-expression-generator-quartz.html) you can generate your own CRON string online.

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
    Once finished, a new WAR package will appear in `target/` folder.

5. Deploy to Tomcat8

    Depending on one's personal preferences, the WAR package can be deployed to Tomcat8 either by copying it to Tomcat `webapps/` folder, or using Tomcat8 Manager.
