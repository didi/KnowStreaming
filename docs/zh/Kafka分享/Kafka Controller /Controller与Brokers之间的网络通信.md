
## 前言
之前我们有解析过[【kafka源码】Controller启动过程以及选举流程源码分析](), 其中在分析过程中,Broker在当选Controller之后,需要初始化Controller的上下文中, 有关于Controller与Broker之间的网络通信的部分我没有细讲,因为这个部分我想单独来讲;所以今天 我们就来好好分析分析**Controller与Brokers之间的网络通信**

## 源码分析
### 1. 源码入口 ControllerChannelManager.startup()
调用链路
->`KafkaController.processStartup`
->`KafkaController.elect()`
->`KafkaController.onControllerFailover()`
->`KafkaController.initializeControllerContext()`
```scala 
  def startup() = {
    // 把所有存活的Broker全部调用 addNewBroker这个方法
    controllerContext.liveOrShuttingDownBrokers.foreach(addNewBroker)

    brokerLock synchronized {
      //开启 网络请求线程
      brokerStateInfo.foreach(brokerState => startRequestSendThread(brokerState._1))
    }
  }
```

### 2. addNewBroker 构造broker的连接信息
> 将所有存活的brokers 构造一些对象例如`NetworkClient`、`RequestSendThread` 等等之类的都封装到对象`ControllerBrokerStateInfo`中; 
> 由`brokerStateInfo`持有对象 key=brokerId； value = `ControllerBrokerStateInfo`

```scala
  private def addNewBroker(broker: Broker): Unit = {
    // 省略部分代码
    val threadName = threadNamePrefix match {
      case None => s"Controller-${config.brokerId}-to-broker-${broker.id}-send-thread"
      case Some(name) => s"$name:Controller-${config.brokerId}-to-broker-${broker.id}-send-thread"
    }

    val requestRateAndQueueTimeMetrics = newTimer(
      RequestRateAndQueueTimeMetricName, TimeUnit.MILLISECONDS, TimeUnit.SECONDS, brokerMetricTags(broker.id)
    )

    //构造请求发送线程
    val requestThread = new RequestSendThread(config.brokerId, controllerContext, messageQueue, networkClient,
      brokerNode, config, time, requestRateAndQueueTimeMetrics, stateChangeLogger, threadName)
    requestThread.setDaemon(false)

    val queueSizeGauge = newGauge(QueueSizeMetricName, () => messageQueue.size, brokerMetricTags(broker.id))
    //封装好对象 缓存在brokerStateInfo中
    brokerStateInfo.put(broker.id, ControllerBrokerStateInfo(networkClient, brokerNode, messageQueue,
      requestThread, queueSizeGauge, requestRateAndQueueTimeMetrics, reconfigurableChannelBuilder))
  }
```
1. 将所有存活broker 封装成一个个`ControllerBrokerStateInfo`对象保存在缓存中; 对象中包含了`RequestSendThread` 请求发送线程 对象； 什么时候执行发送线程 ,我们下面分析
2. `messageQueue：` 一个阻塞队列,里面放的都是待执行的请求,里面的对象`QueueItem` 封装了
 请求接口`ApiKeys`,`AbstractControlRequest`请求体对象;`AbstractResponse` 回调函数和`enqueueTimeMs`入队时间 
3. `RequestSendThread` 发送请求的线程 , 跟Broker们的网络连接就是通过这里进行的；比如下图中向Brokers们(当然包含自己)发送`UPDATE_METADATA`更新元数据的请求
    ![在这里插入图片描述](https://img-blog.csdnimg.cn/20210611174518555.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTA2MzQwNjY=,size_16,color_FFFFFF,t_70)


### 3. startRequestSendThread 启动网络请求线程
>把所有跟Broker连接的网络请求线程开起来
```scala
  protected def startRequestSendThread(brokerId: Int): Unit = {
    val requestThread = brokerStateInfo(brokerId).requestSendThread
    if (requestThread.getState == Thread.State.NEW)
      requestThread.start()
  }
}
```

线程执行代码块 ; 以下省略了部分代码
```scala
override def doWork(): Unit = {

    def backoff(): Unit = pause(100, TimeUnit.MILLISECONDS)

    //从阻塞请求队列里面获取有没有待执行的请求
    val QueueItem(apiKey, requestBuilder, callback, enqueueTimeMs) = queue.take()
    requestRateAndQueueTimeMetrics.update(time.milliseconds() - enqueueTimeMs, TimeUnit.MILLISECONDS)

    var clientResponse: ClientResponse = null
    try {
      var isSendSuccessful = false
      while (isRunning && !isSendSuccessful) {
        // if a broker goes down for a long time, then at some point the controller's zookeeper listener will trigger a
        // removeBroker which will invoke shutdown() on this thread. At that point, we will stop retrying.
        try {
          //检查跟Broker的网络连接是否畅通,如果连接不上会重试
          if (!brokerReady()) {
            isSendSuccessful = false
            backoff()
          }
          else {
            //构建请求参数
            val clientRequest = networkClient.newClientRequest(brokerNode.idString, requestBuilder,
              time.milliseconds(), true)
              //发起网络请求
            clientResponse = NetworkClientUtils.sendAndReceive(networkClient, clientRequest, time)
            isSendSuccessful = true
          }
        } catch {
      }
      if (clientResponse != null) {
        val requestHeader = clientResponse.requestHeader
        val api = requestHeader.apiKey
        if (api != ApiKeys.LEADER_AND_ISR && api != ApiKeys.STOP_REPLICA && api != ApiKeys.UPDATE_METADATA)
          throw new KafkaException(s"Unexpected apiKey received: $apiKey")

        if (callback != null) {
          callback(response)
        }
      }
    } catch {
      
    }
  }
```
1. 从请求队列`queue`中take请求; 如果有的话就开始执行,没有的话就阻塞住
2. 检查请求的目标Broker是否可以连接; 连接不通会一直进行尝试,然后在某个时候，控制器的 zookeeper 侦听器将触发一个 `removeBroker`，它将在此线程上调用 shutdown()。就不会在重试了
3. 发起请求; 
4. 如果请求失败,则重新连接Broker发送请求
5. 返回成功,调用回调接口
6. 值得注意的是<font color="red"> Controller发起的请求,收到Response中的ApiKeys中如果不是 `LEADER_AND_ISR`、`STOP_REPLICA`、`UPDATE_METADATA` 三个请求,就会抛出异常; 不会进行callBack的回调; </font> 不过也是很奇怪,如果Controller限制只能发起这几个请求的话,为什么在发起请求之前去做拦截,而要在返回之后做拦截; **个人猜测 可能是Broker在Response带上ApiKeys, 在Controller 调用callBack的时候可能会根据ApiKeys的不同而处理不同逻辑吧;但是又只想对Broker开放那三个接口;**



### 4. 向RequestSendThread的请求队列queue中添加请求
> 上面的线程启动完成之后，queue中还没有待执行的请求的，那么什么时候有添加请求呢？

添加请求最终都会调用接口`` ，反查一下就知道了; 
```java
  def sendRequest(brokerId: Int, request: AbstractControlRequest.Builder[_ <: AbstractControlRequest],
                  callback: AbstractResponse => Unit = null): Unit = {
    brokerLock synchronized {
      val stateInfoOpt = brokerStateInfo.get(brokerId)
      stateInfoOpt match {
        case Some(stateInfo) =>
          stateInfo.messageQueue.put(QueueItem(request.apiKey, request, callback, time.milliseconds()))
        case None =>
          warn(s"Not sending request $request to broker $brokerId, since it is offline.")
      }
    }
  }
```

**这里举一个**🌰 ; 看看Controller向Broker发起一个`UPDATE_METADATA`请求;
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210611182731937.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTA2MzQwNjY=,size_16,color_FFFFFF,t_70)
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210611183114551.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTA2MzQwNjY=,size_16,color_FFFFFF,t_70)

1. 可以看到调用了`sendRequest`请求 ; 请求的接口ApiKey=`UPDATE_METADATA` 
2. 回调方法就是如上所示;  向事件管理器`ControllerChannelManager`中添加一个事件`UpdateMetadataResponseReceived`
3. 当请求成功之后,调用2中的callBack, `UpdateMetadataResponseReceived`被添加到事件管理器中; 就会立马被执行(排队)
4. 执行地方如下图所示,只不过它也没干啥,也就是如果返回异常response就打印一下日志
   ![在这里插入图片描述](https://img-blog.csdnimg.cn/2021061118385771.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTA2MzQwNjY=,size_16,color_FFFFFF,t_70)







### 5. Broker接收Controller的请求
> 上面说了Controller对所有Brokers(当然也包括自己)发起请求;  那么Brokers接受请求的地方在哪里呢,我们下面分析分析

这个部分内容我们在[【kafka源码】TopicCommand之创建Topic源码解析]()  中也分析过,处理过程都是一样的;
比如还是上面的例子🌰, 发起请求了之后,Broker处理的地方在`KafkaRequestHandler.run`里面的`apis.handle(request)`;
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210611184840506.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTA2MzQwNjY=,size_16,color_FFFFFF,t_70)

可以看到这里列举了所有的接口请求;我们找到`UPDATE_METADATA`处理逻辑;
里面的处理逻辑就不进去看了,不然超出了本篇文章的范畴; 


### 6. Broker服务下线
我们模拟一下Broker宕机了, 手动把zk上的` /brokers/ids/broker节点`删除; 因为Controller是有对节点`watch`的, 就会看到Controller收到了变更通知,并且调用了 `KafkaController.processBrokerChange()`接口;
```scala
  private def processBrokerChange(): Unit = {
    if (!isActive) return
    val curBrokerAndEpochs = zkClient.getAllBrokerAndEpochsInCluster
    val curBrokerIdAndEpochs = curBrokerAndEpochs map { case (broker, epoch) => (broker.id, epoch) }
    val curBrokerIds = curBrokerIdAndEpochs.keySet
    val liveOrShuttingDownBrokerIds = controllerContext.liveOrShuttingDownBrokerIds
    val newBrokerIds = curBrokerIds -- liveOrShuttingDownBrokerIds
    val deadBrokerIds = liveOrShuttingDownBrokerIds -- curBrokerIds
    val bouncedBrokerIds = (curBrokerIds & liveOrShuttingDownBrokerIds)
      .filter(brokerId => curBrokerIdAndEpochs(brokerId) > controllerContext.liveBrokerIdAndEpochs(brokerId))
    val newBrokerAndEpochs = curBrokerAndEpochs.filter { case (broker, _) => newBrokerIds.contains(broker.id) }
    val bouncedBrokerAndEpochs = curBrokerAndEpochs.filter { case (broker, _) => bouncedBrokerIds.contains(broker.id) }
    val newBrokerIdsSorted = newBrokerIds.toSeq.sorted
    val deadBrokerIdsSorted = deadBrokerIds.toSeq.sorted
    val liveBrokerIdsSorted = curBrokerIds.toSeq.sorted
    val bouncedBrokerIdsSorted = bouncedBrokerIds.toSeq.sorted
    info(s"Newly added brokers: ${newBrokerIdsSorted.mkString(",")}, " +
      s"deleted brokers: ${deadBrokerIdsSorted.mkString(",")}, " +
      s"bounced brokers: ${bouncedBrokerIdsSorted.mkString(",")}, " +
      s"all live brokers: ${liveBrokerIdsSorted.mkString(",")}")

    newBrokerAndEpochs.keySet.foreach(controllerChannelManager.addBroker)
    bouncedBrokerIds.foreach(controllerChannelManager.removeBroker)
    bouncedBrokerAndEpochs.keySet.foreach(controllerChannelManager.addBroker)
    deadBrokerIds.foreach(controllerChannelManager.removeBroker)
    if (newBrokerIds.nonEmpty) {
      controllerContext.addLiveBrokersAndEpochs(newBrokerAndEpochs)
      onBrokerStartup(newBrokerIdsSorted)
    }
    if (bouncedBrokerIds.nonEmpty) {
      controllerContext.removeLiveBrokers(bouncedBrokerIds)
      onBrokerFailure(bouncedBrokerIdsSorted)
      controllerContext.addLiveBrokersAndEpochs(bouncedBrokerAndEpochs)
      onBrokerStartup(bouncedBrokerIdsSorted)
    }
    if (deadBrokerIds.nonEmpty) {
      controllerContext.removeLiveBrokers(deadBrokerIds)
      onBrokerFailure(deadBrokerIdsSorted)
    }

    if (newBrokerIds.nonEmpty || deadBrokerIds.nonEmpty || bouncedBrokerIds.nonEmpty) {
      info(s"Updated broker epochs cache: ${controllerContext.liveBrokerIdAndEpochs}")
    }
  }

```
1. 这里会去zk里面获取所有的Broker信息; 并将得到的数据跟当前Controller缓存中的所有Broker信息做对比; 
2. 如果有新上线的Broker,则会执行 Broker上线的流程
3. 如果有删除的Broker,则执行Broker下线的流程; 比如`removeLiveBrokers`

收到删除节点之后, Controller 会觉得Broker已经下线了,即使那台Broker服务是正常的,那么它仍旧提供不了服务

### 7. Broker上下线
本篇主要讲解**Controller与Brokers之间的网络通信** 
故**Broker上下线**内容单独开一篇文章来详细讲解 [【kafka源码】Brokers的上下线流程](https://shirenchuang.blog.csdn.net/article/details/117846476)

## 源码总结
本篇文章内容比较简单, Controller和Broker之间的通信就是通过 `RequestSendThread` 这个线程来进行发送请求;
`RequestSendThread`维护的阻塞请求队列在没有任务的时候处理阻塞状态; 
当有需要发起请求的时候,直接向`queue`中添加任务就行了;

Controller自身也是一个Broker,所以Controller发出的请求,自己也会收到并且执行


## Q&A
### 如果Controller与Broker网络连接不通会怎么办？
> 会一直进行重试, 直到zookeeper发现Broker通信有问题,会将这台Broker的节点移除,Controller就会收到通知,并将Controller与这台Broker的`RequestSendThread`线程shutdown;就不会再重试了;  如果zk跟Broker之间网络通信是正常的,只是发起的逻辑请求就是失败,则会一直进行重试

### 如果手动将zk中的 /brokers/ids/ 下的子节点删除会怎么样？
>手动删除` /brokers/ids/Broker的ID`, Controller收到变更通知,则将该Broker在Controller中处理下线逻辑; 所有该Broker已经游离于集群之外,即使它服务还是正常的,但是它却提供不了服务了; 只能重启该Broker重新注册;