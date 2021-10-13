# Office 365 Spreadsheets DataCollector

A data collector to synchronize an Office 365 Worksheet hosted on a Microsoft Sharepoint site, with a Big Data Platform
using Keycloak.

For Authentication with Microsoft's Services msal4j with certificates is used.  
All further actions are handled by the Sharepoint REST API.

For Authentication with the [OpenDataHub](https://opendatahub.bz.it/) , [Keycloak](https://www.keycloak.org/) is used.  
Note: Any Big Data Platform can be used.

**Table of contents**
- [Office 365 Spreadsheets DataCollector](#office-365-spreadsheets-datacollector)
  - [Getting started](#getting-started)
    - [Prerequisites](#prerequisites)
    - [Source code](#source-code)
    - [Set Up](#set-up)
      - [Without Docker](#without-docker)
      - [With Docker](#with-docker)
      - [Create a Microsoft Sharepoint site](#create-a-microsoft-sharepoint-site)
      - [Create the Excel spreadsheet](#create-the-excel-spreadsheet)
      - [Azure Active Directory](#azure-active-directory)
      - [Keycloak](#keycloak)
    - [Execute without Docker](#execute-without-docker)
    - [Execute with Docker](#execute-with-docker)
  - [Additional information](#additional-information)
    - [Possible optimizations](#possible-optimizations)
      - [Microsoft change notifications to replace cron scheduler](#microsoft-change-notifications-to-replace-cron-scheduler)
    - [Guidelines](#guidelines)
    - [Support](#support)
    - [Contributing](#contributing)
    - [Documentation](#documentation)
    - [License](#license)

## Getting started

These instructions will get you a copy of the project up and running on your local machine for development and testing
purposes.

### Prerequisites

To build the project, the following prerequisites must be met:

- Java JDK 1.8 or higher (e.g. [OpenJDK](https://openjdk.java.net/))
- [Maven](https://maven.apache.org/) 3.x
- Microsoft Sharepoint site
- Excel Document hosted on the Sharepoint site (in the Shared Documents Folder)

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

#### Without Docker
Copy the file `src/main/resources/application.properties` to `src/main/resources/application-local.properties`.  
This file will be your environment variables file.  
Note: The environment variables in application.properties are in lower case, but in further set up instructions  
They are UPPER case. *Just leave them lower case*

#### With Docker
Copy the file `.env.example` to `.env`.  
This file will be your environment variables file.

#### Create a Microsoft Sharepoint site

Create a Sharepoint site collection with a Shared Document library.  
In the most cases you need to contact the Microsoft Administrator of your organization.  
Your created site then has a full URL. For example `https://your-organization.sharepoint.com/sites/your-site-id`

Now you can fill the environment variables file:
```
SHAREPOINT_HOST=your-organization.sharepoint.com
SHAREPOINT_SITE_ID=your-site-id
```

#### Create the Excel spreadsheet 

Create a Excel spreadsheet in the previous step created Sharepoint sites "Shared Documents" folder.  
The spreadsheet can also be inside a folder of the "Shared Documents" folder.

Write into environment variables file the full path starting from the  "Shared Documents" folder.
For example if you have the path `Shared Documents/Example/Example.xlsx` you put only `Example/Example.xlsx`  
into environment variables file:

```
SHAREPOINT_PATH_TO_DOC=Example/Example.xlsx
```

#### Azure Active Directory

A new Application needs to be created in Azure Active Directories:

1. Open [Azure Admin Center](https://aad.portal.azure.com/) and Select Azure Active Directory on the left Sidebar
2. Then select App Registrations under Manager
3. Select New registration:
    - Set Name as you likes
    - Set Supported account types to Account in any organizational directory and personal Microsoft accounts.
    - Under Redirect URI, change the dropdown to Public client/native (mobile & desktop), and set the value to
      ```https://login.microsoftonline.com/common/oauth2/nativeclient```

4. Set permissions in API permissions by clicking the "+" button, selecting Graph API and then in Application
   Permissions check the following Permissions
    ```
    Sites.Read.All
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
   Certificates section, **upload the certificate (cert.crt file) you created.**

6. Take note of ClientID, TenantID you find in Overview in Active Directory and put it into environment variables file.  
   Put also the path to the generated certificate and public key there. 
   Note: You can put the certificates in /resources/auth but also wherever you want,  
   just use an **absolute path** in environment variables files.
    ```
    TENANT_ID=your_tenant_id
    CLIENT_ID=your_tenant_id
    #put pkcs8_key public key here 
    KEY_PATH=auth/pkcs8_key
    #put cert.crt certiticate here
    CERT_PATH=auth/cert.crt
    ```
7. Config the cron annotation to change the Scheduler as you desire.  
   When executed, the last edit timestamp gets compared and if the workbook changed, the workbook gets fetched  
   and synced with the BDP.
   
    ```
    CRON=0 6-20 * * 1-5
    ```

#### Keycloak

For authentication with the Big data Platform, Keycloak O-Auth is used. 
Fill the environment variables file with your Keycloak configuration.
```
OAUTH_AUTH_URI=https://your-auth-uri.com/your-auth-uri
OAUTH_TOKEN_URI=https://your-token-uri.com/your-token-uri
OAUTH_BASE_URI=https://your-base-uri.com/your-base-uri
OAUTH_CLIENT_ID=your-client-id
OAUTH_CLIENT_NAME=your-auth-name
OAUTH_CLIENT_SECRET=your-client-secret
```

### Execute without Docker

Build the project:

```bash
mvn -Dspring.profiles.active=local clean install
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

You can start the application using the following command:

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

#### Microsoft change notifications to replace cron scheduler
The Microsoft graphs offers a [change notification system](https://docs.microsoft.com/en-us/graph/webhooks) to trigger
an application over webhooks, when changes (for example on a document) where made.

In this application a cron job scheduler is used to see if changes where made on the Excel Document,
but it could be replaced by Microsoft's change notifications.  
The cron job is used at the moment, because its simpler and more secure:

- Microsoft's Webhooks don't have any Authentication. So anybody knowing the link could trigger the Webhooks with a
  simple cURL.
- Webhooks could be secured by Firewall and IP-blocking, but Microsoft IPs change periodically without notice, so it
  would break the application, without noticing it.

See [here](https://docs.microsoft.com/en-us/graph/webhooks) for the official statement about the security issue.

StackExchange [discussion](https://sharepoint.stackexchange.com/questions/264609/does-the-microsoft-graph-support-driveitem-change-notifications-for-sharepoint-o)
about change notifications with Sharepoint.

The best case solution would be having the change notifications with Microsofts IP Addresses whitelistet,
and a low frequency cron job, that checks if the change notification service missed some changes.  
So in that case the developer/administraotr of the application gets notified, that the change notifications are not working anymore.

### Guidelines

Find [here](https://opendatahub.readthedocs.io/en/latest/guidelines.html) guidelines for developers.

### Support

For support, please contact [help@opendatahub.bz.it](mailto:help@opendatahub.bz.it).

### Contributing

If you'd like to contribute, please follow the following instructions:

- Fork the repository.

- Checkout a topic branch from th e `development` branch.

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
