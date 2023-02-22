---
title: 6.Kafka监控运维
order: 6
toc: menu
---

## 6.1 Kafka 的基本操作

### 6.1.1 优雅关机

Kafka 集群将自动检测任何代理关闭或故障，并为该机器上的分区选择新的领导者。无论服务器发生故障还是故意将其关闭以进行维护或配置更改，都会发生这种情况。对于后一种情况，Kafka 支持一种更优雅的机制来停止服务器，而不仅仅是杀死它。当服务器正常停止时，它有两个优化，它将利用：
它会将其所有日志同步到磁盘，以避免在重新启动时需要进行任何日志恢复（即验证日志尾部所有消息的校验和）。日志恢复需要时间，因此可以加快有意重启的速度。
它将在关闭之前将服务器作为领导者的任何分区迁移到其他副本。这将使领导权转移更快，并将每个分区不可用的时间最小化到几毫秒。
只要服务器停止而不是通过硬杀，同步日志就会自动发生，但受控领导迁移需要使用特殊设置：

```

      controlled.shutdown.enable=true

```

请注意，只有在代理上托管的所有分区都具有副本 时，受控关闭才会成功（即复制因子大于 1 并且这些副本中至少有一个是活动的）。这通常是您想要的，因为关闭最后一个副本会使该主题分区不可用。

**Leader 自均衡**

每当代理停止或崩溃时，该 Broker 分区的领导权就会转移到其他副本。当 Broker 重新启动时，它只会成为其所有分区的追随者，这意味着它不会用于客户端读取和写入。

为了避免这种不平衡，Kafka 有一个优先副本的概念。如果一个分区的副本列表是 1,5,9，那么节点 1 比节点 5 或 9 更适合作为领导者，因为它在副本列表中更早。默认情况下，Kafka 集群将尝试将领导权恢复到已恢复的副本。此行为配置为：

```

      auto.leader.rebalance.enable=true

```

您也可以将其设置为 false，但是您需要通过运行以下命令手动将领导权恢复到已恢复的副本：

```
  > bin/kafka-preferred-replica-election.sh --bootstrap-server broker_host:port

```

### 6.1.2 扩展集群

将服务器添加到 Kafka 集群很容易，只需为它们分配一个唯一的代理 ID 并在您的新服务器上启动 Kafka。

然而，这些新服务器不会自动分配任何数据分区，因此除非将分区移动到它们，否则它们将不会做任何工作，直到创建新主题。因此，通常当您将机器添加到集群时，您会希望将一些现有数据迁移到这些机器上。

迁移数据的过程是手动启动的，但完全自动化。

具体操作请看下面的 **分区副本重分配**

### 6.1.3 Broker 退役

Broker 退役，先将该 Broker 上的数据迁移到其他 Broker 上(关于迁移情况下面的分区副本重分配部分)，然后直接下线就行了。

### 6.1.4 分区副本重分配 kafka-reassign-partitions

#### 脚本参数

相关属性

| 参数                                | 描述                                                                                                                     | 例子                                                                                                                                                 |
| ----------------------------------- | ------------------------------------------------------------------------------------------------------------------------ | ---------------------------------------------------------------------------------------------------------------------------------------------------- |
| `--zookeeper`                       | 连接 zk                                                                                                                  | `--zookeeper` localhost:2181, localhost:2182                                                                                                         |
| `--topics-to-move-json-file`        | 指定 json 文件,文件内容为 topic 配置                                                                                     | `--topics-to-move-json-file config/move-json-file.json ` Json 文件格式如下: ![在这里插入图片描述](https://img-blog.csdnimg.cn/20210619114249805.png) |
| `--generate `                       | 尝试给出副本重分配的策略,该命令并不实际执行                                                                              |                                                                                                                                                      |
| `--broker-list `                    | 指定具体的 BrokerList,用于尝试给出分配策略,与`--generate `搭配使用                                                       | `--broker-list ` 0,1,2,3                                                                                                                             |
| `--reassignment-json-file`          | 指定要重分配的 json 文件,与`--execute`搭配使用                                                                           | json 文件格式如下例如:![在这里插入图片描述](https://img-blog.csdnimg.cn/aa3f975c914e414b845e4563f947ed8e.png)                                        |
| `--execute`                         | 开始执行重分配任务,与`--reassignment-json-file`搭配使用                                                                  |                                                                                                                                                      |
| `--verify `                         | 验证任务是否执行成功,当有使用`--throttle`限流的话,该命令还会移除限流;该命令很重要,不移除限流对正常的副本之间同步会有影响 |                                                                                                                                                      |
| `--throttle`                        | 迁移过程 Broker 之间现在流程传输的速率,单位 bytes/sec                                                                    | `-- throttle 500000`                                                                                                                                 |
| `--replica-alter-log-dirs-throttle` | broker 内部副本跨路径迁移数据流量限制功能，限制数据拷贝从一个目录到另外一个目录带宽上限 单位 bytes/sec                   | `--replica-alter-log-dirs-throttle` 100000                                                                                                           |
| `--disable-rack-aware `             | 关闭机架感知能力,在分配的时候就不参考机架的信息                                                                          |                                                                                                                                                      |
| `--bootstrap-server`                | 如果是副本跨路径迁移必须有此参数                                                                                         |                                                                                                                                                      |

#### 脚本的使用介绍

**1. 生成推荐配置脚本**

---

**关键参数`--generate`**

在进行分区副本重分配之前,最好是用下面方式获取一个合理的分配文件;
编写`move-json-file.json `文件; 这个文件就是告知想对哪些 Topic 进行重新分配的计算

```jsob
{
  "topics": [
    {"topic": "test_create_topic1"}
  ],
  "version": 1
}
```

然后执行下面的脚本,`--broker-list "0,1,2,3"` 这个参数是你想要分配的 Brokers;

> `sh bin/kafka-reassign-partitions.sh --zookeeper xxx:2181 --topics-to-move-json-file config/move-json-file.json --broker-list "0,1,2,3" --generate`

执行完毕之后会打印

```sh
Current partition replica assignment//当前副本分配方式
{"version":1,"partitions":[{"topic":"test_create_topic1","partition":2,"replicas":[1],"log_dirs":["any"]},{"topic":"test_create_topic1","partition":1,"replicas":[3],"log_dirs":["any"]},{"topic":"test_create_topic1","partition":0,"replicas":[2],"log_dirs":["any"]}]}

Proposed partition reassignment configuration//期望的重新分配方式
{"version":1,"partitions":[{"topic":"test_create_topic1","partition":2,"replicas":[2],"log_dirs":["any"]},{"topic":"test_create_topic1","partition":1,"replicas":[1],"log_dirs":["any"]},{"topic":"test_create_topic1","partition":0,"replicas":[0],"log_dirs":["any"]}]}
```

需求注意的是，此时分区移动尚未开始，它只是告诉你当前的分配和建议。保存当前分配，以防你想要回滚它

**2. 执行 Json 文件**

---

**关键参数`--execute`**
将上面得到期望的重新分配方式文件保存在一个 json 文件里面
`reassignment-json-file.json`

```sh

{"version":1,"partitions":[{"topic":"test_create_topic1","partition":2,"replicas":[2],"log_dirs":["any"]},{"topic":"test_create_topic1","partition":1,"replicas":[1],"log_dirs":["any"]},{"topic":"test_create_topic1","partition":0,"replicas":[0],"log_dirs":["any"]}]}

```

然后执行

> `sh bin/kafka-reassign-partitions.sh --zookeeper xxxxx:2181 --reassignment-json-file config/reassignment-json-file.json --execute`

**迁移过程注意流量陡增对集群的影响**
Kafka 提供一个 broker 之间复制传输的流量限制，限制了副本从机器到另一台机器的带宽上限，当重新平衡集群，引导新 broker，添加或移除 broker 时候，这是很有用的。因为它限制了这些密集型的数据操作从而保障了对用户的影响、
例如我们上面的迁移操作加一个限流选项`-- throttle 50000000`

```shell
> sh bin/kafka-reassign-partitions.sh --zookeeper xxxxx:2181 --reassignment-json-file config/reassignment-json-file.json --execute -- throttle 50000000
```

在后面加上一个` —throttle 50000000` 参数, 那么执行移动分区的时候,会被限制流量在`50000000 B/s`
加上参数后你可以看到

```shell
The throttle limit was set to 50000000 B/s
Successfully started reassignment of partitions.
```

需要注意的是,如果你迁移的时候包含 副本跨路径迁移(同一个 Broker 多个路径)那么这个限流措施不会生效,你需要再加上|`--replica-alter-log-dirs-throttle` 这个限流参数,它限制的是同一个 Broker 不同路径直接迁移的限流;

**如果你想在重新平衡期间修改限制，增加吞吐量，以便完成的更快。你可以重新运行 execute 命令，用相同的 reassignment-json-file**

**3. 验证**

---

**关键参数`--verify`**
该选项用于检查分区重新分配的状态，同时`—throttle`流量限制也会被移除掉; 否则可能会导致定期复制操作的流量也受到限制。

> `sh bin/kafka-reassign-partitions.sh --zookeeper xxxx:2181 --reassignment-json-file config/reassignment-json-file.json --verify` > ![在这里插入图片描述](https://img-blog.csdnimg.cn/20210618163517512.png)
> 注意: 当你输入的 BrokerId 不存在时,该副本的操作会失败,但是不会影响其他的;例如

![在这里插入图片描述](https://img-blog.csdnimg.cn/f1d11e2f6d5f4016965e27bf7813e40c.png)

### 6.1.5 副本扩缩

> kafka 并没有提供一个专门的脚本来支持副本的扩缩, 不像`kafka-topic.sh`脚本一样,是可以扩分区的; 想要对副本进行扩缩,只能是曲线救国了; 利用`kafka-reassign-partitions.sh`来重新分配副本

**副本扩容**

---

假设我们当前的情况是 3 分区 1 副本,为了提供可用性,我想把副本数升到 2;

**1. 计算副本分配方式**

我们用**步骤 2.2.3.2 **的 `--generate` 获取一下当前的分配情况,得到如下 json

```json
{
  "version": 1,
  "partitions": [
    {
      "topic": "test_create_topic1",
      "partition": 2,
      "replicas": [2],
      "log_dirs": ["any"]
    },
    {
      "topic": "test_create_topic1",
      "partition": 1,
      "replicas": [1],
      "log_dirs": ["any"]
    },
    {
      "topic": "test_create_topic1",
      "partition": 0,
      "replicas": [0],
      "log_dirs": ["any"]
    }
  ]
}
```

我们想把所有分区的副本都变成 2,那我们只需修改`"replicas": []`里面的值了,这里面是 Broker 列表,排在第一个的是 Leader; 所以我们根据自己想要的分配规则修改一下 json 文件就变成如下

```json
{
  "version": 1,
  "partitions": [
    {
      "topic": "test_create_topic1",
      "partition": 2,
      "replicas": [2, 0],
      "log_dirs": ["any", "any"]
    },
    {
      "topic": "test_create_topic1",
      "partition": 1,
      "replicas": [1, 2],
      "log_dirs": ["any", "any"]
    },
    {
      "topic": "test_create_topic1",
      "partition": 0,
      "replicas": [0, 1],
      "log_dirs": ["any", "any"]
    }
  ]
}
```

注意`log_dirs`里面的数量要和`replicas`数量匹配;或者直接把`log_dirs`选项删除掉; 这个`log_dirs`是副本跨路径迁移时候的绝对路径

**2 执行--execute**

![在这里插入图片描述](https://img-blog.csdnimg.cn/6d3acfd97d3c4a3ebbc1c92a56a24653.png)

如果你想在重新平衡期间修改限制，增加吞吐量，以便完成的更快。你可以重新运行 execute 命令，用相同的 reassignment-json-file

**3.验证--verify**

![在这里插入图片描述](https://img-blog.csdnimg.cn/20210618191729562.png)
完事之后,副本数量就增加了;

**副本缩容**

---

> 副本缩容跟扩容是一个意思; 当副本分配少于之前的数量时候,多出来的副本会被删除;
> 比如刚刚我新增了一个副本,想重新恢复到一个副本
>
> 执行下面的 json 文件

```json
{
  "version": 1,
  "partitions": [
    {
      "topic": "test_create_topic1",
      "partition": 2,
      "replicas": [2],
      "log_dirs": ["any"]
    },
    {
      "topic": "test_create_topic1",
      "partition": 1,
      "replicas": [1],
      "log_dirs": ["any"]
    },
    {
      "topic": "test_create_topic1",
      "partition": 0,
      "replicas": [0],
      "log_dirs": ["any"]
    }
  ]
}
```

执行之后可以看到其他的副本就被标记为删除了; 一会就会被清理掉

![在这里插入图片描述](https://img-blog.csdnimg.cn/06369332ca32477a8afb50fb6aa79101.png)

<font color=red>用这样一种方式我们虽然是实现了副本的扩缩容, 但是副本的分配需要我们自己来把控好, 要做到负载均衡等等; 那肯定是没有 kafka 自动帮我们分配比较合理一点; 那么我们有什么好的方法来帮我们给出一个合理分配的 Json 文件吗?</font>

PS：

我们之前已经分析过[【kafka 源码】创建 Topic 的时候是如何分区和副本的分配规则]() 那么我们把这样一个分配过程也用同样的规则来分配不就 Ok 了吗？

`--generate` 本质上也是调用了这个方法,`AdminUtils.assignReplicasToBrokers(brokerMetadatas, assignment.size, replicas.size)`

自己写一个工程来实现类似的方法,如果觉得很麻烦,可以直接使用`KnowStreaming` 的新增副本功能直接帮你做了这个事情;

---

**KnowStreaming 可视化操作**

![在这里插入图片描述](https://img-blog.csdnimg.cn/3be529b3e0e2420681d5cc8842f7672e.png)

<font color=blue> 并且支持批量扩缩副本</font>

![在这里插入图片描述](https://img-blog.csdnimg.cn/098a5835ae2447ce9d1982b32c25bb23.png)

<font color=blue> 在操作之前, 预览任务计划, 这里是给你看一下,最终我们会以什么样子的分配方式来迁移, 当然在这里我们还可以手动微调。</font>

#### 分区迁移

> 分区迁移跟上面同理, 请看**2.2.3.2 脚本的使用介绍** 部分;

---

**KnowStreaming 可视化操作**

![在这里插入图片描述](https://img-blog.csdnimg.cn/e73b6ab16c5949e8bfe7c14e77bfaaa3.png)

迁移之前可自定义调整迁移任务

![在这里插入图片描述](https://img-blog.csdnimg.cn/5d6837cc832c4143989affc9368f91ec.png)

查看执行中的迁移任务

![在这里插入图片描述](https://img-blog.csdnimg.cn/82d5eb5cbd464a36a7983bb13a2b40a3.png)

查看迁移进度

![在这里插入图片描述](https://img-blog.csdnimg.cn/e68ce06892264427b0fd2f72a0c65752.png)

#### 副本跨路径迁移

> 为什么线上 Kafka 机器各个磁盘间的占用不均匀，经常出现“一边倒”的情形？ 这是因为 Kafka 只保证分区数量在各个磁盘上均匀分布，但它无法知晓每个分区实际占用空间，故很有可能出现某些分区消息数量巨大导致占用大量磁盘空间的情况。在 1.1 版本之前，用户对此毫无办法，因为 1.1 之前 Kafka 只支持分区数据在不同 broker 间的重分配，而无法做到在同一个 broker 下的不同磁盘间做重分配。1.1 版本正式支持副本在不同路径间的迁移

**怎么在一台 Broker 上用多个路径存放分区呢?**

只需要在配置上接多个文件夹就行了

```
############################# Log Basics #############################


# A comma separated list of directories under which to store log files

log.dirs=kafka-logs-5,kafka-logs-6,kafka-logs-7,kafka-logs-8

```

**注意同一个 Broker 上不同路径只会存放不同的分区，而不会将副本存放在同一个 Broker; 不然那副本就没有意义了(容灾)**

**怎么针对跨路径迁移呢？**

迁移的 json 文件有一个参数是`log_dirs`; 默认请求不传的话 它是`"log_dirs": ["any"]` （这个数组的数量要跟副本保持一致）
但是你想实现跨路径迁移,只需要在这里填入绝对路径就行了,例如下面

迁移的 json 文件示例

```json
{
  "version": 1,
  "partitions": [
    {
      "topic": "test_create_topic4",
      "partition": 2,
      "replicas": [0],
      "log_dirs": ["/Users/xxxxx/work/IdeaPj/source/kafka/kafka-logs-5"]
    },
    {
      "topic": "test_create_topic4",
      "partition": 1,
      "replicas": [0],
      "log_dirs": ["/Users/xxxxx/work/IdeaPj/source/kafka/kafka-logs-6"]
    }
  ]
}
```

**然后执行脚本**

```

sh bin/kafka-reassign-partitions.sh --zookeeper xxxxx --reassignment-json-file config/reassignment-json-file.json --execute --bootstrap-server
xxxxx:9092 --replica-alter-log-dirs-throttle 10000

```

注意 `--bootstrap-server` 在跨路径迁移的情况下,必须传入此参数

如果需要限流的话 加上参数 `--replica-alter-log-dirs-throttle` ; 跟`--throttle`不一样的是 `--replica-alter-log-dirs-throttle`限制的是 Broker 内不同路径的迁移流量;

---

**KnowStreaming 可视化操作**

正在开发中,敬请期待！

### 6.1.6 Topic 增删改查

#### Topic 创建

> `bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 3 --partitions 3 --topic test`

---

相关可选参数

| 参数                                          | 描述                                                                                                                                           | 例子                                                                                                                                                                                                            |
| --------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `--bootstrap-server ` 指定 kafka 服务         | 指定连接到的 kafka 服务; 如果有这个参数,则 `--zookeeper`可以不需要                                                                             | --bootstrap-server localhost:9092                                                                                                                                                                               |
| `--zookeeper`                                 | 弃用, 通过 zk 的连接方式连接到 kafka 集群;                                                                                                     | --zookeeper localhost:2181 或者 localhost:2181/kafka                                                                                                                                                            |
| `--replication-factor `                       | 副本数量,注意不能大于 broker 数量;如果不提供,则会用集群中默认配置                                                                              | --replication-factor 3                                                                                                                                                                                          |
| `--partitions`                                | 分区数量,当创建或者修改 topic 的时候,用这个来指定分区数;如果创建的时候没有提供参数,则用集群中默认值; 注意如果是修改的时候,分区比之前小会有问题 | --partitions 3                                                                                                                                                                                                  |
| `--replica-assignment `                       | 副本分区分配方式;创建 topic 的时候可以自己指定副本分配情况;                                                                                    | `--replica-assignment` BrokerId-0:BrokerId-1:BrokerId-2,BrokerId-1:BrokerId-2:BrokerId-0,BrokerId-2:BrokerId-1:BrokerId-0 ; 这个意思是有三个分区和三个副本,对应分配的 Broker; 逗号隔开标识分区;冒号隔开表示副本 |
| `--config `<String: name=value>               | 用来设置 topic 级别的配置以覆盖默认配置;**只在--create 和--bootstrap-server 同时使用时候生效**; 可以配置的参数列表请看文末附件                 | 例如覆盖两个配置 `--config retention.bytes=123455 --config retention.ms=600001`                                                                                                                                 |
| `--command-config` <String: command 文件路径> | 用来配置客户端 Admin Client 启动配置,**只在--bootstrap-server 同时使用时候生效**;                                                              | 例如:设置请求的超时时间 `--command-config config/producer.proterties `; 然后在文件中配置 request.timeout.ms=300000                                                                                              |

#### 删除 Topic

> `bin/kafka-topics.sh --bootstrap-server localhost:9092 --delete --topic test`

---

支持正则表达式匹配 Topic 来进行删除,只需要将 topic 用双引号包裹起来
**例如: 删除以`create_topic_byhand_zk`为开头的 topic;**

> > bin/kafka-topics.sh --bootstrap-server localhost:9092 --delete --topic "create*topic_byhand_zk.*"
> > `.`表示任意匹配除换行符 \n 之外的任何单字符。要匹配 . ，请使用 \. 。
> > `·_·`：匹配前面的子表达式零次或多次。要匹配 \* 字符，请使用 \*。 `.\*` : 任意字符

**删除任意 Topic (慎用)**

> bin/kafka-topics.sh --bootstrap-server localhost:9092 --delete --topic ".\*?"
>
> 更多的用法请[参考正则表达式]()

相关配置

| 配置                 | 描述                                                               | 默认               |
| -------------------- | ------------------------------------------------------------------ | ------------------ |
| file.delete.delay.ms | topic 删除被标记为--delete 文件之后延迟多长时间删除正在的 Log 文件 | 60000              |
| delete.topic.enable  | true                                                               | 是否能够删除 topic |

#### Topic 分区扩容

**zk 方式(不推荐)**

`>bin/kafka-topics.sh --zookeeper localhost:2181 --alter --topic topic1 --partitions 2`

**kafka 版本 >= 2.2 支持下面方式（推荐）**

**单个 Topic 扩容**

> `bin/kafka-topics.sh --bootstrap-server broker_host:port --alter --topic test_create_topic1 --partitions 4`

**批量扩容** (将所有正则表达式匹配到的 Topic 分区扩容到 4 个)

> `sh bin/kafka-topics.sh --topic ".*?" --bootstrap-server 172.23.248.85:9092 --alter --partitions 4`
>
> `".*?"` 正则表达式的意思是匹配所有; 您可按需匹配

**PS:** 当某个 Topic 的分区少于指定的分区数时候,他会抛出异常;但是不会影响其他 Topic 正常进行;

---

相关可选参数

| 参数                    | 描述                                                        | 例子                                                                                                                                                                                                            |
| ----------------------- | ----------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `--replica-assignment ` | 副本分区分配方式;创建 topic 的时候可以自己指定副本分配情况; | `--replica-assignment` BrokerId-0:BrokerId-1:BrokerId-2,BrokerId-1:BrokerId-2:BrokerId-0,BrokerId-2:BrokerId-1:BrokerId-0 ; 这个意思是有三个分区和三个副本,对应分配的 Broker; 逗号隔开标识分区;冒号隔开表示副本 |

**PS: 虽然这里配置的是全部的分区副本分配配置,但是正在生效的是新增的分区;**
比如: 以前 3 分区 1 副本是这样的

| Broker-1 | Broker-2 | Broker-3 | Broker-4 |
| -------- | -------- | -------- | -------- |
| 0        | 1        | 2        |          |

现在新增一个分区,`--replica-assignment` 2,1,3,4 ; 看这个意思好像是把 0，1 号分区互相换个 Broker

| Broker-1 | Broker-2 | Broker-3 | Broker-4 |
| -------- | -------- | -------- | -------- |
| 1        | 0        | 2        | 3        |

但是实际上不会这样做,Controller 在处理的时候会把前面 3 个截掉; 只取新增的分区分配方式,原来的还是不会变

| Broker-1 | Broker-2 | Broker-3 | Broker-4 |
| -------- | -------- | -------- | -------- |
| 0        | 1        | 2        | 3        |

#### 查询 Topic 描述

**1.查询单个 Topic**

> `sh bin/kafka-topics.sh --topic test --bootstrap-server xxxx:9092 --describe --exclude-internal`
>
> **2.批量查询 Topic**(正则表达式匹配,下面是查询所有 Topic)
> `sh bin/kafka-topics.sh --topic ".*?" --bootstrap-server xxxx:9092 --describe --exclude-internal`

支持正则表达式匹配 Topic,只需要将 topic 用双引号包裹起来

---

相关可选参数

| 参数                                  | 描述                                                                               | 例子                              |
| ------------------------------------- | ---------------------------------------------------------------------------------- | --------------------------------- |
| `--bootstrap-server ` 指定 kafka 服务 | 指定连接到的 kafka 服务; 如果有这个参数,则 `--zookeeper`可以不需要                 | --bootstrap-server localhost:9092 |
| `--at-min-isr-partitions`             | 查询的时候省略一些计数和配置信息                                                   | `--at-min-isr-partitions`         |
| `--exclude-internal`                  | 排除 kafka 内部 topic,比如`__consumer_offsets-*`                                   | `--exclude-internal`              |
| `--topics-with-overrides`             | 仅显示已覆盖配置的主题,也就是单独针对 Topic 设置的配置覆盖默认配置；不展示分区信息 | `--topics-with-overrides`         |

#### 查询 Topic 列表

**1.查询所有 Topic 列表**

> `sh bin/kafka-topics.sh --bootstrap-server xxxxxx:9092 --list --exclude-internal`

**2.查询匹配 Topic 列表**(正则表达式)

> 查询`test_create_`开头的所有 Topic 列表
> ` sh bin/kafka-topics.sh --bootstrap-server xxxxxx:9092 --list --exclude-internal --topic "test_create_.*"`

---

相关可选参数

| 参数                 | 描述                                             | 例子                 |
| -------------------- | ------------------------------------------------ | -------------------- |
| `--exclude-internal` | 排除 kafka 内部 topic,比如`__consumer_offsets-*` | `--exclude-internal` |
| `--topic`            | 可以正则表达式进行匹配,展示 topic 名称           | `--topic`            |

### 6.1.7 ConfigCommand 动态配置

> Config 相关操作; 动态配置可以覆盖默认的静态配置;

#### 查询配置

##### Topic 配置查询

> 展示关于 Topic 的动静态配置

**1.查询单个 Topic 配置**(只列举动态配置)

> `sh bin/kafka-configs.sh --describe --bootstrap-server xxxxx:9092 --topic test_create_topic`
> 或者
> `sh bin/kafka-configs.sh --describe --bootstrap-server 172.23.248.85:9092 --entity-type topics --entity-name test_create_topic`
>
> **2.查询所有 Topic 配置**(包括内部 Topic)(只列举动态配置)
> `sh bin/kafka-configs.sh --describe --bootstrap-server 172.23.248.85:9092 --entity-type topics `

![在这里插入图片描述](https://img-blog.csdnimg.cn/8172055ad7394c4da70af217500af991.png)

![在这里插入图片描述](https://img-blog.csdnimg.cn/d10ce9b066f14c8b9a491aadb86ec1c5.png)

**3.查询 Topic 的详细配置(动态+静态)**

> 只需要加上一个参数`--all`

#### 其他配置/clients/users/brokers/broker-loggers 的查询

> 同理 ；只需要将`--entity-type` 改成对应的类型就行了 (topics/clients/users/brokers/broker-loggers)

![在这里插入图片描述](https://img-blog.csdnimg.cn/496e95e250484df196d85dd3ed11f6e0.png)

#### 增删改 配置 `--alter`

**--alter**

**删除配置**: `--delete-config` k1=v1,k2=v2
**添加/修改配**置: `--add-config` k1,k2
**选择类型**: `--entity-type` (topics/clients/users/brokers/broker-
loggers)
**类型名称**: `--entity-name`

##### Topic 添加/修改动态配置

`--add-config`

> `sh bin/kafka-configs.sh --bootstrap-server xxxxx:9092 --alter --entity-type topics --entity-name test_create_topic1 --add-config file.delete.delay.ms=222222,retention.ms=999999 `

##### Topic 删除动态配置

`--delete-config`

> `sh bin/kafka-configs.sh --bootstrap-server xxxxx:9092 --alter --entity-type topics --entity-name test_create_topic1 --delete-config file.delete.delay.ms,retention.ms `

##### 添加/删除配置同时执行

> `sh bin/kafka-configs.sh --bootstrap-server xxxxx:9092 --alter --entity-type brokers --entity-default --add-config log.segment.bytes=788888888 --delete-config log.retention.ms `

##### 其他配置同理,只需要类型改下`--entity-type`

> 类型有: (topics/clients/users/brokers/broker- loggers)

<font color=red>哪些配置可以修改 请看最后面的附件：**ConfigCommand 的一些可选配置** </font>

#### 默认配置

配置默认 **`--entity-default`**

> `sh bin/kafka-configs.sh --bootstrap-server xxxxx:9090 --alter --entity-type brokers --entity-default --add-config log.segment.bytes=88888888`

动态配置的默认配置是使用了节点 ` <defalut>`;

![在这里插入图片描述](https://img-blog.csdnimg.cn/2a42ce17e1764d95b2358aa1a8a0bf86.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTA2MzQwNjY=,size_16,color_FFFFFF,t_70)
<font center size=1>该图转自https://www.cnblogs.com/lizherui/p/12271285.html</font>

**优先级** 指定动态配置>默认动态配置>静态配置

### 6.1.8 Topic 的发送 kafka-console-producer.sh

**1. 生产无 key 消息**

```shell
## 生产者
bin/kafka-console-producer.sh --bootstrap-server localhost:9092 --topic test --producer.config config/producer.properties

```

**2. 生产有 key 消息**

加上属性`--property parse.key=true`

```shell
## 生产者
bin/kafka-console-producer.sh --bootstrap-server localhost:9092 --topic test --producer.config config/producer.properties  --property parse.key=true

```

<font color=red>默认消息 key 与消息 value 间使用“Tab 键”进行分隔，所以消息 key 以及 value 中切勿使用转义字符(\t)</font>

---

可选参数

| 参数                         | 值类型          | 说明                                                                 | 有效值                                                                    |
| ---------------------------- | --------------- | -------------------------------------------------------------------- | ------------------------------------------------------------------------- |
| --bootstrap-server           | String          | 要连接的服务器必需(除非指定--broker-list)                            | 如：host1:prot1,host2:prot2                                               |
| --topic                      | String          | (必需)接收消息的主题名称                                             |                                                                           |
| --batch-size                 | Integer         | 单个批处理中发送的消息数                                             | 200(默认值)                                                               |
| --compression-codec          | String          | 压缩编解码器                                                         | none、gzip(默认值)snappy、lz4、zstd                                       |
| --max-block-ms               | Long            | 在发送请求期间，生产者将阻止的最长时间                               | 60000(默认值)                                                             |
| --max-memory-bytes           | Long            | 生产者用来缓冲等待发送到服务器的总内存                               | 33554432(默认值)                                                          |
| --max-partition-memory-bytes | Long            | 为分区分配的缓冲区大小                                               | 16384                                                                     |
| --message-send-max-retries   | Integer         | 最大的重试发送次数                                                   | 3                                                                         |
| --metadata-expiry-ms         | Long            | 强制更新元数据的时间阈值(ms)                                         | 300000                                                                    |
| --producer-property          | String          | 将自定义属性传递给生成器的机制                                       | 如：key=value                                                             |
| --producer.config            | String          | 生产者配置属性文件[--producer-property]优先于此配置 配置文件完整路径 |                                                                           |
| --property                   | String          | 自定义消息读取器                                                     | parse.key=true/false key.separator=<key.separator>ignore.error=true/false |
| --request-required-acks      | String          | 生产者请求的确认方式                                                 | 0、1(默认值)、all                                                         |
| --request-timeout-ms         | Integer         | 生产者请求的确认超时时间                                             | 1500(默认值)                                                              |
| --retry-backoff-ms           | Integer         | 生产者重试前，刷新元数据的等待时间阈值                               | 100(默认值)                                                               |
| --socket-buffer-size         | Integer         | TCP 接收缓冲大小                                                     | 102400(默认值)                                                            |
| --timeout                    | Integer         | 消息排队异步等待处理的时间阈值                                       | 1000(默认值)                                                              |
| --sync                       | 同步发送消息    |                                                                      |                                                                           |
| --version                    | 显示 Kafka 版本 | 不配合其他参数时，显示为本地 Kafka 版本                              |                                                                           |
| --help                       | 打印帮助信息    |                                                                      |                                                                           |

### 6.1.9 持续批量推送消息 kafka-verifiable-producer.sh

**单次发送 100 条消息`--max-messages 100`**

一共要推送多少条，默认为-1，-1 表示一直推送到进程关闭位置

> sh bin/kafka-verifiable-producer.sh --topic test_create_topic4 --bootstrap-server localhost:9092 `--max-messages 100`

**每秒发送最大吞吐量不超过消息 `--throughput 100`**

推送消息时的吞吐量，单位 messages/sec。默认为-1，表示没有限制

> sh bin/kafka-verifiable-producer.sh --topic test_create_topic4 --bootstrap-server localhost:9092 `--throughput 100`

**发送的消息体带前缀`--value-prefix`**

> sh bin/kafka-verifiable-producer.sh --topic test_create_topic4 --bootstrap-server localhost:9092 ` --value-prefix 666`
>
> 注意` --value-prefix 666`必须是整数,发送的消息体的格式是加上一个 点号`.` 例如： `666.`

其他参数：
` --producer.config CONFIG_FILE` 指定 producer 的配置文件
`--acks ACKS` 每次推送消息的 ack 值，默认是-1

### 6.1.10 生产者压力测试 kafka-producer-perf-test.sh

**1. 发送 1024 条消息`--num-records 100`并且每条消息大小为 1KB`--record-size 1024` 最大吞吐量每秒 10000 条`--throughput 100`**

> sh bin/kafka-producer-perf-test.sh --topic test_create_topic4 --num-records 1024 --throughput 100000 --producer-props bootstrap.servers=localhost:9092 --record-size 1024

发送了 1024 条消息; 并且总数据量=1M; 1024 条\*1024byte = 1M;

**2. 用指定消息文件`--payload-file `发送 100 条消息最大吞吐量每秒 100 条`--throughput 100`**

1. 先配置好消息文件`batchmessage.txt`

   ![在这里插入图片描述](https://img-blog.csdnimg.cn/072314b792cc407eb439447edf0ce7e1.png)

2. 然后执行命令
   发送的消息会从`batchmessage.txt`里面随机选择; 注意这里我们没有用参数`--payload-delimeter`指定分隔符，默认分隔符是\n 换行;

   > bin/kafka-producer-perf-test.sh --topic test_create_topic4 --num-records 1024 --throughput 100 --producer-props bootstrap.servers=localhost:9090 --payload-file config/batchmessage.txt

3. 验证消息，可以通过 [KnowStreaming](https://github.com/didi/KnowStreaming) 查看发送的消息

---

相关可选参数

| 参数                         | 描述                                                                                                                                                                                                                             | 例子                                                                       |
| ---------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | -------------------------------------------------------------------------- |
| `--topic`                    | 指定消费的 topic                                                                                                                                                                                                                 |                                                                            |
| `--num-records`              | 发送多少条消息                                                                                                                                                                                                                   |                                                                            |
| `--throughput`               | 每秒消息最大吞吐量                                                                                                                                                                                                               |                                                                            |
| `--producer-props`           | 生产者配置, k1=v1,k2=v2                                                                                                                                                                                                          | `--producer-props` bootstrap.servers= localhost:9092,client.id=test_client |
| `--producer.config`          | 生产者配置文件                                                                                                                                                                                                                   | `--producer.config` config/producer.propeties                              |
| `--print-metrics`            | 在 test 结束的时候打印监控信息,默认 false                                                                                                                                                                                        | `--print-metrics` true                                                     |
| `--transactional-id `        | 指定事务 ID，测试并发事务的性能时需要，只有在 --transaction-duration-ms > 0 时生效，默认值为 performance-producer-default-transactional-id                                                                                       |                                                                            |
| `--transaction-duration-ms ` | 指定事务持续的最长时间，超过这段时间后就会调用 commitTransaction 来提交事务，只有指定了 > 0 的值才会开启事务，默认值为 0                                                                                                         |                                                                            |
| `--record-size`              | 一条消息的大小 byte; 和 --payload-file 两个中必须指定一个，但不能同时指定                                                                                                                                                        |                                                                            |
| `--payload-file`             | 指定消息的来源文件，只支持 UTF-8 编码的文本文件，文件的消息分隔符通过 `--payload-delimeter `指定,默认是用换行\nl 来分割的，和 --record-size 两个中必须指定一个，但不能同时指定 ; 如果提供的消息                                  |                                                                            |
| `--payload-delimeter`        | 如果通过 `--payload-file` 指定了从文件中获取消息内容，那么这个参数的意义是指定文件的消息分隔符，默认值为 \n，即文件的每一行视为一条消息；如果未指定`--payload-file`则此参数不生效；发送消息的时候是随机送文件里面选择消息发送的; |                                                                            |

---

**KnowStreaming 可视化操作**

![在这里插入图片描述](https://img-blog.csdnimg.cn/d3bdf768af964ce28448985a8e4b16a0.png)

还可以设置很多方式, 比如手动、周期 发送方式, 还能手动设置 Header 值、指定分区号、压缩格式、Acks 等等

![在这里插入图片描述](https://img-blog.csdnimg.cn/950941fb648f4e67909c5db24a379609.png)

### 6.1.11 Topic 的消费 kafka-console-consumer.sh

**1. 新客户端从头消费`--from-beginning ` (注意这里是新客户端,如果之前已经消费过了是不会从头消费的)**
下面没有指定客户端名称,所以每次执行都是新客户端都会从头消费

> sh bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic test --from-beginning

**2. 正则表达式匹配 topic 进行消费`--whitelist `**
**`消费所有的topic`**

> sh bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --whitelist '.\*'
>
> **`消费所有的topic，并且还从头消费`**
> sh bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --whitelist '.\*' --from-beginning

**3.显示 key 进行消费`--property print.key=true`**

> sh bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic test --property print.key=true

**4. 指定分区消费`--partition` 指定起始偏移量消费`--offset `**

> sh bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic test --partition 0 --offset 100

**5. 给客户端命名`--group`**

注意给客户端命名之后,如果之前有过消费，那么`--from-beginning `就不会再从头消费了

> sh bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic test --group test-group

**6. 添加客户端属性`--consumer-property`**

这个参数也可以给客户端添加属性,但是注意 不能多个地方配置同一个属性,他们是互斥的;比如在下面的基础上还加上属性`--group test-group` 那肯定不行

> sh bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic test `--consumer-property group.id=test-consumer-group`

**7. 添加客户端属性`--consumer.config`**

跟`--consumer-property` 一样的性质,都是添加客户端的属性,不过这里是指定一个文件,把属性写在文件里面, `--consumer-property` 的优先级大于 `--consumer.config`

> sh bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic test --consumer.config config/consumer.properties

---

相关属性

| 参数                       | 描述                                                                                                                                             | 例子                                                                                                                                                                                                                       |
| -------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------ | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `--group `                 | 指定消费者所属组的 ID                                                                                                                            |                                                                                                                                                                                                                            |
| `--topic `                 | 被消费的 topic                                                                                                                                   |                                                                                                                                                                                                                            |
| `--partition `             | 指定分区 ；除非指定`–offset`，否则从分区结束(latest)开始消费                                                                                     | `--partition 0`                                                                                                                                                                                                            |
| `--offset `                | 执行消费的起始 offset 位置 ;默认值: latest; /latest /earliest /偏移量                                                                            | `--offset ` 10                                                                                                                                                                                                             |
| `--whitelist `             | 正则表达式匹配 topic；`--topic`就不用指定了; 匹配到的所有 topic 都会消费; 当然用了这个参数,`--partition` `--offset`等就不能使用了                |                                                                                                                                                                                                                            |
| `--consumer-property `     | 将用户定义的属性以 key=value 的形式传递给使用者                                                                                                  | `--consumer-property `group.id=test-consumer-group                                                                                                                                                                         |
| `--consumer.config `       | 消费者配置属性文件请注意，[`consumer-property`]优先于此配置                                                                                      | `--consumer.config` config/consumer.properties                                                                                                                                                                             |
| `--property `              | 初始化消息格式化程序的属性                                                                                                                       | print.timestamp=true,false 、print.key=true,false 、print.value=true,false 、key.separator=<key.separator> 、line.separator=<line.separator>、key.deserializer=<key.deserializer>、value.deserializer=<value.deserializer> |
| `--from-beginning `        | 从存在的最早消息开始，而不是从最新消息开始,注意如果配置了客户端名称并且之前消费过，那就不会从头消费了                                            |                                                                                                                                                                                                                            |
| `--max-messages `          | 消费的最大数据量，若不指定，则持续消费下去                                                                                                       | `--max-messages ` 100                                                                                                                                                                                                      |
| `--skip-message-on-error ` | 如果处理消息时出错，请跳过它而不是暂停                                                                                                           |                                                                                                                                                                                                                            |
| `--isolation-level `       | 设置为 read_committed 以过滤掉未提交的事务性消息,设置为 read_uncommitted 以读取所有消息,默认值:read_uncommitted                                  |                                                                                                                                                                                                                            |
| `--formatter`              | kafka.tools.DefaultMessageFormatter、kafka.tools.LoggingMessageFormatter、kafka.tools.NoOpMessageFormatter、kafka.tools.ChecksumMessageFormatter |                                                                                                                                                                                                                            |

### 6.1.12 持续批量拉取消息 kafka-verifiable-consumer

**持续消费**

> sh bin/kafka-verifiable-consumer.sh --group-id test_consumer --bootstrap-server localhost:9092 --topic test_create_topic4

**单次最大消费 10 条消息`--max-messages 10 `**

> sh bin/kafka-verifiable-consumer.sh --group-id test_consumer --bootstrap-server localhost:9092 --topic test_create_topic4 `--max-messages 10 `

---

相关可选参数

| 参数                                  | 描述                                                                                               | 例子                              |
| ------------------------------------- | -------------------------------------------------------------------------------------------------- | --------------------------------- |
| `--bootstrap-server ` 指定 kafka 服务 | 指定连接到的 kafka 服务;                                                                           | --bootstrap-server localhost:9092 |
| `--topic`                             | 指定消费的 topic                                                                                   |                                   |
| `--group-id`                          | 消费者 id；不指定的话每次都是新的组 id                                                             |                                   |
| `group-instance-id`                   | 消费组实例 ID,唯一值                                                                               |                                   |
| `--max-messages`                      | 单次最大消费的消息数量                                                                             |                                   |
| `--enable-autocommit`                 | 是否开启 offset 自动提交；默认为 false                                                             |                                   |
| `--reset-policy`                      | 当以前没有消费记录时，选择要拉取 offset 的策略，可以是`earliest`, `latest`,`none`。默认是 earliest |                                   |
| `--assignment-strategy `              | consumer 分配分区策略，默认是`org.apache.kafka.clients.consumer.RangeAssignor`                     |                                   |
| `--consumer.config`                   | 指定 consumer 的配置文件                                                                           |                                   |

### 6.1.13 消费者压力测试 kafka-consumer-perf-test.sh

**消费 100 条消息` --messages 100`**

> sh bin/kafka-consumer-perf-test.sh -topic test_create_topic4 --bootstrap-server localhost:9090 --messages 100

---

相关可选参数

| 参数                     | 描述                                                                    | 例子                          |
| ------------------------ | ----------------------------------------------------------------------- | ----------------------------- |
| `--bootstrap-server`     |                                                                         |                               |
| `--consumer.config`      | 消费者配置文件                                                          |                               |
| `--date-format`          | 结果打印出来的时间格式化                                                | 默认：yyyy-MM-dd HH:mm:ss:SSS |
| `--fetch-size `          | 单次请求获取数据的大小                                                  | 默认 1048576                  |
| `--topic`                | 指定消费的 topic                                                        |                               |
| `--from-latest `         |                                                                         |                               |
| `--group`                | 消费组 ID                                                               |                               |
| `--hide-header`          | 如果设置了,则不打印 header 信息                                         |                               |
| `--messages`             | 需要消费的数量                                                          |                               |
| `--num-fetch-threads`    | feth 数据的线程数(`废弃无效`)                                           | 默认：1                       |
| `--print-metrics `       | 结束的时候打印监控数据                                                  |                               |
| `--show-detailed-stats ` | 如果设置，则按照`--report_interval`配置的方式报告每个报告间隔的统计信息 |                               |
| `--threads`              | 消费线程数;(`废弃无效`)                                                 | 默认 10                       |
| `--reporting-interval`   | 打印进度信息的时间间隔（以毫秒为单位）                                  |                               |

---

**KnowStreaming 可视化操作**

一般用到这个功能可能是我们想要看看某个消息是否正常发送了,并且能够正常被消费到

这里我们贴心的加上了一些过滤条件, 比如消息太多了,你只想关注某些 value 是否被消费,你就可以在这里过滤了

![在这里插入图片描述](https://img-blog.csdnimg.cn/5bb522f70f074bb195c0b6c7cb988c16.png)

### 6.1.14 删除指定分区的消息 kafka-delete-records.sh

**删除指定 topic 的某个分区的消息删除至 offset 为 1024**

先配置 json 文件`offset-json-file.json`

```json
{
  "partitions": [{ "topic": "test1", "partition": 0, "offset": 1024 }],
  "version": 1
}
```

再执行命令

> sh bin/kafka-delete-records.sh --bootstrap-server 172.xxx.xxx.xxx:9090 --offset-json-file config/offset-json-file.json

验证 通过 [KnowStreaming](https://github.com/didi/KnowStreaming) 查看发送的消息

---

**KnowStreaming 可视化操作**

**TODO： Know Streaming 开发中,敬请期待！**

### 6.1.15 查看 Broker 磁盘信息 kafka-log-dirs.sh

**查询指定 topic 磁盘信息` --topic-list topic1,topic2`**

> sh bin/kafka-log-dirs.sh --bootstrap-server xxxx:9090 --describe --topic-list test2

**查询指定 Broker 磁盘信息`--broker-list 0 broker1,broker2`**

> sh bin/kafka-log-dirs.sh --bootstrap-server xxxxx:9090 --describe --topic-list test2 --broker-list 0

例如我一个 3 分区 3 副本的 Topic 的查出来的信息
`logDir` Broker 中配置的`log.dir`

```json
{
  "version": 1,
  "brokers": [
    {
      "broker": 0,
      "logDirs": [
        {
          "logDir": "/Users/xxxx/work/IdeaPj/ss/kafka/kafka-logs-0",
          "error": null,
          "partitions": [
            {
              "partition": "test2-1",
              "size": 0,
              "offsetLag": 0,
              "isFuture": false
            },
            {
              "partition": "test2-0",
              "size": 0,
              "offsetLag": 0,
              "isFuture": false
            },
            {
              "partition": "test2-2",
              "size": 0,
              "offsetLag": 0,
              "isFuture": false
            }
          ]
        }
      ]
    },
    {
      "broker": 1,
      "logDirs": [
        {
          "logDir": "/Users/xxxx/work/IdeaPj/ss/kafka/kafka-logs-1",
          "error": null,
          "partitions": [
            {
              "partition": "test2-1",
              "size": 0,
              "offsetLag": 0,
              "isFuture": false
            },
            {
              "partition": "test2-0",
              "size": 0,
              "offsetLag": 0,
              "isFuture": false
            },
            {
              "partition": "test2-2",
              "size": 0,
              "offsetLag": 0,
              "isFuture": false
            }
          ]
        }
      ]
    },
    {
      "broker": 2,
      "logDirs": [
        {
          "logDir": "/Users/xxxx/work/IdeaPj/ss/kafka/kafka-logs-2",
          "error": null,
          "partitions": [
            {
              "partition": "test2-1",
              "size": 0,
              "offsetLag": 0,
              "isFuture": false
            },
            {
              "partition": "test2-0",
              "size": 0,
              "offsetLag": 0,
              "isFuture": false
            },
            {
              "partition": "test2-2",
              "size": 0,
              "offsetLag": 0,
              "isFuture": false
            }
          ]
        }
      ]
    },
    {
      "broker": 3,
      "logDirs": [
        {
          "logDir": "/Users/xxxx/work/IdeaPj/ss/kafka/kafka-logs-3",
          "error": null,
          "partitions": []
        }
      ]
    }
  ]
}
```

---

**KnowStreaming 可视化操作**

**查询某个 Broker 下面的所有 DataLog**

![在这里插入图片描述](https://img-blog.csdnimg.cn/0b48e81b435b41dca1c0d34762ad87b2.png)

### 6.1.16 消费者组管理 kafka-consumer-groups.sh

#### 查看消费者列表

> `sh bin/kafka-consumer-groups.sh --bootstrap-server xxxx:9090 --list ` >![在这里插入图片描述](https://img-blog.csdnimg.cn/20210625175303377.png)

先调用`MetadataRequest`拿到所有在线 Broker 列表
再给每个 Broker 发送`ListGroupsRequest`请求获取 消费者组数据

---

**KnowStreaming 可视化操作**

![在这里插入图片描述](https://img-blog.csdnimg.cn/3473e0618b52488f8689c2d1dfecc9c6.png)

注意：如果你是直接用[KnowStreaming](https://github.com/didi/KnowStreaming) 的 Consumer 功能测试消费, 这里是不展示的哈,因为那里的消费者不提交消费 Offset(设计如此,那里只是用于测试,无业务逻辑)。

#### 查看消费者组详情

`DescribeGroupsRequest`

**查看消费组详情`--group` 或 `--all-groups`**

> **查看指定消费组详情`--group`** >`sh bin/kafka-consumer-groups.sh --bootstrap-server xxxxx:9090 --describe --group test2_consumer_group`
>
> ---
>
> **查看所有消费组详情`--all-groups`** >`sh bin/kafka-consumer-groups.sh --bootstrap-server xxxxx:9090 --describe --all-groups`
> 查看该消费组 消费的所有 Topic、及所在分区、最新消费 offset、Log 最新数据 offset、Lag 还未消费数量、消费者 ID 等等信息
> ![在这里插入图片描述](https://img-blog.csdnimg.cn/20210625174905816.png)

**查询消费者成员信息`--members`**

> **所有消费组成员信息** >`sh bin/kafka-consumer-groups.sh --describe --all-groups --members --bootstrap-server xxx:9090` >**指定消费组成员信息** >`sh bin/kafka-consumer-groups.sh --describe --members --group test2_consumer_group --bootstrap-server xxxx:9090 ` > ![在这里插入图片描述](https://img-blog.csdnimg.cn/2021062613532445.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTA2MzQwNjY=,size_16,color_FFFFFF,t_70)

**查询消费者状态信息`--state`**

> **所有消费组状态信息** >`sh bin/kafka-consumer-groups.sh --describe --all-groups --state --bootstrap-server xxxx:9090` >**指定消费组状态信息** >`sh bin/kafka-consumer-groups.sh --describe --state --group test2_consumer_group --bootstrap-server xxxxx:9090` >![在这里插入图片描述](https://img-blog.csdnimg.cn/2021062615553298.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTA2MzQwNjY=,size_16,color_FFFFFF,t_70)

**KnowStreaming 可视化操作**

![在这里插入图片描述](https://img-blog.csdnimg.cn/706c6857fb43495484976557af6834da.png)

#### 删除消费者组

`DeleteGroupsRequest`

**删除消费组--delete**

> **删除指定消费组`--group`** >`sh bin/kafka-consumer-groups.sh --delete --group test2_consumer_group --bootstrap-server xxxx:9090` >**删除所有消费组`--all-groups`** >` sh bin/kafka-consumer-groups.sh --delete --all-groups --bootstrap-server xxxx:9090`

**PS: 想要删除消费组前提是这个消费组的所有客户端都停止消费/不在线才能够成功删除;否则会报下面异常**

```
Error: Deletion of some consumer groups failed:

* Group 'test2_consumer_group' could not be deleted due to:
*  java.util.concurrent.ExecutionException: org.apache.kafka.common.errors.GroupNotEmptyException: The group is not empty.
*

```

---

**KnowStreaming 可视化操作**

**TODO: 截一张 消费组详情的图片**

#### 重置消费组的偏移量

<font color=red>能够执行成功的一个前提是 消费组这会是不可用状态;</font>

下面的示例使用的参数是: `--dry-run` ;这个参数表示预执行,会打印出来将要处理的结果;
等你想真正执行的时候请换成参数`--execute` ;

下面示例 重置模式都是 `--to-earliest` 重置到最早的;

请根据需要参考下面 **相关重置 Offset 的模式** 换成其他模式;

**重置指定消费组的偏移量 `--group `**

> **重置指定消费组的所有 Topic 的偏移量`--all-topic`** >`sh bin/kafka-consumer-groups.sh --reset-offsets --to-earliest --group test2_consumer_group --bootstrap-server xxxx:9090 --dry-run --all-topic` >**重置指定消费组的指定 Topic 的偏移量`--topic`** >`sh bin/kafka-consumer-groups.sh --reset-offsets --to-earliest --group test2_consumer_group --bootstrap-server xxxx:9090 --dry-run --topic test2`

**重置所有消费组的偏移量 `--all-group `**

> **重置所有消费组的所有 Topic 的偏移量`--all-topic`** >`sh bin/kafka-consumer-groups.sh --reset-offsets --to-earliest --all-group --bootstrap-server xxxx:9090 --dry-run --all-topic` >**重置所有消费组中指定 Topic 的偏移量`--topic`** >`sh bin/kafka-consumer-groups.sh --reset-offsets --to-earliest --all-group --bootstrap-server xxxx:9090 --dry-run --topic test2`

`--reset-offsets` 后面需要接**重置的模式**

**相关重置 Offset 的模式**

| 参数                  | 描述                                                                                                                                                                                                                                                                         | 例子                                                                                                                                                                                                                                     |
| --------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **`--to-earliest` :** | 重置 offset 到最开始的那条 offset(找到还未被删除最早的那个 offset)                                                                                                                                                                                                           |                                                                                                                                                                                                                                          |
| **`--to-current`:**   | 直接重置 offset 到当前的 offset，也就是 LOE                                                                                                                                                                                                                                  |                                                                                                                                                                                                                                          |
| **`--to-latest`：**   | 重置到最后一个 offset                                                                                                                                                                                                                                                        |                                                                                                                                                                                                                                          |
| **`--to-datetime`:**  | 重置到指定时间的 offset;格式为:`YYYY-MM-DDTHH:mm:SS.sss`;                                                                                                                                                                                                                    | ` --to-datetime "2021-6-26T00:00:00.000"`                                                                                                                                                                                                |
| `--to-offset`         | 重置到指定的 offset,但是通常情况下,匹配到多个分区,这里是将匹配到的所有分区都重置到这一个值; 如果 1.目标最大 offset<`--to-offset`, 这个时候重置为目标最大 offset；2.目标最小 offset>`--to-offset` ，则重置为最小; 3.否则的话才会重置为`--to-offset`的目标值; **一般不用这个** | `--to-offset 3465` ![在这里插入图片描述](https://img-blog.csdnimg.cn/20210626153528929.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTA2MzQwNjY=,size_16,color_FFFFFF,t_70) |
| `--shift-by `         | 按照偏移量增加或者减少多少个 offset；正的为往前增加;负的往后退；当然这里也是匹配所有的;                                                                                                                                                                                      | `--shift-by 100` 、`--shift-by -100`                                                                                                                                                                                                     |
| `--from-file`         | 根据 CVS 文档来重置; 这里下面单独讲解                                                                                                                                                                                                                                        |                                                                                                                                                                                                                                          |

**`--from-file`着重讲解一下**

> 上面其他的一些模式重置的都是匹配到的所有分区; 不能够每个分区重置到不同的 offset；不过**`--from-file`**可以让我们更灵活一点;

1. 先配置 cvs 文档
   格式为: Topic:分区号: 重置目标偏移量
   ```cvs
   test2,0,100
   test2,1,200
   test2,2,300
   ```
2. 执行命令
   > `sh bin/kafka-consumer-groups.sh --reset-offsets --group test2_consumer_group --bootstrap-server xxxx:9090 --dry-run --from-file config/reset-offset.csv`

---

**KnowStreaming 可视化操作**

#### 删除偏移量

<font color=red>能够执行成功的一个前提是 消费组这会是不可用状态;</font>

偏移量被删除了之后,Consumer Group 下次启动的时候,会从头消费;

> `sh bin/kafka-consumer-groups.sh --delete-offsets --group test2_consumer_group2 --bootstrap-server XXXX:9090 --topic test2`

---

相关可选参数

| 参数                  | 描述                                                                                                                   | 例子                              |
| --------------------- | ---------------------------------------------------------------------------------------------------------------------- | --------------------------------- |
| `--bootstrap-server ` | 指定连接到的 kafka 服务;                                                                                               | --bootstrap-server localhost:9092 |
| `--list `             | 列出所有消费组名称                                                                                                     | `--list `                         |
| `--describe`          | 查询消费者描述信息                                                                                                     | `--describe`                      |
| `--group`             | 指定消费组                                                                                                             |                                   |
| `--all-groups`        | 指定所有消费组                                                                                                         |                                   |
| `--members`           | 查询消费组的成员信息                                                                                                   |                                   |
| `--state`             | 查询消费者的状态信息                                                                                                   |                                   |
| `--offsets`           | 在查询消费组描述信息的时候,这个参数会列出消息的偏移量信息; 默认就会有这个参数的;                                       |                                   |
| `dry-run`             | 重置偏移量的时候,使用这个参数可以让你预先看到重置情况，这个时候还没有真正的执行,真正执行换成`--excute`;默认为`dry-run` |
| `--excute`            | 真正的执行重置偏移量的操作;                                                                                            |                                   |
| `--to-earliest`       | 将 offset 重置到最早                                                                                                   |                                   |
| `to-latest`           | 将 offset 重置到最近                                                                                                   |                                   |

---

**KnowStreaming 可视化操作**

### 6.1.17 查看日志文件 kafka-dump-log.sh

| 参数                                  | 描述                                                                                                                                                                                                      | 例子                   |
| ------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ---------------------- |
| `--deep-iteration `                   |                                                                                                                                                                                                           |                        |
| `--files <String: file1, file2, ...>` | 必需; 读取的日志文件                                                                                                                                                                                      | --files 0000009000.log |
| `--key-decoder-class`                 | 如果设置，则用于反序列化键。这类应实现 kafka.serializer。解码器特性。自定义 jar 应该是在 kafka/libs 目录中提供                                                                                            |                        |
| `--max-message-size`                  | 最大的数据量,默认：5242880                                                                                                                                                                                |
| `--offsets-decoder`                   | if set, log data will be parsed as offset data from the \_\_consumer_offsets topic.                                                                                                                       |                        |
| `--print-data-log `                   | 打印内容                                                                                                                                                                                                  |                        |
| `--transaction-log-decoder`           | if set, log data will be parsed as transaction metadata from the \_\_transaction_state topic                                                                                                              |
| `--value-decoder-class [String] `     | if set, used to deserialize the messages. This class should implement kafka. serializer.Decoder trait. Custom jar should be available in kafka/libs directory. (default: kafka.serializer. StringDecoder) |
| `--verify-index-only `                | if set, just verify the index log without printing its content.                                                                                                                                           |

**查询 Log 文件**

> `sh bin/kafka-dump-log.sh --files kafka-logs-0/test2-0/00000000000000000300.log` >![在这里插入图片描述](https://img-blog.csdnimg.cn/img_convert/e90ad7f1c39e5208120080475845dbb3.png)

**查询 Log 文件具体信息 `--print-data-log`**

> `sh bin/kafka-dump-log.sh --files kafka-logs-0/test2-0/00000000000000000300.log --print-data-log` > ![在这里插入图片描述](https://img-blog.csdnimg.cn/img_convert/3306f80db58a943e6a59c39e66b4c4fe.png)

**查询 index 文件具体信息**

> `sh bin/kafka-dump-log.sh --files kafka-logs-0/test2-0/00000000000000000300.index` >![在这里插入图片描述](https://img-blog.csdnimg.cn/img_convert/bd918af516229bbb80640483831d6595.png)
> 配置项为`log.index.size.max.bytes`； 来控制创建索引的大小;

**查询 timeindex 文件**

> `sh bin/kafka-dump-log.sh --files kafka-logs-0/test2-0/00000000000000000300.timeindex ` > ![在这里插入图片描述](https://img-blog.csdnimg.cn/img_convert/2254d080ca4e63d13714b40b6c0c3b63.png)

---

**KnowStreaming 可视化操作**

### 6.1.18 kafka-leader-election Leader 重新选举

**1 指定 Topic 指定分区用重新`PREFERRED：优先副本策略` 进行 Leader 重选举**

```shell

> sh bin/kafka-leader-election.sh --bootstrap-server xxxx:9090 --topic test_create_topic4 --election-type PREFERRED --partition 0

```

**2 所有 Topic 所有分区用重新`PREFERRED：优先副本策略` 进行 Leader 重选举**

```shell
sh bin/kafka-leader-election.sh --bootstrap-server xxxx:9090 --election-type preferred  --all-topic-partitions

```

**3 设置配置文件批量指定 topic 和分区进行 Leader 重选举**

先配置 leader-election.json 文件

```json
{
  "partitions": [
    {
      "topic": "test_create_topic4",
      "partition": 1
    },
    {
      "topic": "test_create_topic4",
      "partition": 2
    }
  ]
}
```

```shell

 sh bin/kafka-leader-election.sh --bootstrap-server xxx:9090 --election-type preferred  --path-to-json-file config/leader-election.json

```

---

相关可选参数

| 参数                                  | 描述                                                                                   | 例子                              |
| ------------------------------------- | -------------------------------------------------------------------------------------- | --------------------------------- |
| `--bootstrap-server ` 指定 kafka 服务 | 指定连接到的 kafka 服务                                                                | --bootstrap-server localhost:9092 |
| `--topic`                             | 指定 Topic，此参数跟`--all-topic-partitions`和`path-to-json-file` 三者互斥             |                                   |
| `--partition `                        | 指定分区,跟`--topic`搭配使用                                                           |                                   |
| `--election-type`                     | 两个选举策略(`PREFERRED: `优先副本选举,如果第一个副本不在线的话会失败;`UNCLEAN`: 策略) |                                   |
| `--all-topic-partitions`              | 所有 topic 所有分区执行 Leader 重选举; 此参数跟`--topic`和`path-to-json-file` 三者互斥 |                                   |
| ` --path-to-json-file`                | 配置文件批量选举，此参数跟`--topic`和`all-topic-partitions` 三者互斥                   |                                   |

---

**KnowStreaming 可视化操作**

开发中, 尽情期待！

## 6.2 数据中心

一些部署需要管理跨越多个数据中心的数据管道。我们推荐的方法是在每个数据中心部署一个本地 Kafka 集群，每个数据中心中的应用程序实例仅与其本地集群交互并在集群之间镜像数据（有关如何执行此操作， 请参阅有关[Geo-Replication 的文档](https://kafka.apache.org/27/documentation.html#georeplication)）。
这种部署模式允许数据中心充当独立的实体，并允许我们集中管理和调整数据中心间的复制。即使数据中心间的链接不可用，这也允许每个设施独立运行：当发生这种情况时，镜像会落后，直到链接恢复时才赶上。

对于需要所有数据的全局视图的应用程序，您可以使用镜像来提供集群，这些集群具有从所有数据中心的本地集群镜像的聚合数据。这些聚合集群用于需要完整数据集的应用程序的读取。

这不是唯一可能的部署模式。可以通过 WAN 读取或写入远程 Kafka 集群，但显然这会增加获取集群所需的任何延迟。

Kafka 自然地在生产者和消费者中对数据进行批处理，因此即使在高延迟连接上也可以实现高吞吐量。为了实现这一点，可能需要使用 socket.send.buffer.bytes 和 socket.receive.buffer.bytes 配置增加生产者、消费者和代理的 TCP 套接字缓冲区大小。[此处](http://en.wikipedia.org/wiki/Bandwidth-delay_product)记录了设置此设置的适当方法。

通常不建议在高延迟链路上运行跨越多个数据中心的单个 Kafka 集群。这将导致 Kafka 写入和 ZooKeeper 写入的复制延迟非常高，如果位置之间的网络不可用，Kafka 和 ZooKeeper 都不会在所有位置保持可用。

## 6.3 异地复制(跨集群数据镜像)

[异地复制(跨集群数据镜像)](https://kafka.apache.org/27/documentation.html#georeplication)

## 6.4 多租户

## 6.5 Java 版本

支持 Java 8 和 Java 11。如果启用 TLS，Java 11 的性能会显着提高，因此强烈推荐（它还包括许多其他性能改进：G1GC、CRC32C、紧凑字符串、线程本地握手等）。从安全角度来看，我们建议使用最新发布的补丁版本，因为较旧的免费版本已经披露了安全漏洞。使用基于 OpenJDK 的 Java 实现（包括 Oracle JDK）运行 Kafka 的典型参数是：

```sh

-Xmx6g -Xms6g -XX:MetaspaceSize=96m -XX:+UseG1GC
  -XX:MaxGCPauseMillis=20 -XX:InitiatingHeapOccupancyPercent=35 -XX:G1HeapRegionSize=16M
  -XX:MinMetaspaceFreeRatio=50 -XX:MaxMetaspaceFreeRatio=80 -XX:+ExplicitGCInvokesConcurrent

```

## 6.6 硬件和操作系统

您需要足够的内存来缓冲 活动的读取器和写入器，您可以通过假设您希望能够缓冲 30 秒并将您的内存需求计算为 `write_throughput*30` 来粗略估计内存需求。

磁盘吞吐量很重要, 一般来说，**磁盘吞吐量是性能瓶颈，磁盘越多越好**。

磁盘吞吐量很重要。一般来说，磁盘吞吐量是性能瓶颈，磁盘越多越好。

根据您配置刷新行为的方式，您可能会或可能不会从更昂贵的磁盘中受益（如果您经常强制刷新，那么使用更高 转速的 SAS 驱动器可能会更好，如果您不经常强制刷新, 使用 7200 RPM 的 SATA 也未尝不可, ）。

---

PS:

**RPM**：硬盘转速以每分钟多少转来表示，单位表示为 RPM，RPM 是 Revolutions Per minute 的缩写，是转/每分钟。RPM 值越大，内部传输率就越快，访问时间就越短，硬盘的整体性能也就越好。

**SATA**

2001 年，由 Intel、APT、Dell、IBM、希捷、迈拓这几大厂商组成的 Serial ATA 委员会正式确立了 Serial ATA 1.0 规范，2002 年，虽然串行 ATA 的相关设备还未正式上市，但 Serial ATA 委员会已抢先确立了 Serial ATA 2.0 规范。Serial ATA 采用串行连接方式，串行 ATA 总线运用嵌入式时钟信号，具备了更强的纠错能力，与以往相比其最大的分别在于能对传输指令(不仅仅是数据)进行检查如果发现错误会自动矫正

**SAS(Serial Attached SCSI)** 即串行连接 SCSI，是新一代的 SCSI 技术，和现在流行的 Serial ATA(SATA)硬盘相同，都是采用串行技术以获得更高的传输速度。并通过缩短连结线改善内部空间等。SAS 是并行 SCSI 接口之后开发出的全新接口。此接口的规划是为了改善储存系统的效能、可用性和扩充性，并且提供与 SATA 硬盘的兼容性。

**SAS 硬盘与 SATA 硬盘的区别**

SAS 速度更快,更稳定, SATA 便宜 速度比不上 SAS。

<font size=5 ><b> 操作系统 </b></font>

Kafka 应该可以在任何 unix 系统上运行良好，但是在 windows 上可能会出现一些问题。

**有三个可能很重要的操作系统级别配置：**

- 文件描述符限制：Kafka 会将文件描述符用于 **打开日志段 Segment** 和 **建立连接**。
  如果 Broker 有很多的分区，请考虑 Broker 至少需要` (number_of_partitions)*(partition_size/segment_size) (这个计算出来的就是日志段的个数)` 个文件描述符来用于**打开日志段 Segment**。建议配置至少 100000 个文件描述符限制。
  注意： mmap() 函数添加了对与文件描述符 fildes 关联的文件的额外引用，该文件描述符上的后续 close() 不会删除该引用。当文件没有更多映射时，将删除此引用。

- 最大套接字缓冲区大小：适当增加这个值以实现数据中心之间的高性能数据传输。
- 进程可能拥有的最大内存映射区域数（又名 vm.max_map_count）：在考虑代理可能拥有的最大分区数时，您应该留意这个操作系统级别的属性。默认情况下，在许多 Linux 系统上，vm.max_map_count 的值在 65535 左右。每个分区分配的每个日志段都需要一对索引/时间索引文件，每个文件占用 1 个映射区域。换句话说，每个日志段使用 2 个 map 区域。因此，每个分区至少需要 2 个映射区域(如果它只有一个日志段 Segment 的话)。<font color=red>如果超过 vm.max_map_count 的话会导致 broker 崩溃并出现 OutOfMemoryError (Map failed)</font>

---

知识点补充：

**文件描述符**：每一个文件描述符会与一个打开文件相对应，同时，不同的文件描述符也会指向同一个文件。相同的文件可以被不同的进程打开也可以在同一个进程中被多次打开。系统为每一个进程维护了一个文件描述符表，该表的值都是从 0 开始的，所以在不同的进程中你会看到相同的文件描述符，这种情况下相同文件描述符有可能指向同一个文件，也有可能指向不同的文件。

你可能平时会遇到“Too many open files”的问题，这主要是因为文件描述符是系统的一个重要资源，虽然说系统内存有多少就可以打开多少的文件描述符，但是在实际实现过程中内核是会做相应的处理的，一般最大打开文件数会是系统内存的 10%（以 KB 来计算）（称之为系统级限制），查看系统级别的最大打开文件数可以使用`sysctl -a | grep fs.file-max`命令查看。

<font size=5 ><b> 应用程序与操作系统刷新管理 </b></font>

Kafka 总是立即将所有数据写入文件系统，并支持配置刷新策略的能力，该策略控制何时将数据强制从操作系统缓存中取出并使用刷新写入磁盘。可以控制此刷新策略以在一段时间后或在写入一定数量的消息后强制将数据写入磁盘。此配置有多种选择。

Kafka 最终必须调用 fsync 才能知道数据已刷新。当从任何未知的 fsync 日志段的崩溃中恢复时，Kafka 将通过检查其 CRC 来检查每条消息的完整性，并重建随附的偏移量索引文件，作为启动时执行的恢复过程的一部分。

请注意，Kafka 中的持久性不需要将数据同步到磁盘，因为故障节点总是会从其副本中恢复。

建议完全禁用应用程序`fsync`的默认刷新配置，这意味着依赖于操作系统完成的后台刷新和 Kafka 自己的后台刷新。

复制提供的保证比同步到本地磁盘要强，但是部分人可能更喜欢两者兼有，所以 kafka 支持应用程序级别的 fsync 策略。

使用应用程序级别刷新设置的缺点是它的磁盘使用模式效率较低（它给操作系统重新排序写入的余地）并且它可以引入延迟，因为大多数 Linux 文件系统中的 fsync 会阻止对文件的写入，而后台刷新执行更细粒度的页面级锁定。

**一般来说，您不需要对文件系统进行任何低级调整**

<font size=5 ><b> Linux 操作系统系统刷新的行为 </b></font>

在 Linux 中，写入文件系统的数据在[Page Cache](http://en.wikipedia.org/wiki/Page_cache)中维护，直到必须将其写入磁盘（由于应用程序级 fsync 或操作系统自己的刷新策略）。数据的刷新由一组称为` pdflush` 的后台线程（或在 2.6.32 后内核中的“flusher threads”中）完成。

**Pdflush** 有一个可配置的策略，控制可以在缓存中维护多少脏数据以及必须将其写回磁盘之前的时间。详情看:[此处](http://web.archive.org/web/20160518040713/http://www.westnet.com/~gsmith/content/linux-pdflush.htm)

<font color=red><b>当 Pdflush 无法跟上数据写入的速度时，它最终会导致写入过程阻塞写入中产生的延迟，从而减慢数据的积累</b></font>

您可以通过执行查看操作系统内存使用的当前状态

```sh

> cat /proc/meminfo

```

与进程内缓存相比，使用 pagecache 有几个优点，用于存储将写入磁盘的数据：

- **I/O 调度程序将把连续的小写入批处理成更大的物理写入，从而提高吞吐量。**
- **I/O 调度程序将尝试重新排序写入以最小化磁盘磁头的移动，从而提高吞吐量。**
- **它会自动使用机器上的所有空闲内存**

<font size=5 ><b> 文件系统选择 </b></font>

Kafka 使用磁盘上的常规文件，因此它对特定文件系统没有硬依赖。然而，使用最多的两个文件系统是 EXT4 和 XFS。从历史上看，EXT4 的使用率更高，但最近对 XFS 文件系统的改进表明它对于 Kafka 的工作负载具有更好的性能特征，而不会影响稳定性。

使用各种文件系统创建和挂载选项在具有大量消息负载的集群上执行比较测试。Kafka 中监控的主要指标是“请求本地时间”，表示追加操作所花费的时间。XFS 带来了更好的本地时间（最佳 EXT4 配置为 160 毫秒对 250 毫秒+），以及更低的平均等待时间。XFS 性能还显示磁盘性能的变化较小。

**一般文件系统说明**

对于任何用于数据目录的文件系统，在 Linux 系统上，建议在挂载时使用以下选项：

- noatime：此选项在读取文件时禁用更新文件的 atime（上次访问时间）属性。这可以消除大量文件系统写入，尤其是在引导消费者的情况下。Kafka 根本不依赖 atime 属性，因此禁用它是安全的。

**XFS**

XFS 文件系统具有大量的自动调整功能，因此它不需要在文件系统创建时或挂载时对默认设置进行任何更改。唯一值得考虑的调整参数是：

- largeio：这会影响 stat 调用报告的首选 I/O 大小。虽然这可以在更大的磁盘写入上实现更高的性能，但实际上它对性能的影响很小或没有影响。
- nobarrier：对于具有电池后备缓存的底层设备，此选项可以通过禁用定期写入刷新来提供更多性能。但是，如果底层设备表现良好，它将向文件系统报告它不需要刷新，并且此选项将无效。

**EXT4**

EXT4 是适用于 Kafka 数据目录的文件系统的可用选择，但是要从中获得最大性能将需要调整几个挂载选项。此外，这些选项在故障情况下通常是不安全的，并且会导致更多的数据丢失和损坏。对于单个 Broker 故障，这不是什么大问题，因为可以擦除磁盘并从集群重建副本。在多故障情况下，例如断电，这可能意味着底层文件系统（以及因此数据）损坏且不易恢复。可以调整以下选项：

- data=writeback：Ext4 默认为 data=ordered，这对某些写入设置了严格的顺序。Kafka 不需要这种排序，因为它对所有未刷新的日志进行非常偏执的数据恢复。此设置消除了排序约束，并且似乎显着减少了延迟。
- 禁用日志：日志是一种权衡：它可以在服务器崩溃后更快地重新启动，但它引入了大量额外的锁定，从而增加了写入性能的差异。那些不关心重新启动时间并希望减少写入延迟峰值的主要来源的人可以完全关闭日志记录。
- commit=num_secs：这会调整 ext4 提交到其元数据日志的频率。将此设置为较低的值可减少崩溃期间未刷新数据的丢失。将此设置为更高的值将提高吞吐量。
- nobh：此设置控制使用 data=writeback 模式时的额外排序保证。这对于 Kafka 来说应该是安全的，因为我们不依赖于写入顺序并提高了吞吐量和延迟。
- delalloc：延迟分配意味着文件系统在物理写入发生之前避免分配任何块。这允许 ext4 分配大范围而不是较小的页面，并有助于确保数据按顺序写入。此功能非常适合吞吐量。它似乎确实涉及文件系统中的一些锁定，这增加了一些延迟差异。

## 6.7 监控

Kafka 使用 Yammer Metrics 在服务器中报告指标。Java 客户端使用 Kafka Metrics，这是一个内置的指标注册表，可以最大限度地减少引入客户端应用程序的传递依赖。两者都通过 JMX 公开指标，并且可以配置为使用可插入的统计报告器报告统计信息以连接到您的监控系统。
所有 Kafka 速率指标都有一个相应的累积计数指标，后缀为-total。例如， records-consumed-rate 有一个名为 records-consumed-total.

查看可用指标的最简单方法是启动 jconsole 并将其指向正在运行的 kafka 客户端或服务器；这将允许使用 JMX 浏览所有指标。

### 6.7.1 启用 JMX 并上报指标

Kafka 默认禁用远程 JMX，Kafka 启动 JMX 方式

**方式一：**

```

JMX_PORT=端口号 nohup bin/kafka-server-start.sh config/server.properties &

```

![在这里插入图片描述](https://img-blog.csdnimg.cn/adfcffa967874a79a44d7d36e5a13419.png)

**方式二：**

在启动脚本里面 对 JMX_PORT 赋值，在`kafka-server-start.sh` 增加一句

```
export JMX_PORT="端口号"
```

![在这里插入图片描述](https://img-blog.csdnimg.cn/26eeca4c7c2b49408c67f1d451af20eb.png)

然后再启动脚本,JMX 就会自动开启了

**方式三：在 IDEA 中启用 JMX**

如果你是在 IDEA 启动 Kafka 源码的形式开启 JMX 那么你可以在启动的时候加入以下参数

```
-Djava.rmi.server.hostname=127.0.0.1
-Dcom.sun.management.jmxremote
-Dcom.sun.management.jmxremote.port=端口
-Dcom.sun.management.jmxremote.ssl=false
-Dcom.sun.management.jmxremote.authenticate=false
```

![在这里插入图片描述](https://img-blog.csdnimg.cn/1b98ed1131ea4406a283eb4a7c7468b0.png)

**方式四：安全启用 JMX**

在生产场景中启用远程 JMX 时，您必须启用安全性，以确保未经授权的用户无法监视或控制您的代理或应用程序以及运行它们的平台.

更详细的请看：[使用 JMX 技术进行监控和管理](https://docs.oracle.com/javase/8/docs/technotes/guides/management/agent.html)

### 6.7.2 查看 JMX 指标的方式

启动 JMX 之后, 我们在 Zookeeper 中的节点`/brokers/ids/{brokerID}` 数据中可以看到我们的端口是否注册成功。

```json
{
  "features": {},
  "listener_security_protocol_map": {
    "PLAINTEXT": "PLAINTEXT"
  },
  "endpoints": ["PLAINTEXT://localhost:9092"],
  "jmx_port": 9999,
  "port": 9092,
  "host": "localhost",
  "version": 5,
  "timestamp": "1659670870502"
}
```

其中数据 **jmx_port": 9999** 就可以指定我们的 JMX 已经开启并且端口号是 9999

**使用 jconsole 连接信息并打开**

在按照 JDK 的时候，jconsole 已经按照好了, 我们可以直接使用这个工具来可视化界面监控 Java 程序运行状况。

```sh

shizhenzhen@localhost  % jconsole

```

![在这里插入图片描述](https://img-blog.csdnimg.cn/09fd43f7cde84181a4881844dc9b24b8.png)

这里可以连接本地的也可以是远程的，链接之后, 选择 MBean 就可以看到指标了

![在这里插入图片描述](https://img-blog.csdnimg.cn/c7c17ba93da6416996397d16d91b23ef.png)

### 6.7.3 指标采集和统计机制

在开始分析之前,我们可以 自己思考一下

**如果让你统计前一分钟内的流速,你会怎么统计才能够让数字更加精确呢？**

我相信你脑海中肯定出现了一个词：**滑动窗口**

在 kafka 的数据采样和统计中,也是用了这个方法, 通过多个样本`Sample`进行采样,并合并统计

当然这一个过程少不了**滑动窗口**的影子

#### 采集和统计类图

我们先看下整个 Kafka 的数据采集和统计机制的类图

![数据采集和统计全类图](https://img-blog.csdnimg.cn/ffd6b4f9dcdb41369ad0923ef157d4a9.png#pic_center)

看着整个类图好像很复杂,但是最核心的就是两个 Interface 接口

**`Measurable: `** 可测量的、可统计的 Interface。这个 Interface 有一个方法, 专门用来计算需要被统计的值的

```java
/**
* 测量这个数量并将结果作为双精度返回
* 参数：
* config – 此指标的配置
* now – 进行测量的 POSIX 时间（以毫秒为单位）
* 返回：
* 测量值
*/
double measure(MetricConfig config, long now);

```

比如说返回 `近一分钟的bytesIn`

**`Stat:`** 记录数据, 上面的是统计,但是统计需要数据来支撑, 这个 Interface 就是用来做记录的,这个 Interface 有一个方法

```java

 /**
 * 记录给定的值
 * 参数：
 * config – 用于该指标的配置
 * value – 要记录的值
 * timeMs – 此值发生的 POSIX 时间（以毫秒为单位）
 */
 void record(MetricConfig config, double value, long timeMs);

```

有了这两个接口,就基本上可以**记录数据**和**数据统计**了

当然这两个接口都有一个 **`MetricConfig`** 对象

![MetricConfig](https://img-blog.csdnimg.cn/4dc9c0f7538a443cb3cb82ddd6c4bc87.png)

这是一个统计配置类, 主要是定义
**采样的样本数**、**单个样本的时间窗口大小**、**单个样本的事件窗口大小**、**限流机制**
有了这样一个配置了,就可以自由定义时间窗口的大小,和采样的样本数之类的影响最终数据精度的变量。

这里我需要对两个参数重点说明一下

**单个样本的时间窗口大小:** 当前记录时间 - 当前样本的开始时间 >= 此值 则需要使用下一个样本。
**单个样本的事件窗口大小:** 当前样本窗口时间次数 >= 此值 则需要使用下一个样本

在整个统计中,不一定是按照**时间窗口**来统计的, 也可以按照**事件窗口**来统计, 具体按照不同需求选择配置

好了,大家脑海里面已经有了最基本的概念了,我们接下来就以一个 kafka 内部经常使用的 `SampledStat` 记录和统计的抽象类来好好的深入分析理解一下。

#### SampledStat 样本记录统计抽象类

> 这个记录统计抽象类,是按照采样的形式来计算的。
> 里面使用了一个或者多个样本进行采样统计 `List<Sample> samples`;
> 当前使用的样本: `current`
> 样本初始化的值: `initialValue`

**`SampledStat :`** 实现了`MeasurableStat` 的抽象类,说明它又能采集记录数据,又能统计分析数据

当然它自身也定义了有两个抽象方法

```java

  /** 更新具体样本的数值 (单个样本)**/
  protected abstract void update(Sample sample, MetricConfig config, double value, long timeMs);

```

```java
  /**组合所有样本的数据 来统计出想要的数据 **/
  public abstract double combine(List<Sample> samples, MetricConfig config, long now);

```

![在这里插入图片描述](https://img-blog.csdnimg.cn/ba436f8d254f4db5abf35bf545e47ceb.png#pic_center)

如上图所示, 是一个`SampledStat` 的图形化展示, 其中定义了 若干个样本 Sample

**记录数据**

```java
    @Override
    public void record(MetricConfig config, double value, long timeMs) {
        Sample sample = current(timeMs);
        if (sample.isComplete(timeMs, config))
            sample = advance(config, timeMs);
        update(sample, config, value, timeMs);
        sample.eventCount += 1;
    }
```

1.  获取当前的**Sample**号,如果没有则创建一个新的**Sample**, 创建的时候设置 **初始化值** 和 **Sample 起始时间(当前时间)** ,并保存到样品列表里面
2.  判断这个**Sample**是否完成(超过窗口期),判断的逻辑是 `当前时间 - 当前Sample的开始时间 >= 配置的时间窗口值 或者 事件总数 >= 配置的事件窗口值`

```java
		/** 当前时间 - 当前Sample的开始时间 >= 配置的时间窗口值 或者  事件总数 >= 配置的事件窗口值 **/
		public boolean isComplete(long timeMs, MetricConfig config) {
            return timeMs - lastWindowMs >= config.timeWindowMs() || eventCount >= config.eventWindow();
        }

```

3.  如果这个**Sample**已经完成(超过窗口期), 则开始选择下一个窗口,如果下一个还没创建则创建新的,如果下一个已经存在,则重置这个**Sample**
4.  拿到最终要使用的**Sample**后, 将数据记录到这个**Sample**中。具体怎么记录是让具体的实现类来实现的,因为想要最终统计的数据可以不一样,比如你只想记录**Sample**中的最大值,那么更新的时候判断是不是比之前的值大则更新,如果你想统计平均值,那么这里就让单个**Sample**中所有的值累加（最终会 除以 **Sample**数量 求平均数的）
5.  记录事件次数+1。

![在这里插入图片描述](https://img-blog.csdnimg.cn/aa7935bf205f4e75a38099a0ef52076b.png#pic_center)

**统计数据**

```java
    /** 测量  统计 数据**/
    @Override
    public double measure(MetricConfig config, long now) {
        // 重置过期样本
        purgeObsoleteSamples(config, now);
        // 组合所有样本数据,并展示最终统计数据,具体实现类来实现该方法
        return combine(this.samples, config, now);
    }

```

1. 先重置 **过期样本** , 过期样本的意思是：当前时间 - 每个样本的起始事件 > 样本数量 \* 每个样本的窗口时间 ; 就是滑动窗口的概念,只统计这个滑动窗口的样本数据, 过期的样本数据会被重置(过期数据不采纳), 如下图所示

![在这里插入图片描述](https://img-blog.csdnimg.cn/d3200ee460c24ed6a11e610e3516e07d.png#pic_center)

2. 组合所有样本数据并进行不同维度的统计并返回数值, 因为不同场景想要得到的数据不同，所以这个只是一个抽象方法,需要实现类来实现这个计算逻辑,比如如果是计算平均值 `Avg`, 它的计算逻辑就是把所有的**样本数据值累加**并除以**累积的次数**

那我们再来看看不同的统计实现类

##### Avg 计算平均值

> 一个简单的`SampledStat`实现类 它统计所有样本最终的平均值
> 每个样本都会累加每一次的记录值， 最后把所有样本数据叠加 / 总共记录的次数

![在这里插入图片描述](https://img-blog.csdnimg.cn/c80e55acf4ce41069c1ee4abdb80b6b8.png)

##### Max 计算最大值

> 每个样本都保存这个样本的最大值, 然后最后再对比所有样本值的最大值
>
> ![在这里插入图片描述](https://img-blog.csdnimg.cn/0d804f43a50f4aacb45527b469da94d5.png)

##### WindowedSum 所有样本窗口总和值

> 每个样本累积每一次的记录值, 统计的时候 把所有样本的累计值 再累积返回
>
> ![在这里插入图片描述](https://img-blog.csdnimg.cn/eeecdb48d91040919d12c68362e5cf21.png)

#### Rate 样本记录统计求速率

> `Rate` 也是实现了 `MeasurableStat`接口的,说明 它也有 记录`record` 和 统计 `measure` 的方法,
> 实际上这个类 是一个组合类 ，里面组合了 ` SampledStat` 和`TimeUnit unit ` ,这不是很明显了么, SampledStat 负责记录和统计, 得到的数据 跟时间`TimeUnit`做一下处理就得出来速率了, 比如`SampledStat`的实现类`AVG`可以算出来 被统计的 评价值, 但是如果我们再除以 一个时间维度, 是不是就可以得出 **平均速率** 了

##### 如何计算统计的有效时间呢

这个**有效时间** 的计算会影响着最终**速率**的结果

```java
   public long windowSize(MetricConfig config, long now) {
        // 将过期的样本给重置掉
        stat.purgeObsoleteSamples(config, now);
        // 总共运行的时候 = 当前时间 - 最早的样本的开始时间
        long totalElapsedTimeMs = now - stat.oldest(now).lastWindowMs;
        // 总时间/单个创建时间 = 多少个完整的窗口时间
        int numFullWindows = (int) (totalElapsedTimeMs / config.timeWindowMs());
        int minFullWindows = config.samples() - 1;
        // If the available windows are less than the minimum required, add the difference to the totalElapsedTime
        if (numFullWindows < minFullWindows)
            totalElapsedTimeMs += (minFullWindows - numFullWindows) * config.timeWindowMs();

        return totalElapsedTimeMs;
    }
```

这是 Rate 的有效时间的计算逻辑,当然`Rate` 还有一个子类是 `SampleRate`

![SampleRate的窗口Size计算逻辑](https://img-blog.csdnimg.cn/b960ad94320247d49969403d0cedff8c.png)

这个子类,将 有效时间的计算逻辑改的更简单, 如果运行时间<一个样本窗口的时间 则他的运行时间就是单个样本的窗口时间, 否则就直接用这个运行的时间, 这个计算逻辑更简单
它跟`Rate`的区别就是, 不考虑采样的时间是否足够多,我们用图来简单描述一下

**SampleRate**

![SampleRate 速率逻辑](https://img-blog.csdnimg.cn/a60fde04b05146f792342b2518401b54.jpg#pic_center)

**Rate**

![Rate 速率逻辑](https://img-blog.csdnimg.cn/248ea5032aa2418789836f3258494df5.jpg#pic_center)

#### Meter 包含速率和累积总指标的复合统计数据

> 这是一个`CompoundStat`的实现类, 说明它是一个复合统计, 可以统计很多指标在这里面
> 它包含速率指标和累积总指标的复合统计数据

底层实现的逻辑还是上面讲解过的

#### 副本 Fetch 流量的速率统计 案例分析

> 我们知道 在分区副本重分配过程中,有一个限流机制,就是指定某个限流值,副本同步过程不能超过这个阈值。
> 做限流,那么肯定首先就需要统计 副本同步 的流速；那么上面我们将了这么多,你应该很容易能够想到如果统计了吧？
> 流速 bytes/s , 统计一秒钟同步了多少流量, 那么我们可以把样本窗口设置为 `1s`,然后多设置几个样本窗口求平均值。

接下来我们看看 Kafka 是怎么统计的, 首先找到记录 Follower Fetch 副本流量的地方如下

`ReplicaFetcherThread#processPartitionData`

```scala

if(quota.isThrottled(topicPartition))
  quota.record(records.sizeInBytes)

```

![设置时间窗口配置](https://img-blog.csdnimg.cn/f7b85b339eba4594bffa06a1337720f4.png)

这里设置的
`timeWindowMs` 单个样本窗口时间= 1 s
`numQuotaSamples` 样本数 = 11
当然这些都是可以配置的

![查看使用了哪个实现类](https://img-blog.csdnimg.cn/894a8202f68846498e072ed5a9b31f38.png)

我们可以看到最终是使用了 **`SampleRate`** 来统计流量 !

#### Gauge 瞬时读数的指标

> 上面我们起始是主要讲解了`Measurable`接口, 它的父类是`MetricValueProvider<Double> ` ,它没有方法,只是定义,当还有一个子接口是 `Gauge` ,它并不是上面那种采样的形式来统计数据, 它返回的是当前的值, <font color=red><b>瞬时值</b></font>
> 它提供的方法是 `value()` ， `Measurable`提供的是`measure()`

这个在 kafka 中使用场景很少,就不详细介绍了。

### 6.7.4 常见指标分析

如果你想看所有指标请看：[所有监控指标](https://kafka.apache.org/27/documentation.html#selector_monitoring)

#### 指标的属性

Kafka 中的指标有几百个,我们这边不可能把每一个指标都给分析一遍,这里我们从里面挑出来几个监控指标来分析分析

想要查看所有指标请跳转官网：[Kafka 监控](https://kafka.apache.org/27/documentation.html#monitoring)

我们用**jconsole**连接上 Broker 之后, 可以看到所有的指标,如下图

![在这里插入图片描述](https://img-blog.csdnimg.cn/5839736738584c00a42fdafe88dc7042.png)

如图所示有很多的指标,并且每个指标有很多的属性值
比如指标 `kafka.server:type=BrokerTopicMetrics,name=MessagesInPerSec` 表示的是这台 Broker 每秒写入的消息条数。

但是这个数据是如何统计的呢, 可以看看 [图解 Kafka 中的数据采集和统计机制]()

一般情况下我们获取这个数据的话 是拿的 **OneMinuteRate** 一分钟内流入的平均速度。
`kafka.server:type=BrokerTopicMetrics,name=MessagesInPerSec.OneMinuteRate`  
当然还有**FiveMinuteRate** 、**FifteenMinuteRate**

每个指标下面都会有很多属性，一般可能有以下几个

| 属性名            | 描述                                                                                                      |
| ----------------- | --------------------------------------------------------------------------------------------------------- |
| RateUnit          | 时间单位, 值固定为 SECONDS 秒，它和 EventType 组成这个指标的单位,即 messages/s                            |
| EventType         | 事件类型,对于 MessagesIn 来说，它的值是 messages, 表示消息的个数,对于其他一些类型的指标来说可能会有所不同 |
| Count             | 消息流入的总数                                                                                            |
| MeanRate          | 平均速率，自统计开始时候的平均                                                                            |
| OneMinuteRate     | 一分钟内流入的平均速率                                                                                    |
| FiveMinuteRate    | 五分钟内流入的平均速率                                                                                    |
| FifteenMinuteRate | 十五分钟内流入的平均速率                                                                                  |

那如果我还想知道在这台 Broker 上某个 Topic 的指标呢？

![在这里插入图片描述](https://img-blog.csdnimg.cn/d1fb531c43e34576b805997088d9a115.png)

刚刚上面说的指标是流入这台 Broker 的消息数速率, 但是它的子目录下还有各个 Topic 的统计数据
指标名：`kafka.server:type=BrokerTopicMetrics,name=BytesInPerSec,topic=P1_R1`

#### 常见重要指标

##### 1. UnderReplicatedPartitions 失效副本分区数

**指标:** `kafka.server:type=ReplicaManager,name=UnderReplicatedPartitions`

**含义:** 失效副本的分区数量, 代码逻辑为 统计**该 Broker 上的 Leader 分区，并且该分区的副本数 - isr 数量 > 0** 的数量

**异常值:** 非 0 值

---

这个 UnderReplicatedPartitions 表示的是什么意思呢？我们直接看代码它是怎么统计的

**ReplicaManager**

![在这里插入图片描述](https://img-blog.csdnimg.cn/a49f61009c764ab5800fbb30a0bdfadd.png)

通过代码所示, **UnderReplicatedPartitions**是**Gauge**类型,也就是说表示 瞬时数据, 会不停的把这个数据上报给 JmxReport
那么主要的数值计算逻辑是：

```scala

leaderPartitionsIterator.count(_.isUnderReplicated)

def isUnderReplicated: Boolean = isLeader && (assignmentState.replicationFactor - isrState.isr.size) > 0

```

代码逻辑：统计**该 Broker 上的 Leader 分区，并且该分区的副本数 - isr 数量 > 0** 的数量

简单来说： UnderReplicatedPartitions 值表示该 Broker 上的 Leader 分区存在有没有完全同步并跟上 ISR 的副本的 分区数量

**问题分析**

如果你这个指标出现了 , **说明该 Broker 上的 Leader 分区存在 Follower 副本跟不上 ISR 的情况。**

这么个情况就是 **副本为何掉出 ISR** 的问题了

<font color=red><b>PS：当我们在进行分区副本重分配的时候可能会出现这种情况,因为有可能新增了副本并且还没有跟上 ISR.</b></font>

**1. 可能存在某个 Broker 宕机**

![在这里插入图片描述](https://img-blog.csdnimg.cn/591071ae25e241d580f5f291f5bfb33c.png)

看 [KnowStreaming](https://github.com/didi/KnowStreaming) 的展示, Broker2 存在 5 个**UnderReplicatedPartitions**, 通过左边可以看到刚好是 Broker-0 宕机了。

这种很容易就找到问题所在, 然后启动 Broker 恢复副本同步。

**2. 可能副本所在磁盘故障/写满,导致副本离线**

当磁盘出现故障时，会导致磁盘 IO 能力下降、集群吞吐下降、消息读写延时或日志目录 offline 等问题。

当磁盘写满时，相应磁盘上的 Kafka 日志目录会出现 offline 问题，此时，该磁盘上的分区副本不可读写，降低了分区的可用性与容错能力，同时由于 leader 迁移到其他 Broker，增加了其他 Broker 的负载

我们可以通过指标 **OfflineLogDirectoryCount**来及时发现日志 Offline 的情况。

**指标:** `kafka.log:type=LogManager,name=OfflineLogDirectoryCount`

**含义:** 离线日志目录数量

**异常值:** 非 0 值

![在这里插入图片描述](https://img-blog.csdnimg.cn/9f75265ec638408f84b4998c3a1638ab.png)

如果我们这个值是>0 的话，表示已经有目录处于离线中了, 具体是哪个处于离线中我们也可以通过指标来确定

**指标:** `kafka.log:type=LogManager,name=LogDirectoryOffline,logDirectory="绝对路径地址"`

**含义:** 该目录是否离线

**异常值:** 非 0 值, 0 表示正常, 1 表示离线

当然如果你想监控到具体的离线目录的话，你可以先把 Broker 上的所有目录绝对路径查询出来,然后再遍历一下这个指标就行了。

![在这里插入图片描述](https://img-blog.csdnimg.cn/ff73b31b3de14c8abdae18e78e677b57.png)

如果确定是目录离线了, 那么接下来就是让副本上线就行了, 如果磁盘满了可以考虑删除旧数据或更换磁盘，如果磁盘坏了那就换磁盘吧。

**3. 性能问题,导致副本来不及同步数据**

首先我们先了解一下 Kafka 的 [ISR 的伸缩机制]()

一般会有两种情况导致副本失效

1. Follower 副本进程卡住,在一段时间内根本没有向 Leader 发起同步请求，比如频繁的 Full GC.
2. Follower 副本进程同步过慢, 在一段时间内都无法追赶上 Leader 副本,比如 I/O 开销过大。

出现 1 的情况可能性不是那么大,你可以通过查看 kafka 的 gc 日志`kafkaServer-gc.log` 来确定是否存在频繁的 Full GC

其他情况呢, 我们可以先检查一下是否有一些异常日志出现， 看看具体的异常是什么

```

Error sending fetch request {} to node {}

Failed to connect within $socketTimeout ms"

```

因为 ISR 伸缩的时候,在更新 HW 的时候需要加一个**leaderIsrUpdateLock**写锁, 这个时候消息的发送、客户端的读取等等都会发生锁竞争，并发度会下降。

解决问题的方案

我们可以尝试的调大`replica.lag.time.max.ms` ，2.5 之前默认值是 10s, 后面是 30s.  
也可以调大`num.replica.fetchers`的值,这个值表示的是：Broker 去读取消息的 Fetcher 线程数,增加这个值可以增加 follow broker 中的 I/O 并行度。默认是 1

##### 2. 更多指标

敬请期待
