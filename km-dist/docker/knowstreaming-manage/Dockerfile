FROM bitnami/java:11
ENV JAR_FILE=./jar/*.jar
ENV INIT_ES_FILE=./esinit/*
ENV CONFIG_FILE=./conf/*
COPY ${JAR_FILE} /app/app.jar
COPY ${INIT_ES_FILE} /
COPY ${CONFIG_FILE} /conf/

ENTRYPOINT ["/bin/bash","/es.sh"]
CMD ["/bin/bash","/es.sh"]
#ENTRYPOINT java -jar /app/app.jar