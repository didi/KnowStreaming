/bin/bash /template.sh start

sed -i "s/SERVER_MYSQL_ADDRESS/${SERVER_MYSQL_ADDRESS}/g" /conf/application.yml
sed -i "s/SERVER_MYSQL_DB/${SERVER_MYSQL_DB}/g" /conf/application.yml
sed -i "s/SERVER_MYSQL_USER/${SERVER_MYSQL_USER}/g" /conf/application.yml
sed -i "s/SERVER_MYSQL_PASSWORD/${SERVER_MYSQL_PASSWORD}/g" /conf/application.yml
sed -i "s/SERVER_ES_ADDRESS/${SERVER_ES_ADDRESS}/g" /conf/application.yml

java ${JAVA_OPTS} -jar /app/app.jar --spring.config.location=/conf/application.yml