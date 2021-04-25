FROM openjdk:16-jdk-alpine3.13

LABEL author="yangvipguang"

ENV  VERSION 2.3.1

RUN sed -i 's/dl-cdn.alpinelinux.org/mirrors.aliyun.com/g' /etc/apk/repositories
RUN apk add --no-cache  --virtual .build-deps \
    font-adobe-100dpi \
    ttf-dejavu \
    fontconfig \ 
    curl \
    apr \
    apr-util \
    apr-dev \
    tomcat-native \
    && apk del .build-deps

RUN apk add --no-cache tini




ENV AGENT_HOME /opt/agent/

WORKDIR /tmp

COPY $JAR_PATH/kafka-manager.jar app.jar
# COPY application.yml     application.yml   ##默认使用helm 挂载，防止敏感配置泄露

COPY docker-depends/config.yaml    $AGENT_HOME
COPY docker-depends/jmx_prometheus_javaagent-0.15.0.jar $AGENT_HOME

ENV JAVA_AGENT="-javaagent:$AGENT_HOME/jmx_prometheus_javaagent-0.15.0.jar=9999:$AGENT_HOME/config.yaml"
ENV JAVA_HEAP_OPTS="-Xms1024M -Xmx1024M -Xmn100M "
ENV JAVA_OPTS="-verbose:gc  \
               -XX:MaxMetaspaceSize=256M  -XX:+DisableExplicitGC -XX:+UseStringDeduplication \
               -XX:+UseG1GC  -XX:+HeapDumpOnOutOfMemoryError   -XX:-UseContainerSupport" 
EXPOSE 8080  9999

ENTRYPOINT ["tini", "--"]

CMD ["sh","-c","java     -jar  $JAVA_AGENT  $JAVA_HEAP_OPTS  $JAVA_OPTS app.jar --spring.config.location=application.yml"]
