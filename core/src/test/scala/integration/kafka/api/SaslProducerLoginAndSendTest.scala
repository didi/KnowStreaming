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

package kafka.api

import java.util.Properties
import java.util.concurrent.{Executors, TimeUnit, TimeoutException}

import kafka.integration.KafkaServerTestHarness
import kafka.server.KafkaConfig
import kafka.utils.TestUtils
import org.apache.kafka.clients.producer._
import org.apache.kafka.common.MetricName
import org.apache.kafka.common.config.SaslConfigs
import org.apache.kafka.common.security.JaasUtils
import org.apache.kafka.common.security.auth.SecurityProtocol
import org.junit.Assert._
import org.junit.{After, Before, Ignore}

import scala.collection.mutable.Buffer

class SaslProducerLoginAndSendTest extends KafkaServerTestHarness {

  override protected def securityProtocol = SecurityProtocol.SASL_PLAINTEXT
  protected val kafkaClientSaslMechanism = "PLAIN"
  protected val kafkaServerSaslMechanisms = List("PLAIN")
  override protected val serverSaslProperties = Some(kafkaServerSaslProperties(kafkaServerSaslMechanisms, kafkaClientSaslMechanism))
  override protected val clientSaslProperties = Some(kafkaClientSaslProperties(kafkaClientSaslMechanism))


  def kafkaServerSaslProperties(serverSaslMechanisms: Seq[String], interBrokerSaslMechanism: String) = {
    val props = new Properties
    props.put(KafkaConfig.SaslMechanismInterBrokerProtocolProp, interBrokerSaslMechanism)
    props.put(SaslConfigs.SASL_ENABLED_MECHANISMS, serverSaslMechanisms.mkString(","))
    System.setProperty(JaasUtils.JAVA_LOGIN_CONFIG_PARAM, "../config/kafka_server_jaas.conf")
    props
  }

  def kafkaClientSaslProperties(clientSaslMechanism: String, dynamicJaasConfig: Boolean = false) = {
    val props = new Properties
    props.put(SaslConfigs.SASL_MECHANISM, clientSaslMechanism)
    val format = "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"%s.%s\" password=\"%s\";"
    val jaas_config = String.format(format, "0", "kafka", "12345")
    props.put(SaslConfigs.SASL_JAAS_CONFIG, jaas_config)
    props
  }

  def errorKafkaClientSaslProperties(clientSaslMechanism: String, dynamicJaasConfig: Boolean = false) = {
    val props = new Properties
    props.put(SaslConfigs.SASL_MECHANISM, clientSaslMechanism)
    val format = "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"%s.%s\" password=\"%s\";"
    val jaas_config = String.format(format, "0", "kafka1", "12345")
    props.put(SaslConfigs.SASL_JAAS_CONFIG, jaas_config)
    props
  }

  def generateConfigs = {
    val overridingProps = new Properties()
    val numServers = 1
    overridingProps.put(KafkaConfig.NumPartitionsProp, 4.toString)
    //overridingProps.put(KafkaConfig.MaxConnectionsPerSecondProp, 2)
    overridingProps.put(KafkaConfig.NumNetworkThreadsProp, 1)
    TestUtils.createBrokerConfigs(numServers, zkConnect, false, interBrokerSecurityProtocol = Some(securityProtocol),
      trustStoreFile = trustStoreFile, saslProperties = serverSaslProperties).map(KafkaConfig.fromProps(_, overridingProps))
  }

  private val producers = Buffer[KafkaProducer[Array[Byte], Array[Byte]]]()

  private val numRecords = 100

  @Before
  override def setUp() {
    super.setUp()
  }

  @After
  override def tearDown() {
    // Ensure that all producers are closed since unclosed producers impact other tests when Kafka server ports are reused
    producers.foreach(_.close())

    super.tearDown()
  }

  protected def createProducer1(brokerList: String, retries: Int = 0, lingerMs: Int = 0, props: Option[Properties] = None): KafkaProducer[Array[Byte],Array[Byte]] = {
    val producer = TestUtils.createProducer(brokerList,
      securityProtocol = securityProtocol,
      trustStoreFile = trustStoreFile,
      saslProperties = clientSaslProperties,
      lingerMs = lingerMs,
      deliveryTimeoutMs = 2 * 60 * 1000,
      maxBlockMs = 60 * 1000L,
      bufferSize = 1024L * 1024L)
    registerProducer(producer)
  }

  protected def createProducer2(brokerList: String, retries: Int = 0, lingerMs: Long = 0, props: Option[Properties] = None): KafkaProducer[Array[Byte],Array[Byte]] = {
    val producer = TestUtils.createProducer(brokerList,
      securityProtocol = securityProtocol,
      trustStoreFile = trustStoreFile,
      saslProperties = Option(errorKafkaClientSaslProperties(kafkaClientSaslMechanism)),
      deliveryTimeoutMs = 2 * 60 * 1000,
      maxBlockMs = 60 * 1000L,
      bufferSize = 1024L * 1024L)
    registerProducer(producer)
  }

  protected def registerProducer(producer: KafkaProducer[Array[Byte], Array[Byte]]): KafkaProducer[Array[Byte], Array[Byte]] = {
    producers += producer
    producer
  }

  protected def sendAndVerify(topic:String, producer: KafkaProducer[Array[Byte], Array[Byte]],
                              numRecords: Int = numRecords,
                              timeoutMs: Long = 20000L) {
    val partition = 0
    try {
      TestUtils.createTopic(zkClient , topic, 1, 1, servers)
      var recordMetadata:RecordMetadata = null;
      var success = 0;
      val endTime = System.currentTimeMillis() + timeoutMs*2;
      for (i <- 0 to numRecords) {
        if (endTime < System.currentTimeMillis())
          throw new TimeoutException("test timeout expect %s ms".format(timeoutMs));
        val record = new ProducerRecord(topic, partition, s"key$i".getBytes, s"value$i".getBytes)
        recordMetadata = producer.send(record, (metadata: RecordMetadata, exception: Exception) => {
          if (exception != null) {
            System.out.println(exception)
          } else {
            success = success + 1;
          }
          assertEquals(topic, metadata.topic)
          assertEquals(partition, metadata.partition)
          assertEquals(i, metadata.offset)
        }).get()
      }
      producer.close(1000, TimeUnit.MILLISECONDS)
      assertEquals(numRecords, success-1)
    } catch {
      case t: Throwable =>
        error("Uncaught exception: ", t)
        throw t;
    }
  }

  @Ignore
  def testSendWithFailedLogin() {
      val runnable: Runnable = () => {
      Thread.sleep(1000)
      try {
        val props = new Properties
        props.setProperty(ProducerConfig.RECONNECT_BACKOFF_MS_CONFIG, 10.toString)
        val producer = createProducer2(brokerList = brokerList, lingerMs = Long.MaxValue, props = Option(props))
        sendAndVerify("topic_0", producer, 300, 5000)
      } catch {
        case t: Throwable => error("Uncaught exception: ", t)
      }
    }

    val produceExecutors = Executors.newFixedThreadPool(1)
    produceExecutors.execute(runnable)

    val producer = createProducer1(brokerList = brokerList, lingerMs = 10)
    val time = System.currentTimeMillis()
    val timeout = 10000;
    sendAndVerify("topic_1", producer, 500, timeout)
    assertTrue("cost time: %d, expect time %d".format(System.currentTimeMillis() - time, timeout),
      System.currentTimeMillis() - time < timeout)
  }

  @Ignore
  def testFailedLogin() {
    val runnable: Runnable = () => {
      try {
        val props = new Properties
        props.setProperty(ProducerConfig.RECONNECT_BACKOFF_MS_CONFIG, 10.toString)
        val producer = createProducer2(brokerList = brokerList, lingerMs = Long.MaxValue, props = Option(props))
        sendAndVerify("topic_0", producer, 300, 5000)
      } catch {
        case t: Throwable => error("Uncaught exception: ", t)
      }
    }
    val produceExecutors = Executors.newFixedThreadPool(1)
    produceExecutors.execute(runnable)
    Thread.sleep(100000);
    assert(connUserMetric.value() < 2.5);
    assert(connIpMetric.value() < 2.5);
  }

  private def throttleMetricName(appId:String, ip:String): MetricName = {
    if (appId.nonEmpty) {
      servers.head.metrics.metricName(
        "user-rate", "ConnectionQuotas",
        "Tracking connect-rate per user",
        "user", appId)
    } else {
      servers.head.metrics.metricName(
        "ip-rate", "ConnectionQuotas",
        "Tracking connect-rate per ip",
        "ip", ip)
    }
  }
  private def connUserMetric = servers.head.metrics.metrics.get(throttleMetricName("kafka1", ""))
  private def connIpMetric = servers.head.metrics.metrics.get(throttleMetricName("", "127.0.0.1"))
}
