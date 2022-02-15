FROM mysql:5.7.37

COPY mysqld.cnf /etc/mysql/mysql.conf.d/
ENV TZ=Asia/Shanghai
ENV MYSQL_ROOT_PASSWORD=root

RUN apt-get update \
    && apt -y install wget \
    && wget https://ghproxy.com/https://raw.githubusercontent.com/didi/LogiKM/master/distribution/conf/create_mysql_table.sql -O /docker-entrypoint-initdb.d/create_mysql_table.sql

EXPOSE 3306

VOLUME ["/var/lib/mysql"]