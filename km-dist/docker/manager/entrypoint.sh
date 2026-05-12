#!/bin/bash
set -e

CONF_FILE="/opt/ks/conf/application.yml"

sed -i "s|SERVER_MYSQL_ADDRESS|${SERVER_MYSQL_ADDRESS:-knowstreaming-mysql:3306}|g" "$CONF_FILE"
sed -i "s|SERVER_MYSQL_DB|${SERVER_MYSQL_DB:-know_streaming}|g" "$CONF_FILE"
sed -i "s|SERVER_MYSQL_USER|${SERVER_MYSQL_USER:-root}|g" "$CONF_FILE"
sed -i "s|SERVER_MYSQL_PASSWORD|${SERVER_MYSQL_PASSWORD:-admin2022_}|g" "$CONF_FILE"
sed -i "s|SERVER_ES_ADDRESS|${SERVER_ES_ADDRESS:-elasticsearch-single:9200}|g" "$CONF_FILE"

JAVA_OPTS="${JAVA_OPTS:--Xmx1g -Xms1g}"

exec java -server ${JAVA_OPTS} \
    -XX:+UseStringDeduplication \
    -Dfile.encoding=UTF-8 \
    -Djava.security.egd=file:/dev/./urandom \
    -Duser.timezone=GMT+08 \
    -XX:+HeapDumpOnOutOfMemoryError \
    -XX:HeapDumpPath=/opt/ks/logs \
    -XX:ErrorFile=/opt/ks/logs/hs_err_pid%p.log \
    -jar /opt/ks/libs/ks-km.jar \
    --spring.config.location="$CONF_FILE" \
    --logging.config=/opt/ks/conf/logback-spring.xml
