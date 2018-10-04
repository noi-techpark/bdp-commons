FROM debian:9

RUN groupadd --gid 113 jenkins && \
    useradd --uid 109 --gid 113 jenkins

RUN apt-get update && \
    apt-get install -y git openjdk-8-jdk maven

COPY entrypoint.sh /entrypoint.sh

ENTRYPOINT [ "/entrypoint.sh" ]
