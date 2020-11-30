FROM maven:3-openjdk-8 as builder

COPY ./ /tmp/

ENV SIMULATOR_HOME=/opt/opencloud/scenario-simulator

RUN mkdir -p /opt/opencloud && unzip -d /opt/opencloud /tmp/scenario-simulator-package-2.3.1.2.zip && \
    ln -s /opt/opencloud/scenario-simulator-2.3.1.2 /opt/opencloud/scenario-simulator && \
    cp /tmp/scenario-simulator-remote.sh /opt/opencloud/scenario-simulator/ && mvn -f /tmp/pom.xml install

FROM openjdk:8

ENV SIMULATOR_HOME=/opt/opencloud/scenario-simulator

COPY --from=builder /opt/opencloud/ /opt/opencloud/

ENTRYPOINT ["/opt/opencloud/scenario-simulator/scenario-simulator-remote.sh"]
