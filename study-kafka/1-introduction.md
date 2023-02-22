---
title: 1.Kafka新手入门
order: 1
toc: menu
---

## 1.1 简介

<font size=5 ><b>什么是事件流？</b></font>

> 从技术上讲，事件流是从事件源（如数据库、传感器、移动设备、云服务和软件应用程序）以事件流的形式实时捕获数据的实践；持久存储这些事件流以供以后检索；实时和回顾性地操作、处理和响应事件流；并根据需要将事件流路由到不同的目标技术。因此，事件流确保了数据的连续流动和解释，以便正确的信息在正确的时间出现在正确的位置。

<font size=5 ><b> 我可以将事件流用于什么？ </b></font>

事件流应用于 众多行业和组织的各种场景。它的许多例子包括：

> - 实时处理支付和金融交易，例如在证券交易所、银行和保险中。
> - 实时跟踪和监控汽车、卡车、车队和货运，例如在物流和汽车行业。
> - 持续捕获和分析来自物联网设备或其他设备的传感器数据，例如工厂和风电场。
> - 收集并立即响应客户互动和订单，例如零售、酒店和旅游行业以及移动应用程序。
> - 监测住院病人，预测病情变化，确保在紧急情况下及时治疗。
> - 连接、存储和提供公司不同部门产生的数据。
> - 作为数据平台、事件驱动架构和微服务的基础。

<font size=5 ><b> Apache Kafka 是一个事件流平台 </b></font>

Kafka 结合了三个关键功能，因此您可以 通过一个经过实战考验的解决方案实现端到端的事件流

1. **发布**（写入）和**订阅**（读取）事件流，包括从其他系统持续导入/导出数据 。
2. 根据需要持久可靠地 **存储事件流**。
3. 在事件发生时或回顾性 地**处理事件流**。

所有这些功能都以分布式、高度可扩展、弹性、容错和安全的方式提供。Kafka 可以部署在裸机硬件、虚拟机和容器上，也可以部署在本地和云端。您可以在自行管理 Kafka 环境和使用各种供应商提供的完全托管服务之间进行选择。

<font size=5 ><b> Apache Kafka 整体架构 </b></font>

![在这里插入图片描述](https://img-blog.csdnimg.cn/7da12223043a4882a6ffc82becd79cde.png#pic_center)

**Kafka 是一个分布式系统**，由通过高性能**TCP 网络协议进行通信**的 <font color=red>服务器</font>和 <font color=red>客户端</font>组成。它可以部署在本地和云环境中的裸机硬件、虚拟机和容器上。

**服务器：** Kafka 作为一个或多个服务器的集群运行，可以跨越多个数据中心或云区域。其中一些服务器形成存储层，称为代理(Broker)。其他服务器运行 Kafka Connect 以将数据作为事件流持续导入和导出，将 Kafka 与您现有的系统（如关系数据库以及其他 Kafka 集群）集成。为了让您实现关键任务用例，Kafka 集群具有高度可扩展性和容错性：如果其中任何一个服务器出现故障，其他服务器将接管它们的工作，以确保持续运行而不会丢失任何数据。

**客户端**：它们允许您编写分布式应用程序和微服务，以并行、大规模和容错方式读取、写入和处理事件流，即使在网络问题或机器故障的情况下也是如此。Kafka 附带了一些这样的客户端，这些客户端由 Kafka 社区提供的 数十个客户端进行了扩充：客户端可用于 Java 和 Scala，包括更高级别的 Kafka Streams 库，用于 Go、Python、C/C++ 和许多其他编程语言以及 REST API。

**主题分区与副本**

在 kafka 中有 2 个非常重要的概念, 主题（Topic）和分区（Partition）, Kafka 中的消息是以 Topic 为单位进行归类。

生产者发送消息的是必须指定消息 Topic, 消费组则订阅具体的 Topic 来消费。

**主题**（Topic）是逻辑上的概念, 它有一个或多个分区（Partition）, 消息就是存在这些具体的分区里面。

**分区**（Partition）其实也是一个逻辑上的概念, 每个主题会有一个/多个 **分区**, 我们生产的消息会追加到这些分区的 Log 文件里面, 同一个主题下的分区包含的消息是不同的。消息是以追加的形式存储在分区中。消息在被追加到分区日志文件的时候都会分配一个特定的偏移量(offset)，offset 是消息在分区中的唯一标识。**Kafka 保证分区有序,并不保证主题消息有序。**

**副本**（Replica）是分区的物理实现, 一个分区可以有多个副本, 一个分区的多个副本分为 **Leader 副本**、**Follower 副本**, **Leader 副本**负责提供读写能力，**Follower 副本**会从**Leader 副本** 同步数据用于备份。

<font color=red><b>多分区解决了 I/O 性能瓶颈问题</b></font>
<font color=red><b>多副本解决了高可用问题</b></font>

**主题与分区**

![在这里插入图片描述](https://img-blog.csdnimg.cn/51f3aafa624543cda022f76bedf40afc.png#pic_center)

1. 在消息发送到 Broker 之前,会经过一定的 [分配策略]() 来选择存储到哪个具体的分区, 合理的分配方式能够尽量的让数据更均衡的分配在各个分区上。
2. 同一个主题的多个分区可以分配在同一个 Brker 上。

**分区与副本**

![在这里插入图片描述](https://img-blog.csdnimg.cn/092bb15c14764b03ba7596f4701078cb.png#pic_center)

看上面的图示, Topic1 有 3 个分区分别是 Topic1-0、 Topic1-1、 Topic1-2
每个分区有 3 个副本(包括 Leader 副本), 均衡的分配在 3 个 Broker 中。

例如分区 **Topic-0** 的 Leader 副本在 Broker-0 上, 那么它承担着读写, Broker-1 和 Broker-2 上的 **Topic-0** Follower 副本会从 Broker-0 中的 Leader 副本去同步数据。

1. 同一个分区的多个副本不可以分配在同一个 Broker 上。
2. 分区的 Leader 副本角色可以切换, 遵循[优先副本选举]()原则
3. 只有 Leader 副本才会承担读写职责, Follower 副本用于同步备份

<font color=red><b>我们往往会在口述中把分区和副本给搞混, 一般默认情况下,我们说分区的时候潜台词说的是 Leader 副本, 说副本的时候实际上是 Follower 副本</b></font>

<font size=5 ><b> Kafka Api </b></font>

除了用于管理和任务的命令行工具外，Kafka 还为 Java 和 Scala 提供了五个核心 API：

- 用于管理和检查主题（Topic）、代理（Broker）和其他 Kafka 对象 的管理 API 。
- 将事件流发布（写入）到一个或多个 Kafka 主题 的 Producer API 。
- Consumer API 订阅（读取）一个或多个主题并处理向它们生成的事件流 。
- 用于实现流处理应用程序和微服务 的 Kafka Streams API 。它提供了更高级别的函数来处理事件流，包括转换、聚合和连接等有状态操作、窗口化、基于事件时间的处理等等。从一个或多个主题读取输入以生成一个或多个主题的输出，有效地将输入流转换为输出流。
- Kafka Connect API 用于构建和运行可重用 的数据导入/导出连接器，这些连接器从外部系统和应用程序消耗（读取）或生成（写入）事件流，以便它们可以与 Kafka 集成。例如，与 PostgreSQL 等关系数据库的连接器可能会捕获对一组表的每次更改。但是，在实践中，您通常不需要实现自己的连接器，因为 Kafka 社区已经提供了数百个即用型连接器。

## 1.2 使用场景

<font size=5 ><b> 消息队列 </b></font>

Kafka 可以很好的替代更传统的消息队列, 与大部分消息队列相比, Kafka 具有更好的吞吐量、内置的分区、复制和容错能力，这使其成为大规模消息处理应用程序的良好解决方案。

<font size=5 ><b> 网站活动跟踪 </b></font>

Kafka 的原始用例是能够将用户活动跟踪管道重建为一组实时发布-订阅源。这意味着站点活动（页面查看、搜索或用户可能采取的其他操作）将发布到中心主题，每种活动类型都有一个主题。这些订阅源可用于订阅一系列用例，包括实时处理、实时监控以及加载到 Hadoop 或离线数据仓库系统以进行离线处理和报告。
活动跟踪的数量通常非常高，因为每个用户页面查看都会生成许多活动消息。

<font size=5 ><b> 指标 </b></font>

Kafka 常用于运营监控数据。这涉及聚合来自分布式应用程序的统计数据以生成操作数据的集中提要。

<font size=5 ><b> 日志聚合 </b></font>

许多人使用 Kafka 作为日志聚合解决方案的替代品。日志聚合通常从服务器收集物理日志文件，并将它们放在一个中心位置（可能是文件服务器或 HDFS）进行处理。Kafka 抽象出文件的细节，并将日志或事件数据更清晰地抽象为消息流。这允许更低延迟的处理和更容易支持多个数据源和分布式数据消费。与 Scribe 或 Flume 等以日志为中心的系统相比，Kafka 提供同样出色的性能、由于复制而产生的更强大的持久性保证以及更低的端到端延迟。

<font size=5 ><b> 流处理 </b></font>

许多 Kafka 用户在由多个阶段组成的处理管道中处理数据，其中原始输入数据从 Kafka 主题中消费，然后聚合、丰富或以其他方式转换为新主题以供进一步消费或后续处理。例如，用于推荐新闻文章的处理管道可能会从 RSS 提要中抓取文章内容并将其发布到“文章”主题；进一步的处理可能会对该内容进行规范化或去重，并将清理后的文章内容发布到新主题；最终处理阶段可能会尝试向用户推荐此内容。此类处理管道基于各个主题创建实时数据流图。从 0.10.0.0 开始，一个轻量级但功能强大的流处理库，称为 Kafka Streams 可以在 Apache Kafka 中执行上述数据处理。除了 Kafka Streams，替代的开源流处理工具包括 Apache Storm 和 Apache Samza。

<font size=5 ><b> 事件溯源 </b></font>

事件溯源是一种应用程序设计风格，其中状态更改被记录为按时间排序的记录序列。Kafka 对非常大的存储日志数据的支持使其成为以这种风格构建的应用程序的出色后端。

<font size=5 ><b> 提交日志 </b></font>

Kafka 可以作为分布式系统的一种外部提交日志。该日志有助于在节点之间复制数据，并充当故障节点恢复其数据的重新同步机制。Kafka 中的日志压缩功能有助于支持这种用法。在这种用法中，Kafka 类似于 Apache BookKeeper 项目。

## 1.3 快速入门

<font size=5 ><b> 第 1 步：获取 KAFKA </b></font>

选择并下载 [Kafka 安装包](https://kafka.apache.org/downloads), 并解压
例如这里我下载 2.7 版本的
![在这里插入图片描述](https://img-blog.csdnimg.cn/3825171a97994b09a665790ce8399914.png)
解压

```sh

tar -xzf kafka_2.13-2.7.0.tgz
cd kafka_2.13-2.7.0
```

<font size=5 ><b> 第 2 步：启动 KAFKA 环境 </b></font>

> 注意：您的本地环境必须安装 Java 8+。

运行以下命令以按正确顺序启动所有服务：

```sh

# Start the ZooKeeper service
# Note: Soon, ZooKeeper will no longer be required by Apache Kafka.
$ bin/zookeeper-server-start.sh config/zookeeper.properties

```

打开另一个终端会话并运行：

```sh
# Start the Kafka broker service
$ bin/kafka-server-start.sh config/server.properties
```

成功启动所有服务后，您将拥有一个基本的 Kafka 环境运行并可以使用

<font size=5 ><b> 第 3 步：创建一个主题(Topic)来存储您的事件 </b></font>

Kafka 是一个分布式事件流平台，可让您跨多台机器 读取、写入、存储和处理 事件（在文档中也称为记录或 消息）。

在您编写第一个事件之前，您必须创建一个主题。打开另一个终端会话并运行：

```sh

$ bin/kafka-topics.sh --create --topic quickstart-events --bootstrap-server localhost:9092

```

查看一下该 Topic 的的详情

```sh

$ bin/kafka-topics.sh --describe --topic quickstart-events --bootstrap-server localhost:9092

Topic:quickstart-events  PartitionCount:1    ReplicationFactor:1 Configs:
    Topic: quickstart-events Partition: 0    Leader: 0   Replicas: 0 Isr: 0


```

<font size=5 ><b> 第 4 步：将一些事件写入主题 </b></font>

Kafka 客户端通过网络与 Kafka Broker 通信以写入（或读取）事件。一旦收到，Broker 将以持久和容错的方式存储事件，只要您需要 - 甚至永远。

```sh
$ bin/kafka-console-producer.sh --topic quickstart-events --bootstrap-server localhost:9092
这是第一条消息
这是第二条消息
```

您可以随时停止生产者客户端 Ctrl-C。

<font size=5 ><b> 第 5 步：读取事件 </b></font>

打开另一个终端会话并运行控制台使用者客户端以读取您刚刚创建的事件：

```sh

$ bin/kafka-console-consumer.sh --topic quickstart-events --from-beginning --bootstrap-server localhost:9092
这是第一条消息
这是第二条消息

```

您可以随时停止消费者客户端 Ctrl-C.

因为事件被持久地存储在 Kafka 中，所以它们可以被尽可能多的消费者多次读取。您可以通过打开另一个终端会话并再次重新运行上一个命令来轻松验证这一点。

<font size=5 ><b> 第 6 步：使用 KAFKA CONNECT 将数据作为事件流导入/导出 </b></font>

您可能在现有系统（如关系数据库或传统消息传递系统）中拥有大量数据，以及许多已经使用这些系统的应用程序。 Kafka Connect 允许您不断地将来自外部系统的数据摄取到 Kafka 中，反之亦然。因此很容易将现有系统与 Kafka 集成。为了使这个过程更容易，有数百个这样的连接器随时可用。

<font size=5 ><b> 第 7 步：使用 KAFKA STREAMS 处理您的事件 </b></font>

一旦您的数据作为事件存储在 Kafka 中，您就可以使用 Java/Scala 的 [Kafka Streams](https://kafka.apache.org/documentation/streams)客户端库处理数据。它允许您实现关键任务的实时应用程序和微服务，其中输入和/或输出数据存储在 Kafka 主题中。Kafka Streams 将在客户端编写和部署标准 Java 和 Scala 应用程序的简单性与 Kafka 的服务器端集群技术的优势相结合，使这些应用程序具有高度可扩展性、弹性、容错性和分布式性。该库支持一次性处理、有状态操作和聚合、窗口化、连接、基于事件时间的处理等等。

为了让您初步了解，以下是实现流行 WordCount 算法的方法：

```java
KStream<String, String> textLines = builder.stream("quickstart-events");

KTable<String, Long> wordCounts = textLines
            .flatMapValues(line -> Arrays.asList(line.toLowerCase().split(" ")))
            .groupBy((keyIgnored, word) -> word)
            .count();

wordCounts.toStream().to("output-topic", Produced.with(Serdes.String(), Serdes.Long()));
```

[Kafka Streams 演示](https://kafka.apache.org/25/documentation/streams/quickstart) 和[应用程序开发教程](https://kafka.apache.org/25/documentation/streams/tutorial) 演示了如何从头到尾编写和运行这样的流应用程序 。

<font size=5 ><b> 第 8 步：终止 KAFKA 环境 </b></font>

现在您已经完成了快速入门，请随意拆除 Kafka 环境，或者继续玩。

1. 停止生产者和消费组客户端
2. 停止 Broker 按 Ctrl-C
3. 最后，使用 Ctrl-C 停止 ZooKeeper 服务器

如果您还想删除本地 Kafka 环境的任何数据，包括您在此过程中创建的任何事件，请运行以下命令：

```sh

$ rm -rf /tmp/kafka-logs /tmp/zookeeper

```

**您已成功完成 Apache Kafka 快速入门。**

## 1.4 升级

该部分请查看 [从旧版本升级](https://kafka.apache.org/27/documentation.html#quickstart_kafkaterminate)
