
![Logo](../assets/KnowStreamingLogo.png)


# FAQ 

- [FAQ](#faq)
  - [1、支持哪些Kafka版本？](#1支持哪些kafka版本)
  - [2、页面流量信息等无数据？](#2页面流量信息等无数据)
  - [3、`Jmx`连接失败如何解决？](#3jmx连接失败如何解决)
  - [4、有没有 API 文档？](#4有没有-api-文档)
  - [5、删除Topic成功后，为何过段时间又出现了？](#5删除topic成功后为何过段时间又出现了)
  - [6、如何在不登录的情况下，调用接口？](#6如何在不登录的情况下调用接口)

---

## 1、支持哪些Kafka版本？

- 支持 0.10+ 的Kafka版本；
- 支持 ZK 及 Raft 运行模式的Kafka版本；


&nbsp;


## 2、页面流量信息等无数据？

- 1、`Broker JMX`未正确开启

可以参看：[Jmx连接配置&问题解决说明文档](../dev_guide/解决连接JMX失败.md) 

- 2、`ES` 存在问题

建议使用`ES 7.6`版本，同时创建近7天的索引，具体见：[单机部署手册](../install_guide/单机部署手册.md) 中的ES索引模版及索引创建。


&nbsp;


## 3、`Jmx`连接失败如何解决？

- 参看 [Jmx连接配置&问题解决说明文档](../dev_guide/解决连接JMX失败.md) 说明。


&nbsp;


## 4、有没有 API 文档？

`KnowStreaming` 采用 Swagger 进行 API 说明，在启动 KnowStreaming 服务之后，就可以从下面地址看到。 

Swagger-API地址： [http://IP:PORT/swagger-ui.html#/](http://IP:PORT/swagger-ui.html#/)


&nbsp;


## 5、删除Topic成功后，为何过段时间又出现了？

**原因说明：**

`KnowStreaming` 会去请求Topic的endoffset信息，要获取这个信息就需要发送metadata请求，发送metadata请求的时候，如果集群允许自动创建Topic，那么当Topic不存在时，就会自动将该Topic创建出来。


**问题解决：**

因为在 `KnowStreaming` 上，禁止Kafka客户端内部元信息获取这个动作非常的难做到，因此短时间内这个问题不好从 `KnowStreaming` 上解决。

当然，对于不存在的Topic，`KnowStreaming` 是不会进行元信息请求的，因此也不用担心会莫名其妙的创建一个Topic出来。

但是，另外一点，对于开启允许Topic自动创建的集群，建议是关闭该功能，开启是非常危险的，如果关闭之后，`KnowStreaming` 也不会有这个问题。

最后这里举个开启这个配置后，非常危险的代码例子吧：

```java
for (int i= 0; i < 100000; ++i) {
    // 如果是客户端类似这样写的，那么一启动，那么将创建10万个Topic出来，集群元信息瞬间爆炸，controller可能就不可服务了。
    producer.send(new ProducerRecord<String, String>("know_streaming" + i,"hello logi_km"));
}
```


&nbsp;


## 6、如何在不登录的情况下，调用接口？

具体见：[免登录调用接口](../dev_guide/免登录调用接口.md)


