eCharging DataCollector
======================
This data collector takes data from different companies which comply to the same standard, created in the workgroup

[![CI emobility-echarging](https://github.com/noi-techpark/bdp-commons/actions/workflows/ci-emobility-echarging.yml/badge.svg)](https://github.com/noi-techpark/bdp-commons/actions/workflows/ci-emobility-echarging.yml)

## Prerequisits

- maven for dependency management
- dependencies of core module(dto, dc-interface) in your local maven repo installed

Remember that your data-collector will not do anything with the data if you do not have the writer module upp and running.
https://github.com/idm-suedtirol/bdp-core


## How to make it run

- get eMobility module

  `git clone git@github.com:idm-suedtirol/bdp-emobility.git`
- go to the eCharging data-collector project

  `cd data-collectors/eChargingPoints`
- go to src/main/resources/META-INF/spring/application.properties and fill it out
- the chron which schedules the tasks can be changed in src/main/resources/META-INF/spring/applicationContext.xml

By now you can already deploy the module.
For further documentation check the core repo.
