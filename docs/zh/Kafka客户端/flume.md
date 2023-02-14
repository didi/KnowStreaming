
![kafka-manager-logo](../../assets/images/common/logo_name.png)

**一站式`Apache Kafka`集群指标监控与运维管控平台**

# Flume 接入 Kafka

## 配置

```shell
a1.sources = avro-source
a1.sinks = kafka-sink
a1.channels = memory-channel
 
a1.sources.avro-source.type = netcat
a1.sources.avro-source.bind = localhost
a1.sources.avro-source.port = 44444

a1.sinks.kafka-sink.type = org.apache.flume.sink.kafka.KafkaSink
a1.sinks.kafka-sink.kafka.bootstrap.servers = 192.168.0.1:9093
a1.sinks.kafka-sink.kafka.topic = my_topic

# 如果接入非安全管控时, 则删除下面三行配置，如果是consumer，则将producer修改为consumer
a1.sinks.kafka-sink.kafka.producer.security.protocol = SASL_PLAINTEXT
a1.sinks.kafka-sink.kafka.producer.sasl.mechanism = PLAIN
a1.sinks.kafka-sink.kafka.producer.sasl.jaas.config = org.apache.kafka.common.security.plain.PlainLoginModule required \
	username="{clusterId}.{appId}" \
	password="{password}";

# sasl.jaas.config 例子
# a1.sinks.kafka-sink.kafka.producer.sasl.jaas.config = org.apache.kafka.common.security.plain.PlainLoginModule required \
#	username="8.appId_000855_cn" \
#	password="wzJ80zSL3xv4";

a1.channels.memory-channel.type = memory
a1.sources.avro-source.channels = memory-channel
a1.sinks.kafka-sink.channel = memory-channel
```

## 引用

- [Flume 用户手册](http://flume.apache.org/releases/content/1.9.0/FlumeUserGuide.html)
