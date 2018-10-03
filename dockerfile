FROM debian:9

RUN apt-get update && \
    apt-get install -y git openjdk-8-jdk maven

RUN mkdir -p /var/maven/.m2 && \
    chmod -R 777 /var/maven

RUN echo "<settings><localRepository>/var/maven/.m2</localRepository></settings>" > /settings.xml
