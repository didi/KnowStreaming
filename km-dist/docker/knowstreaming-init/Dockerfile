FROM debian:stable-20220912-slim
LABEL org.opencontainers.image.authors="haoqi123"
ARG INIT_ES_FILE=./init/*
COPY ${INIT_ES_FILE} /
ENV TZ=Asia/Shanghai

RUN apt-get update \
    && apt-get install -y curl

ENTRYPOINT ["/bin/bash","/template.sh","start"]