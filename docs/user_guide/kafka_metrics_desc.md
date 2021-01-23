
---

![kafka-manager-logo](../assets/images/common/logo_name.png)

**一站式`Apache Kafka`集群指标监控与运维管控平台**

--- 



# Topic 指标说明

## 1. 实时流量指标说明


| 指标名称|  单位| 指标含义|
|-- |---- |---|
| messagesIn| 条/s   | 每秒发送到kafka的消息条数  |
| byteIn| B/s               | 每秒发送到kafka的字节数   |
| byteOut| B/s              | 每秒流出kafka的字节数（所有消费组消费的流量，如果是Kafka版本较低，这个还包括副本同步的流量） |
| byteRejected| B/s         | 每秒被拒绝的字节数    |
| failedFetchRequest| qps   | 每秒拉取失败的请求数     |
| failedProduceRequest| qps | 每秒发送失败的请求数    |
| totalProduceRequest| qps  | 每秒总共发送的请求数，与messagesIn的区别是一个是发送请求里面可能会有多条消息  |
| totalFetchRequest| qps    | 每秒总共拉取消息的请求数   |

&nbsp;

## 2. 历史流量指标说明

| 指标名称|  单位| 指标含义|
|-- |---- |---|
| messagesIn| 条/s   | 近一分钟每秒发送到kafka的消息条数  |
| byteIn| B/s               | 近一分钟每秒发送到kafka的字节数   |
| byteOut| B/s              | 近一分钟每秒流出kafka的字节数（所有消费组消费的流量，如果是Kafka版本较低，副本同步的流量） |
| byteRejected| B/s         | 近一分钟每秒被拒绝的字节数    |
| totalProduceRequest| qps  | 近一分钟每秒总共发送的请求数，与messagesIn的区别是一个是发送请求里面可能会有多条消息  |

&nbsp;

## 3. 实时耗时指标说明

**基于滴滴加强版Kafka引擎的特性，可以获取Broker的实时耗时信息和历史耗时信息**

| 指标名称| 单位   | 指标含义  | 耗时高原因    | 解决方案|
|-- |-- |-- |-- |--|                                                 
| RequestQueueTimeMs| ms  | 请求队列排队时间                                             | 请求多，服务端处理不过来                                     | 联系运维人员处理                                          |
| LocalTimeMs| ms         | Broker本地处理时间                                           | 服务端读写数据慢，可能是读写锁竞争                           | 联系运维人员处理                                          |
| RemoteTimeMs| ms        | 请求等待远程完成时间，对于发送请求，如果ack=-1，该时间表示副本同步时间，对于消费请求，如果当前没有数据，该时间为等待新数据时间，如果请求的版本与topic存储的版本不同，需要做版本转换，也会拉高该时间 | 对于生产，ack=-1必然会导致该指标耗时高，对于消费，如果topic数据写入很慢，该指标高也正常。如果需要版本转换，该指标耗时也会高 | 对于生产，可以考虑修改ack=1，消费端问题可以联系运维人员具体分析 |
| ThrottleTimeMs| ms      | 请求限流时间                                                 | 生产／消费被限流                                             | 申请提升限流值                                               |
| ResponseQueueTimeMs| ms | 响应队列排队时间                                             | 响应多，服务端处理不过来                                     | 联系运维人员处理                                          |
| ResponseSendTimeMs| ms  | 响应返回客户端时间                                           | 1：下游消费能力差，导致向consumer发送数据时写网络缓冲区过慢；2：消费lag过大，一直从磁盘读取数据 | 1:提升客户端消费性能；2: 联系运维人员确认是否读取磁盘问题 |
| TotalTimeMs| ms         | 接收到请求到完成总时间，理论上该时间等于上述六项时间之和，但由于各时间都是单独统计，总时间只是约等于上述六部分时间之和 | 上面六项有些耗时高                                           | 具体针对高的指标解决                                         |

**备注：由于kafka消费端实现方式，消费端一次会发送多个Fetch请求，在接收到一个Response之后就会开始处理数据，使Broker端返回其他Response等待，因此ResponseSendTimeMs并不完全是服务端发送时间，有时会包含一部分消费端处理数据时间**

## 4. 历史耗时指标说明

**基于滴滴加强版Kafka引擎的特性，可以获取Broker的实时耗时信息和历史耗时信息**

| 指标名称|  单位| 指标含义|
|-- | ---- |---|
| produceRequestTime99thPercentile|ms|Topic近一分钟发送99分位耗时|
| fetchRequestTime99thPercentile|ms|Topic近一分钟拉取99分位耗时|
| produceRequestTime95thPercentile|ms|Topic近一分钟发送95分位耗时|
| fetchRequestTime95thPercentile|ms|Topic近一分钟拉取95分位耗时|
| produceRequestTime75thPercentile|ms|Topic近一分钟发送75分位耗时|
| fetchRequestTime75thPercentile|ms|Topic近一分钟拉取75分位耗时|
| produceRequestTime50thPercentile|ms|Topic近一分钟发送50分位耗时|
| fetchRequestTime50thPercentile|ms|Topic近一分钟拉取50分位耗时|

