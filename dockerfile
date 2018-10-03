FROM debian:9

RUN apt-get update && \
    apt-get install -y git openjdk-8-jdk maven

RUN echo "<localRepository>/root/.m2</localRepository>" > /settings.xml
