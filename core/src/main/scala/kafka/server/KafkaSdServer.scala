/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kafka.server

import com.didichuxing.datachannel.kafka.jmx.MetricsLogReporter

import com.didichuxing.datachannel.kafka.util.HttpUtils
import kafka.controller.KafkaController
import kafka.coordinator.group.GroupCoordinator
import kafka.coordinator.transaction.TransactionCoordinator
import kafka.log.LogManager
import kafka.metrics.{KafkaMetricsGroup, KafkaMetricsReporter}
import kafka.network.SocketServer
import kafka.security.CredentialProvider
import kafka.utils._
import kafka.zk.KafkaZkClient
import org.apache.kafka.common.ClusterResource
import org.apache.kafka.common.internals.ClusterResourceListeners
import org.apache.kafka.common.metrics._
import org.apache.kafka.common.network.ListenerName
import org.apache.kafka.common.security.JaasUtils
import org.apache.kafka.common.security.scram.internals.ScramMechanism
import org.apache.kafka.common.security.token.delegation.internals.DelegationTokenCache
import org.apache.kafka.common.utils.{AppInfoParser, KafkaThread, LogContext, Time}
import org.apache.kafka.server.authorizer.Authorizer
import org.apache.zookeeper.client.ZKClientConfig

import java.io.File
import java.util
import java.util.concurrent._
import java.util.concurrent.atomic.{AtomicBoolean, AtomicInteger}
import scala.collection.JavaConverters._
import scala.collection.{Map, Seq}


/**
 * Represents the lifecycle of a single Kafka broker. Handles all functionality required
 * to start up and shutdown a single Kafka node.
 */
class KafkaSdServer(val config: KafkaConfig, time: Time = Time.SYSTEM, threadNamePrefix: Option[String] = None,
                    kafkaMetricsReporters: Seq[KafkaMetricsReporter] = List()) extends Logging with KafkaMetricsGroup {
  private val startupComplete = new AtomicBoolean(false)
  private val isShuttingDown = new AtomicBoolean(false)
  private val isStartingUp = new AtomicBoolean(false)

  private var shutdownLatch = new CountDownLatch(1)

  private val jmxPrefix: String = "kafka.server"

  private var logContext: LogContext = null

  var metrics: Metrics = null

  val brokerState: BrokerState = new BrokerState

  var discoverer: Discoverer = null
  var requestThread: KafkaThread = null

  var dataPlaneRequestProcessor: KafkaSdApis = null
  var controlPlaneRequestProcessor: KafkaApis = null

  var authorizer: Option[Authorizer] = None
  var socketServer: SocketServer = null
  var dataPlaneRequestHandlerPool: KafkaSdRequestHandlerPool = null
  var controlPlaneRequestHandlerPool: KafkaRequestHandlerPool = null

  var logDirFailureChannel: LogDirFailureChannel = null
  var logManager: LogManager = null

  var replicaManager: ReplicaManager = null
  var adminManager: AdminManager = null
  var tokenManager: DelegationTokenManager = null

  var dynamicConfigHandlers: Map[String, ConfigHandler] = null
  var dynamicConfigManager: DynamicConfigManager = null
  var credentialProvider: CredentialProvider = null
  var tokenCache: DelegationTokenCache = null

  var groupCoordinator: GroupCoordinator = null

  var transactionCoordinator: TransactionCoordinator = null

  var kafkaController: KafkaController = null

  var kafkaScheduler: KafkaScheduler = null
  var kafkaGatewayScheduler: KafkaScheduler = null

  var metadataCache: MetadataCache = null
  var quotaManagers: QuotaFactory.QuotaManagers = null

  val zkClientConfig: ZKClientConfig = KafkaServer.zkClientConfigFromKafkaConfig(config).getOrElse(new ZKClientConfig())
  private var _zkClient: KafkaZkClient = null
  val correlationId: AtomicInteger = new AtomicInteger(0)
  val brokerMetaPropsFile = "meta.properties"
  val brokerMetadataCheckpoints = config.logDirs.map(logDir => (logDir, new BrokerMetadataCheckpoint(new File(logDir + File.separator + brokerMetaPropsFile)))).toMap

  private var _clusterId: String = null
  private var _brokerTopicStats: BrokerTopicStats = null

  def clusterId: String = _clusterId

  // Visible for testing
  private[kafka] def zkClient = _zkClient

  private[kafka] def brokerTopicStats = _brokerTopicStats

  newGauge("BrokerState", () => brokerState.currentState)
  newGauge("ClusterId", () => clusterId)
  newGauge("yammer-metrics-count", () => com.yammer.metrics.Metrics.defaultRegistry.allMetrics.size)

  /**
   * Start up API for bringing up a single instance of the Kafka server.
   * Instantiates the LogManager, the SocketServer and the request handlers - KafkaRequestHandlers
   */
  def startup(): Unit = {
    try {
      info("starting")

      if (isShuttingDown.get)
        throw new IllegalStateException("Kafka server is still shutting down, cannot re-start!")

      if (startupComplete.get)
        return

      val canStartup = isStartingUp.compareAndSet(false, true)
      if (canStartup) {
        brokerState.newState(Starting)

        /* start scheduler */
        kafkaScheduler = new KafkaScheduler(config.backgroundThreads)
        kafkaScheduler.startup()

        /* create and configure metrics */
        val reporters = new util.ArrayList[MetricsReporter]
        reporters.add(new JmxReporter(jmxPrefix))
        reporters.add(new MetricsLogReporter)
        val metricConfig = KafkaServer.metricConfig(config)
        metrics = new Metrics(metricConfig, reporters, time, true)


        var gatewayUrl = config.originals().getOrDefault("gateway.address", "").asInstanceOf[String]
        if (gatewayUrl.isEmpty) gatewayUrl = config.gatewayUrl
        val updateInterval = config.gatewayUpdateInterval
        val maxRequestPerCluster = config.gatewayMaxRequestPerCluster
        val maxNodeConnection = config.gatewayMaxNodeConnection

        tokenCache = new DelegationTokenCache(ScramMechanism.mechanismNames)
        credentialProvider = new CredentialProvider(ScramMechanism.mechanismNames, tokenCache)
        /* setup zookeeper */


        // Create and start the socket server acceptor threads so that the bound port is known.
        // Delay starting processors until the end of the initialization sequence to ensure
        // that credentials have been loaded before processing authentications.
        socketServer = new SocketServer(config, metrics, time, credentialProvider)
        socketServer.startup(startupProcessors = false)

        initZkClient(time)


        discoverer = new Discoverer(config,socketServer.dataPlaneRequestChannel, socketServer.memoryPool, metrics, 3,
          time, config.transmitTimeoutMs, gatewayUrl, updateInterval, maxRequestPerCluster, maxNodeConnection)
        requestThread = new KafkaThread("kafka-server-discoverer-thread", discoverer, true)

        // Create the config manager. start listening to notifications
        /* start dynamic config manager */
        dynamicConfigHandlers = Map[String, ConfigHandler](ConfigType.User -> new GatewayUserConfigHandler(),
          ConfigType.HACluster -> new ClusterConfigHandler(discoverer))

        dynamicConfigManager = new DynamicConfigManager(zkClient, dynamicConfigHandlers)
        dynamicConfigManager.startup()
        /* start processing requests */
        dataPlaneRequestProcessor = new KafkaSdApis(socketServer.dataPlaneRequestChannel, config.brokerId,
          config, metrics, discoverer, time)

        dataPlaneRequestHandlerPool = new KafkaSdRequestHandlerPool(config.brokerId, socketServer.dataPlaneRequestChannel, dataPlaneRequestProcessor, time,
          config.numIoThreads, s"${SocketServer.DataPlaneMetricPrefix}RequestHandlerAvgIdlePercent", SocketServer.DataPlaneThreadPrefix)

        Mx4jLoader.maybeLoad()

        socketServer.startDataPlaneProcessors()
        requestThread.start()

        brokerState.newState(RunningAsBroker)
        shutdownLatch = new CountDownLatch(1)
        startupComplete.set(true)
        isStartingUp.set(false)
        HttpUtils.setFastFailed(false);
        AppInfoParser.registerAppInfo(jmxPrefix, config.brokerId.toString, metrics, time.milliseconds())
        info("started")
      }
    }
    catch {
      case e: Throwable =>
        fatal("Fatal error during KafkaServer startup. Prepare to shutdown", e)
        isStartingUp.set(false)
        shutdown()
        throw e
    }
  }

  private[server] def notifyClusterListeners(clusterListeners: Seq[AnyRef]): Unit = {
    val clusterResourceListeners = new ClusterResourceListeners
    clusterResourceListeners.maybeAddAll(clusterListeners.asJava)
    clusterResourceListeners.onUpdate(new ClusterResource(clusterId))
  }

  /**
   * Shutdown API for shutting down a single instance of the Kafka server.
   * Shuts down the LogManager, the SocketServer and the log cleaner scheduler thread
   */
  def shutdown(): Unit = {
    try {
      info("shutting down")

      if (isStartingUp.get)
        throw new IllegalStateException("Kafka server is still starting up, cannot shut down!")

      // To ensure correct behavior under concurrent calls, we need to check `shutdownLatch` first since it gets updated
      // last in the `if` block. If the order is reversed, we could shutdown twice or leave `isShuttingDown` set to
      // `true` at the end of this method.
      if (shutdownLatch.getCount > 0 && isShuttingDown.compareAndSet(false, true)) {
        brokerState.newState(BrokerShuttingDown)

        // Stop socket server to stop accepting any more connections and requests.
        // Socket server will be shutdown towards the end of the sequence.
        if (socketServer != null)
          CoreUtils.swallow(socketServer.stopProcessingRequests(), this)
        if (kafkaScheduler != null)
          CoreUtils.swallow(kafkaScheduler.shutdown(), this)

        if (dataPlaneRequestProcessor != null)
          CoreUtils.swallow(dataPlaneRequestProcessor.close(), this)

        // Even though socket server is stopped much earlier, controller can generate
        // response for controlled shutdown request. Shutdown server at the end to
        // avoid any failures (e.g. when metrics are recorded)
        if (socketServer != null)
          CoreUtils.swallow(socketServer.shutdown(), this)
        if (metrics != null)
          CoreUtils.swallow(metrics.close(), this)

        brokerState.newState(NotRunning)

        startupComplete.set(false)
        isShuttingDown.set(false)
        CoreUtils.swallow(AppInfoParser.unregisterAppInfo(jmxPrefix, config.brokerId.toString, metrics), this)
        shutdownLatch.countDown()
        info("shut down completed")
      }
    }
    catch {
      case e: Throwable =>
        fatal("Fatal error during KafkaServer shutdown.", e)
        isShuttingDown.set(false)
        throw e
    }
  }

  /**
   * After calling shutdown(), use this API to wait until the shutdown is complete
   */
  def awaitShutdown(): Unit = shutdownLatch.await()

  def boundPort(listenerName: ListenerName): Int = socketServer.boundPort(listenerName)

  private def initZkClient(time: Time): Unit = {
    info(s"Connecting to zookeeper on ${config.zkConnect}")

    def createZkClient(zkConnect: String, isSecure: Boolean) = {
      KafkaZkClient(zkConnect, isSecure, config.zkSessionTimeoutMs, config.zkConnectionTimeoutMs,
        config.zkMaxInFlightRequests, time, name = Some("Kafka server"), zkClientConfig = Some(zkClientConfig))
    }

    val chrootIndex = config.zkConnect.indexOf("/")
    val chrootOption = {
      if (chrootIndex > 0) Some(config.zkConnect.substring(chrootIndex))
      else None
    }

    val secureAclsEnabled = config.zkEnableSecureAcls
    val isZkSecurityEnabled = JaasUtils.isZkSaslEnabled() || KafkaConfig.zkTlsClientAuthEnabled(zkClientConfig)

    if (secureAclsEnabled && !isZkSecurityEnabled)
      throw new java.lang.SecurityException(s"${KafkaConfig.ZkEnableSecureAclsProp} is true, but ZooKeeper client TLS configuration identifying at least $KafkaConfig.ZkSslClientEnableProp, $KafkaConfig.ZkClientCnxnSocketProp, and $KafkaConfig.ZkSslKeyStoreLocationProp was not present and the " +
        s"verification of the JAAS login file failed ${JaasUtils.zkSecuritySysConfigString}")

    // make sure chroot path exists
    chrootOption.foreach { chroot =>
      val zkConnForChrootCreation = config.zkConnect.substring(0, chrootIndex)
      val zkClient = createZkClient(zkConnForChrootCreation, secureAclsEnabled)
      zkClient.makeSurePersistentPathExists(chroot)
      info(s"Created zookeeper path $chroot")
      zkClient.close()
    }

    _zkClient = createZkClient(config.zkConnect, secureAclsEnabled)
    _zkClient.createTopLevelPaths()
  }
}
