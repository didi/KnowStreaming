FROM amd64/openjdk:17.0.2

LABEL org.opencontainers.image.authors="haoqi123"

EXPOSE 80

ENV JAR_FILE=./jar/*.jar
ENV INIT_FILE=./init/*
ENV CONFIG_FILE=./conf/*
COPY ${JAR_FILE} /app/app.jar
COPY ${INIT_FILE} /
COPY ${CONFIG_FILE} /conf/

ENTRYPOINT ["/bin/bash","/start.sh"]