FROM maven:3.6.3-jdk-8 AS builder

WORKDIR /build

COPY pom.xml .
COPY km-common/pom.xml km-common/
COPY km-persistence/pom.xml km-persistence/
COPY km-core/pom.xml km-core/
COPY km-biz/pom.xml km-biz/
COPY km-extends/km-account/pom.xml km-extends/km-account/
COPY km-extends/km-monitor/pom.xml km-extends/km-monitor/
COPY km-task/pom.xml km-task/
COPY km-collector/pom.xml km-collector/
COPY km-rest/pom.xml km-rest/
COPY km-dist/pom.xml km-dist/
COPY km-console/pom.xml km-console/

RUN mvn dependency:go-offline -B 2>/dev/null || true

COPY . .

RUN mvn -U clean package -Dmaven.test.skip=true -B

FROM openjdk:8-jre-slim AS backend

LABEL maintainer="KnowStreaming"
LABEL description="Know Streaming - Kafka Management Platform - Backend"

RUN apt-get update && apt-get install -y --no-install-recommends curl && rm -rf /var/lib/apt/lists/*

RUN mkdir -p /opt/ks/conf /opt/ks/libs /opt/ks/logs

COPY --from=builder /build/km-rest/target/ks-km.jar /opt/ks/libs/ks-km.jar
COPY docker/manager/application.yml /opt/ks/conf/application.yml
COPY docker/manager/entrypoint.sh /opt/ks/entrypoint.sh
COPY docker/manager/logback-spring.xml /opt/ks/conf/logback-spring.xml

RUN chmod +x /opt/ks/entrypoint.sh

EXPOSE 80

WORKDIR /opt/ks

ENTRYPOINT ["/opt/ks/entrypoint.sh"]

FROM nginx:1.18 AS ui

LABEL description="Know Streaming - Kafka Management Platform - UI"

RUN rm -rf /etc/nginx/conf.d/default.conf

COPY --from=builder /build/km-rest/src/main/resources/templates /pub
COPY docker/nginx/knowstreaming.conf /etc/nginx/conf.d/default.conf
