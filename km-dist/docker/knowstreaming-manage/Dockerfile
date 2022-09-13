FROM bitnami/java:11
ARG JAVA_OPTS
ARG JAR_FILE=./jar/*.jar
ARG INIT_ES_FILE=./esinit/*
COPY ${JAR_FILE} /app/app.jar
COPY ${INIT_ES_FILE} /

ENTRYPOINT ["/bin/bash","/es.sh"]
CMD ["dumb-init","java","${JAVA_OPTS}", "-jar", "/app/app.jar"]
#ENTRYPOINT java -jar /app/app.jar