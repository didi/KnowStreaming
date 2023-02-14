
![kafka-manager-logo](../../assets/images/common/logo_name.png)

**一站式`Apache Kafka`集群指标监控与运维管控平台**

# Confluent-Schema-Registry 接入 Kafka

## 配置

schema-registry即可以配置zookeeper的地址, 也可以配置 kafka-broker 的地址。
建议使用 kafka-broker 的地址，因为zk获取到的 kafka-broker 地址, 不一定能直接连接成功。

```shell
# 增加如下配置
kafkastore.bootstrap.servers=SASL_PLAINTEXT://127.0.0.1:9093
kafkastore.sasl.mechanism=PLAIN
kafkastore.security.protocol=SASL_PLAINTEXT

kafkastore.sasl.jaas.config=org.apache.kafka.common.security.plain.PlainLoginModule required \
  username="{clusterId}.{appId}" \
  password="{password}";
  
# example 
# kafkastore.sasl.jaas.config=org.apache.kafka.common.security.plain.PlainLoginModule required \
#  username="8.appId_000855_cn" \
#  password="12345678";
 
```
