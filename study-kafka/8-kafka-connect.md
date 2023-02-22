---
title: 8.Kafka Connect
order: 8
toc: menu
---

# 8. Kafka Connect

## 8.1 概述

Kafka Connect 是一种用于在 Apache Kafka 和其他系统之间可扩展且可靠地流式传输数据的工具。它使快速定义将大量数据移入和移出 Kafka 的连接器变得简单。Kafka Connect 可以摄取整个数据库或将所有应用服务器的指标收集到 Kafka 主题中，从而使数据可用于低延迟的流处理。导出作业可以将来自 Kafka 主题的数据传送到辅助存储和查询系统或批处理系统以进行离线分析。

Kafka Connect 功能包括：

- **Kafka 连接器的通用框架**——Kafka Connect 标准化了其他数据系统与 Kafka 的集成，简化了连接器的开发、部署和管理
- **分布式和独立模式**- 扩展到支持整个组织的大型集中管理服务或缩减到开发、测试和小型生产部署
- **REST 接口**- 通过易于使用的 REST API 向 Kafka Connect 集群提交和管理连接器
- **自动偏移管理**——只需来自连接器的少量信息，Kafka Connect 就可以自动管理偏移提交过程，因此连接器开发人员无需担心连接器开发中容易出错的部分
- **默认情况下是分布式和可扩展的**——Kafka Connect 建立在现有的组管理协议之上。可以添加更多工作人员来扩展 Kafka Connect 集群。
- **流/批处理集成**——利用 Kafka 的现有功能，Kafka Connect 是连接流和批处理数据系统的理想解决方案

## 8.2 用户指南

<font size=5 ><b> 运行 Kafka Connect </b></font>

Kafka Connect 目前支持两种执行模式：独立（单进程）和分布式。

在独立模式下，所有工作都在一个进程中执行。此配置更易于设置和开始使用，并且在只有一个工作人员有意义的情况下（例如收集日志文件）可能很有用，但它不能从 Kafka Connect 的某些功能（例如容错）中受益。您可以使用以下命令启动独立进程：

```

    > bin/connect-standalone.sh config/connect-standalone.properties connector1.properties [connector2.properties ...]


```

第一个参数是 worker 的配置。这包括诸如 Kafka 连接参数、序列化格式以及提交偏移量的频率等设置。提供的示例应该适用于使用 config/server.properties. 它将需要调整以用于不同的配置或生产部署。所有工作人员（独立和分布式）都需要一些配置：

- `bootstrap.servers`- 用于引导连接到 Kafka 的 Kafka 服务器列表
- `key.converter` - 转换器类用于在 Kafka Connect 格式和写入 Kafka 的序列化格式之间进行转换。这控制了写入 Kafka 或从 Kafka 读取的消息中键的格式，并且由于它独立于连接器，它允许任何连接器使用任何序列化格式。常见格式的示例包括 JSON 和 Avro。
- `value.converter`- 转换器类用于在 Kafka Connect 格式和写入 Kafka 的序列化格式之间进行转换。这控制了写入或读取 Kafka 的消息中值的格式，并且由于它独立于连接器，它允许任何连接器使用任何序列化格式。常见格式的示例包括 JSON 和 Avro。

特定于独立模式的重要配置选项是：

- `offset.storage.file.filename`- 用于存储偏移数据的文件

此处配置的参数旨在供 Kafka Connect 使用的生产者和消费者访问配置、偏移和状态主题。Kafka 源任务使用的生产者和 Kafka 接收器任务使用的消费者的配置，可以使用相同的参数，但需要分别加上 producer.and 前缀 consumer.。唯一从工作配置中继承的不带前缀的 Kafka 客户端参数是 bootstrap.servers，这在大多数情况下就足够了，因为同一个集群通常用于所有目的。一个值得注意的例外是安全集群，它需要额外的参数才能允许连接。这些参数最多需要在工作器配置中设置 3 次，一次用于管理访问，一次用于 Kafka 源，一次用于 Kafka 接收器。

从 2.3.0 开始，客户端配置覆盖可以通过使用前缀`producer.override`.和`consumer.override`.Kafka 源或 Kafka 接收器分别为每个连接器单独配置。这些覆盖包含在连接器的其余配置属性中。

其余参数是连接器配置文件。您可以包含任意数量的内容，但所有内容都将在同一个进程中执行（在不同的线程上）。

分布式模式处理工作的自动平衡，允许您动态扩展（或缩减），并在活动任务以及配置和偏移提交数据中提供容错。执行与独立模式非常相似：

```

    > bin/connect-distributed.sh config/connect-distributed.properties

```

不同之处在于启动的类和配置参数，这些参数改变了 Kafka Connect 进程决定在哪里存储配置、如何分配工作以及在哪里存储偏移量和任务状态的配置参数。在分布式模式下，Kafka Connect 将偏移量、配置和任务状态存储在 Kafka 主题中。建议手动创建偏移量、配置和状态的主题，以实现所需的分区数和复制因子。如果在启动 Kafka Connect 时尚未创建主题，则会使用默认的分区数和复制因子自动创建主题，这可能不适合其使用。

特别是，除了上面提到的常见设置之外，以下配置参数对于在启动集群之前进行设置至关重要：

- group.id（默认 connect-cluster）- 集群的唯一名称，用于形成 Connect 集群组；请注意，这不能与消费者组 ID 冲突
- config.storage.topic（默认 connect-configs）- 用于存储连接器和任务配置的主题；请注意，这应该是单个分区、高度复制、压缩的主题。您可能需要手动创建主题以确保正确配置，因为自动创建的主题可能有多个分区或自动配置为删除而不是压缩
- offset.storage.topic（默认 connect-offsets） - 用于存储偏移量的主题；这个主题应该有很多分区，被复制，并被配置为压缩
- status.storage.topic（默认 connect-status） - 用于存储状态的主题；这个主题可以有多个分区，并且应该被复制并配置为压缩

请注意，在分布式模式下，连接器配置不会在命令行上传递。相反，使用下面描述的 REST API 来创建、修改和销毁连接器。

<font size=5 ><b> 配置 Connect </b></font>

连接器配置是简单的键值映射。对于独立模式，这些是在属性文件中定义的，并在命令行上传递给 Connect 进程。在分布式模式下，它们将包含在创建（或修改）连接器的请求的 JSON 有效负载中。

大多数配置都依赖于连接器，因此此处无法概述。但是，有一些常见的选项：

- name- 连接器的唯一名称。尝试使用相同名称再次注册将失败。
- connector.class- 连接器的 Java 类
- tasks.max- 应为此连接器创建的最大任务数。如果连接器无法达到这种并行度，它可能会创建更少的任务。
- key.converter- （可选）覆盖工作人员设置的默认密钥转换器。
- value.converter- （可选）覆盖工人设置的默认值转换器。

`connector.class`配置支持多种格式：此连接器的类的全名或别名。如果连接器是 org.apache.kafka.connect.file.FileStreamSinkConnector，您可以指定此全名或使用 FileStreamSink 或 FileStreamSinkConnector 使配置更短一些。

接收器连接器还有一些额外的选项来控制它们的输入。每个接收器连接器必须设置以下之一：

`topics`- 以逗号分隔的主题列表，用作此连接器的输入
`topics.regex`- 用作此连接器输入的主题的 Java 正则表达式

<font size=5 ><b> transforms </b></font>

[transforms](https://kafka.apache.org/27/documentation.html#connect_transforms)

<font size=5 ><b> Rest Api </b></font>

由于 Kafka Connect 旨在作为服务运行，因此它还提供了用于管理连接器的 REST API。可以使用 listeners 配置选项配置 REST API 服务器。此字段应包含以下格式的侦听器列表：`protocol://host:port,protocol2://host2:port2. `当前支持的协议是 http 和 https。例如：

```

        listeners=http://localhost:8080,https://localhost:8443

```

默认情况下，如果未 listeners 指定，则 REST 服务器使用 HTTP 协议在端口 8083 上运行。使用 HTTPS 时，配置必须包含 SSL 配置。默认情况下，它将使用 ssl.*设置。如果需要对 REST API 使用与连接到 Kafka 代理不同的配置，则字段可以以 listeners.https. 使用前缀时，只会使用带前缀的 ssl.*选项，不带前缀的选项将被忽略。以下字段可用于为 REST API 配置 HTTPS：

- ssl.keystore.location
- ssl.keystore.password
- ssl.keystore.type
- ssl.key.password
- ssl.truststore.location
- ssl.truststore.password
- ssl.truststore.type
- ssl.enabled.protocols
- ssl.provider
- ssl.protocol
- ssl.cipher.suites
- ssl.keymanager.algorithm
- ssl.secure.random.implementation
- ssl.trustmanager.algorithm
- ssl.endpoint.identification.algorithm
- ssl.client.auth

REST API 不仅被用户用来监控/管理 Kafka Connect。它还用于 Kafka Connect 跨集群通信。在跟随节点 REST API 上收到的请求将被转发到领导节点 REST API。如果给定主机可达的 URI 与其侦听的 URI 不同，则配置选项 rest.advertised.host.name 可 rest.advertised.port 用于 rest.advertised.listener 更改跟随者节点用于连接领导者的 URI。同时使用 HTTP 和 HTTPS 侦听器时，该 rest.advertised.listener 选项还可用于定义哪个侦听器将用于跨集群通信。当使用 HTTPS 进行节点之间的通信时，将使用相同的 ssl.\*或 listeners.https 选项来配置 HTTPS 客户端。

以下是当前支持的 REST API 端点：

- GET /connectors- 返回活动连接器列表
- POST /connectors- 创建一个新的连接器；请求正文应该是一个 JSON 对象，其中包含一个字符串 name 字段和一个 config 带有连接器配置参数的对象字段
- GET /connectors/{name}- 获取有关特定连接器的信息
- GET /connectors/{name}/config- 获取特定连接器的配置参数
- PUT /connectors/{name}/config- 更新特定连接器的配置参数
- GET /connectors/{name}/status- 获取连接器的当前状态，包括它是否正在运行、失败、暂停等，它被分配给哪个工作人员，如果它失败了，错误信息，以及它所有任务的状态
- GET /connectors/{name}/tasks- 获取当前为连接器运行的任务列表
- GET /connectors/{name}/tasks/{taskid}/status- 获取任务的当前状态，包括它是否正在运行、失败、暂停等，它被分配到哪个工作人员，以及失败时的错误信息
- PUT /connectors/{name}/pause- 暂停连接器及其任务，这会停止消息处理，直到连接器恢复
- PUT /connectors/{name}/resume- 恢复暂停的连接器（如果连接器未暂停，则不执行任何操作）
- POST /connectors/{name}/restart- 重新启动连接器（通常是因为它失败了）
- POST /connectors/{name}/tasks/{taskId}/restart- 重新启动单个任务（通常是因为它失败了）
- DELETE /connectors/{name}- 删除连接器，停止所有任务并删除其配置
- GET /connectors/{name}/topics- 获取特定连接器自创建连接器或发出重置其活动主题集的请求以来正在使用的主题集
- PUT /connectors/{name}/topics/reset- 发送请求以清空连接器的活动主题集

Kafka Connect 还提供了一个 REST API，用于获取有关连接器插件的信息：

- GET /connector-plugins- 返回安装在 Kafka Connect 集群中的连接器插件列表。请注意，API 仅检查处理请求的工作人员上的连接器，这意味着您可能会看到不一致的结果，尤其是在滚动升级期间，如果您添加了新的连接器 jar
- PUT /connector-plugins/{connector-type}/config/validate- 根据配置定义验证提供的配置值。此 API 执行每个配置验证，在验证期间返回建议值和错误消息。
  以下是顶级（根）端点上受支持的 REST 请求：

- GET /- 返回有关 Kafka Connect 集群的基本信息，例如为 REST 请求提供服务的 Connect worker 的版本（包括源代码的 git commit ID）和连接的 Kafka 集群 ID

## 8.3 Connect 开发指南

[Connect 开发指南](https://kafka.apache.org/27/documentation.html#connect_development)
