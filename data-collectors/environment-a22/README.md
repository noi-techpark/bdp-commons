# Open Data Hub - Data collector for AugeG4


## Configuration

  - See `src/main/resources/META-INF/spring/application.properties`
  - See `src/main/resources/META-INF/spring/applicationContext.xml`
  - Logsystem: `src/main/resources/log4j2.properties` (Make sure the log-files are writable)
  - Staging and production parameters are read by ConnectorConfig using spring config,
  to override test config parameters they have to be set in operating system environment variables.

## Important

  - Credentials should never be pushed to a public repository
  - Make sure you don't put sensitive data inside your code. Create placeholders for that, and insert them through build-scripts later on.

## To Do

  - complete and check mappings configuration csv files.

## Package

        mvn clean package

## Run test

This will run only safe and fast unit tests.

        mvn test

## Run integration test

This will run only slow integration test suitable for integration env.

        mvn verify

## Run manual test

Inside integration test folder there are IMT tests, that have to be launched manually if/when needed.
Those tests are slow, have a required environment context and usually they change the context,
e.g. consuming mqtt messages or  writing on webservices.

## Run locally on port 9999

       mvn jetty:run

Run with a different port:

        mvn jetty:run -Djetty.port=7000 



## Hub Documentation

[Open Data Hub - Site](https://opendatahub.bz.it/)

[Open Data Hub - ReadTheDocs](https://opendatahub.readthedocs.io/en/latest/intro.html)

[Open Data Hub - Source](https://github.com/idm-suedtirol)



## Data Collector Documentation


### naming convention and main data types flow

 at startup time  spring post construct:
   reads csv datatype.
    and never more updated
    cron scheduled job will send them to hub with syncDataType()


###  naming convention and main data stations flow

 at startup time  spring post construct:
    stationsDto are loaded from hub
    eventually updated with new stations received from Auge
    cron scheduled job will send them to hub with syncStations() cleaning local collection


### naming convention and main data flow

1. receive data
ELABORATED STATE(as received from Auge)
AugeG4ElaboratedDataDto, single message
    ElaboratedResVal, single measurement dato elaborato da Auge e ricevuto in messaggio mqtt json "resVal".
    retriever will consume mqtt queue and store the messages in memory buffer

2. fetch data (attenzione CONCORRENZA)
RAW (delinearized data, inverse function application)
    retriever.fetchData ritorna il dato come depositato in buffer e svuota il buffer.
    delinearizza in AugeG4RawData: messaggio calcolato con funzione inversa
            RawMesurement
            eventualmente scartato se mapping fallisce per la funzione ignota TOTEST

3. process data (applies complex formula)
PROCESSED (stato dei dati corretti)
    viene processato con formula complessa in AugeG4ProcessedData: messaggio
            ProcessedMesurement
    preparati per Auge
            AugeG4ProcessedDataToAuge messaggio
            possono essere scartati se l'id non trova mappatura
            attualmente vuoto
            ProcessedResValToAuge
    preparati per Hub
            AugeG4ProcessedDataToHub per il messaggio
                stationId  (con prefisso da converter)
                controlUnitId (senza prefisso)
                stationName (da converter da CSV label)
            possono essere scartati se l'id non trova mappatura (non sappiamo quale dataType usare)
            ProcessedMesurement (da rinominare in ToHub)
            alimenta DataMapDto aggiungendo in rootMap
            prepareStationsForHub
                aggiunge la station in StationsMap
4. push data
    sends via webserveces processed and raw data to Hub
    sends via mqtt processed data to Auge
