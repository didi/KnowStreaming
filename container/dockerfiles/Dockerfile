FROM openjdk:8-jdk-alpine3.9

LABEL author="yangvipguang"

ENV  VERSION 2.1.0
ENV  JAR_PATH kafka-manager-web/target
COPY $JAR_PATH/kafka-manager-web-$VERSION-SNAPSHOT.jar /tmp/app.jar
COPY $JAR_PATH/application.yml  /km/

RUN sed -i 's/dl-cdn.alpinelinux.org/mirrors.aliyun.com/g' /etc/apk/repositories
RUN apk add --no-cache --virtual .build-deps \
    font-adobe-100dpi \
    ttf-dejavu \
    fontconfig \ 
    curl \
    apr \
    apr-util \
    apr-dev \
    tomcat-native \
    && apk del .build-deps

ENV AGENT_HOME /opt/agent/

WORKDIR /tmp
COPY docker-depends/config.yaml    $AGENT_HOME
COPY docker-depends/jmx_prometheus_javaagent-0.14.0.jar $AGENT_HOME

ENV JAVA_AGENT="-javaagent:$AGENT_HOME/jmx_prometheus_javaagent-0.14.0.jar=9999:$AGENT_HOME/config.yaml"

ENV JAVA_HEAP_OPTS="-Xms1024M -Xmx1024M -Xmn100M "

ENV JAVA_OPTS="-verbose:gc  \
               -XX:+PrintGC -XX:+PrintGCDetails -XX:+PrintHeapAtGC -Xloggc:/tmp/gc.log -XX:+PrintGCDateStamps  -XX:+PrintGCTimeStamps \
               -XX:MaxMetaspaceSize=256M  -XX:+DisableExplicitGC -XX:+UseStringDeduplication \
               -XX:+UseG1GC  -XX:+HeapDumpOnOutOfMemoryError   -XX:-UseContainerSupport" 
#-Xlog:gc -Xlog:gc* -Xlog:gc+heap=trace  -Xlog:safepoint

EXPOSE 8080  9999

ENTRYPOINT ["sh","-c","java     -jar    $JAVA_HEAP_OPTS  $JAVA_OPTS /tmp/app.jar --spring.config.location=/km/application.yml"]

## 默认不带Prometheus JMX监控，需要可以自行取消以下注释并注释上面一行默认Entrypoint 命令。
## ENTRYPOINT ["sh","-c","java     -jar  $JAVA_AGENT  $JAVA_HEAP_OPTS  $JAVA_OPTS /tmp/app.jar --spring.config.location=/km/application.yml"]

