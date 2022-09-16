FROM docker.io/amd64/eclipse-temurin:17.0.4.1_1-jdk-centos7

LABEL maintainer="haoqi123 <1148648445@qq.com>"

WORKDIR /knowstreaming

ARG VERSION_MAVEN=3.6.3
ARG VERSION_KM=v3.0.0-beta.2
ARG INIT_FILE=./init/*
ARG CONFIG_FILE=./conf/*

COPY ${INIT_FILE} /
COPY ${CONFIG_FILE} /conf/

# SHELL ["/bin/bash", "-o", "pipefail", "-c"]

RUN yum install -y git

RUN curl -fsSLO --compressed "https://dlcdn.apache.org/maven/maven-3/${VERSION_MAVEN}/binaries/apache-maven-${VERSION_MAVEN}-bin.tar.gz" \
  && mkdir maven/ \
  && tar -zxf "apache-maven-${VERSION_MAVEN}-bin.tar.gz" -C maven/ --strip-components=1 --no-same-owner \
  && rm -rf /knowstreaming/*.tar.gz \
  && ln -s /knowstreaming/maven/bin/mvn /usr/local/bin/mvn \
#  && curl -fsSLO --compressed https://github.com/didi/KnowStreaming/archive/refs/tags/${VERSION_KM}.zip \
  && git clone https://github.com/didi/KnowStreaming.git \
#  && unzip -o -q master.zip \
  && cd KnowStreaming && git checkout ${VERSION_KM} \
  && sed -i "/km-console/d" ./pom.xml \
  && mvn -U clean package -Dmaven.test.skip=true \
  && cd / \
  && mv /knowstreaming/KnowStreaming/km-rest/target/ks-km.jar /app/app.jar \
  && rm -rf /knowstreaming/* /usr/local/bin/mvn \
  && yum erase -y git && yum clean all \
  && rm -rf /root/.m2 /tmp/*

ENTRYPOINT ["/bin/bash","/start.sh"]
