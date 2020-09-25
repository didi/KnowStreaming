FROM fabric8/java-alpine-openjdk8-jdk
MAINTAINER xuzhengxi
ENV LANG=C.UTF-8 LC_ALL=C.UTF-8
ADD ./web/target/kafka-manager-web-1.1.0-SNAPSHOT.jar kafka-manager-web.jar
ADD ./docker/kafka-manager/application-standalone.yml application.yml
ENTRYPOINT ["java","-jar","/kafka-manager-web.jar","--spring.config.location=./application.yml"]
EXPOSE 8080