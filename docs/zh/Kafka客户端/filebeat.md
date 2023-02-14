
![kafka-manager-logo](../../assets/images/common/logo_name.png)

**一站式`Apache Kafka`集群指标监控与运维管控平台**

# Filebeat 接入 Kafka

## 配置

```shell
output.kafka:
  hosts: ["192.168.0.1:9093"]

  username: {clusterId}.{appId}  # example 8.appId_000855_cn
  password: {password}           # example wzJ80zSL3xv4

  topic: "my_topic"
  partition.round_robin:
    reachable_only: false

  required_acks: 1
  compression: lz4
  max_message_bytes: 100000
```

