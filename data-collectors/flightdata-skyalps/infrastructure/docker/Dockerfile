FROM maven:3-jdk-8-alpine as base
COPY target/ROOT.jar app.jar

EXPOSE 8999

CMD [ "java", "-jar", "app.jar" ]