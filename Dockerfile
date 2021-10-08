FROM maven:3-openjdk-11 as builder

COPY ./ /tmp/

ENV SIMULATOR_HOME=/opt/opencloud/scenario-simulator

RUN mkdir -p /opt/opencloud && unzip -d /opt/opencloud /tmp/scenario-simulator-package-3.0.0.1.zip && \
    ln -s /opt/opencloud/scenario-simulator-3.0.0.1 /opt/opencloud/scenario-simulator && \
    cp /tmp/scenario-simulator-remote.sh /opt/opencloud/scenario-simulator/ && mvn -f /tmp/pom.xml install

FROM openjdk:11

ENV SIMULATOR_HOME=/opt/opencloud/scenario-simulator

COPY --from=builder /opt/opencloud/ /opt/opencloud/

ENTRYPOINT ["/opt/opencloud/scenario-simulator/scenario-simulator-remote.sh"]
