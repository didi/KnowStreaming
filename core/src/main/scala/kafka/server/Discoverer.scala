package kafka.server

import java.util
import kafka.network.RequestChannel
import kafka.utils.Logging
import org.apache.kafka.clients._
import org.apache.kafka.common.requests.{AbstractResponse, BufferRequest, BufferResponse, RequestHeader, RequestUtils}
import org.apache.kafka.common.utils.{LogContext, Time, Utils}
import org.apache.kafka.common.Node

import scala.util.Random
import scala.collection.JavaConverters._
import java.util.concurrent._
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.locks.ReentrantReadWriteLock
import java.text.SimpleDateFormat
import com.alibaba.fastjson.JSON
import com.didichuxing.datachannel.kafka.config.{GatewayConfigs, HAUserConfig}
import com.didichuxing.datachannel.kafka.config.manager.{ClusterConfigManager, UserConfigManager}
import com.didichuxing.datachannel.kafka.util.{HttpUtils, JsonUtils, KafkaUtils, ResponseCommonResult}
import com.google.common.base.Splitter
import com.google.common.cache.{CacheBuilder, CacheLoader}
import com.google.common.util.concurrent.RateLimiter
import com.yammer.metrics.core.Gauge
import kafka.metrics.KafkaMetricsGroup
import kafka.server.RequestState.RequestState
import org.apache.commons.lang.StringUtils
import org.apache.kafka.common.config.SaslConfigs
import org.apache.kafka.common.memory.MemoryPool
import org.apache.kafka.common.metrics.stats.Rate
import org.apache.kafka.common.metrics.{Metrics, Sensor}
import org.apache.kafka.common.network.{ChannelBuilders, NetworkSend, Selector, Send}
import org.apache.kafka.common.protocol.ApiKeys
import org.apache.kafka.common.protocol.types.RawTaggedField
import org.apache.kafka.common.security.JaasContext
import org.slf4j.LoggerFactory

import java.util.Properties
import scala.collection.{Map, mutable}
import scala.util.control.Breaks._

class Discoverer(val config: KafkaConfig,
                 val requestChannel: RequestChannel,
                 val memoryPool: MemoryPool,
                 val metrics: Metrics,
                 val retries: Int,
                 val time: Time,
                 val transmitTimeoutMs: Int,
                 val urlPrefix: String,
                 val updateInterval: Long,
                 val requestSize: Int,
                 val maxNodeListSize: Int) extends Runnable with KafkaMetricsGroup with Logging {

  private val userLogger = LoggerFactory.getLogger("userError")
  private val requestStatusLogger = LoggerFactory.getLogger("requestStatus")
  private val log = LoggerFactory.getLogger(classOf[Discoverer])
  private val TRANSMIT_TIMOUT_NS = transmitTimeoutMs*1000000L

  //need to validate Cluster config item
  private val validateClusterConfigKeySet = Utils.mkSet(
    CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG,
    SaslConfigs.SASL_JAAS_CONFIG)
  // default cluster config item for reversed
  private val defaultClusterConfigKeySet = Utils.mkMap(
    Utils.mkEntry(KafkaConfig.ConnectionsMaxIdleMsProp,6000))
  // clusterid => brokerlist
  var clusterBootstrapNode = scala.collection.mutable.Map[String, mutable.Buffer[Node]]()
  // clusterid => brokerlist from KM
  var clusterBootstrapNodeFromKM = scala.collection.mutable.Map[String, mutable.Buffer[Node]]()
  // 保存不同 cluster
  var clusterClients = scala.collection.mutable.Map[String, KafkaClient]()
  //  useful cluster config map use by client
  var clusterConfigMap = scala.collection.mutable.Map[String, java.util.Properties]()

  val requestMap = new ConcurrentHashMap[String, ConcurrentLinkedQueue[RequestChannel.Request]]()

  val delayCloseConnectionQueue = new util.TreeSet[RequestChannel.Request](
    (t1: RequestChannel.Request, t2: RequestChannel.Request) => (t1.startTimeNanos - t2.startTimeNanos).toInt)

    //(req1:RequestChannel.Request, req2:RequestChannel.Request) => req1.startTimeNanos - req2.startTimeNanos)
  var spRateLimiterMap = new ConcurrentHashMap[String, Integer]()

  private val lock = new ReentrantReadWriteLock()
  val executorService = Executors.newSingleThreadScheduledExecutor()

  @volatile var running = true
  var lastUpdateTime = 0L
  val clusterUpdateInterval = updateInterval
  var maxRequestPerClusterVersion = 0L
  var maxRequestPerCluster = requestSize
  var clusterVersion = 0L
  var retryHttpRequest = retries
  val nodeListSize = maxNodeListSize
  val logRateLimitPerMin = 1
  var appIdRateVersion = 0L
  var appIdRate = 2000
  var ipRateVersion = 0L
  var ipRate = 2000
  var spRateVersion = 0L
  val defaultSleepInterval = 500
  val clientPollTimeInterval = 0
  private val TAG_FORWARD_CLIENT_HEADER_PRINCIPAL_CODE = "ForwardClientHeaderPrincipal".hashCode

  val logRateLimitCache = CacheBuilder.newBuilder()
    .expireAfterAccess(15, TimeUnit.MINUTES)
    .maximumSize(10000)
    .build(new CacheLoader[String, RateLimiter] {
      def load(key: String): RateLimiter = {
        return RateLimiter.create(logRateLimitPerMin)
      }
    })
  val appIdRateLimiterCache = CacheBuilder.newBuilder()
    .expireAfterAccess(15, TimeUnit.MINUTES)
    .maximumSize(100000)
    .build(new CacheLoader[String, RateLimiter] {
      def load(appId: String): RateLimiter = {
        return RateLimiter.create(appIdRate)
      }
    })
  val ipRateLimiterCache = CacheBuilder.newBuilder()
    .expireAfterAccess(15, TimeUnit.MINUTES)
    .maximumSize(100000)
    .build(new CacheLoader[String, RateLimiter] {
      def load(ip: String): RateLimiter = {
        return RateLimiter.create(ipRate)
      }
    })
  val spRateLimiterCache = CacheBuilder.newBuilder()
    .expireAfterWrite(1, TimeUnit.SECONDS)
    .maximumSize(10000)
    .build(new CacheLoader[String, AtomicLong] {
      def load(key: String): AtomicLong = {
        return new AtomicLong(0L)
      }
    })

  // metrics
  var discoverSensor: Sensor = null
  private val discoverMeter = newMeter("DiscoverAvgIdlePercent", "percent", TimeUnit.NANOSECONDS)

  init()

  def run(): Unit = {
    log.info("Starting discoverer request thread.")

    // main loop, runs until close is called
    while (running) {
      try {
        run(time.milliseconds())
      } catch {
        case t: Throwable =>
          log.error("Uncaught error in discoverer thread: ", t)
      }
    }
    log.info("Beginning shutdown of discoverer thread.")
    releaseAllClient
    info("Shutdown of discoverer thread has completed.")
  }

  def run(now: Long) {
    for ((key, value) <- requestMap.asScala) {
      breakable {
        if (value.isEmpty) {
          break()
        }
        val node = getNodeByClusterId(key)
        if (node.equals(Node.noNode())) {
          val itr = value.iterator
          var request = itr.next()
          while (request != null && time.nanoseconds - request.startTimeNanos > TRANSMIT_TIMOUT_NS) {
            handleError(request, "TRANSMIT_TIMEOUT")
            value.remove()
            if (itr.hasNext)
              request = itr.next
            else
              request = null
          }
          break()
        }

        val request = value.remove()
        log.debug("Get a request from queue, connectionId:{}, correlationId:{}, clusterId:{}, request:{}",
          request.context.connectionId, request.header.correlationId().toString, key, request)

        request.apiRemoteCompleteTimeNanos = Time.SYSTEM.nanoseconds
        clusterClients.get(key) match {
          case Some(client) =>
            request.header.apiKey match {
              case ApiKeys.METADATA => transmitRequest(key, request, node, client)
              case ApiKeys.API_VERSIONS => transmitRequest(key, request, node, client)
              case ApiKeys.FIND_COORDINATOR => transmitRequest(key, request, node, client)
              case ApiKeys.INIT_PRODUCER_ID => transmitRequest(key, request, node, client)
              case _ => handleError(request, "INVALID_REQUEST_ID")
            }
          case None =>
            handleError(request, "TRANSMIT_NOCLIENT")
        }
      }
    }

    // delay close
    this.synchronized {
      var continue = true
      while (!delayCloseConnectionQueue.isEmpty && continue) {
        val request = delayCloseConnectionQueue.first()
        if (time.nanoseconds - request.startTimeNanos > TRANSMIT_TIMOUT_NS) {
          requestChannel.sendResponse(new RequestChannel.CloseConnectionResponse(request))
          delayCloseConnectionQueue.pollFirst()
        } else {
          continue = false
        }
      }
    }

    lock.readLock().lock()
    try {
      val startSelectTime = time.nanoseconds()

//      val responseSize = clusterClients.flatMap(client => {
//        client._2.poll(clientPollTimeInterval, now).asScala
//      }).size
      val responseSize = clusterClients.map(client => {
        client._2.poll(clientPollTimeInterval, now)
        client._2.inFlightRequestCount()
      }).sum

      val idleTime = time.nanoseconds() - startSelectTime
      discoverMeter.mark(idleTime)

      // 此处如果让线程sleep 否则cpu高
      if (responseSize == 0) {
        time.sleep(defaultSleepInterval)
      }
    } finally {
      lock.readLock().unlock()
    }
  }

  def init(): Unit = {
    // TODO: 此处 如果配置 Cluster 太多可能存在性能问题，修改为异步
    initExecutorService()
    discoverSensor = metrics.sensor("discoverer-stat")
    val metricName = metrics.metricName("request-fail-rate", "discoverer-metrics")
    discoverSensor.add(metricName, new Rate)
  }

  def releaseAllClient(): Unit = {
    try {
      clusterClients.keySet.foreach(clusterId => {
        removeClusterClient(clusterId)
      })
    } catch {
      case e: Exception =>
        log.error("Failed to close network client", e)
    }
  }

  // validate zk config
  // validateClusterConfigKeySet need to be set
  // we will filter invalidate zk cluster config
  private def validateClusterConfig(prop: Properties): Boolean = {
    val fliterSet = validateClusterConfigKeySet.stream().filter(item => prop.containsKey(item) && prop.getProperty(item).nonEmpty).count()
    validateClusterConfigKeySet.size == fliterSet
  }

  // 通过 NodeList 恢复 BootStrap.Servers
  def combineClusterBootstrap(nodeList: mutable.Buffer[Node]): String = {
    val bootStrapServer = nodeList.map(node => node.host() + ":" + node.port()).distinct.mkString(",")
    bootStrapServer
  }

  // load kafkaclient from cluster config item
  def initClusterClientFromZK(clusterId: String): Unit = {
    lock.readLock().lock()
    try {
      loadClusterConfigs(clusterId)
    } catch {
      case e: Exception =>
      log.error(s"can not close cluster error detail:${e}")
    } finally {
      lock.readLock().unlock()
    }
  }

  // load cluster config form zk
  // when config is changed we need load cluster with new config
  def loadClusterConfigs(entityName: String): Unit = {
    val clusterProperties = ClusterConfigManager.getConfigs(entityName)
    if (clusterProperties.isEmpty) {
      removeClusterClient(entityName)
    } else {
      if (!validateClusterConfig(clusterProperties)) {
        removeClusterClient(entityName)
      } else {
        if (!clusterClients.contains(entityName)) {
          createClusterClient(entityName, clusterProperties)
        } else {
          reloadClusterClient(entityName, clusterProperties)
        }
      }
    }
  }

  // reload clusterId config
  def reloadClusterClient(clusterId: String, properties: Properties): Unit = {
    removeClusterClient(clusterId)
    createClusterClient(clusterId, properties)
  }

  // reload clusterClients this method will action when
  // 1  sync from km succeed
  // 2  init from zk
  // we need combine clusterBootstrapNodeFromKM && clusterBootstrapNodeFromZk
  // release unused clients
  // recreate cluster clients
  def reloadAllClusterClientFromKM(): Unit = {
    lock.readLock().lock()
    try {
      loadAllClusterClientFromKM()
    } catch {
      case e: Exception =>
        log.error(s"can not close cluster error detail:${e}")
    } finally {
      lock.readLock().unlock()
    }
  }

  def initClusterClientProperties(clusterId: String, props: Properties): Unit = {
    // Clone 一个 Kafka 配置
    log.debug(s"init cluster with Properties ${props}")
    val configClone = config.originals
    props.asScala.foreach(item => {
      configClone.put(item._1, item._2)
    })
    val cloneKafka = new KafkaConfig(configClone)
    createClusterClientIfPersent(clusterId, cloneKafka)
  }

  def releaseClusterClient(clusterId: String): Unit = {
      try {
        if (clusterClients.contains(clusterId)) {
          clusterClients.get(clusterId).get.close()
        }
      } catch {
        case e: Exception =>
          log.error(s"can not close cluster ${clusterId}, error detail:${e}")
      } finally {
        clusterClients.remove(clusterId)
      }
  }

  // create or update networkclient for clusterId user Config
  def createClusterClientIfPersent(clusterId: String, config: KafkaConfig): Unit = {
    try {
      val client = createNetworkClient(clusterId, config)
      if (client != null) {
        clusterClients += (clusterId -> client)
      }
    } catch {
      case e: Exception =>
        log.error(s"can not create cluster ${clusterId}, error detail:${e}")
    }
  }

  //create a new networkclient instance use config for clusterId
  private def createNetworkClient(clusterId: String, config: KafkaConfig): NetworkClient = {
    val networkClient = {
      val logContext = new LogContext(s"[LegacyAdminClient]")
      val channelBuilder = ChannelBuilders.clientChannelBuilder(
        config.interBrokerSecurityProtocol,
        JaasContext.Type.CLIENT,
        config,
        null,
        config.saslMechanismInterBrokerProtocol,
        time,
        config.saslInterBrokerHandshakeRequestEnable,
        logContext
      )
      val selector = new Selector(
        config.socketRequestMaxBytes,
        config.connectionsMaxIdleMs,
        config.failedAuthenticationDelayMs,
        metrics,
        time,
        "service-discovery".concat(clusterId),
        new util.HashMap,
        false,
        false,
        channelBuilder,
        memoryPool,
        logContext
      )

      new NetworkClient(
        selector,
        new ManualMetadataUpdater(),
        config.brokerId.toString,
        10,
        100,
        200,
        config.socketSendBufferBytes,
        config.socketReceiveBufferBytes,
        config.requestTimeoutMs,
        ClientDnsLookup.DEFAULT,
        time,
        false,
        new ApiVersions,
        logContext
      )
    }
    networkClient
  }

  /**
   * release client include client && Node && config
   *
   * @param clusterId
   */
  def removeClusterClient(clusterId: String): Unit = {
    releaseClusterClient(clusterId)
    clusterBootstrapNode.remove(clusterId)
    clusterConfigMap.remove(clusterId)
  }

  /**
   * create client include client && Node && config
   *
   * @param clusterId
   * @param properties
   */
  def createClusterClient(clusterId: String, properties: Properties): Unit = {
    initClusterClientProperties(clusterId, properties)
    if (clusterClients.contains(clusterId)) {
      clusterConfigMap += (clusterId -> properties);
      val brokerList = properties.get(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG).toString.split(",").toList
      clusterBootstrapNode += parserClusterBrokerList(clusterId.toInt, brokerList.asJava)
    }
  }

  /**
   * filter nonexisted cluesteId
   *
   * @param clusterId
   * @param zkClusterConfigMap
   * @param zmClusterNode
   * @return
   */
  def clusterExisted(clusterId: String, zmClusterNode: Map[String, mutable.Buffer[Node]]): Boolean = {
    ClusterConfigManager.getConfigs(clusterId).asScala.nonEmpty || zmClusterNode.contains(clusterId)
  }


  // really init cluster client map from clusterBootstrapNodeFromKM
  // in this case we need
  // remove unused client
  // add new client
  // update config change client
  def loadAllClusterClientFromKM(): Unit = {
    // release unused client
    clusterClients.filter(x => !clusterExisted(x._1, clusterBootstrapNodeFromKM)).foreach(y => {
      removeClusterClient(y._1)
    })
    clusterBootstrapNodeFromKM.foreach(x => {
        try {
          val config = new Properties()
          val bootstrapList = combineClusterBootstrap(x._2)
          config.setProperty(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, bootstrapList)
          if (ClusterConfigManager.getConfigs(x._1).asScala.nonEmpty) {
            // use zk config to replace config properties because prior zk > km
            ClusterConfigManager.getConfigs(x._1).asScala.foreach(key => {
              config.setProperty(key._1, key._2)
            })
          }
          if (!clusterClients.contains(x._1)) {
            createClusterClient(x._1, config)
          } else {
            if (!clusterConfigMap.get(x._1).get.equals(config)) {
              reloadClusterClient(x._1, config)
            } else {
              // close previous connect
              clusterBootstrapNode.get(x._1).get.foreach(node => {
                clusterClients.get(x._1).get.close(node.idString())
              })
            }
          }
        } catch {
          case e: Exception =>
            log.error(s"can not handle cluster ${x._1}, error detail:${e}")
        }
      })
  }

  //parser cluster config item [host:port,host:port]
  def parserClusterBrokerList(clusterId: Integer, brokerList: util.List[String]):(String, mutable.Buffer[Node]) = {
    var nodeIndex = clusterId * nodeListSize - 1
    val value = brokerList.asScala.map(address => new Node({
      nodeIndex += 1
      nodeIndex
    }, address.split(":")(0), address.split(":")(1).toInt))
    val endIndex = value.size
    for (i <- endIndex until nodeListSize) {
      val idx = i % endIndex
      value.asJava.add(new Node({
        nodeIndex += 1
        nodeIndex
      }, value(idx).host(), value(idx).port()))
    }
    (clusterId.toString, value)
  }

  def initExecutorService(): Unit = {
    val gatewayAddressList = HttpUtils.get(GatewayConfigs.getGatewayAddressUrl(urlPrefix), null, 0)

    try {
      val result = Discoverer.parseHttpResponse(gatewayAddressList)
      parseInitClusterJson(result)
      // update per hour

      val runnable:Runnable = () => {
        try {
          syncMaxRequestNum()
          syncAppIdRate()
          syncIpRate()
          syncSpLimit()
          syncCluster()
        } catch {
          case t: Throwable =>
            log.error("Uncaught exception in update cluster scheduled task:", t)
        }
      }
      executorService.scheduleWithFixedDelay(runnable, 0, 1, TimeUnit.MINUTES)
    } catch {
      case e: Exception =>
        log.error("Error occurred when init cluster, http response:{}, error detail:{}", Array(gatewayAddressList.toString, e):_*)
        throw e
    }
  }

  def syncCluster(): Unit = {
    if (time.milliseconds() - lastUpdateTime < clusterUpdateInterval) {
      return
    }

    val params = new util.HashMap[String, String]()
    params.put("versionNumber", clusterVersion.toString)
    // pivot async
    val gatewayAddressList = HttpUtils.get(GatewayConfigs.updateGateWayAddressUrl(urlPrefix), params, 5)
    try {
      val result = Discoverer.parseHttpResponse(gatewayAddressList)
      if (result.isEmpty) {
        log.info("Do not need to update cluster address, current version is {}", clusterVersion)
      } else {
        parseUpdateClusterJson(result)
        log.info("Success to update cluster address, current version is {}", clusterVersion)
      }
      lastUpdateTime = time.milliseconds()
    } catch {
      case t: Throwable =>
        log.error("Error occurred when update cluster, current clusterVersion:{}, http response:{}, error detail:{}", Array(clusterVersion.toString, gatewayAddressList.toString, t):_*)
    }
  }

  def syncMaxRequestNum(): Unit = {
    val params = new util.HashMap[String, String]()
    params.put("versionNumber", maxRequestPerClusterVersion.toString)
    val resultMaxRequestSize = HttpUtils.get(GatewayConfigs.updateGateWayMaxRequestNum(urlPrefix), params, 5)
    try {
      val result = Discoverer.parseHttpResponse(resultMaxRequestSize)
      if (result.isEmpty) {
        log.info("Do not need to update max request num, current version is {}", maxRequestPerClusterVersion)
      } else {
        parseUpdateMaxRequestSize(result)
        log.info("Success to update max request num, current value is {}, current version is {}", maxRequestPerCluster, maxRequestPerClusterVersion)
      }
    } catch {
      case t: Throwable =>
        log.error("Error occurred when update max request num, current clusterVersion:{}, http response:{}, error detail:{}", Array(maxRequestPerClusterVersion.toString, resultMaxRequestSize.toString, t):_*)
    }
  }

  def syncAppIdRate(): Unit = {
    val params = new util.HashMap[String, String]()
    params.put("versionNumber", appIdRateVersion.toString)
    val resultAppIdRate = HttpUtils.get(GatewayConfigs.updateGateWayAppIdRate(urlPrefix), params, 5)
    try {
      val result = Discoverer.parseHttpResponse(resultAppIdRate)
      if (result.isEmpty) {
        log.info("Do not need to update appId rate, current version is {}", appIdRateVersion)
      } else {
        parseUpdateAppIdRate(result)
        log.info("Success to update appId rate, current value is {}, current version is {}", appIdRate, appIdRateVersion)
      }
    } catch {
      case t: Throwable =>
        log.error("Error occurred when update appId rate, current clusterVersion:{}, http response:{}, error detail:{}", Array(appIdRateVersion.toString, resultAppIdRate.toString, t):_*)
    }
  }

  def syncIpRate(): Unit = {
    val params = new util.HashMap[String, String]()
    params.put("versionNumber", ipRateVersion.toString)
    val resultIpRate = HttpUtils.get(GatewayConfigs.updateGateWayIpRate(urlPrefix), params, 5)
    try {
      val result = Discoverer.parseHttpResponse(resultIpRate)
      if (result.isEmpty) {
        log.info("Do not need to update ip rate, current version is {}", ipRateVersion)
      } else {
        parseUpdateIpRate(result)
        log.info("Success to update ip rate, current value is {}, current version is {}", ipRate, ipRateVersion)
      }
    } catch {
      case t: Throwable =>
        log.error("Error occurred when update ip rate, current clusterVersion:{}, http response:{}, error detail:{}", Array(ipRateVersion.toString, resultIpRate.toString, t):_*)
    }
  }

  def syncSpLimit(): Unit = {
    val params = new util.HashMap[String, String]()
    params.put("versionNumber", spRateVersion.toString)
    val resultSpLimit = HttpUtils.get(GatewayConfigs.updateGateWaySpLimit(urlPrefix), params, 5)
    try {
      val result = Discoverer.parseHttpResponse(resultSpLimit)
      if (result.isEmpty) {
        log.info("Do not need to update special limit appId/ip, current version is {}", spRateVersion)
      } else {
        parseUpdateSpLimit(result)
        log.info("Success to update special limit appId/ip, current value is {}, current version is {}", spRateLimiterMap.toString, spRateVersion)
      }
    } catch {
      case t: Throwable =>
        log.error("Error occurred when update special limit appId/ip, current clusterVersion:{}, http response:{}, error detail:{}", Array(spRateVersion.toString, resultSpLimit.toString, t):_*)
    }
  }

  def handleRequest(request: RequestChannel.Request): Unit = {
    log.trace(s"request api ${request.header.apiKey} with user: ${request.session.principal.getName}")
    val (clusterId, appId) = clusterAndAppId(request)

    if (!clusterBootstrapNode.contains(clusterId)) {
      // pivot merge
      val key = Discoverer.getClientKey(appId, request.session.clientAddress.getHostAddress, "_")
      // 60s=1m
      if (logRateLimitCache.get(key).tryAcquire(60)) {
        userLogger.error("ClusterId:{} is not exist, request from user:{} ip:{}", clusterId, appId, request.session.clientAddress.getHostAddress)
      }
      handleError(request, "CLUSTER_ID_ERROR")
      return
    }

    val checkResult = checkRateAccess(appId, request.session.clientAddress.getHostAddress)
    if (!checkResult._1) {
      handleError(request, checkResult._2)
      return
    }

    requestMap.asScala.get(clusterId) match {
      case Some(queue) =>
        if (queue.size >= maxRequestPerCluster) {
          handleError(request, "ACCESS_MAX_QUEUE_SIZE")
          return
        }
        queue.offer(request)
      case None =>
        val queue = new ConcurrentLinkedQueue[RequestChannel.Request]()
        queue.offer(request)
        requestMap.put(clusterId, queue)
        newGauge("DiscovererRequestQueueSize",
          new Gauge[String] {
            def value = {
              requestMap.get(clusterId).size().toString
            }
          }, scala.collection.Map("cluster" -> clusterId)
        )
    }
    wakeup()
  }

  def handleError(request: RequestChannel.Request, error: String): Unit = {
    discoverSensor.record(1)
    logRequest(request, RequestState.Fail, error)
    this.synchronized {
      delayCloseConnectionQueue.add(request)
    }
    request.releaseBuffer()
  }

  private def clusterAndAppId(request: RequestChannel.Request): (String, String) = {
    val (clusterId, appId) = request.session.principal.getName match {
      case newName if newName.startsWith("U") && newName.length > 5 =>
        (KafkaUtils.fromClusterID(newName.substring(1, 5)).toString, newName)
      case oldName if oldName.split("\\.").length == 2 =>
        val splits = oldName.split("\\.")
        (splits(0), splits(1))
      case commonName =>
        ("None", commonName)
    }
    (UserConfigManager.activeCluster(request.context, clusterId), appId)
  }

  def logRequest(request: RequestChannel.Request, state:RequestState, message: String): Unit = {
    val (clusterId, appId) = clusterAndAppId(request)
    val totoltime = (time.nanoseconds() - request.startTimeNanos)/1000000
    requestStatusLogger.info("_undef||appId={}||ip={}||requestType={}||cluster={}||totalTime={}||state={}||msg={}",
      appId, request.session.kafkaSession.getHostAddress, request.header.apiKey().name, clusterId,
      totoltime.toString, state, message)
  }

  def transmitRequest(clusterId: String, request: RequestChannel.Request, node: Node,client:KafkaClient): Unit = {
    log.debug(s" transmit request apiKey = ${request.header.apiKey} send node = ${node}")
    val requestBuffer = request.buffer
    requestBuffer.flip()
    val headerVersion = request.header.headerVersion()
    val requestBuilder = if (headerVersion >= 2) {
      val apiKey = request.header.apiKey
      log.debug(s"Add the header of passthrough principal ${request.context.principal} for request $apiKey")
      val headerStruct = RequestHeader.parse(requestBuffer).toStruct
      val taggedFields = headerStruct.get("_tagged_fields").asInstanceOf[util.TreeMap[Integer, RawTaggedField]]
      taggedFields.put(TAG_FORWARD_CLIENT_HEADER_PRINCIPAL_CODE,
        new RawTaggedField(TAG_FORWARD_CLIENT_HEADER_PRINCIPAL_CODE, EnvelopeUtils.serialize(request.context.principal)))

      val bodyStruct = apiKey.parseRequest(request.header.apiVersion(), requestBuffer)
      new BufferRequest.Builder(RequestUtils.serialize(headerStruct, bodyStruct))
    } else {
      new BufferRequest.Builder(requestBuffer)
    }

    def transmitResponse(response: ClientResponse): Unit = {
      log.debug("Send response to metadata request, connectionId:{}, correlationId:{}, request:{}, responseBody:{}",
        request.context.connectionId, request.header.correlationId().toString, request, response.responseBody())
      val totalTime = time.milliseconds() - request.startTimeNanos;
      if (response.wasDisconnected()) {
        handleError(request, "TRANSMIT_ERROR")
      } else {
        val responseBuffer = response.responseBody().asInstanceOf[BufferResponse].buffer
        requestChannel.sendResponse(new RequestChannel.SendResponse(
          request, new NetworkSend(request.context.connectionId,responseBuffer), None, Option(transmitResponseCallback)))
        logRequest(request, RequestState.Success, "SUCCESS")
        def transmitResponseCallback(send: Send): Unit = {
          memoryPool.release(responseBuffer)
        }
      }
    }

    val callback = new RequestCompletionHandler() {
      def onComplete(response: ClientResponse) {
        request.releaseBuffer()
        transmitResponse(response)
      }
    }

    val clientRequest = new ClientRequest(node.idString(), requestBuilder,
      request.header.correlationId(), request.header.clientId(), time.milliseconds(), true, transmitTimeoutMs, callback)
    log.debug("Transmit {} request, connectionId:{}, correlationId:{}, destination:{}, originalRequest:{}, transmitRequest:{}",
      request.header.apiKey(), request.context.connectionId, request.header.correlationId().toString, node, request, clientRequest)

    client.send(clientRequest, time.milliseconds())
  }

  def wakeup(): Unit = {
    clusterClients.foreach(kv =>{
      kv._2.wakeup()
    })
  }

  def close(): Unit = {
    running = false
    releaseAllClient
  }

  // get random node for clusterId which client need to connected
  def getNodeByClusterId(clusterId: String): Node = {
    clusterBootstrapNode.get(clusterId) match {
      case Some(nodeList) =>
        getReadyNode(clusterId, nodeList)
      case None =>
        log.error("ClusterId:{} is not exist", clusterId)
        Node.noNode()
    }
  }

  def getNodeList(nodeList: util.Map[Integer, util.List[String]]): scala.collection.mutable.Map[String, mutable.Buffer[Node]] = {
    val nodeMap = nodeList.asScala.map(kv => {
      var nodeIndex = kv._1 * nodeListSize - 1
      val value = kv._2.asScala.map(address => new Node({
        nodeIndex += 1
        nodeIndex
      }, address.split(":")(0), address.split(":")(1).toInt))
      val endIndex = value.size
      for (i <- endIndex until nodeListSize) {
        val idx = i % endIndex
        value.asJava.add(new Node({
          nodeIndex += 1
          nodeIndex
        }, value(idx).host(), value(idx).port()))
      }
      (kv._1.toString, value)
    })
    nodeMap
  }

  // select node to connect
  // if node is ready for client just it
  // else random node for client to connect by ready && isReady
  def getReadyNode(clusterId: String, nodeList: mutable.Buffer[Node]): Node = {
    val length = nodeList.length
    val client = clusterClients.get(clusterId).getOrElse(null)
    if (client == null) {
      log.warn(s" can not find client for clusterId ${clusterId}, you need config cluster");
      return Node.noNode()
    }
    val random = Random.nextInt(length)
    for (index <- 0 until length) {
      val idx = (random + index) % length
      if (client.isReady(nodeList(idx), time.milliseconds())) {
        log.debug("choose node:{}", nodeList(idx).toString)
        return nodeList(idx)
      }
      client.ready(nodeList(idx), time.milliseconds())
    }

    // 在随机选取一个节点连接，防止一段时间没有到某集群的连接后，服务发现与bootstrap连接全部断开，此时有一个新的连接请求服务发现
    if (client.isReady(nodeList(random), time.milliseconds())) {
      log.debug("choose node:{}", nodeList(random).toString)
      return nodeList(random)
    }

    Node.noNode()
  }

  def parseUpdateMaxRequestSize(jsonString: String): Unit = {
    val jsonObject = JSON.parseObject(jsonString)
    if (!jsonObject.containsKey("version")
      || !StringUtils.isNumeric(jsonObject.getString("version"))
      || !jsonObject.containsKey("data")) {
      val errorMsg = "The response of update max request size is error, response detail:%s".format(jsonString)
      throw new Exception(errorMsg)
    }
    val returnVersion = jsonObject.getLong("version")
    val maxRequestSize = jsonObject.getString("data")
    if (maxRequestSize == null || maxRequestSize.isEmpty) {
      maxRequestPerClusterVersion = returnVersion
      log.error("Max request size version rollback to version {}", maxRequestPerClusterVersion)
      return
    }
    if (!StringUtils.isNumeric(maxRequestSize)) {
      val errorMsg = "Max request size should be Numeric, actually value:%s".format(maxRequestSize)
      throw new Exception(errorMsg)
    }
    maxRequestPerCluster = Integer.parseInt(maxRequestSize)
    maxRequestPerClusterVersion = returnVersion
  }

  def parseUpdateAppIdRate(jsonString: String): Unit = {
    val jsonObject = JSON.parseObject(jsonString)
    if (!jsonObject.containsKey("version")
      || !StringUtils.isNumeric(jsonObject.getString("version"))
      || !jsonObject.containsKey("data")) {
      val errorMsg = "The response of update appId rate is error, response detail:%s".format(jsonString)
      throw new Exception(errorMsg)
    }
    val returnVersion = jsonObject.getLong("version")
    val appIdRate = jsonObject.getString("data")
    if (appIdRate == null || appIdRate.isEmpty) {
      appIdRateVersion = returnVersion
      log.error("AppId rate version rollback to version {}", appIdRateVersion)
      return
    }
    if (!StringUtils.isNumeric(appIdRate)) {
      val errorMsg = "AppId Rate should be Numeric, actually value:%s".format(appIdRate)
      throw new Exception(errorMsg)
    }
    this.appIdRate = Integer.parseInt(appIdRate)
    appIdRateVersion = returnVersion
    appIdRateLimiterCache.invalidateAll()
  }

  def parseUpdateIpRate(jsonString: String): Unit = {
    val jsonObject = JSON.parseObject(jsonString)
    if (!jsonObject.containsKey("version")
      || !StringUtils.isNumeric(jsonObject.getString("version"))
      || !jsonObject.containsKey("data")) {
      val errorMsg = "The response of update ip rate is error, response detail:%s".format(jsonString)
      throw new Exception(errorMsg)
    }
    val returnVersion = jsonObject.getLong("version")
    val ipRate = jsonObject.getString("data")
    if (ipRate == null || ipRate.isEmpty) {
      ipRateVersion = returnVersion
      log.error("Ip rate version rollback to version {}", ipRateVersion)
      return
    }
    if (!StringUtils.isNumeric(ipRate)) {
      val errorMsg = "Ip Rate should be Numeric, actually value:%s".format(ipRate)
      throw new Exception(errorMsg)
    }
    this.ipRate = Integer.parseInt(ipRate)
    ipRateVersion = returnVersion
    ipRateLimiterCache.invalidateAll()
  }

  def parseUpdateSpLimit(jsonString: String): Unit = {
    val jsonObject = JSON.parseObject(jsonString)
    if (!jsonObject.containsKey("version")
      || !StringUtils.isNumeric(jsonObject.getString("version"))
      || !jsonObject.containsKey("data")) {
      val errorMsg = "The response of update special limit appId/ip is error, response detail:%s".format(jsonString)
      throw new Exception(errorMsg)
    }
    val returnVersion = jsonObject.getLong("version")
    val spLimitStr = jsonObject.getString("data")
    if ((spLimitStr == null || spLimitStr.isEmpty) && spRateVersion > returnVersion) {
      spRateVersion = returnVersion
      log.error("Sp limit version rollback to version {}", spRateVersion)
      return
    }
    this.spRateLimiterMap = Discoverer.parseSpLimitStr(spLimitStr)
    spRateVersion = returnVersion
  }

  def parseUpdateClusterJson(jsonString: String): Unit = {
    val jsonObject = JSON.parseObject(jsonString)
    if (!jsonObject.containsKey("version")
      || !StringUtils.isNumeric(jsonObject.getString("version"))
      || !jsonObject.containsKey("data")) {
      val errorMsg = "The response of update cluster is error, response detail:%s".format(jsonString)
      throw new Exception(errorMsg)
    }
    val returnVersion = jsonObject.getLong("version")
    val clusterAddress = jsonObject.getString("data")
    if (clusterAddress == null || clusterAddress.isEmpty) {
      clusterVersion = returnVersion
      log.error("Cluster version rollback to version {}", clusterVersion)
      return
    }
    parseInitClusterJson(jsonObject.getString("data"))
    clusterVersion = returnVersion
  }

  def parseInitClusterJson(jsonString: String): Unit = {
    //val str = "{\"95\":[\"10.179.16.246:8093\"]}"
    if (jsonString.isEmpty) {
      log.info("Do not need to update cluster address, current version is {}", clusterVersion)
      return
    }
    val data = JsonUtils.jsonString2MapForCluster(jsonString)
    log.debug(s"cluster json data ${jsonString}")
    if (!JsonUtils.checkClusterAddress(data)) {
      val errorMsg = "The response init cluster is error, response detail:%s".format(jsonString)
      throw new Exception(errorMsg)
    }
    lock.writeLock().lock()
    try {
      // 需要过滤掉 不存在的 clusterid
      clusterBootstrapNodeFromKM.clear()
      data.asScala.foreach(item => {
        try {
          clusterBootstrapNodeFromKM += parserClusterBrokerList(item._1, item._2)
        } catch {
          case e: Exception =>
            log.error(s"parser Cluster broker from ${data} failed error detail:${e}")
        }
      })
    } finally {
      reloadAllClusterClientFromKM
      lock.writeLock().unlock()
    }
  }

  def checkRateAccess(appId: String, ip: String): (Boolean, String) = {
    // special appId limit
    if (spRateLimiterMap.containsKey(appId) && spRateLimiterCache.get(appId).getAndIncrement() >= spRateLimiterMap.getOrDefault(appId, Integer.MAX_VALUE)) {
      log.warn("AppId:%s has reached request upper limit:%s/s, ip:%s".format(appId, spRateLimiterMap.getOrDefault(appId, Integer.MAX_VALUE), ip))
      return (false, "ACCESS_SP_APP_ID_LIMIT")
    }

    // special ip limit
    if (spRateLimiterMap.containsKey(ip) && spRateLimiterCache.get(ip).getAndIncrement() >= spRateLimiterMap.getOrDefault(ip, Integer.MAX_VALUE)) {
      log.warn("Ip:%s has reached request upper limit:%s/s, appId:%s".format(ip, spRateLimiterMap.getOrDefault(ip, Integer.MAX_VALUE), appId))
      return (false, "ACCESS_SP_IP_LIMIT")
    }

    // appId limit
    if (!appIdRateLimiterCache.get(appId).tryAcquire()) {
      log.warn("AppId:%s has reached request upper limit:%s/s, ip:%s".format(appId, appIdRate, ip))
      return (false, "ACCESS_APP_ID_LIMIT")
    }

    // ip limit
    if (!ipRateLimiterCache.get(ip).tryAcquire()) {
      log.warn("Ip:%s has reached request upper limit:%s/s, appId:%s".format(ip, ipRate, appId))
      return (false, "ACCESS_IP_LIMIT")
    }

    // no limit
    (true, "")
  }

  def getClusterBootstrapNode(): scala.collection.mutable.Map[String, mutable.Buffer[Node]] = {
    clusterBootstrapNode
  }

  def sendResponse(request: RequestChannel.Request,
                           createResponse: Int => AbstractResponse,
                           onComplete: Option[Send => Unit] = None): Unit = {
    sendResponse(request, Some(createResponse(0)), onComplete)
  }

  private def sendResponse(request: RequestChannel.Request,
                           responseOpt: Option[AbstractResponse],
                           onComplete: Option[Send => Unit]): Unit = {
    val response = responseOpt match {
      case Some(response) =>
        val responseSend = request.context.buildResponse(response)
        val responseString =
          if (RequestChannel.isRequestLoggingEnabled) Some(response.toString(request.context.apiVersion))
          else None
        new RequestChannel.SendResponse(request, responseSend, responseString, onComplete)
      case None =>
        new RequestChannel.NoOpResponse(request)
    }
    sendResponse(response)
  }

  private def sendResponse(response: RequestChannel.Response): Unit = {
    requestChannel.sendResponse(response)
  }
}

class ThrottledRequest(val time: Time, val throttleTimeMs: Int, val request: RequestChannel.Request) extends Delayed {
  val endTime = time.milliseconds + throttleTimeMs

  override def getDelay(unit: TimeUnit): Long = {
    unit.convert(endTime - time.milliseconds, TimeUnit.MILLISECONDS)
  }

  override def compareTo(d: Delayed): Int = {
    val other = d.asInstanceOf[ThrottledRequest]
    if (this.endTime < other.endTime) -1
    else if (this.endTime > other.endTime) 1
    else 0
  }
}

class ThrottledCloseConnection(val time: Time, val throttleTimeMs: Int, val processor: Int, val request: RequestChannel.Request) extends Delayed {
  val endTime = time.milliseconds + throttleTimeMs

  override def getDelay(unit: TimeUnit): Long = {
    unit.convert(endTime - time.milliseconds, TimeUnit.MILLISECONDS)
  }

  override def compareTo(d: Delayed): Int = {
    val other = d.asInstanceOf[ThrottledCloseConnection]
    if (this.endTime < other.endTime) -1
    else if (this.endTime > other.endTime) 1
    else 0
  }
}

object Discoverer {
  val formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

  def getClientKey(userName: String, ip: String, split: String): String = {
    "%s%s%s".format(userName, split, ip)
  }

  def parseHttpResponse(responseCommonResult: ResponseCommonResult[_]): String = {
    responseCommonResult.getCode match {
      case ResponseCommonResult.SUCCESS_STATUS =>
        val result = JsonUtils.string2ResponseCommonResult(responseCommonResult.getData.asInstanceOf[String])
        result.getCode match {
          case ResponseCommonResult.SUCCESS_STATUS =>
            if (result.getData == null) {
              ""
            } else {
              result.getData.asInstanceOf[String]
            }
          case ResponseCommonResult.FAILED_STATUS =>
            val errMsg = result.getData.asInstanceOf[String]
            throw new Exception("Failed to parse http response, gateway result detail:%s".format(errMsg))
        }
      case ResponseCommonResult.FAILED_STATUS =>
        throw new Exception("Failed to parse http response, gateway result detail:%s".format(responseCommonResult.getData.asInstanceOf[String]))
    }
  }

  def parseSpLimitStr(spLimitStr: String): ConcurrentHashMap[String, Integer] = {
    // spLimit format: id#limit,id#limit,...  id=appId/ip  eg: appId_00000001#55,192.168.3.4#66
    val resultMap = new ConcurrentHashMap[String, Integer]()
    Splitter.on(",").omitEmptyStrings().trimResults().splitToList(spLimitStr).asScala.map(kv => kv.split("#")).filter(array => array.size == 2)
      .map{ ele =>
        if (StringUtils.isNumeric(ele(1))) {
          resultMap.put(ele(0), Integer.parseInt(ele(1)))
        }
      }
    resultMap
  }


}

object RequestState extends Enumeration {
  type RequestState = Value
  val Success = Value("Success")
  val Fail = Value("Fail")
}













