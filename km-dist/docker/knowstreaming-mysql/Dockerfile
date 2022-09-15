FROM mysql:5.7.39-oracle

LABEL org.opencontainers.image.authors="haoqi123"

EXPOSE 3306

ENV INIT_SOURCE_FILE=./sql/*.sql
ENV MYSQL_CNF_FILE=./conf/*.cnf
COPY ${INIT_SOURCE_FILE} /docker-entrypoint-initdb.d/
COPY ${MYSQL_CNF_FILE} /etc/mysql/conf.d/