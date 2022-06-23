ARG MAVEN_VERSION=3.8.4-openjdk-8-slim
ARG JAVA_VERSION=8-jdk-alpine3.9
FROM maven:${MAVEN_VERSION} AS builder
ARG CONSOLE_ENABLE=true

WORKDIR /opt
COPY . .
COPY distribution/conf/settings.xml /root/.m2/settings.xml

# whether to build console
RUN set -eux; \
    if [ $CONSOLE_ENABLE = 'false' ]; then \
        sed -i "/kafka-manager-console/d" pom.xml; \
    fi \
    && mvn -Dmaven.test.skip=true clean install -U

FROM openjdk:${JAVA_VERSION}

RUN sed -i 's/dl-cdn.alpinelinux.org/mirrors.aliyun.com/g' /etc/apk/repositories && apk add --no-cache tini

ENV TZ=Asia/Shanghai
ENV AGENT_HOME=/opt/agent/

COPY --from=builder /opt/kafka-manager-web/target/kafka-manager.jar /opt
COPY --from=builder /opt/container/dockerfiles/docker-depends/config.yaml $AGENT_HOME
COPY --from=builder /opt/container/dockerfiles/docker-depends/jmx_prometheus_javaagent-0.15.0.jar $AGENT_HOME
COPY --from=builder /opt/distribution/conf/application-docker.yml /opt

WORKDIR /opt

ENV JAVA_AGENT="-javaagent:$AGENT_HOME/jmx_prometheus_javaagent-0.15.0.jar=9999:$AGENT_HOME/config.yaml"
ENV JAVA_HEAP_OPTS="-Xms1024M -Xmx1024M -Xmn100M "
ENV JAVA_OPTS="-verbose:gc  \
    -XX:MaxMetaspaceSize=256M  -XX:+DisableExplicitGC -XX:+UseStringDeduplication \
    -XX:+UseG1GC  -XX:+HeapDumpOnOutOfMemoryError   -XX:-UseContainerSupport"

EXPOSE 8080 9999

ENTRYPOINT ["tini", "--"]

CMD [ "sh", "-c", "java -jar $JAVA_AGENT $JAVA_HEAP_OPTS $JAVA_OPTS kafka-manager.jar --spring.config.location=application-docker.yml"]
