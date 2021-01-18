# Office 365 Spreadsheets DataCollector

A data collector to automatically fetch data from an Office 365 Worksheet and write the data to a database.

This application was written to be used with the [OpenDataHub](https://opendatahub.bz.it/) of NOI-Tech Park, but you can
change the code and make it work with every database system you want. Just change the mapping and writing of the
extracted data from the worksheet to your database.

For Authentication with the Graph API msal4j is used. All other requests are made using the REST API.

For Authentication with the ODH, [Keycloak](https://www.keycloak.org/) is used.

A cron job checks if changes were made (comparing last change date) in the worksheet and downloads the new version in
case changes where made. Then the data in the sheet gets converted and mapped to StationDtos and then send to the ODH.

## Table of contents

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->

- [Getting started](#getting-started)
    - [Prerequisites](#prerequisites)
    - [Source code](#source-code)
    - [Set Up](#set-up)
        - [Microsoft Account](#microsoft-account)
        - [Excel Spreadsheet](#excel-spreadsheet)
        - [Azure Active Directory](#azure-active-directory)
        - [O-Auth with ODH](#o-auth-with-odh)
        - [ODH configuration](#odh-configuration)
    - [Execute without Docker](#execute-without-docker)
    - [Execute with Docker](#execute-with-docker)
- [Additional information](#additional-information)
    - [Possible optimizations](#possible-optimizations)
    - [Guidelines](#guidelines)
    - [Support](#support)
    - [Contributing](#contributing)
    - [Documentation](#documentation)
    - [License](#license)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

## Getting started

These instructions will get you a copy of the project up and running on your local machine for development and testing
purposes.

### Prerequisites

To build the project, the following prerequisites must be met:

- Java JDK 1.8 or higher (e.g. [OpenJDK](https://openjdk.java.net/))
- [Maven](https://maven.apache.org/) 3.x
- Microsoft Business Account
- Office 365 Worksheet in OneDrive

If you want to run the application using [Docker](https://www.docker.com/), the environment is already set up with all
dependencies for you. You only have to install [Docker](https://www.docker.com/)
and [Docker Compose](https://docs.docker.com/compose/) and follow the instruction in
the [dedicated section](#execute-with-docker).

### Source code

Get a copy of the repository:

```bash
git clone https://github.com/noi-techpark/bdp-commons.git
```

Change to data collector directory:

```bash
cd bdp-commons/data-collectors/spreadsheets-office365
```

### Set Up

#### Microsoft Account

You need a microsoft account to make this app works, so if you don't have one please create one.

#### Excel Spreadsheet

Create an Office 365 spreadsheet in your OneDrive folder and take note of name. The put the values in **.env**

```
SHEET_NAME=YourSpreadsheetName.xlsx
```

NOTE: If you leave SINGLE_SHEET_NAMES empty, all sheets get printed to console

#### Azure Active Directory

Attention: The Microsoft Java Auth API might change with time. If you have problems with the following guide please go
to the official [example repo](https://github.com/microsoftgraph/msgraph-sdk-java-auth) and see if changes where made.
Please consider updating also the guide here or contact the current developer of this application, to update the guides.

A new Application needs to be created in Azure Active Directories:

1. Open [Azure Admin Center](https://aad.portal.azure.com/) and Select Azure Active Directory on the left Sidebar
2. Then select App Registrations under Manager
3. Select New registration:
    - Set Name as you like
    - Set Supported account types to Accounts in any organizational directory and personal Microsoft accounts.
    - Under Redirect URI, change the dropdown to Public client/native (mobile & desktop), and set the value to
      ```https://login.microsoftonline.com/common/oauth2/nativeclient```

4. Set permissions in API permissions by clicking the "+" button, selecting Graph API and then in Application
   Permissions check the following Permissions
    ```
    Users.Read.All
    Files.Read.All
    ```
5. Create a certificate to be able to call the Graph API Generate the private key in PEM format and create a PKCS8
   version

    ```
    openssl genrsa -out private_key.pem 2048
    openssl pkcs8 -topk8 -inform PEM -outform DER -in private_key.pem -nocrypt > pkcs8_key
    ```
   Generate a certificate using the private key.
    ```
    openssl req -new -key private_key.pem -out cert.csr
   ```
   This first command will ask for a variety of extra information, like company name, country, and a password. None of
   this is used by the application, so you can set these values as nothing/anything you want
    ```
    openssl x509 -req -days 365 -in cert.csr -signkey private_key.pem -out cert.crt
   ``` 
   Finally, go back to the Azure portalIn the Application menu blade, click on the **Certificates & secrets**, in the
   Certificates section, **upload the certificate you created.**

6. Take note of Client ID, Tenant ID you can find in Overview in Active Directory and put it into **application.properties** 
   or **.env** if using Docker.
   Put also the path to the generated certificate and key and add your Microsoft account E-Mail you used to *create the Spreadsheet*.
   Note: You can put the certificates in /resources/auth but also wherever you want, just use an **absolute path** in configuration files.
    ```
    TENANT_ID=YOUR_TENANT_ID
    #pkcs8_key
    CLIENT_ID=YOUR_CLIENT_ID
    #cert.crt
    KEY_PATH=YOUR_KEY_ABSOLUTE_PATH
    CERT_PATH=YOUR_CERT_ABSOLUTE_PATH
    EMAIL=YOUR_MICOROSFT_EMAIL
    ```
7. Config cron to change Scheduler timing in **application.properties** or **.env** if using Docker. as you desire. When
   executed, the sheet gets fetched, compared and written to BDP.
    ```dtd
    CRON=0 6-20 * * 1-5
    ```

### Execute without Docker

Copy the file `src/main/resources/application.properties` to `src/main/resources/application-local.properties` and
adjust the variables that get their values from environment variables. You can take a look at the `.env.example` for
some help.

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

## Additional information

### Possible optimizations

The Microsoft graphs offers a [change notification system](https://docs.microsoft.com/en-us/graph/webhooks) to trigger
an application over webhooks that changes where made. In this application a cron job is used to make this done, but it
could be replaced by Microsoft's change notifications.

We preferred the cron job for now, because its simpler and more secure:

- Microsoft's Webhooks don't have any Authentication. So anybody knowing the link could trigger the Webhooks with a
  simple cURL.
- Webhooks could be secured by Firewall and IP-blocking, but Microsoft IPs change periodically without notice, so it
  would break the application, without noticing it.

### Guidelines

Find [here](https://opendatahub.readthedocs.io/en/latest/guidelines.html) guidelines for developers.

### Support

For support, please contact [info@opendatahub.bz.it](mailto:info@opendatahub.bz.it).

### Contributing

If you'd like to contribute, please follow the following instructions:

- Fork the repository.

- Checkout a topic branch from the `development` branch.

- Make sure the tests are passing.

- Create a pull request against the `development` branch.

A more detailed description can be found
here: [https://github.com/noi-techpark/documentation/blob/master/contributors.md](https://github.com/noi-techpark/documentation/blob/master/contributors.md)
.

### Documentation

More documentation can be found
at [https://opendatahub.readthedocs.io/en/latest/index.html](https://opendatahub.readthedocs.io/en/latest/index.html).

### License

The code in this project is licensed under the GNU AFFERO GENERAL PUBLIC LICENSE Version 3 license. See
the [LICENSE.md](LICENSE.md) file for more information.