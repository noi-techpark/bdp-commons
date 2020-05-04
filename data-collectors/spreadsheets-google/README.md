Spreadsheets DataCollector
======================

## User Guide
### Prerequisits

the first row is used for the column names and has to ...
- ... be in english
- ... contain **name**,(**address** or (**longitude** and **latitude**))
Name and address, together **must** create a unique composite key.

### Skills
The spreadsheet reader does some magic but tries to not influence to much. These are things happening when you sync:
- if the column **description** exists it gets analyzed and all contents gets associated to a language (en,de or it)
- each column can be prefixed with a valid ISO 639 locale(eg. de,en,it,fr,cz...) to define the correct language of that column; a prefixed column has always precedence on a non prefixed(do not prefix required columns. In that case create new ones) Example: How can I translate the column `city` in english,german and italian? Create 3 columns with the following names:  `de:city`,`it:city`,`en:city`. The old column can be removed or used as replacement of one of the 3.
- if you have specified an address the app will try to find the correct coordinate informations using Openstreetmaps data which may not have fancy things like google has(word stamming, multiple language recognition...), but has the correct housenumbers of southtyrol, which google has not(does some strange approximation which rarely work). 

Each **address must** ...
- ...be in a single language
- ...have the format `[street-name][street-number],[postalcode][city]`
- ...contain no additional information

### What if the import failed
If after the import, the **address** cell in the spreadsheet displays with a red background, than the import of that POI failed and the following steps have to be done to make it work:
- go to https://nominatim.openstreetmap.org/
- try to find the address you were looking for with the search box
- as soon as you have the wished result, copy the search string to the Spreadsheets in the red cell sheet
- let the importer run again
- check if your change is being displayed on https://geodata.integreen-life.bz.it/edi/wms?service=WMS&version=1.1.0&request=GetMap&layers=edi%3Acreative%20points%20on%20map&bbox=11.1433265%2C46.3395693%2C11.9563958%2C46.8330732&width=768&height=466&srs=EPSG%3A4326&format=application/openlayers
and if the cell is still displayed in red.

### Features for the future
- additional prefixes for validation (email,address,website ...)
- handle data without position information
- do further quality checks on language
- push for Sync

## Developer guide

### Configuration
To make the app work properly you need to make configuration and can do optional configurations.

#### Authentication
Before you start you need to create an app in google play API console with a google account and get oauth authentication credentials. Google it or follow this guide: https://support.google.com/googleapi/answer/6158849.
Put the client_secret.json you will get in the folder`src/main/resources/META-INF/spring/`. Start the application and the app will prompt you an URL to finish the authorization process. Click on it an use the account where you registered the app. The app will now create a credentials folder on the root of you project in which it will place your access token. You can move it to another place if you prefer to keep it for longer(in case your project folder gets removed) by modifying the property `credentialsFolder` in `src/main/resources/META-INF/spring/application.properties`
#### Spreadsheet
Currently the app uses the only the first Sheet of a Spreadsheet which can be specified in `src/main/resources/META-INF/spring/application.properties`. Furthermore the range which is red can be specified or extended to any wished range(currently A1:Z, which means each row containing data and all columns till Z)

#### Column identifier
For required columns the identifier can be changed by modifying the `header.*` values in `src/main/resources/META-INF/spring/application.properties`
#### Update frequency
In `src/main/resources/META-INF/spring/applicationContext.xml` you will find a cron expression to schedule how often the app synchronizes spreadsheet and opendatahub. Do **not** set to low intervals since it takes time to complete the whole operation and the google spreadsheet API has limits on the requests you can do. Currently we set it once a day.
### Tests


### Building
Quiet easy to build, just install maven>=3 and `mvn clean package`
