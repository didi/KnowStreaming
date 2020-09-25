FROM java:8
MAINTAINER xuzhengxi
ENV LANG=C.UTF-8 LC_ALL=C.UTF-8
ADD ../../web/target/kafka-manager-web-1.1.0-SNAPSHOT.jar kafka-manager-web.jar
ADD ./application.yml application.yml
ENTRYPOINT ["java","-jar","/kafka-manager-web.jar","--spring.config.location=./application.yml"]
EXPOSE 8080