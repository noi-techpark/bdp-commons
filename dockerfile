FROM debian:9

RUN apt-get update && \
    apt-get install -y git openjdk-8-jdk maven

RUN echo "<settings><localRepository>/root/.m2</localRepository></settings>" > /settings.xml
