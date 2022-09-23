sed -i "s/SERVER_MYSQL_ADDRESS/${SERVER_MYSQL_ADDRESS}/g" /conf/application.yml
sed -i "s/SERVER_MYSQL_DB/${SERVER_MYSQL_DB}/g" /conf/application.yml
sed -i "s/SERVER_MYSQL_USER/${SERVER_MYSQL_USER}/g" /conf/application.yml
sed -i "s/SERVER_MYSQL_PASSWORD/${SERVER_MYSQL_PASSWORD}/g" /conf/application.yml
sed -i "s/SERVER_ES_ADDRESS/${SERVER_ES_ADDRESS}/g" /conf/application.yml

java -server ${JAVA_OPTS} -XX:+UseStringDeduplication -Dfile.encoding=UTF-8 -Djava.security.egd=file:/dev/./urandom -Duser.timezone=GMT+08 -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/logs -XX:ErrorFile=/logs/jvm/hs_err_pid%p.log -jar /km-rest.jar --spring.config.location=/conf/application.yml
