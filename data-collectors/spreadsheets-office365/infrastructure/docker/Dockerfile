FROM maven:3-jdk-8-alpine as base

RUN mkdir -p /code

# Dev
FROM base as dev

# Build
FROM base as build

WORKDIR /code

COPY src /code/src
COPY pom.xml /code/pom.xml

RUN mvn clean package spring-boot:repackage

CMD [ "java", "-jar", "target/ROOT.jar" ]


