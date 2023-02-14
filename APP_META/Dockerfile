FROM registry.xiaojukeji.com/didionline/bigdatadatabus-didi-jdk11-tomcat-nginx-centos7:stable
MAINTAINER zhuyefeng <zhuyefeng@didichuxing.com>

ENV JAVA_HOME /usr/local/jdk-11.0.2
# TODO 设置模块名字
ENV APPNAME service-discovery

RUN mkdir -p /etc/container/prestop
ADD ./APP_META/nginx/conf/nginx.conf /home/xiaoju/nginx/conf/ 
ADD ./APP_META/monit/monitrc /etc/monitrc
#ADD ./APP_META/monit/nginx.cfg /etc/monit.d/
ADD ./APP_META/990-startapp.required.sh /etc/container/init/990-startapp.required.sh
ADD ./APP_META/990-stopapp.sh /etc/container/prestop/990-stopapp.sh

RUN mkdir -p /home/xiaoju/${APPNAME} && \
    # TODO 如果tomcat容器应用需要下面这步
    #mkdir -p /home/xiaoju/tomcat/webapps && \
    chmod 0700 /etc/monitrc && \
    chmod a+x /etc/container/init/990-startapp.required.sh && \
    chmod a+x /etc/container/prestop/990-stopapp.sh

COPY ./home-xiaoju-${APPNAME} /home/xiaoju/${APPNAME}

# TODO 如果tomcat容器应用需要下面这步
#RUN ln -s /home/xiaoju/${APPNAME} /home/xiaoju/tomcat/webapps/