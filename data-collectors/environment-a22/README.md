# Open Data Hub - Data collector for AugeG4


## Configuration

  - See `src/main/resources/META-INF/spring/application.properties`
  - See `src/main/resources/META-INF/spring/applicationContext.xml`
  - Logsystem: `src/main/resources/log4j2.properties` (Make sure the log-files are writable)


## Important

  - Credentials should never be pushed to a public repository
  - Make sure you don't put sensitive data inside your code. Create placeholders for that, and insert them through build-scripts later on.


## Mocking

    ACTUALLY not only tests, even packaged version, uses mocked retrieval and pusher.

    public DataService(DataPusherI pusher, DataRetrievalI retrieval) {
        this.pusher = new DataPusherMock();
        this.retrieval = new DataRetrievalMock();
        this.converter = new DataConverter();
    }


## Package

        mvn clean package


## Run locally:

        mvn jetty:run


## Steps to create your own data collector:

- Create your data model through one or more data transport objects (DTOs) see [folder dto](https://github.com/idm-suedtirol/bdp-helloworld/tree/master/data-collectors/my-first-data-collector/src/main/java/it/bz/idm/bdp/myfirstdatacollector/dto)
- Implement retrieval mechanisms from your data source inside [DataRetrieval.java](https://github.com/idm-suedtirol/bdp-helloworld/blob/master/data-collectors/my-first-data-collector/src/main/java/it/bz/idm/bdp/myfirstdatacollector/DataRetrieval.java)
  - See source code comments for further details
  - If you need to configure parts of your retrieval with an URL or some prefix, use `env.getProperty` and put your configs inside [application.properties](https://github.com/idm-suedtirol/bdp-helloworld/blob/master/data-collectors/my-first-data-collector/src/main/resources/META-INF/spring/application.properties). 
- Test your data retrieval with [unit tests](https://github.com/idm-suedtirol/bdp-helloworld/blob/master/data-collectors/my-first-data-collector/src/test/java/it/bz/idm/bdp/myfirstdatacollector/DataRetrievalTest.java)
- Create a [data pusher](https://github.com/idm-suedtirol/bdp-helloworld/blob/master/data-collectors/my-first-data-collector/src/main/java/it/bz/idm/bdp/myfirstdatacollector/DataPusher.java), that pushes your data to the Open Data Hub, which takes care of correctly storing your data in our DB
  - See source code comments for further details
- Finally, implement a [job scheduler](https://github.com/idm-suedtirol/bdp-helloworld/blob/master/data-collectors/my-first-data-collector/src/main/java/it/bz/idm/bdp/myfirstdatacollector/JobScheduler.java), which takes care of executing data collection jobs at the right time
  - You need to implement three jobs (or a single I-do-everything job for simple collectors):
    - `pushStation`, which pushes station details through `DataPusher.syncStations`
    - `pushDataTypes`, which pushes data type details through `DataPusher.syncDataTypes`
    - `pushData`, which pushes the data itself after adaptations with `DataPusher.mapData` through `DataPusher.pushData`
  - Test it with [unit tests](https://github.com/idm-suedtirol/bdp-helloworld/blob/master/data-collectors/my-first-data-collector/src/test/java/it/bz/idm/bdp/myfirstdatacollector/DataPusherTest.java)
  
  
## Documentation

[Open Data Hub - Site](https://opendatahub.bz.it/)

[Open Data Hub - ReadTheDocs](https://opendatahub.readthedocs.io/en/latest/intro.html)

[Open Data Hub - Source](https://github.com/idm-suedtirol)