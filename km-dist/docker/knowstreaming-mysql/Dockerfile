FROM mysql:5.7.39-oracle
EXPOSE 3306
ENV INIT_SOURCE_FILE=./sql/*.sql
#RUN mkdir /data/mysql
#RUN mkdir /var/lib/mysql-files
COPY ${INIT_SOURCE_FILE} /docker-entrypoint-initdb.d/
COPY ./*.cnf /etc/mysql/conf.d/