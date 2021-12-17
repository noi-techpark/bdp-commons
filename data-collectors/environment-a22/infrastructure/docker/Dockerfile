FROM maven:3-jdk-8-alpine as base

RUN mkdir -p /code

# Dev
FROM base as dev

# Build
FROM base as build

WORKDIR /code

COPY src /code/src
COPY pom.xml /code/pom.xml
RUN mvn -B package -DfinalName=ROOT

# Archive
FROM tomcat:8.5-jdk8-openjdk-slim-buster as archive
COPY --from=build /code/target/ROOT.war /usr/local/tomcat/webapps/ROOT.war

# Run
FROM archive as run
CMD ["catalina.sh", "run"]
