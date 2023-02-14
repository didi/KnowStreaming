# Kafka生产者客户端——整体概述

- [Kafka生产者客户端——整体概述](#kafka生产者客户端整体概述)
  - [1、前言](#1前言)
  - [2、使用例子](#2使用例子)
  - [3、数据流转](#3数据流转)
  - [4、类图](#4类图)
  - [5、总结](#5总结)

---

## 1、前言

本次分享，将会基于社区Kafka 2.5版本，简单介绍下生产者的设计架构与消息发送流程。

---

## 2、使用例子

开始分享前，我们先来看一个简单的生产者客户端的代码示例:

```java
public class ProducerTest {  
    public static void main(String[] args) {
        // 生产者客户端配置
        Properties props = new Properties(); 
        props.put("bootstrap.servers", "xxx.xxx.xxx.xxx:9092");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("compression.type", "lz4"); 
        props.put("linger.ms", 500);
        props.put("batch.size", 100000);

        // 创建Kafka生产者客户端
        Producer<String, String> producer = new KafkaProducer<>(props);
        
        String topicName = "logi-km";
        String msg = "hello kafka";

        // 往指定Topic发送数据
        for (int i = 0; i < 1000; i++) {
            producer.send(new ProducerRecord<String, String>(topicName, msg));
        }

        // 关闭生产者客户端
        producer.close();
    }
}
```

---

## 3、数据流转

生产者客户端的使用例子还是非常简单的，包含配置等代码，仅有短短的50行代码，那么这么一个简单的例子，背后是如何将数据发送到Kafka的呢？

这里之前有个同学画了一个图，觉得还是非常清晰的，本文就直接拷贝过来了。

&nbsp;

从这个图中，我们可以很清晰的看到，生产者客户端主要有两个线程，分别是主线程(主线程允许有多个)和Sender线程(一个客户端仅有一个)。

- 主线程：将数据数据生产到`RecordAccumulator`中；
- Sender线程：从`RecordAccumulator`获取数据，然后通过`Networkclient`将数据发送给Kafka集群；

![producer_send_progress](./assets/producer_send_progress.png)

---

## 4、类图

数据流转中的组件：
- ProducerInterceptors：拦截器；
- Serializer：序列化；
- Partitioner：分区器；
- RecordAccumulator：消息累加器；
- 


## 5、总结

本节概要的给大家介绍了一下Kafka生产者客户端，其中非常多的细节没有进行分析，比如：

1. 日常问题：如何做消息有序性、常见异常原因及如何解决；
2. 架构合理：是否会导致Broker出现热点、
3. 性能消耗：CPU和内存的消耗是否合理；
