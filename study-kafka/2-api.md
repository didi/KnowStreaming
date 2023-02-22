---
title: 2.Kafka官方API
order: 2
toc: menu
---

## 2.1 Producer API

Producer API 允许应用程序将数据流发送到 Kafka 集群中的主题。

要使用生产者，您可以使用以下 maven 依赖项：

```mvn

		<dependency>
			<groupId>org.apache.kafka</groupId>
			<artifactId>kafka-clients</artifactId>
			<version>2.7.2</version>
		</dependency>
```

## 2.2 Consumer API

Consumer API 允许应用程序从 Kafka 集群中的主题中读取数据流。

要使用消费者，您可以使用以下 maven 依赖项：

```mvn

	  <dependency>
			<groupId>org.apache.kafka</groupId>
			<artifactId>kafka-clients</artifactId>
			<version>2.7.2</version>
		</dependency>
```

## 2.3 Stream API

Consumer API 允许应用程序从 Kafka 集群中的主题中读取数据流。

要使用 Kafka Streams，您可以使用以下 maven 依赖项：

```mvn

		<dependency>
			<groupId>org.apache.kafka</groupId>
			<artifactId>kafka-streams</artifactId>
			<version>2.7.2</version>
		</dependency>

```

使用 Scala 时，您可以选择包含该 kafka-streams-scala 库。开发人员指南中提供了有关使用 Kafka Streams DSL for Scala 的其他文档。

要使用 Kafka Streams DSL for Scala for Scala 2.13，您可以使用以下 maven 依赖项：

```mvn

		<dependency>
			<groupId>org.apache.kafka</groupId>
			<artifactId>kafka-streams-scala_2.13</artifactId>
			<version>2.7.2</version>
		</dependency>

```

## 2.4 Connect API

Connect API 允许实现连接器，这些连接器不断地从某个源数据系统拉入 Kafka 或从 Kafka 推送到某个接收器数据系统。
许多 Connect 用户不需要直接使用此 API，但他们可以使用预构建的连接器，而无需编写任何代码。此处提供有关使用 Connect 的更多信息。

## 2.5 Admin API

Admin API 支持管理和检查主题、代理、acls 和其他 Kafka 对象。
要使用 Admin API，请添加以下 Maven 依赖项：

```mvn

		<dependency>
			<groupId>org.apache.kafka</groupId>
			<artifactId>kafka-clients</artifactId>
			<version>2.7.2</version>
		</dependency>

```
