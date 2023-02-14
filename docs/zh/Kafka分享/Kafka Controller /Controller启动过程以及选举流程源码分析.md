[TOC]

## 前言
>本篇文章,我们开始来分析分析Kafka的`Controller`部分的源码,Controller 作为 Kafka Server 端一个重要的组件，它的角色类似于其他分布式系统 Master 的角色，跟其他系统不一样的是，Kafka 集群的任何一台 Broker 都可以作为 Controller，但是在一个集群中同时只会有一个 Controller 是 alive 状态。Controller 在集群中负责的事务很多，比如：集群 meta 信息的一致性保证、Partition leader 的选举、broker 上下线等都是由 Controller 来具体负责。

## 源码分析
老样子,我们还是先来撸一遍源码之后,再进行总结
<font color="red">如果觉得阅读源码解析太枯燥,请直接看 **源码总结及其后面部分**</font>


### 1.源码入口KafkaServer.startup
我们在启动kafka服务的时候,最开始执行的是`KafkaServer.startup`方法; 这里面包含了kafka启动的所有流程; 我们主要看Controller的启动流程
```scala
  def startup(): Unit = {
    try {
        //省略部分代码....
        /* start kafka controller */
        kafkaController = new KafkaController(config, zkClient, time, metrics, brokerInfo, brokerEpoch, tokenManager, threadNamePrefix)
        kafkaController.startup()
         //省略部分代码....
    }
  }
```
### 2. kafkaController.startup() 启动
```scala
  /**
    每个kafka启动的时候都会调用, 注意这并不假设当前代理是控制器。
    它只是注册会话过期侦听器 并启动控制器尝试选举Controller
    */
  def startup() = {
    //注册状态变更处理器; 这里是把`StateChangeHandler`这个处理器放到一个`stateChangeHandlers` Map中了
    zkClient.registerStateChangeHandler(new StateChangeHandler {
      override val name: String = StateChangeHandlers.ControllerHandler
      override def afterInitializingSession(): Unit = {
        eventManager.put(RegisterBrokerAndReelect)
      }
      override def beforeInitializingSession(): Unit = {
        val queuedEvent = eventManager.clearAndPut(Expire)

        // Block initialization of the new session until the expiration event is being handled,
        // which ensures that all pending events have been processed before creating the new session
        queuedEvent.awaitProcessing()
      }
    })
    // 在事件管理器的队列里面放入 一个 Startup启动事件; 这个时候放入还不会执行;
    eventManager.put(Startup)
    //启动事件管理器,启动的是一个 `ControllerEventThread`的线程
    eventManager.start()
  }
```
1. `zkClient.registerStateChangeHandler` 注册一个`StateChangeHandler` 状态变更处理器; 有一个map `stateChangeHandlers`来维护这个处理器列表; 这个类型的处理器有下图三个方法,可以看到我们这里实现了`beforeInitializingSession`和`afterInitializingSession`方法,具体调用的时机,我后面再分析(监听zk的数据变更)![在这里插入图片描述](https://img-blog.csdnimg.cn/20210611112428811.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTA2MzQwNjY=,size_16,color_FFFFFF,t_70)
2. `ControllerEventManager`是Controller的事件管理器; 里面维护了一个阻塞队列`queue`; 这个queue里面存放的是所有的Controller事件; 按顺序排队执行入队的事件; 上面的代码中`eventManager.put(Startup)` 在队列中放入了一个`Startup`启动事件; 所有的事件都是集成了`ControllerEvent`类的![在这里插入图片描述](https://img-blog.csdnimg.cn/20210611113223844.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTA2MzQwNjY=,size_16,color_FFFFFF,t_70)
3. 启动事件管理器, 从待执行事件队列`queue`中获取事件进行执行,刚刚不是假如了一个`StartUp`事件么,这个事件就会执行这个事件

### 3. ControllerEventThread 执行事件线程
` eventManager.start()` 之后执行了下面的方法

```scala
  class ControllerEventThread(name: String) extends ShutdownableThread(name = name, isInterruptible = false) {
    override def doWork(): Unit = {
      //从待执行队列里面take一个事件; 没有事件的时候这里会阻塞
      val dequeued = queue.take()
      dequeued.event match {
        case ShutdownEventThread => // The shutting down of the thread has been initiated at this point. Ignore this event.
        case controllerEvent =>
          //获取事件的ControllerState值；不同事件不一样,都集成自ControllerState
          _state = controllerEvent.state
          eventQueueTimeHist.update(time.milliseconds() - dequeued.enqueueTimeMs)
          try {
           // 定义process方法; 最终执行的是 事件提供的process方法；
            def process(): Unit = dequeued.process(processor)
            
            //根据state获取不同的KafkaTimer 主要是为了采集数据； 我们只要关注里面是执行了 process()方法就行了
            rateAndTimeMetrics.get(state) match {
              case Some(timer) => timer.time { process() }
              case None => process()
            }
          } catch {
            case e: Throwable => error(s"Uncaught error processing event $controllerEvent", e)
          }

          _state = ControllerState.Idle
      }
    }
  }

}

```
1. `val dequeued  = queue.take()`从待执行队列里面take一个事件; 没有事件的时候这里会阻塞
2.  `dequeued.process(processor)`调用具体事件实现的 `process方法`如下图, 不过要注意的是这里使用了`CountDownLatch(1)`, 那肯定有个地方调用了`processingStarted.await()` 来等待这里的`process()执行完成`;上面的startUp方法就调用了; ![在这里插入图片描述](https://img-blog.csdnimg.cn/20210611114915829.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTA2MzQwNjY=,size_16,color_FFFFFF,t_70)
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210611115440890.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTA2MzQwNjY=,size_16,color_FFFFFF,t_70)

### 4. processStartup 启动流程
启动Controller的流程
```scala
  private def processStartup(): Unit = {
    //注册znode变更事件和watch Controller节点是否在zk中存在
    zkClient.registerZNodeChangeHandlerAndCheckExistence(controllerChangeHandler)
    //选举逻辑
    elect()
  }

```
1. 注册`ZNodeChangeHandler` 节点变更事件处理器,在map `zNodeChangeHandlers`中保存了key=`/controller`;value=`ZNodeChangeHandler`的键值对; 其中`ZNodeChangeHandler`处理器有如下三个接口
![在这里插入图片描述](https://img-blog.csdnimg.cn/2021061111595850.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTA2MzQwNjY=,size_16,color_FFFFFF,t_70)
2. 然后向zk发起一个`ExistsRequest(/controller)`的请求,去查询一下`/controller`节点是否存在; 并且如果不存在的话,就注册一个`watch` 监视这个节点;从下面的代码可以看出
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210611120501331.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTA2MzQwNjY=,size_16,color_FFFFFF,t_70)
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210611120515913.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTA2MzQwNjY=,size_16,color_FFFFFF,t_70)
因为上一步中我们在map `zNodeChangeHandlers`中保存了key=`/controller`; 所以上图中可知,需要注册`watch`来进行`/controller`节点的监控;
kafka是是怎实现监听的呢？`zookeeper`构建的时候传入了自定义的`WATCH`
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210613104354849.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTA2MzQwNjY=,size_16,color_FFFFFF,t_70)
![在这里插入图片描述](https://img-blog.csdnimg.cn/2021061310443834.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTA2MzQwNjY=,size_16,color_FFFFFF,t_70)



3.  选举; 选举的过程其实就是几个Broker抢占式去成为Controller; 谁先创建`/controller`这个节点; 谁就成为Controller; 我们下面仔细分析以下选择

### 5. Controller的选举elect()

```scala
  private def elect(): Unit = {
    //去zk上获取 /controller 节点的数据  如果没有就赋值为-1
    activeControllerId = zkClient.getControllerId.getOrElse(-1)
    //如果获取到了数据就
    if (activeControllerId != -1) {
      debug(s"Broker $activeControllerId has been elected as the controller, so stopping the election process.")
      return
    }

    try {
     
      //尝试去zk中写入自己的Brokerid作为Controller；并且更新Controller epoch
      val (epoch, epochZkVersion) = zkClient.registerControllerAndIncrementControllerEpoch(config.brokerId)
      controllerContext.epoch = epoch
      controllerContext.epochZkVersion = epochZkVersion
      activeControllerId = config.brokerId
      //
      onControllerFailover()
    } catch {
    //尝试卸任Controller的职责
    maybeResign()
   //省略...
    }
  }
```
1. 去zk上获取` /controller `节点的数据  如果没有就赋值为-1
2. 如果获取到了数据说明已经有Controller注册成功了;直接结束选举流程
3. 尝试去zk中写入自己的Brokerid作为Controller；并且更新Controller epoch
	-  获取zk节点`/controller_epoch`, 这个节点是表示Controller变更的次数,如果没有的话就创建这个节点(**持久节点**); 起始`controller_epoch=0` `ControllerEpochZkVersion=0`
	- 向zk发起一个`MultiRequest`请求;里面包含两个命令; 一个是向zk中创建`/controller`节点,节点内容是自己的brokerId;另一个命令是向`/controller_epoch`中更新数据; 数据+1 ;
	- 如果写入过程中抛出异常提示说节点已经存在,说明别的Broker已经抢先成为Controller了; 这个时候会做一个检查`checkControllerAndEpoch` 来检查是不是别的Controller抢先了; 如果是的话就抛出`ControllerMovedException`异常; 抛出了这个异常之后,当前Broker会尝试的去卸任一下Controller的职责; （因为有可能他之前是Controller,Controller转移之后都需要尝试卸任一下）

5. Controller确定之后,就是做一下成功之后的事情了 `onControllerFailover`


### 6. 当选Controller之后的处理  onControllerFailover
进入到`KafkaController.onControllerFailover`
```scala
private def onControllerFailover(): Unit = {

    // 都是ZNodeChildChangeHandler处理器； 含有接口 handleChildChange；注册了不同事件的处理器
    // 对应的事件分别有`BrokerChange`、`TopicChange`、`TopicDeletion`、`LogDirEventNotification`
    val childChangeHandlers = Seq(brokerChangeHandler, topicChangeHandler, topicDeletionHandler, logDirEventNotificationHandler,
      isrChangeNotificationHandler)
    //把这些handle都维护在 map类型`zNodeChildChangeHandlers`中  
    childChangeHandlers.foreach(zkClient.registerZNodeChildChangeHandler)
    //都是ZNodeChangeHandler处理器,含有增删改节点接口;
      //分别对应的事件 `ReplicaLeaderElection`、`ZkPartitionReassignment`、``
    val nodeChangeHandlers = Seq(preferredReplicaElectionHandler, partitionReassignmentHandler)
       //把这些handle都维护在 map类型`zNodeChangeHandlers`中  
    nodeChangeHandlers.foreach(zkClient.registerZNodeChangeHandlerAndCheckExistence)

    info("Deleting log dir event notifications")
    //删除所有日志目录事件通知。 ;获取zk中节点`/log_dir_event_notification`的值;然后把节点下面的节点全部删除
    zkClient.deleteLogDirEventNotifications(controllerContext.epochZkVersion)
    info("Deleting isr change notifications")
    // 删除节点 `/isr_change_notification`下的所有节点
    zkClient.deleteIsrChangeNotifications(controllerContext.epochZkVersion)
    info("Initializing controller context")
    initializeControllerContext()
    info("Fetching topic deletions in progress")
    val (topicsToBeDeleted, topicsIneligibleForDeletion) = fetchTopicDeletionsInProgress()
    info("Initializing topic deletion manager")
    topicDeletionManager.init(topicsToBeDeleted, topicsIneligibleForDeletion)

    // We need to send UpdateMetadataRequest after the controller context is initialized and before the state machines
    // are started. The is because brokers need to receive the list of live brokers from UpdateMetadataRequest before
    // they can process the LeaderAndIsrRequests that are generated by replicaStateMachine.startup() and
    // partitionStateMachine.startup().
    info("Sending update metadata request")
    sendUpdateMetadataRequest(controllerContext.liveOrShuttingDownBrokerIds.toSeq, Set.empty)

    replicaStateMachine.startup()
    partitionStateMachine.startup()

    info(s"Ready to serve as the new controller with epoch $epoch")

    initializePartitionReassignments()
    topicDeletionManager.tryTopicDeletion()
    val pendingPreferredReplicaElections = fetchPendingPreferredReplicaElections()
    onReplicaElection(pendingPreferredReplicaElections, ElectionType.PREFERRED, ZkTriggered)
    info("Starting the controller scheduler")
    kafkaScheduler.startup()
    if (config.autoLeaderRebalanceEnable) {
      scheduleAutoLeaderRebalanceTask(delay = 5, unit = TimeUnit.SECONDS)
    }
    scheduleUpdateControllerMetricsTask()

    if (config.tokenAuthEnabled) {
      info("starting the token expiry check scheduler")
      tokenCleanScheduler.startup()
      tokenCleanScheduler.schedule(name = "delete-expired-tokens",
        fun = () => tokenManager.expireTokens,
        period = config.delegationTokenExpiryCheckIntervalMs,
        unit = TimeUnit.MILLISECONDS)
    }
  }
```
1. 把事件`BrokerChange`、`TopicChange`、`TopicDeletion`、`LogDirEventNotification`对应的handle处理器都维护在 map类型`zNodeChildChangeHandlers`中  
2. 把事件 `ReplicaLeaderElection`、`ZkPartitionReassignment`对应的handle处理器都维护在 map类型`zNodeChildChangeHandlers`中  
3. 删除zk中节点`/log_dir_event_notification`下的所有节点
4. 删除zk中节点 `/isr_change_notification`下的所有节点
5. 初始化Controller的上下文对象`initializeControllerContext()`
	- 获取`/brokers/ids`节点信息，拿到所有的存活的BrokerID; 然后获取每个Broker的信息 `/brokers/ids/对应BrokerId`的信息以及对应的节点的Epoch; 也就是`cZxid`; 然后将数据保存在内存中
	- 获取`/brokers/topics`节点信息;拿到所有Topic之后,放到Map `partitionModificationsHandlers`中,key=topicName;value=对应节点的`PartitionModificationsHandler`; 节点是`/brokers/topics/topic名称`；最终相当于是在事件处理队列`queue`中给每个Topic添加了一个`PartitionModifications`事件; 这个事件是怎么处理的,我们下面分析
	- 同时又注册一下上面的`PartitionModificationsHandler`,保存在map `zNodeChangeHandlers` 中; key= `/brokers/topics/Topic名称`,Value=`PartitionModificationsHandler`; 我们上面也说到过,这个有个功能就是判断需不需要向zk中注册`watch`; 从下图的代码中可以看出,在获取zk数据(`GetDataRequest`)的时候,会去 `zNodeChangeHandlers`判断一下存不存在对应节点key;存在的话就注册`watch`监视数据![在这里插入图片描述](https://img-blog.csdnimg.cn/20210611120515913.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTA2MzQwNjY=,size_16,color_FFFFFF,t_70)
	- zk中获取`/brokers/topics/topic名称`所有topic的分区数据; 保存在内存中
	- 给每个broker注册broker变更处理器`BrokerModificationsHandler`（也是`ZNodeChangeHandler`)它对应的事件是`BrokerModifications`; 同样的`zNodeChangeHandlers`中也保存着对应的`/brokers/ids/对应BrokerId` 同样的`watch`监控；并且map `brokerModificationsHandlers`保存对应关系 key=`brokerID` value=`BrokerModificationsHandler`
	- 从zk中获取所有的topic-partition 信息;  节点: `/brokers/topics/Topic名称/partitions/分区号/state` ; 然后保存在缓存中`controllerContext.partitionLeadershipInfo`
	![在这里插入图片描述](https://img-blog.csdnimg.cn/20210611161631995.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTA2MzQwNjY=,size_16,color_FFFFFF,t_70)
	- `controllerChannelManager.startup()` 这个单独开了一篇文章讲解,请看[【kafka源码】Controller与Brokers之间的网络通信](), 简单来说就是创建一个map来保存于所有Broker的发送请求线程对象`RequestSendThread`;这个对象中有一个 阻塞队列`queue`; 用来排队执行要执行的请求,没有任务时候回阻塞; Controller需要发送请求的时候只需要向这个`queue`中添加任务就行了

6. 初始化删除Topic管理器`topicDeletionManager.init()`
	- 读取zk节点`/admin/delete_topics`的子节点数据,表示的是标记为已经删除的Topic
   - 将被标记为删除的Topic,做一些开始删除Topic的操作;具体详情情况请看[【kafka源码】TopicCommand之删除Topic源码解析]()
 
7. `sendUpdateMetadataRequest` 给Brokers们发送`UPDATA_METADATA` 更新元数据的请求，关于更新元数据详细情况 [【kafka源码】更新元数据`UPDATA_METADATA`请求源码分析 ]()
8. `replicaStateMachine.startup()` 启动副本状态机，获取所有在线的和不在线的副本;
①. 将在线副本状态变更为`OnlineReplica:`将带有当前领导者和 isr 的 `LeaderAndIsr `请求发送到新副本，并将分区的 `UpdateMetadata `请求发送到每个实时代理
②. 将不在线副本状态变更为`OfflineReplica:` 向副本发送 [StopReplicaRequest]() ； 从 isr 中删除此副本并将 [LeaderAndIsr]() 请求（带有新的 isr）发送到领导副本，并将分区的 UpdateMetadata 请求发送到每个实时代理。
详细请看 [【kafka源码】Controller中的状态机](https://shirenchuang.blog.csdn.net/article/details/117848213)
9. `partitionStateMachine.startup()`启动分区状态机,获取所有在线的和不在线(判断Leader是否在线)的分区;
     1. 如果分区不存在`LeaderIsr`,则状态是`NewPartition`
	 2. 如果分区存在`LeaderIsr`,就判断一下Leader是否存活
	    2.1 如果存活的话,状态是`OnlinePartition`
	    2.2 否则是`OfflinePartition`
	 3. 尝试将所有处于 `NewPartition `或 `OfflinePartition `状态的分区移动到 `OnlinePartition` 状态，但属于要删除的主题的分区除外
	    
	PS:如果之前创建Topic过程中,Controller发生了变更,Topic创建么有完成,那么这个状态流转的过程会继续创建下去; [【kafka源码】TopicCommand之创建Topic源码解析]()
	关于状态机 详细请看 [【kafka源码】Controller中的状态机](https://shirenchuang.blog.csdn.net/article/details/117848213)

11. ` initializePartitionReassignments` 初始化挂起的重新分配。这包括通过 `/admin/reassign_partitions` 发送的重新分配，它将取代任何正在进行的 API 重新分配。[【kafka源码】分区重分配 TODO..]()
12. `topicDeletionManager.tryTopicDeletion()`尝试恢复未完成的Topic删除操作;相关情况 [【kafka源码】TopicCommand之删除Topic源码解析](https://shirenchuang.blog.csdn.net/article/details/117847877)
13. 从`/admin/preferred_replica_election` 获取值,调用`onReplicaElection()` 尝试为每个给定分区选举一个副本作为领导者 ;相关内容请看[【kafka源码】Kafka的优先副本选举源码分析]();
14. `kafkaScheduler.startup()`启动一些定时任务线程
15. 如果配置了`auto.leader.rebalance.enable=true`，则启动LeaderRebalace的定时任务;线程名`auto-leader-rebalance-task`
16. 如果配置了 `delegation.token.master.key`,则启动一些token的清理线程
	

### 7. Controller重新选举
当我们把zk中的节点`/controller`删除之后; 会调用下面接口;进行重新选举
```scala
  private def processReelect(): Unit = {
    //尝试卸任一下
    maybeResign()
    //进行选举
    elect()
  }
```




## 源码总结

![在这里插入图片描述](https://img-blog.csdnimg.cn/20210630195523983.jpg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTA2MzQwNjY=,size_16,color_FFFFFF,t_70#pic_center)



PS: 可以看到 Broker当选Controller之后,保存了很多zk上的数据到自己的内存中, 也承担了很多责任; 如果这台Broker自身压力就挺大,那么它当选Controller之后压力会更大,所以尽量让比较空闲的Broker当选Controller,那么如何实现这样一个目标呢? 可以指定Broker作为Controller; 
这样一个功能可以在 <font color=red size=5>项目地址: [didi/Logi-KafkaManager: 一站式Apache Kafka集群指标监控与运维管控平台](https://github.com/didi/Logi-KafkaManager)</font> 里面可以实现


## Q&A

### 直接删除zk节点`/controller`会怎么样
>Broker之间会立马重新选举Controller;

### 如果修改节点`/controller/`下的数据会成功将Controller转移吗
假如`/controller`节点数据是`{"version":1,"brokerid":3,"timestamp":"1623746563454"}` 我把BrokerId=1；Controller会直接变成Broker-1？
>Answer:  **不会成功转移,并且当前的集群中Broker是没有Controller角色的;这就是一个非常严重的问题了**

分析源码:
修改`/controller/`数据在Controller执行的代码是
```scala
  private def processControllerChange(): Unit = {
    maybeResign()
  }
    
    private def maybeResign(): Unit = {
    val wasActiveBeforeChange = isActive
    zkClient.registerZNodeChangeHandlerAndCheckExistence(controllerChangeHandler)
    activeControllerId = zkClient.getControllerId.getOrElse(-1)
    if (wasActiveBeforeChange && !isActive) {
      onControllerResignation()
    }
  }

```
代码就非常清楚的看到, 修改数据之后,如果修改后的Broker-Id和当前的Controller的BrokerId不一致,执行`onControllerResignation` 就让当前的Controller卸任这个角色了; 

### /log_dir_event_notification 是干啥 的
> 当`log.dir`日志文件夹出现访问不了,磁盘损坏等等异常导致读写失败,就会触发一些异常通知事件; 
> 流程是->
> 1.  Broker检查到`log.dir`异常,做一些清理工作,然后向zk中创建持久序列节点`/log_dir_event_notification/log_dir_event_+序列号`；数据是 BrokerID;例如:
>`/log_dir_event_notification/log_dir_event_0000000003`
>2. Controller 监听到了zk的变更; 将从zk节点 /log_dir_event_notification/log_dir_event_序列号 中获取到的数据的Broker上的所有副本进行一个副本状态流转 ->OnlineReplica
>		2.1 给所有broker 发送`LeaderAndIsrRequest`请求，让brokers们去查询他们的副本的状态，如果副本logDir已经离线则返回KAFKA_STORAGE_ERROR异常;
> 		2.2 完事之后会删除节点

###  /isr_change_notification 是干啥用的
> 当有isr变更的时候会在这个节点写入数据; Controller监听之后做一些通知
### /admin/preferred_replica_election 是干啥用的
>优先副本选举, 详情请戳[kafka的优先副本选举流程 .]()
>

## 思考
### 有什么办法实现Controller的优先选举？
>既然我们知道了Controller承担了这么多的任务,又是Broker又是Controller,身兼数职压力难免会比较大;
>所以我们很希望能够有一个功能能够知道Broker为Controller角色; 这样就可以指定压力比较小的Broker来承担Controller的角色了;

**那么,如何实现呢？**
>Kafka原生目前并不支持这个功能,所以我们想要实现这个功能,就得要改源码了;
>知道了原理, 改源码实现这个功能就很简单了; 有很多种实现方式;

比如说: 在zk里面设置一个节点专门用来存放候选节点; 竞选Controller的时候优先从这里面选择;
然后Broker们启动的时候,可以判断一下自己是不是候选节点, 如果不是的话,那就让它睡个两三秒; (让候选者99米再跑)
那么大概率的情况下,候选者肯定就会当选了;  