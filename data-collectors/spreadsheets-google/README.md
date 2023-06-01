<!--
SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>

SPDX-License-Identifier: CC0-1.0
-->

Spreadsheets DataCollector
======================

[![CI spreadsheets-google](https://github.com/noi-techpark/bdp-commons/actions/workflows/ci-spreadsheets-google.yml/badge.svg)](https://github.com/noi-techpark/bdp-commons/actions/workflows/ci-spreadsheets-google.yml)

## User Guide
### Prerequisits

the first row of the google spreadsheet is used for the column names and has to ...
- ... be in english
- ... contain **name**,(**address** or (**longitude** and **latitude**))

### Skills
The spreadsheet reader does some magic under the hood, which clean up data and structure it in a meaningful way. As soon as the sheet gets changed, synchronization with Opendatahub will be triggered:
- if the column **description** exists it will get analyzed and each sentence gets associated to a language (en,de or it)
- each column can be prefixed with a valid ISO 639 locale(eg. de,en,it,fr,cz...); a prefixed column has always precedence on a non prefixed(do not prefix required columns. In that case create new ones) Example: How can I translate the column `city` in english,german and italian? Create 3 columns with the following names:  `de:city`,`it:city`,`en:city`. The old column can be removed or used as replacement of one of the three.
- if you have specified an address the app will try to find the correct coordinate information using Openstreetmaps data which has the correct housenumbers of all streets in southtyrol. 

Each address field **must** ...
- ...be in a single language
- ...have the format `[street-name][street-number],[postalcode][city]`
- ...contain no additional information

### What if the import failed
If after the import, the **address** cell in the spreadsheet displays with a red background, than the import of that POI failed and the following steps have to be done to make it work:
- go to https://nominatim.openstreetmap.org/
- try to find the address you were looking for with the search box
- as soon as you have the wished result, copy the search string to the Spreadsheets in the red cell sheet

### Features for the future
- additional prefixes for validation (email,address,website ...)
- handle data without position information
- do further quality checks on language

## Developer guide

### Configuration

#### Authentication
Before you start you need to create an app in google play API console with a google account and get oauth authentication credentials. Google it or follow this guide: https://support.google.com/googleapi/answer/6158849.
Put the client_secret.json you will get in the folder`src/main/resources/META-INF/spring/`. Start the application and the app will prompt you an URL to finish the authorization process. Click on it an use the account where you registered the app. The app will now create a credentials folder on the root of you project in which it will place your access token. You can move it to another place if you prefer to keep it for longer(in case your project folder gets removed) by modifying the property `credentialsFolder` in `src/main/resources/META-INF/spring/application.properties`
#### Spreadsheet
Each tab in th sheet which follows the minimal rules will be imported. Furthermore, the range which will be red can be specified or extended to any wished range(currently A1:Z, which means each row containing data and all columns till Z)

#### Column identifier
For required columns the identifier can be changed by modifying the `header.*` values in `src/main/resource


### Building
Quiet easy to build, just install maven>=3 and `mvn clean package`
To run it you will need to set specific environment variables:
'spreadsheetId','suportedLanguages','headers_nameId','headers_addressId','headers_longitudeId','headers_latitudeId','headers_metaDataId','spreadsheet_range','spreadsheet_notificationUrl','stationtype','composite_unique_key','origin'

