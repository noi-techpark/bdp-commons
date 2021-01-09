# Spreadsheets Office365 DataCollector

A data collector to fetch data from an Office 365 Worksheet and write the data to the BDP.

A cron job checks if changes were made in the worksheet and downloads the new version in case changes where made.
Then the data in the sheet gets converted to the BDP.
The the Writer of the BDP writes the data to the platform


## Table of contents

- [Gettings started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Source code](#source-code)
  - [Set Up](#set-up)
    - [Microsoft Account](#microsoft-account)
    - [Azure Active Directory](#azure-active-directory)
    - [Credentials](#credentials)
    - [Excel Spreadsheet](#excel-spreadsheet)
  - [Execute without Docker](#execute-without-docker)
  - [Execute with Docker](#execute-with-docker)
- [Information](#information)

## Getting started

These instructions will get you a copy of the project up and running
on your local machine for development and testing purposes.

### Prerequisites

To build the project, the following prerequisites must be met:

- ToDo: Check the prerequisites
- Java JDK 1.8 or higher (e.g. [OpenJDK](https://openjdk.java.net/))
- [Maven](https://maven.apache.org/) 3.x
- Microsoft Business Account

If you want to run the application using [Docker](https://www.docker.com/), the environment is already set up with all dependencies for you. You only have to install [Docker](https://www.docker.com/) and [Docker Compose](https://docs.docker.com/compose/) and follow the instruction in the [dedicated section](#execute-with-docker).

### Source code

Get a copy of the repository:

```bash
git clone https://github.com/noi-techpark/bdp-commons.git
```

Change directory:

```bash
cd bdp-commons/data-collectors/spreadsheets-office365
```


### Set Up

#### Microsoft Account

You need a microsoft account to make this app works, so if you don't have one please create one.


#### Excel Spreadsheet

Create a Excel Spreadsheet in Office 365 and take note of name and the sheets you want to print.
The put the values in **application.properties**

```
SHEET_NAME=YourSpreadsheet.xlsx
```
NOTE: If you leave  SINGLE_SHEET_NAMES empty, all sheets get printed to console

#### Azure Active Directory

A new Application needs to be created in Azure Active Directories:
1. Open [Azure Admin Center](https://aad.portal.azure.com/) and Select Azure Active Directory on the left Sidebar
2. Then select App Registrations under Manager
3. Select New registration:
   - Set Name as you like
   - Set Supported account types to Accounts in any organizational directory and personal Microsoft accounts.
   - Under Redirect URI, change the dropdown to Public client/native (mobile & desktop), and set the value to 
    ```https://login.microsoftonline.com/common/oauth2/nativeclient```
    
4. Set permissions in API permissions by clicking the "+" button, selecting Graph API
    and then in Application Permissions check the following Permissions
    ```
    Users.Read.All
    Files.Read.All
    ```
5. Create a certificate to be able to call the Graph API
    Generate the private key in PEM format (used to make the certificate) and create a PKCS8 version (use by the sample application)
    ```
    openssl genrsa -out private_key.pem 2048
    openssl pkcs8 -topk8 -inform PEM -outform DER -in private_key.pem -nocrypt > pkcs8_key
    ```
    Generate a certificate using the private key.
    ```
    openssl req -new -key private_key.pem -out cert.csr
   ```
    This first command will ask for a variety of extra information, like company name, country, and a password. None of this is used by the sample, so you can set these values as nothing/anything you want
    openssl x509 -req -days 365 -in cert.csr -signkey private_key.pem -out cert.crt
    Finally, go back to the Azure portalIn the Application menu blade, click on the Certificates & secrets, in the Certificates section, upload the certificate you created.

6. Take note of Client ID, Tenant ID you can find in Overview in Active Directory and put it into **application.properties** or **.env** if using Docker.
    Put also the path to the generated certificate and key and add your Account E-Mail you used to *create the Spreadsheet*
    ```
    TENANT_ID=YOUR_TENANT_ID
    CLIENT_ID=YOUR_CLIENT_ID
    KEY_PATH=YOUR_KEY_PATH
    CERT_PATH=YOUR_CERT_PATH
    EMAIL=YOUR_MICOROSFT_EMAIL
    ```
7. Config cron to change Scheduler timing in **application.properties** or **.env** if using Docker. as you desire. When executed, the sheet gets fetched, compared and written to BDP.
    ```dtd
    CRON=0 6-20 * * 1-5
    ```
   
   
#### O-Auth with ODH
TBD

#### ODH configuration
TBD

### Execute without Docker

Copy the file `src/main/resources/application.properties` to `src/main/resources/application-local.properties` and adjust the variables that get their values from environment variables. You can take a look at the `.env.example` for some help.

Build the project:

```bash
mvn -Dspring.profiles.active=local clean install
```

Run external dependencies, such as the database:

```
docker-compose -f docker-compose.dependencies.yml up --detach
```

Run the project:

```bash
mvn -Dspring.profiles.active=local spring-boot:run
```

The service will be available at localhost and your specified server port.

To execute the test you can run the following command:

```bash
mvn clean test
```

### Execute with Docker

Copy the file `.env.example` to `.env` and adjust the configuration parameters.

Then you can start the application using the following command:

```bash
docker-compose up
```

The service will be available at localhost and your specified server port.

To execute the test you can run the following command:

```bash
docker-compose run --rm app mvn clean test
```

## Information

### Guidelines

Find [here](https://opendatahub.readthedocs.io/en/latest/guidelines.html) guidelines for developers.

### Support

ToDo: For support, please contact [info@opendatahub.bz.it](mailto:info@opendatahub.bz.it).

### Contributing

If you'd like to contribute, please follow the following instructions:

- Fork the repository.

- Checkout a topic branch from the `development` branch.

- Make sure the tests are passing.

- Create a pull request against the `development` branch.

A more detailed description can be found here: [https://github.com/noi-techpark/documentation/blob/master/contributors.md](https://github.com/noi-techpark/documentation/blob/master/contributors.md).

### Documentation

More documentation can be found at [https://opendatahub.readthedocs.io/en/latest/index.html](https://opendatahub.readthedocs.io/en/latest/index.html).

### License

The code in this project is licensed under the GNU AFFERO GENERAL PUBLIC LICENSE Version 3 license. See the [LICENSE.md](LICENSE.md) file for more information.
