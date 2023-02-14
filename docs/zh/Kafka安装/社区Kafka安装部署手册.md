# 社区Kafka部署手册

[TOC]

## 1、前言

假设我们要部署三台Kafka，机器IP分别为IP1、IP2、IP3。

如果部署五台的时候，过程也类似。


## 2、部署

**步骤一：环境初始化**

- 安装JDK-11，这块比较通用，不解释。
- **安装ZK，这块可以看ZK部署文档**。
- POC或测试的时候，建议在/home/kafka目录下进行安装，以使得kafka相关的组件都在一块。

**步骤二：解压安装包**

```bash

cd /home/kafka

tar xvzf kafka_2.12-2.5.1.tgz
```

**步骤三：创建数据目录**

```bash
# 创建数据目录
mkdir /home/kafka/kafka-data
```

**步骤四：修改Kafka配置**

```bash
# 进入配置文件
cd kafka_2.12-2.5.1/config

# 修改server.properties这个配置文件

broker.id=1 # 第一台机器设置为1，第二台设置为2，依次类推
delete.topic.enable=true  # 允许删除Topic
auto.create.topics.enable=true # 允许自动创建Topic，如果没有部署Kafka管控平台，那么建议开启Topic的自动创建以省略单独手动创建Topic的麻烦过程

listeners=PLAINTEXT://IP1:9092 # 该机器的服务地址

log.dirs=/home/kafka/kafka-data # 数据目录，可自定义修改

# 如果开启了Topic自动创建，同时机器>=3台，那么分区数建议默认3个，副本建议2个，以保证可靠性
num.partitions=3
default.replication.factor=2

zookeeper.connect=ZK_IP1:2181,ZK_IP2:2181,ZK_IP3:2181/apache_kafka_2_5_1   #这块是配置的ZK地址，最后的apache_kafka_2_5_1可以随意自定义，也可以就是apache_kafka_2_5_1


```

**步骤五：修改启动脚本**

```bash
# cd /home/kafka/kafka_2.12-2.5.1/bin
# 修改kafka-server-start.sh 文件内容为

#!/bin/bash
# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

if [ $# -lt 1 ];
then
        echo "USAGE: $0 [-daemon] server.properties [--override property=value]*"
        exit 1
fi
base_dir=$(dirname $0)

if [ "x$KAFKA_LOG4J_OPTS" = "x" ]; then
    export KAFKA_LOG4J_OPTS="-Dlog4j.configuration=file:$base_dir/../config/log4j.properties"
fi

if [ "x$KAFKA_HEAP_OPTS" = "x" ]; then
    export KAFKA_HEAP_OPTS="-Xmx4G -Xms4G"
    export JMX_PORT=6099
    #export KAFKA_DEBUG=debug
    #export DAEMON_MODE=true
    export DEBUG_SUSPEND_FLAG="n"
    export JAVA_DEBUG_PORT="8096"
    export GC_LOG_ENABLED=true
fi

EXTRA_ARGS=${EXTRA_ARGS-'-name kafkaServer -loggc'}

COMMAND=$1
case $COMMAND in
  -daemon)
    EXTRA_ARGS="-daemon "$EXTRA_ARGS
    shift
    ;;
  *)
    ;;
esac

exec $base_dir/kafka-run-class.sh $EXTRA_ARGS kafka.Kafka "$@"
```

&nbsp;

再将kafka-run-class.sh文件中的JMX相关配置进行修改，具体修改位置如下：

```bash
# JMX settings
if [ -z "$KAFKA_JMX_OPTS" ]; then
  KAFKA_JMX_OPTS="-Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.authenticate=false  -Dcom.sun.management.jmxremote.ssl=false "
fi

# JMX port to use
if [  $JMX_PORT ]; then
  KAFKA_JMX_OPTS="$KAFKA_JMX_OPTS -Dcom.sun.management.jmxremote.port=$JMX_PORT -Dcom.sun.management.jmxremote.rmi.port=$JMX_PORT"
fi

```


**步骤六：启动Kafka**

```bash
# 启动命令，在bin目录下执行
nohup ./kafka-server-start.sh ../config/server.properties > /dev/null 2>&1 &

# 检查是否正常启动，通过检查../logs/server.log里面是否有started日志，如果有的话，那么就启动成功了。
```


**步骤七：集群部署**

重复上面的步骤一到步骤六，在另外两台进行部署即可完成最终的部署。


## 3、验证

生产测试命令
```
./kafka-console-producer.sh --bootstrap-server IP1:9092,IP2:9092,IP3:9092 --topic logi_kafka_test
```

消费测试命令
```
./kafka-console-consumer.sh --bootstrap-server IP1:9092,IP2:9092,IP3:9092 --topic logi_kafka_test --group logi_kafka_consume_test --property enable.auto.commit=true
```

&nbsp;

以上命令都是在kafka的bin目录下执行，可以在生产的终端输入一串数据，在消费终端可以看到时就表示搭建完成了。


## 4、历史安装问题记录

### 4.1、防火墙问题

在安装的过程中，我们发现zk的状态一直是异常的，查看日志发现当前的zk和其他机器连接失败，后面关闭防火墙之后问题就解决了。
