# APPATN Opendata Data Collector

## Table of contents

1. [Project overview](#Project-overview)
2. [Installation instructions](#Installation-instructions)

## Project overview

The purpose of this project is to provide the [Open Data Hub](https://github.com/idm-suedtirol/bdp-core) with data from APPA TN environment stations.

It is to be distributed in form of a WAR package for Tomcat and it consists of five classes, three dealing with data gathering, transformation and transmission (**JobScheduler**, **DataFetcher** and **DataPusher**) and one helper class (**CSVHandler**, **DateHelper**).

---

#### JobScheduler

It is the entry point of the project, and has the purpose of calling the components in the specific order. It is managed by the Spring Scheduler and can be configured in `src/main/resources/META-INF/spring/applicationContext.xml` using CRON-like timing syntax.

The collection of **historic data**, up unitil the present day, is done automatically, based on the API call `/getDateOfLastRecord`. This allows the data collector to complete the collection at most from `odh.scheduler.historic.from` (found in `src/main/resources/config.properties`), but if partial data was already passed, only the remaining will be collected.

#### DataFetcher

This is the class that takes care of managing API requests to the source of the data. Strongly relies on the ResourceBundle configuration file that can be found in `src/main/resources/config.properties`. Additionally, it relies on **CSVHandler** to read two CSV files with important data (more on them below).

#### DataPusher

Relying on [Google Gson](https://github.com/google/gson) on input, it creates the required format for the data to be pushed to the [Open Data Hub](https://github.com/idm-suedtirol/bdp-core). As the DataFetcher, it strongly relies on the ResourceBundle configuration file that can be found in `src/main/resources/config.properties`.

#### CSVHandler

This is a helper class that has the sole purpose of parsing the two additional CSV files aforementioned.

THe two files specified in `src/main/resources/config.properties` are:
- `odh.station.metadata.csv` containing the (_preferably absolute_) path to the CSV file that will be needed to map available stations in the following fashion

| ID | NOME | LAT | LONG | COMUNE |
| -- | -- | -- | -- | -- |
| 8 | BORGO VAL | 46.051841 | 11.453893 | Borgo Valsugana |


- `odh.types.metadata.csv` containing the (_preferably absolute_) path to the CSV file that will be needed to map available data types in the following fashion

| ID | NOME | SIGLA | UNITA_MISURA |
| -- | -- | -- | -- |
| 2 | sulphur dioxide | so2 | ug/mc |

Only proper **c**omma **s**eparated **v**alues are accepted, **not** semicolon or similar. It is advised to store them in a folder outside the CLASSPATH. Hotswapping of files, even if tested and working, is not advised. Since the datacollector collects the data from the last available data already uploaded up until the present day, it represents no issues and no delays when restarted.

#### DateHelper

This class takes care of date and date intervals management. For example, it converts the ODH format to the accepted date for the input API, it creates 90-days intervals (maximum accepted for the API calls).

## Installation instructions

As mentioned before, this project is thought to be packaged in a WAR file and deployed to a Tomcat8 instance.

Dependencies are managed through Maven, as you can see from the root `pom.xml` file.

Testing is done with the JUnit framework and is executed automatically when Maven compiles and packages the project.

Before compiling and packaging a few steps must be executed:

1. Edit Log4j config file

	Log4j manages the logging of the whole data collector, being it information, error or debug messages. Thus, it should be correctly configured. To do so, edit `src/main/resources/log4j2.properties` to match your desired configuration.

    Minimal configuration is provided, given that you set `property.logFileFolder` (use absolute paths) and `property.logFileName`.

2. Edit ResourceBundle config file

	The one file that contains variables that are actively used at runtime from the data collector itself is `src/main/resources/config.properties`. Such file contains both sensitive and non-sensitive information. A list follows:

##### to be set

	- `odp.url.stations.trentino.historic=` endpoint of the API (ending in opendata/json/)
	- `odh.station.metadata.csv=` stations CSV (preferably absolute, see above) path
	- `odh.types.metadata.csv=` data types CSV (preferably absolute, see above) path

##### already set, leave as-is or know what you're changing

	- `odh.station.type=Environmentstation`
	- `odh.station.origin=APPATN`
	- `odh.station.projection=EPSG:4326`
	- `odh.station.description=Medie orarie`
	- `odh.station.rtype=Mean`
	- `odh.station.polluters={pm10,pm25,no2,o3,so2,co}`
	- `odh.scheduler.historic.from=2017-01-01`

3. Check Scheduling CRON repetition

	Although a value has already been set, it may be that it has to be changed, based on how frequently you want the data collector to look for data. By default the CRON expression is `0 0 10 * * *`, which means that every day at 10:00 it will be run. The choice of 10:00 is due to the fact that most of the times at 10:00 CST new data from the previous day are added to the source API.

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
