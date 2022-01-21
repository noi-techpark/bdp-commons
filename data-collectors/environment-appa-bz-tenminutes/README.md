# Open Data Hub - Data collector for Air Quality Data

[![CI environment-appa-bz-tenminutes](https://github.com/noi-techpark/bdp-commons/actions/workflows/ci-environment-appa-bz-tenminutes.yml/badge.svg)](https://github.com/noi-techpark/bdp-commons/actions/workflows/ci-environment-appa-bz-tenminutes.yml)

This is a **data collector for air-quality** information near South Tyrolean
streets taken from a SFTP server and provided by APPABZ. We download the
station details from the [Open Data Portal](http://dati.retecivica.bz.it).

Important Information:
  - [SFTP server setup](https://github.com/idm-suedtirol/documentation/wiki/SFTP-server-setup-on-ec2-instance) or [this](https://blog.e-zest.com/setting-up-sftp-server-on-amazon-ec2) guide which not in a private repo
  - [Mapping configuration](https://github.com/idm-suedtirol/bdp-commons/blob/master/data-collectors/airquality-appabz/infos/mapping-files/README.md)
  - [Parser Generator ANTLR4](http://www.antlr.org/)

Configuration:
  - See `src/main/resources/META-INF/spring/application.properties`
  - See `src/main/resources/META-INF/spring/applicationContext.xml`
  - Logsystem: `src/main/resources/log4j2.properties` (Make sure the
    log-files are writable)
  - Make sure you have sftp credentials installed for your tomcat
    user, usually in `/usr/share/tomcat8/.ssh/id_rsa_sftp`. Do not
    forget to set the correct read permissions for the tomcat user,
    ex. `chown tomcat8:tomcat8 /usr/share/tomcat8/.ssh/id_rsa_sftp`.

NB: **If you update your SFTP server**, do not forget to also put the new
server's public host key into `src/main/resources/META-INF/.ssh/known_hosts`.
Probably, you also need to update `id_rsa_sftp` and corresponding passphrases
inside `application.properties`.
