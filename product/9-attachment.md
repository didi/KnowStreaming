---
order: 9
title: '9. 附录'
toc: menu
---

## 9.1、JMX-连接失败问题解决

- [9.1、JMX-连接失败问题解决](#91jmx-连接失败问题解决)
  - [9.1.1、问题&说明](#911问题说明)
  - [9.1.2、解决方法](#912解决方法)
  - [9.1.3、解决方法 —— 认证的 JMX](#913解决方法--认证的-jmx)
- [9.2、新旧版本对比](#92新旧版本对比)
  - [9.2.1、全新的设计理念](#921全新的设计理念)
  - [9.2.2、产品名称&协议](#922产品名称协议)
  - [9.2.3、功能架构](#923功能架构)
  - [9.2.4、功能变更](#924功能变更)

集群正常接入 Logi-KafkaManager 之后，即可以看到集群的 Broker 列表，此时如果查看不了 Topic 的实时流量，或者是 Broker 的实时流量信息时，那么大概率就是 JMX 连接的问题了。

下面我们按照步骤来一步一步的检查。

### 9.1.1、问题说明

**类型一：JMX 配置未开启**

未开启时，直接到`2、解决方法`查看如何开启即可。

![check_jmx_opened](http://img-ys011.didistatic.com/static/dc2img/do1_dRX6UHE2IUSHqsN95DGb)

**类型二：配置错误**

`JMX`端口已经开启的情况下，有的时候开启的配置不正确，此时也会导致出现连接失败的问题。这里大概列举几种原因：

- `JMX`配置错误：见`2、解决方法`。
- 存在防火墙或者网络限制：网络通的另外一台机器`telnet`试一下看是否可以连接上。
- 需要进行用户名及密码的认证：见`3、解决方法 —— 认证的JMX`。

错误日志例子：

```
# 错误一： 错误提示的是真实的IP，这样的话基本就是JMX配置的有问题了。
2021-01-27 10:06:20.730 ERROR 50901 --- [ics-Thread-1-62] c.x.k.m.c.utils.jmx.JmxConnectorWrap     : JMX connect exception, host:192.168.0.1 port:9999.
java.rmi.ConnectException: Connection refused to host: 192.168.0.1; nested exception is:


# 错误二：错误提示的是127.0.0.1这个IP，这个是机器的hostname配置的可能有问题。
2021-01-27 10:06:20.730 ERROR 50901 --- [ics-Thread-1-62] c.x.k.m.c.utils.jmx.JmxConnectorWrap     : JMX connect exception, host:127.0.0.1 port:9999.
java.rmi.ConnectException: Connection refused to host: 127.0.0.1;; nested exception is:
```

**类型三：连接特定 IP**

Broker 配置了内外网，而 JMX 在配置时，可能配置了内网 IP 或者外网 IP，此时 `KnowStreaming` 需要连接到特定网络的 IP 才可以进行访问。

比如：

Broker 在 ZK 的存储结构如下所示，我们期望连接到 `endpoints` 中标记为 `INTERNAL` 的地址，但是 `KnowStreaming` 却连接了 `EXTERNAL` 的地址，此时可以看 `4、解决方法 —— JMX连接特定网络` 进行解决。

```json
{
  "listener_security_protocol_map": {
    "EXTERNAL": "SASL_PLAINTEXT",
    "INTERNAL": "SASL_PLAINTEXT"
  },
  "endpoints": ["EXTERNAL://192.168.0.1:7092", "INTERNAL://192.168.0.2:7093"],
  "jmx_port": 8099,
  "host": "192.168.0.1",
  "timestamp": "1627289710439",
  "port": -1,
  "version": 4
}
```

### 9.1.2、解决方法

这里仅介绍一下比较通用的解决方式，如若有更好的方式，欢迎大家指导告知一下。

修改`kafka-server-start.sh`文件：

```
# 在这个下面增加JMX端口的配置
if [ "x$KAFKA_HEAP_OPTS" = "x" ]; then
    export KAFKA_HEAP_OPTS="-Xmx1G -Xms1G"
    export JMX_PORT=9999  # 增加这个配置, 这里的数值并不一定是要9999
fi
```

&nbsp;

修改`kafka-run-class.sh`文件

```
# JMX settings
if [ -z "$KAFKA_JMX_OPTS" ]; then
  KAFKA_JMX_OPTS="-Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.authenticate=false  -Dcom.sun.management.jmxremote.ssl=false -Djava.rmi.server.hostname=${当前机器的IP}"
fi

# JMX port to use
if [  $JMX_PORT ]; then
  KAFKA_JMX_OPTS="$KAFKA_JMX_OPTS -Dcom.sun.management.jmxremote.port=$JMX_PORT -Dcom.sun.management.jmxremote.rmi.port=$JMX_PORT"
fi
```

### 9.1.3、解决方法 —— 认证的 JMX

如果您是直接看的这个部分，建议先看一下上一节：`2、解决方法`以确保`JMX`的配置没有问题了。

在`JMX`的配置等都没有问题的情况下，如果是因为认证的原因导致连接不了的，可以在集群接入界面配置你的`JMX`认证信息。

<img src='http://img-ys011.didistatic.com/static/dc2img/do1_EUU352qMEX1Jdp7pxizp' width=350>

### 9.1.4、解决方法 —— JMX 连接特定网络

可以手动往`ks_km_physical_cluster`表的`jmx_properties`字段增加一个`useWhichEndpoint`字段，从而控制 `KnowStreaming` 连接到特定的 JMX IP 及 PORT。

`jmx_properties`格式：

```json
{
    "maxConn": 100,           # KM对单台Broker的最大JMX连接数
    "username": "xxxxx",     # 用户名，可以不填写
    "password": "xxxx",      # 密码，可以不填写
    "openSSL": true,         # 开启SSL, true表示开启ssl, false表示关闭
    "useWhichEndpoint": "EXTERNAL"  #指定要连接的网络名称，填写EXTERNAL就是连接endpoints里面的EXTERNAL地址
}
```

&nbsp;

SQL 例子：

```sql
UPDATE ks_km_physical_cluster SET jmx_properties='{ "maxConn": 10, "username": "xxxxx", "password": "xxxx", "openSSL": false , "useWhichEndpoint": "xxx"}' where id={xxx};
```

注意：

- 目前此功能只支持采用 `ZK` 做分布式协调的 kafka 集群。

## 9.2、新旧版本对比

### 9.2.1、全新的设计理念

- 在 0 侵入、0 门槛的前提下提供直观 GUI 用于管理和观测 Apache Kafka®，帮助用户降低 Kafka CLI 操作门槛，轻松实现对原生 Kafka 集群的可管、可见、可掌控，提升 Kafka 使用体验和降低管理成本。
- 支持海量集群一键接入，无需任何改造，即可实现集群深度纳管，真正的 0 侵入、插件化系统设计，覆盖 0.10.x-3.x.x 众多 Kafka 版本无缝纳管。

### 9.2.2、产品名称&协议

- Know Streaming V3.0

  - 名称：Know Streaming
  - 协议：AGPL 3.0

- Logi-KM V2.x

  - 名称：Logi-KM
  - 协议：Apache License 2.0

### 9.2.3、功能架构

- Know Streaming V3.0

![text](http://img-ys011.didistatic.com/static/dc2img/do1_VQD9ke5jewpjCIWamUKV)

- Logi-KM V2.x

![text](http://img-ys011.didistatic.com/static/dc2img/do1_F211q5lVCXQCXQNzWalu)

### 9.2.4、功能变更

- 多集群管理

  - 增加健康监测体系、关键组件&指标 GUI 展示
  - 增加 2.8.x 以上 Kafka 集群接入，覆盖 0.10.x-3.x
  - 删除逻辑集群、共享集群、Region 概念

- Cluster 管理

  - 增加集群概览信息、集群配置变更记录
  - 增加 Cluster 健康分，健康检查规则支持自定义配置
  - 增加 Cluster 关键指标统计和 GUI 展示，支持自定义配置
  - 增加 Cluster 层 I/O、Disk 的 Load Reblance 功能，支持定时均衡任务（企业版）
  - 删除限流、鉴权功能
  - 删除 APPID 概念

- Broker 管理

  - 增加 Broker 健康分
  - 增加 Broker 关键指标统计和 GUI 展示，支持自定义配置
  - 增加 Broker 参数配置功能，需重启生效
  - 增加 Controller 变更记录
  - 增加 Broker Datalogs 记录
  - 删除 Leader Rebalance 功能
  - 删除 Broker 优先副本选举

- Topic 管理

  - 增加 Topic 健康分
  - 增加 Topic 关键指标统计和 GUI 展示，支持自定义配置
  - 增加 Topic 参数配置功能，可实时生效
  - 增加 Topic 批量迁移、Topic 批量扩缩副本功能
  - 增加查看系统 Topic 功能
  - 优化 Partition 分布的 GUI 展示
  - 优化 Topic Message 数据采样
  - 删除 Topic 过期概念
  - 删除 Topic 申请配额功能

- Consumer 管理

  - 优化了 ConsumerGroup 展示形式，增加 Consumer Lag 的 GUI 展示

- ACL 管理

  - 增加原生 ACL GUI 配置功能，可配置生产、消费、自定义多种组合权限
  - 增加 KafkaUser 功能，可自定义新增 KafkaUser

- 消息测试（企业版）

  - 增加生产者消息模拟器，支持 Data、Flow、Header、Options 自定义配置（企业版）
  - 增加消费者消息模拟器，支持 Data、Flow、Header、Options 自定义配置（企业版）

- Job

  - 优化 Job 模块，支持任务进度管理

- 系统管理

  - 优化用户、角色管理体系，支持自定义角色配置页面及操作权限
  - 优化审计日志信息
  - 删除多租户体系
  - 删除工单流程
