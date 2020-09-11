FROM maven:3-jdk-8-alpine as base

RUN mkdir -p /code

# Dev
FROM base as dev

# Build
FROM base as build

WORKDIR /code

COPY src /code/src
COPY pom.xml /code/pom.xml
RUN mvn package -DfinalName=ROOT

# Run
FROM tomcat:8.5.57-jdk8-openjdk-buster as run
COPY --from=build /code/target/ROOT.war /usr/local/tomcat/webapps/ROOT.war
