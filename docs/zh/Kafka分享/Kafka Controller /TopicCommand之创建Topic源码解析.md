
## 脚本参数

`sh bin/kafka-topic -help` 查看更具体参数

下面只是列出了跟` --create` 相关的参数

| 参数 |描述  |例子|
|--|--|--|
|`--bootstrap-server ` 指定kafka服务|指定连接到的kafka服务; 如果有这个参数,则 `--zookeeper`可以不需要|--bootstrap-server localhost:9092 |
|`--zookeeper`|弃用, 通过zk的连接方式连接到kafka集群;|--zookeeper localhost:2181 或者localhost:2181/kafka|
|`--replication-factor `|副本数量,注意不能大于broker数量;如果不提供,则会用集群中默认配置|--replication-factor 3 |
|`--partitions`|分区数量|当创建或者修改topic的时候,用这个来指定分区数;如果创建的时候没有提供参数,则用集群中默认值; 注意如果是修改的时候,分区比之前小会有问题|--partitions 3 |
|`--replica-assignment `|副本分区分配方式;创建topic的时候可以自己指定副本分配情况; |`--replica-assignment` BrokerId-0:BrokerId-1:BrokerId-2,BrokerId-1:BrokerId-2:BrokerId-0,BrokerId-2:BrokerId-1:BrokerId-0  ; 这个意思是有三个分区和三个副本,对应分配的Broker; 逗号隔开标识分区;冒号隔开表示副本|
|  `--config `<String: name=value> |用来设置topic级别的配置以覆盖默认配置;**只在--create 和--bootstrap-server 同时使用时候生效**; 可以配置的参数列表请看文末附件   |例如覆盖两个配置 `--config retention.bytes=123455 --config retention.ms=600001`|
|`--command-config` <String: command    文件路径>   |用来配置客户端Admin Client启动配置,**只在--bootstrap-server 同时使用时候生效**;|例如:设置请求的超时时间 `--command-config config/producer.proterties `; 然后在文件中配置 request.timeout.ms=300000|
|`--create`|命令方式; 表示当前请求是创建Topic|`--create`|




## 创建Topic脚本
**zk方式(不推荐)**
```shell
bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 3 --partitions 3 --topic test
```
<font color="red">需要注意的是--zookeeper后面接的是kafka的zk配置, 假如你配置的是localhost:2181/kafka  带命名空间的这种,不要漏掉了 </font>

**kafka版本 >= 2.2 支持下面方式（推荐）** 
```shell
bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 3 --partitions 3 --topic test
```







当前分析的kafka源码版本为 `kafka-2.5`

## 创建Topic 源码分析
<font color="red">温馨提示: 如果阅读源码略显枯燥,你可以直接看源码总结以及后面部分</font>

首先我们找到源码入口处, 查看一下 `kafka-topic.sh`脚本的内容
`exec $(dirname $0)/kafka-run-class.sh kafka.admin.TopicCommand "$@"`
最终是执行了`kafka.admin.TopicCommand`这个类，找到这个地方之后就可以断点调试源码了,用IDEA启动
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210608151956926.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTA2MzQwNjY=,size_16,color_FFFFFF,t_70)
记得配置一下入参
比如: `--create --bootstrap-server 127.0.0.1:9092 --partitions 3 --topic test_create_topic3`
 ![在这里插入图片描述](https://img-blog.csdnimg.cn/20210608152149713.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTA2MzQwNjY=,size_16,color_FFFFFF,t_70)
                           

### 1. 源码入口
![在这里插入图片描述](https://img-blog.csdnimg.cn/2021060815275820.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTA2MzQwNjY=,size_16,color_FFFFFF,t_70)
上面的源码主要作用是
1. 根据是否有传入参数`--zookeeper` 来判断创建哪一种 对象`topicService`
	如果传入了`--zookeeper` 则创建 类 `ZookeeperTopicService`的对象
	否则创建类`AdminClientTopicService`的对象(我们主要分析这个对象)
2. 根据传入的参数类型判断是创建topic还是删除等等其他 判断依据是 是否在参数里传入了`--create`


### 2. 创建AdminClientTopicService 对象
 >  `val topicService =  new AdminClientTopicService(createAdminClient(commandConfig, bootstrapServer))`

#### 2.1 先创建 Admin
```scala
object AdminClientTopicService {
    def createAdminClient(commandConfig: Properties, bootstrapServer: Option[String]): Admin = {
      bootstrapServer match {
        case Some(serverList) => commandConfig.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, serverList)
        case None =>
      }
      Admin.create(commandConfig)
    }

    def apply(commandConfig: Properties, bootstrapServer: Option[String]): AdminClientTopicService =
      new AdminClientTopicService(createAdminClient(commandConfig, bootstrapServer))
  }
```

1. 如果有入参`--command-config` ,则将这个文件里面的参数都放到map `commandConfig`里面, 并且也加入`bootstrap.servers`的参数;假如配置文件里面已经有了`bootstrap.servers`配置，那么会将其覆盖
2. 将上面的`commandConfig` 作为入参调用`Admin.create(commandConfig)`创建 Admin; 这个时候调用的Client模块的代码了, 从这里我们就可以看出,我们调用`kafka-topic.sh`脚本实际上是kafka模拟了一个客户端`Client`来创建Topic的过程;
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210608160130820.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTA2MzQwNjY=,size_16,color_FFFFFF,t_70)



### 3. AdminClientTopicService.createTopic 创建Topic
`        topicService.createTopic(opts)`

```scala
  case class AdminClientTopicService private (adminClient: Admin) extends TopicService {

    override def createTopic(topic: CommandTopicPartition): Unit = {
      //如果配置了副本副本数--replication-factor 一定要大于0
      if (topic.replicationFactor.exists(rf => rf > Short.MaxValue || rf < 1))
        throw new IllegalArgumentException(s"The replication factor must be between 1 and ${Short.MaxValue} inclusive")
       //如果配置了--partitions 分区数 必须大于0
      if (topic.partitions.exists(partitions => partitions < 1))
        throw new IllegalArgumentException(s"The partitions must be greater than 0")

	  //查询是否已经存在该Topic
      if (!adminClient.listTopics().names().get().contains(topic.name)) {
        val newTopic = if (topic.hasReplicaAssignment)
          //如果指定了--replica-assignment参数；则按照指定的来分配副本
          new NewTopic(topic.name, asJavaReplicaReassignment(topic.replicaAssignment.get))
        else {
          new NewTopic(
            topic.name,
            topic.partitions.asJava,
            topic.replicationFactor.map(_.toShort).map(Short.box).asJava)
        }

        // 将配置--config 解析成一个配置map
        val configsMap = topic.configsToAdd.stringPropertyNames()
          .asScala
          .map(name => name -> topic.configsToAdd.getProperty(name))
          .toMap.asJava

        newTopic.configs(configsMap)
        //调用adminClient创建Topic
        val createResult = adminClient.createTopics(Collections.singleton(newTopic))
        createResult.all().get()
        println(s"Created topic ${topic.name}.")
      } else {
        throw new IllegalArgumentException(s"Topic ${topic.name} already exists")
      }
    }
```
1. 检查各项入参是否有问题
2. `adminClient.listTopics()`,然后比较是否已经存在待创建的Topic;如果存在抛出异常; 
3. 判断是否配置了参数`--replica-assignment` ; 如果配置了,那么Topic就会按照指定的方式来配置副本情况
4. 解析配置`--config ` 配置放到` configsMap`中; `configsMap`给到`NewTopic`对象
5. 调用`adminClient.createTopics`创建Topic; 它是如何创建Topic的呢？往下分析源码

#### 3.1 KafkaAdminClient.createTopics(NewTopic) 创建Topic

```java
    @Override
    public CreateTopicsResult createTopics(final Collection<NewTopic> newTopics,
                                           final CreateTopicsOptions options) {
       
       //省略部分源码...
        Call call = new Call("createTopics", calcDeadlineMs(now, options.timeoutMs()),
            new ControllerNodeProvider()) {

            @Override
            public CreateTopicsRequest.Builder createRequest(int timeoutMs) {
                return new CreateTopicsRequest.Builder(
                    new CreateTopicsRequestData().
                        setTopics(topics).
                        setTimeoutMs(timeoutMs).
                        setValidateOnly(options.shouldValidateOnly()));
            }

            @Override
            public void handleResponse(AbstractResponse abstractResponse) {
                //省略
            }

            @Override
            void handleFailure(Throwable throwable) {
                completeAllExceptionally(topicFutures.values(), throwable);
            }
        };
        
    }
```
这个代码里面主要看下Call里面的接口; 先不管Kafka如何跟服务端进行通信的细节; 我们主要关注创建Topic的逻辑;
1. `createRequest`会构造一个请求参数`CreateTopicsRequest` 例如下图
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210609174617186.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTA2MzQwNjY=,size_16,color_FFFFFF,t_70)
2. 选择ControllerNodeProvider这个节点发起网络请求
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210609200925505.png)
可以清楚的看到, 创建Topic这个操作是需要Controller来执行的;
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210609200938586.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTA2MzQwNjY=,size_16,color_FFFFFF,t_70)




### 4. 发起网络请求
 [==>服务端客户端网络模型 ](TODO)

### 5. Controller角色的服务端接受请求处理逻辑
首先找到服务端处理客户端请求的 **源码入口** ⇒ `KafkaRequestHandler.run()`


主要看里面的 `apis.handle(request)` 方法; 可以看到客户端的请求都在`request.bodyAndSize()`里面
![在这里插入图片描述](https://img-blog.csdnimg.cn/2021060917574268.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTA2MzQwNjY=,size_16,color_FFFFFF,t_70)
#### 5.1 KafkaApis.handle(request) 根据请求传递Api调用不同接口
进入方法可以看到根据`request.header.apiKey`  调用对应的方法,客户端传过来的是`CreateTopics`
![在这里插入图片描述](https://img-blog.csdnimg.cn/2021060918000338.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTA2MzQwNjY=,size_16,color_FFFFFF,t_70)

#### 5.2 KafkaApis.handleCreateTopicsRequest 处理创建Topic的请求

```java

def handleCreateTopicsRequest(request: RequestChannel.Request): Unit = {
    // 部分代码省略
	//如果当前Broker不是属于Controller的话,就抛出异常
    if (!controller.isActive) {
      createTopicsRequest.data.topics.asScala.foreach { topic =>
        results.add(new CreatableTopicResult().setName(topic.name).
          setErrorCode(Errors.NOT_CONTROLLER.code))
      }
      sendResponseCallback(results)
    } else {
     // 部分代码省略
    }
      adminManager.createTopics(createTopicsRequest.data.timeoutMs,
          createTopicsRequest.data.validateOnly,
          toCreate,
          authorizedForDescribeConfigs,
          handleCreateTopicsResults)
    }
  }

```
1. 判断当前处理的broker是不是Controller,如果不是Controller的话直接抛出异常,从这里可以看出,CreateTopic这个操作必须是Controller来进行, 出现这种情况有可能是客户端发起请求的时候Controller已经变更; 
2. 鉴权 [【Kafka源码】kafka鉴权机制]()
3. 调用`adminManager.createTopics()` 

#### 5.3 adminManager.createTopics()
> 创建主题并等等主题完全创建,回调函数将会在超时、错误、或者主题创建完成时触发

该方法过长,省略部分代码
```scala
def createTopics(timeout: Int,
                   validateOnly: Boolean,
                   toCreate: Map[String, CreatableTopic],
                   includeConfigsAndMetatadata: Map[String, CreatableTopicResult],
                   responseCallback: Map[String, ApiError] => Unit): Unit = {

    // 1. map over topics creating assignment and calling zookeeper
    val brokers = metadataCache.getAliveBrokers.map { b => kafka.admin.BrokerMetadata(b.id, b.rack) }
    val metadata = toCreate.values.map(topic =>
      try {
          //省略部分代码
         //检查Topic是否存在
         //检查 --replica-assignment参数和 (--partitions	|| --replication-factor ) 不能同时使用
         // 如果(--partitions	|| --replication-factor ) 没有设置,则使用 Broker的配置(这个Broker肯定是Controller)
		// 计算分区副本分配方式

        createTopicPolicy match {
          case Some(policy) =>
          //省略部分代码
            adminZkClient.validateTopicCreate(topic.name(), assignments, configs)
            if (!validateOnly)
              adminZkClient.createTopicWithAssignment(topic.name, configs, assignments)

          case None =>
            if (validateOnly)
             //校验创建topic的参数准确性
              adminZkClient.validateTopicCreate(topic.name, assignments, configs)
            else
              //把topic相关数据写入到zk中
              adminZkClient.createTopicWithAssignment(topic.name, configs, assignments)
        }

       
  }
```
1. 做一些校验检查 
    ①.检查Topic是否存在
    ②. 检查` --replica-assignment`参数和 (`--partitions	|| --replication-factor` ) 不能同时使用
    ③.如果(`--partitions	|| --replication-factor` ) 没有设置,则使用 Broker的配置(这个Broker肯定是Controller)
    ④.计算分区副本分配方式

2. `createTopicPolicy` 根据Broker是否配置了创建Topic的自定义校验策略; 使用方式是自定义实现`org.apache.kafka.server.policy.CreateTopicPolicy`接口;并 在服务器配置 `create.topic.policy.class.name=自定义类`; 比如我就想所有创建Topic的请求分区数都要大于10; 那么这里就可以实现你的需求了
3. `createTopicWithAssignment`把topic相关数据写入到zk中; 进去分析一下



#### 5.4 写入zookeeper数据
我们进入到`              adminZkClient.createTopicWithAssignment(topic.name, configs, assignments)
`看看有哪些数据写入到了zk中;
```scala
  def createTopicWithAssignment(topic: String,
                                config: Properties,
                                partitionReplicaAssignment: Map[Int, Seq[Int]]): Unit = {
    validateTopicCreate(topic, partitionReplicaAssignment, config)

    // 将topic单独的配置写入到zk中
    zkClient.setOrCreateEntityConfigs(ConfigType.Topic, topic, config)

    // 将topic分区相关信息写入zk中
    writeTopicPartitionAssignment(topic, partitionReplicaAssignment.mapValues(ReplicaAssignment(_)).toMap, isUpdate = false)
  }

```
源码就不再深入了,这里直接详细说明一下

**写入Topic配置信息**
1. 先调用`SetDataRequest`请求往节点` /config/topics/Topic名称` 写入数据; 这里
一般这个时候都会返回 `NONODE (NoNode)`;节点不存在;  假如zk已经存在节点就直接覆盖掉
2. 节点不存在的话,就发起`CreateRequest`请求,写入数据; 并且节点类型是**持久节点**

这里写入的数据,是我们入参时候传的topic配置`--config`; 这里的配置会覆盖默认配置

**写入Topic分区副本信息**
1. 将已经分配好的副本分配策略写入到 `/brokers/topics/Topic名称` 中; 节点类型 **持久节点**
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210610152129161.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTA2MzQwNjY=,size_16,color_FFFFFF,t_70)

**具体跟zk交互的地方在**
`ZookeeperClient.send()` 这里包装了很多跟zk的交互;
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210610151032490.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTA2MzQwNjY=,size_16,color_FFFFFF,t_70)
### 6. Controller监听 `/brokers/topics/Topic名称`, 通知Broker将分区写入磁盘
> Controller 有监听zk上的一些节点; 在上面的流程中已经在zk中写入了 `/brokers/topics/Topic名称` ; 这个时候Controller就监听到了这个变化并相应;

`KafkaController.processTopicChange`
```scala

  private def processTopicChange(): Unit = {
    //如果处理的不是Controller角色就返回
    if (!isActive) return
    //从zk中获取 `/brokers/topics 所有Topic
    val topics = zkClient.getAllTopicsInCluster
    //找出哪些是新增的
    val newTopics = topics -- controllerContext.allTopics
    //找出哪些Topic在zk上被删除了
    val deletedTopics = controllerContext.allTopics -- topics
    controllerContext.allTopics = topics

    
    registerPartitionModificationsHandlers(newTopics.toSeq)
    val addedPartitionReplicaAssignment = zkClient.getFullReplicaAssignmentForTopics(newTopics)
    deletedTopics.foreach(controllerContext.removeTopic)
    addedPartitionReplicaAssignment.foreach {
      case (topicAndPartition, newReplicaAssignment) => controllerContext.updatePartitionFullReplicaAssignment(topicAndPartition, newReplicaAssignment)
    }
    info(s"New topics: [$newTopics], deleted topics: [$deletedTopics], new partition replica assignment " +
      s"[$addedPartitionReplicaAssignment]")
    if (addedPartitionReplicaAssignment.nonEmpty)
      onNewPartitionCreation(addedPartitionReplicaAssignment.keySet)
  }
```
1. 从zk中获取 `/brokers/topics` 所有Topic跟当前Broker内存中所有Broker`controllerContext.allTopics`的差异; 就可以找到我们新增的Topic; 还有在zk中被删除了的Broker(该Topic会在当前内存中remove掉)
2. 从zk中获取`/brokers/topics/{TopicName}` 给定主题的副本分配。并保存在内存中![在这里插入图片描述](https://img-blog.csdnimg.cn/20210616175718504.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTA2MzQwNjY=,size_16,color_FFFFFF,t_70)

4. 执行`onNewPartitionCreation`;分区状态开始流转

#### 6.1 onNewPartitionCreation 状态流转
> 关于Controller的状态机 详情请看: [【kafka源码】Controller中的状态机](TODO)

```scala
  /**
   * This callback is invoked by the topic change callback with the list of failed brokers as input.
   * It does the following -
   * 1. Move the newly created partitions to the NewPartition state
   * 2. Move the newly created partitions from NewPartition->OnlinePartition state
   */
  private def onNewPartitionCreation(newPartitions: Set[TopicPartition]): Unit = {
    info(s"New partition creation callback for ${newPartitions.mkString(",")}")
    partitionStateMachine.handleStateChanges(newPartitions.toSeq, NewPartition)
    replicaStateMachine.handleStateChanges(controllerContext.replicasForPartition(newPartitions).toSeq, NewReplica)
    partitionStateMachine.handleStateChanges(
      newPartitions.toSeq,
      OnlinePartition,
      Some(OfflinePartitionLeaderElectionStrategy(false))
    )
    replicaStateMachine.handleStateChanges(controllerContext.replicasForPartition(newPartitions).toSeq, OnlineReplica)
  }
```
1. 将待创建的分区状态流转为`NewPartition`; 
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210616180239988.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTA2MzQwNjY=,size_16,color_FFFFFF,t_70)
2. 将待创建的副本 状态流转为`NewReplica`;
 ![在这里插入图片描述](https://img-blog.csdnimg.cn/20210616180940961.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTA2MzQwNjY=,size_16,color_FFFFFF,t_70)
 3. 将分区状态从刚刚的`NewPartition`流转为`OnlinePartition`
 		0. 获取`leaderIsrAndControllerEpochs`; Leader为副本的第一个;
		1. 向zk中写入`/brokers/topics/{topicName}/partitions/` 持久节点; 无数据
		2. 向zk中写入`/brokers/topics/{topicName}/partitions/{分区号}` 持久节点; 无数据
		3. 向zk中写入`/brokers/topics/{topicName}/partitions/{分区号}/state` 持久节点; 数据为`leaderIsrAndControllerEpoch`![在这里插入图片描述](https://img-blog.csdnimg.cn/20210616183747171.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTA2MzQwNjY=,size_16,color_FFFFFF,t_70)
	4. 向副本所属Broker发送[`leaderAndIsrRequest`]()请求
	5. 向所有Broker发送[`UPDATE_METADATA` ]()请求
4. 将副本状态从刚刚的`NewReplica`流转为`OnlineReplica` ,更新下内存
 	
关于分区状态机和副本状态机详情请看[【kafka源码】Controller中的状态机](TODO)

### 7. Broker收到LeaderAndIsrRequest 创建本地Log
>上面步骤中有说到向副本所属Broker发送[`leaderAndIsrRequest`]()请求,那么这里做了什么呢
>其实主要做的是 创建本地Log
>
代码太多,这里我们直接定位到只跟创建Topic相关的关键代码来分析
`KafkaApis.handleLeaderAndIsrRequest->replicaManager.becomeLeaderOrFollower->ReplicaManager.makeLeaders...LogManager.getOrCreateLog`

```scala
  /**
   * 如果日志已经存在，只返回现有日志的副本否则如果 isNew=true 或者如果没有离线日志目录，则为给定的主题和给定的分区创建日志 否则抛出 KafkaStorageException
   */
  def getOrCreateLog(topicPartition: TopicPartition, config: LogConfig, isNew: Boolean = false, isFuture: Boolean = false): Log = {
    logCreationOrDeletionLock synchronized {
      getLog(topicPartition, isFuture).getOrElse {
        // create the log if it has not already been created in another thread
        if (!isNew && offlineLogDirs.nonEmpty)
          throw new KafkaStorageException(s"Can not create log for $topicPartition because log directories ${offlineLogDirs.mkString(",")} are offline")

        val logDirs: List[File] = {
          val preferredLogDir = preferredLogDirs.get(topicPartition)

          if (isFuture) {
            if (preferredLogDir == null)
              throw new IllegalStateException(s"Can not create the future log for $topicPartition without having a preferred log directory")
            else if (getLog(topicPartition).get.dir.getParent == preferredLogDir)
              throw new IllegalStateException(s"Can not create the future log for $topicPartition in the current log directory of this partition")
          }

          if (preferredLogDir != null)
            List(new File(preferredLogDir))
          else
            nextLogDirs()
        }

        val logDirName = {
          if (isFuture)
            Log.logFutureDirName(topicPartition)
          else
            Log.logDirName(topicPartition)
        }

        val logDir = logDirs
          .toStream // to prevent actually mapping the whole list, lazy map
          .map(createLogDirectory(_, logDirName))
          .find(_.isSuccess)
          .getOrElse(Failure(new KafkaStorageException("No log directories available. Tried " + logDirs.map(_.getAbsolutePath).mkString(", "))))
          .get // If Failure, will throw

        val log = Log(
          dir = logDir,
          config = config,
          logStartOffset = 0L,
          recoveryPoint = 0L,
          maxProducerIdExpirationMs = maxPidExpirationMs,
          producerIdExpirationCheckIntervalMs = LogManager.ProducerIdExpirationCheckIntervalMs,
          scheduler = scheduler,
          time = time,
          brokerTopicStats = brokerTopicStats,
          logDirFailureChannel = logDirFailureChannel)

        if (isFuture)
          futureLogs.put(topicPartition, log)
        else
          currentLogs.put(topicPartition, log)

        info(s"Created log for partition $topicPartition in $logDir with properties " + s"{${config.originals.asScala.mkString(", ")}}.")
        // Remove the preferred log dir since it has already been satisfied
        preferredLogDirs.remove(topicPartition)

        log
      }
    }
  }
```
1. 如果日志已经存在，只返回现有日志的副本否则如果 isNew=true 或者如果没有离线日志目录，则为给定的主题和给定的分区创建日志 否则抛出` KafkaStorageException`

详细请看 [【kafka源码】LeaderAndIsrRequest请求]()


## 源码总结
> 如果上面的源码分析,你不想看,那么你可以直接看这里的简洁叙述

1. 根据是否有传入参数`--zookeeper` 来判断创建哪一种 对象`topicService`
如果传入了`--zookeeper` 则创建 类 `ZookeeperTopicService`的对象
否则创建类`AdminClientTopicService`的对象(我们主要分析这个对象)
2. 如果有入参`--command-config` ,则将这个文件里面的参数都放到mapl类型 `commandConfig`里面, 并且也加入`bootstrap.servers`的参数;假如配置文件里面已经有了`bootstrap.servers`配置，那么会将其覆盖
3. 将上面的`commandConfig `作为入参调用`Admin.create(commandConfig)`创建 Admin; 这个时候调用的Client模块的代码了, 从这里我们就可以猜测,我们调用`kafka-topic.sh`脚本实际上是kafka模拟了一个客户端Client来创建Topic的过程;
4. 一些异常检查
    ①.如果配置了副本副本数--replication-factor 一定要大于0
    ②.如果配置了--partitions 分区数 必须大于0
    ③.去zk查询是否已经存在该Topic
5. 判断是否配置了参数`--replica-assignment` ; 如果配置了,那么Topic就会按照指定的方式来配置副本情况
6. 解析配置`--config ` 配置放到`configsMap`中; configsMap给到NewTopic对象
7. **将上面所有的参数包装成一个请求参数`CreateTopicsRequest` ；然后找到是`Controller`的节点发起请求(`ControllerNodeProvider`)**
8. 服务端收到请求之后,开始根据`CreateTopicsRequest`来调用创建Topic的方法; 不过首先要判断一下自己这个时候是不是`Controller`; 有可能这个时候Controller重新选举了; 这个时候要抛出异常
9. 服务端进行一下请求参数检查
    ①.检查Topic是否存在
    ②.检查 `--replica-assignment`参数和 (`--partitions`	|| `--replication-factor` ) 不能同时使用
10. 如果(`--partitions`	|| `--replication-factor` ) 没有设置,则使用 Broker的默认配置(这个Broker肯定是Controller) 
 11. 计算分区副本分配方式;如果是传入了 `--replica-assignment`;则会安装自定义参数进行组装;否则的话系统会自动计算分配方式; 具体详情请看 [【kafka源码】创建Topic的时候是如何分区和副本的分配规则 ]()
 12. `createTopicPolicy `根据Broker是否配置了创建Topic的自定义校验策略; 使用方式是自定义实现`org.apache.kafka.server.policy.CreateTopicPolicy`接口;并 在服务器配置 `create.topic.policy.class.name`=自定义类; 比如我就想所有创建Topic的请求分区数都要大于10; 那么这里就可以实现你的需求了
 13. **zk中写入Topic配置信息** 发起`CreateRequest`请求,这里写入的数据,是我们入参时候传的topic配置`--config`; 这里的配置会覆盖默认配置；并且节点类型是持久节点;**path** = `/config/topics/Topic名称`
 14. **zk中写入Topic分区副本信息** 发起`CreateRequest`请求 ,将已经分配好的副本分配策略 写入到 `/brokers/topics/Topic名称 `中; 节点类型 持久节点
 15. `Controller`监听zk上面的topic信息; 根据zk上变更的topic信息;计算出新增/删除了哪些Topic; 然后拿到新增Topic的 副本分配信息; 并做一些状态流转
 16. 向新增Topic所在Broker发送`leaderAndIsrRequest`请求, 
 17. Broker收到`发送leaderAndIsrRequest请求`; 创建副本Log文件;

![在这里插入图片描述](https://img-blog.csdnimg.cn/20210616220350958.jpg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTA2MzQwNjY=,size_16,color_FFFFFF,t_70#pic_center)


## Q&A


### 创建Topic的时候 在Zk上创建了哪些节点
>接受客户端请求阶段：
>1. topic的配置信息 ` /config/topics/Topic名称`  持久节点
>2.  topic的分区信息`/brokers/topics/Topic名称`  持久节点
>
>Controller监听zk节点`/brokers/topics`变更阶段
>1. `/brokers/topics/{topicName}/partitions/ `持久节点; 无数据
>2. 向zk中写入`/brokers/topics/{topicName}/partitions/{分区号}`  持久节点; 无数据
>3. 向zk中写入`/brokers/topics/{topicName}/partitions/{分区号}/state` 持久节点;

### 创建Topic的时候 什么时候在Broker磁盘上创建的日志文件
>当Controller监听zk节点`/brokers/topics`变更之后,将新增的Topic 解析好的分区状态流转
>`NonExistentPartition`->`NewPartition`->`OnlinePartition` 当流转到`OnlinePartition`的时候会像分区分配到的Broker发送一个`leaderAndIsrRequest`请求,当Broker们收到这个请求之后,根据请求参数做一些处理,其中就包括检查自身有没有这个分区副本的本地Log;如果没有的话就重新创建;
### 如果我没有指定分区数或者副本数,那么会如何创建
>我们都知道,如果我们没有指定分区数或者副本数, 则默认使用Broker的配置, 那么这么多Broker,假如不小心默认值配置不一样,那究竟使用哪一个呢？ 那肯定是哪台机器执行创建topic的过程,就是使用谁的配置; 
**所以是谁执行的？**  那肯定是Controller啊! 上面的源码我们分析到了,创建的过程,会指定Controller这台机器去进行;


### 如果我手动删除了`/brokers/topics/`下的某个节点会怎么样？
>在Controller中的内存中更新一下相关信息
>其他Broker呢？TODO.

### 如果我手动在zk中添加`/brokers/topics/{TopicName}`节点会怎么样
>**先说结论:** 根据上面分析过的源码画出的时序图可以指定; 客户端发起创建Topic的请求,本质上是去zk里面写两个数据
>1. topic的配置信息 ` /config/topics/Topic名称`  持久节点
>2.  topic的分区信息`/brokers/topics/Topic名称`  持久节点
>所以我们绕过这一步骤直接去写入数据,可以达到一样的效果;不过我们的数据需要保证准确
>因为在这一步已经没有了一些基本的校验了; 假如这一步我们写入的副本Brokerid不存在会怎样,从时序图中可以看到，`leaderAndIsrRequest请求`; 就不会正确的发送的不存在的BrokerId上,那么那台机器就不会创建Log文件;
>
>
>**下面不妨让我们来验证一下;**
>创建一个节点`/brokers/topics/create_topic_byhand_zk` 节点数据为下面数据; 
>```
>{"version":2,"partitions":{"2":[3],"1":[3],"0":[3]},"adding_replicas":{},"removing_replicas":{}}
>```
>![在这里插入图片描述](https://img-blog.csdnimg.cn/20210617112646965.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTA2MzQwNjY=,size_16,color_FFFFFF,t_70)
>这里我用的工具`PRETTYZOO`手动创建的,你也可以用命令行创建;
>创建完成之后我们再看看本地有没有生成一个Log文件
>![在这里插入图片描述](https://img-blog.csdnimg.cn/20210617112806599.png)
>可以看到我们指定的Broker,已经生成了对应的分区副本Log文件;
>而且zk中也写入了其他的数据![在这里插入图片描述](https://img-blog.csdnimg.cn/20210617113415168.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTA2MzQwNjY=,size_16,color_FFFFFF,t_70)
>`在我们写入zk数据的时候，就已经确定好了哪个每个分区的Leader是谁了,那就是第一个副本默认为Leader`
>





### 如果写入`/brokers/topics/{TopicName}`节点之后Controller挂掉了会怎么样
> **先说结论**：Controller 重新选举的时候,会有一些初始化的操作; 会把创建过程继续下去

> 然后我们来模拟这么一个过程,先停止集群,然后再zk中写入`/brokers/topics/{TopicName}`节点数据; 然后再启动一台Broker; 
> **源码分析:** 我们之前分析过[Controller的启动过程与选举]() 有提到过,这里再提一下Controller当选之后有一个地方处理这个事情
> ```
> replicaStateMachine.startup()
> partitionStateMachine.startup()
> ```
> 启动状态机的过程是不是跟上面的**6.1 onNewPartitionCreation 状态流转** 的过程很像; 最终都把状态流转到了`OnlinePartition`; 伴随着是不发起了`leaderAndIsrRequest`请求; 是不是Broker收到请求之后,创建本地Log文件了
> 





## 附件

### --config 可生效参数
请以`sh bin/kafka-topic -help` 为准
```xml
configurations:                      
                  cleanup.policy                        
                  compression.type                      
                  delete.retention.ms                   
                  file.delete.delay.ms                  
                  flush.messages                        
                  flush.ms                              
                  follower.replication.throttled.       
replicas                             
                index.interval.bytes                  
                leader.replication.throttled.replicas 
                max.compaction.lag.ms                 
                max.message.bytes                     
                message.downconversion.enable         
                message.format.version                
                message.timestamp.difference.max.ms   
                message.timestamp.type                
                min.cleanable.dirty.ratio             
                min.compaction.lag.ms                 
                min.insync.replicas                   
                preallocate                           
                retention.bytes                       
                retention.ms                          
                segment.bytes                         
                segment.index.bytes                   
                segment.jitter.ms                     
                segment.ms                            
                unclean.leader.election.enable
```


---
<font color=red size=5>Tips:如果关于本篇文章你有疑问,可以在评论区留下,我会在**Q&A**部分进行解答 </font>



<font color=red size=2>PS: 文章阅读的源码版本是kafka-2.5 </font>

