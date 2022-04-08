FROM maven:3-jdk-8-alpine as base
COPY target/ROOT.jar app.jar
CMD [ "java", "-jar", "app.jar" ]