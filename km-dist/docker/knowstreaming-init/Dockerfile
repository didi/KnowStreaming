FROM centos:7.9.2009
LABEL org.opencontainers.image.authors="haoqi123"
ARG INIT_ES_FILE=./init/*
COPY ${INIT_ES_FILE} /
ENV TZ=Asia/Shanghai

ENTRYPOINT ["/bin/sh","/template.sh","start"]