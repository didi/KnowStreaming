/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kafka.server

import java.io.{File, IOException}
import java.nio.ByteBuffer
import java.nio.channels.GatheringByteChannel
import java.util
import java.util.{Optional, Properties}
import java.util.concurrent.atomic.{AtomicBoolean, AtomicLong, AtomicReference}
import java.util.concurrent.{Semaphore, TimeUnit}

import com.didichuxing.datachannel.kafka.server.{DiskLoadProtector, OSUtil, TopicPartitionAndReplica}
import com.yammer.metrics.core.Gauge
import kafka.log.{AppendOrigin, LogConfig}
import kafka.metrics.KafkaMetricsGroup
import kafka.server.QuotaFactory.UnboundedQuota
import kafka.utils.TestUtils.createBroker
import kafka.utils._
import kafka.zk.KafkaZkClient
import org.apache.kafka.common.message.LeaderAndIsrRequestData.LeaderAndIsrPartitionState
import org.apache.kafka.common.metrics.Metrics
import org.apache.kafka.common.protocol.{ApiKeys, Errors}
import org.apache.kafka.common.record.{MemoryRecords, Record}
import org.apache.kafka.common.requests.FetchRequest.PartitionData
import org.apache.kafka.common.requests.LeaderAndIsrRequest
import org.apache.kafka.common.requests.ProduceResponse.PartitionResponse
import org.apache.kafka.common.utils.Time
import org.apache.kafka.common.{IsolationLevel, Node, TopicPartition}
import org.apache.zookeeper.data.Stat
import org.easymock.EasyMock
import org.junit.Assert.assertEquals
import org.junit.{After, Before, Ignore, Test}

import scala.collection.JavaConverters._
import scala.collection.{Map, Seq, mutable}
import scala.util.Random

class DiskLoadProtectorTest extends Logging with KafkaMetricsGroup {

  val topic = "test-topic"
  val time = new MockTime
  val metrics = new Metrics
  var replicaManager : ReplicaManager = _
  var diskLoadProtector : DiskLoadProtector = _
  val seed = new Random;
  var kafkaScheduler : KafkaScheduler = _
  var kafkaZkClient: KafkaZkClient = _

  @volatile var numProduce : Int = 0
  @volatile var numFetch: Int = 0
  var testSemaphore : Semaphore = _

  var currentProduceOffset: mutable.HashMap[TopicPartition, AtomicLong] = mutable.HashMap();
  var logStartOffsets: mutable.HashMap[TopicPartition, Long] = mutable.HashMap();

  var totalRecords : Long = 0
  var batchRecords : Int = 200
  var fastProduce = false

  @Before
  def setUp() {
    kafkaZkClient = EasyMock.createMock(classOf[KafkaZkClient])
    EasyMock.expect(kafkaZkClient.getEntityConfigs(EasyMock.anyString(), EasyMock.anyString())).andReturn(new Properties()).anyTimes()
    EasyMock.replay(kafkaZkClient)
    createReplicaManager()
  }

  @After
  def tearDown() {
    metrics.close()
  }

  def createReplicaManager(): Unit = {
    val props = TestUtils.createBrokerConfig(0, TestUtils.MockZkConnect)
    props.put("log.dir", TestUtils.tempRelativeDir("data").getAbsolutePath)
    //props.put("log.dir", "data/kafka-logs")
    val config = KafkaConfig.fromProps(props)

    kafkaScheduler = new KafkaScheduler(config.backgroundThreads)
    kafkaScheduler.startup()
    OSUtil.start(config.logDirs.asJava, kafkaScheduler.getExecutor);

    val mockLogMgr = TestUtils.createLogManager(config.logDirs.map(new File(_)), LogConfig(props))
    mockLogMgr.startup();

    diskLoadProtector = new DiskLoadProtector(kafkaScheduler.getExecutor, true)

    replicaManager = new ReplicaManager(config, metrics, time, kafkaZkClient, new MockScheduler(time), mockLogMgr,
      new AtomicBoolean(false), QuotaFactory.instantiate(config, metrics, time, ""), new BrokerTopicStats,
      new MetadataCache(config.brokerId), new LogDirFailureChannel(config.logDirs.size), Option(this.getClass.getName))
    replicaManager.diskLoadProtector = diskLoadProtector
  }

  def createTestTopic(topicPartitions : Seq[TopicPartition]): Unit = {
    //val aliveBrokers = Seq(createBroker(0, "host0", 0), createBroker(1, "host1", 1))
    //val brokerList: java.util.List[Integer] = Seq[Integer](0, 1).asJava
    //val brokerSet: java.util.Set[Integer] = Set[Integer](0, 1).asJava
    val aliveBrokers = Seq(createBroker(0, "host0", 0))
    val brokerList  = Seq[Integer](0).asJava
    val brokerSet = Set[Integer](0).asJava
    val liveLeaders = Set(new Node(0, "host0", 0)).asJava

    val metadataCache: MetadataCache = EasyMock.createMock(classOf[MetadataCache])
    EasyMock.expect(metadataCache.getAliveBrokers).andReturn(aliveBrokers).anyTimes()
    EasyMock.replay(metadataCache)

    //create topic partitions
    topicPartitions.foreach { topicPartition =>
      val partition = replicaManager.createPartition(topicPartition);
      val partition0Replicas = Seq[Integer](0, 1).asJava
      val becomeLeaderRequest = new LeaderAndIsrRequest.Builder(ApiKeys.LEADER_AND_ISR.latestVersion, 0, 0, 0,
        Seq(new LeaderAndIsrPartitionState()
          .setTopicName(topicPartition.topic)
          .setPartitionIndex(topicPartition.partition)
          .setControllerEpoch(0)
          .setLeader(0)
          .setLeaderEpoch(1)
          .setIsr(partition0Replicas)
          .setZkVersion(0)
          .setReplicas(partition0Replicas)
          .setIsNew(true)).asJava,
        Set(new Node(0, "host1", 0), new Node(1, "host2", 1)).asJava).build()
      replicaManager.becomeLeaderOrFollower(1, becomeLeaderRequest, (_, _) => ())

      val replica = partition.getReplica(0).get
      currentProduceOffset.put(topicPartition, new AtomicLong(replica.logEndOffset))
      logStartOffsets.put(topicPartition, replica.logEndOffset)
    }
    debug("start offset: %s".format(currentProduceOffset.toString))
  }


  def startProducer(replicaId : Int, topicPartitions : Seq[TopicPartition], style : Int): ShutdownableThread = {
    val name = "produce_%d_%d".format(numProduce, replicaId)
    numProduce = numProduce + 1;

    val BytesInRate = newMeter("BytesIn", "disk write bytes in metrics", TimeUnit.SECONDS, Map("role" -> name))

    val sampleData =initTestData();

    val thread: ShutdownableThread = new ShutdownableThread(name) {
      def produceCallback(responseStatus: Map[TopicPartition, PartitionResponse]) = {
        assertEquals("Should success", Errors.NONE,
          responseStatus.values.head.error)
      }

      override def doWork(): Unit = {
        try {
          var t1 = Time.SYSTEM.milliseconds
          var sum = 0
          var stop = false
          var i = 0;
          while (!stop) {
            val total = currentProduceOffset.map(entry => entry._2.get()).sum
            val initTotal = logStartOffsets.values.sum
            if (total-initTotal >= totalRecords) {
              stop = true
            } else {
              val data = generateTestData(topicPartitions, 0, sampleData)
              data.foreach(entry => currentProduceOffset(entry._1).addAndGet(batchRecords))

              val size = data.map(el => el._2.sizeInBytes()).sum;
              sum += size
              BytesInRate.mark(size)

              replicaManager.appendRecords(
                timeout = 10,
                requiredAcks = -1,
                internalTopicsAllowed = false,
                origin = AppendOrigin.Client,
                data,
                responseCallback = produceCallback)

              if (Time.SYSTEM.milliseconds - t1 > 30000) {
                debug("procude requset %d, %f mb per second, current record: %s".format(
                  i, sum.toDouble / (Time.SYSTEM.milliseconds - t1) / 1000, currentProduceOffset.toString))
                sum = 0;
                t1 = Time.SYSTEM.milliseconds
              }

              i = i + 1;

              if (!fastProduce && (i+1) % 2 == 0) Thread.sleep(seed.nextInt(8) + 6)
            }
          }
          info("produce finish");
          initiateShutdown()
          testSemaphore.release()
        } catch {
          case t: Throwable => error("Uncaught exception: ", t)
        }
      }
    }
    info("produce started");
    thread.start()
    testSemaphore.release()
    thread
  }

  def startFetcher(replicaId : Int, fetchInfo: mutable.HashMap[TopicPartition, PartitionData]): ShutdownableThread = {
    val name = "fetch_%d_%d".format(numFetch, replicaId)
    numFetch = numFetch + 1
    val fetchOffset = fetchInfo.clone()

    val BytesOutRate = newMeter("BytesOut", "disk write bytes in metrics", TimeUnit.SECONDS, Map("role" -> name))
    val LagRate = newGauge("Lag",
      new Gauge[Long] {
        def value = {
          val v1 = fetchOffset.map(entry => entry._2.fetchOffset).sum
          val v2 = currentProduceOffset.filter(entry => fetchOffset.contains(entry._1)).map(entry => entry._2.get()).sum
          v2 - v1
        }
      },
      Map("role" -> name))

    val thread: ShutdownableThread = new ShutdownableThread(name) {
      val fetchSemaphore = new Semaphore(1)
      val fetchResult =  new AtomicReference[Seq[(TopicPartition, FetchPartitionData)]];

      def fetchCallback(responseStatus: Seq[(TopicPartition, FetchPartitionData)]): Unit = {
        assertEquals("Should success", Errors.NONE, responseStatus.map(_._2).head.error)
        fetchResult.set(responseStatus)
        //debug("fetch callback: " +  responseStatus.toString())
        fetchSemaphore.release()
      }

      override def doWork(): Unit = {
        try {
          var t1 = Time.SYSTEM.milliseconds
          var sum = 0
          var stop = false;
          var i = 0;
          while(!stop) {
            fetchSemaphore.acquire()

            val result = fetchResult.getAndSet(null)
            if (result != null) {
              //debug("handle fetch result start");
              result.foreach {
                case (tp, data) =>
                  /*
                  val oldOffset = fetchOffset(tp).offset
                  val newOffset = data.records.shallowEntries.asScala.lastOption.map{
                    case(logEntry) =>
                      logEntry.record().ensureValid()
                      logEntry.nextOffset}.
                    getOrElse(oldOffset)
                  fetchOffset.put(tp, new PartitionData(newOffset, -1,  10000000, Optional.of(0)))
                  if (data.records.sizeInBytes() != 0 && (oldOffset == newOffset || data.hw < newOffset)) {
                    error("fetch error")
                  }
                   */
                  val size = data.records.sizeInBytes();
                  if (size != 0) {
                    val channel = new ByteBufferChannel(data.records.sizeInBytes())
                    data.records.writeTo(channel, 0, size)
                    channel.close()
                    val memRecords = MemoryRecords.readableRecords(channel.buf)
                    val oldOffset = fetchOffset(tp).fetchOffset
                    val startOffset = memRecords.batches.iterator().next().nextOffset
                    if (startOffset != oldOffset) {
                      error("fetch error")
                    }
                    val newOffset = memRecords.batches.asScala.lastOption.map(_.nextOffset).getOrElse(oldOffset)
                    if ((size != 0 && (oldOffset == newOffset)) || (replicaId < 0 && data.highWatermark < newOffset)) {
                      error("fetch error")
                    }
                    fetchOffset.update(tp, new PartitionData(newOffset, -1, 1000000, Optional.of(0)))
                    sum += size
                    BytesOutRate.mark(size)
                  }
              }
            }

            //debug("fetch info : " + fetchOffset)
            replicaManager.fetchMessages(
              timeout = if (replicaId >=0) 15 else seed.nextInt(50),
              replicaId = replicaId,
              fetchMinBytes = 100000,
              fetchMaxBytes = 16000000,
              hardMaxBytesLimit = false,
              fetchInfos = fetchOffset.toSeq,
              quota = UnboundedQuota,
              isolationLevel = IsolationLevel.READ_UNCOMMITTED,
              responseCallback = fetchCallback,
              clientMetadata = None)

            var fininsh = true
            fetchOffset.foreach{
              case (topicPartition, partitionData) =>
                if (currentProduceOffset(topicPartition).get() != partitionData.fetchOffset) {
                  fininsh = false
                }
            }
            if (fininsh) {
              val total = currentProduceOffset.map(entry => entry._2.get()).sum
              val initTotal = logStartOffsets.values.sum
              if (total-initTotal >= totalRecords) {
                stop = true;
              }
            }


            if (Time.SYSTEM.milliseconds - t1 > 30000) {
              debug("fetch requset %d, %f mb per second. fetch total: %s".format(
                i, sum.toDouble / (Time.SYSTEM.milliseconds - t1) / 1000, fetchOffset))
                sum = 0;
              t1 = Time.SYSTEM.milliseconds
            }
            i = i+1;
          }
          info("fetch finish");
          initiateShutdown()
          testSemaphore.release()
        } catch {
          case t: Throwable => error("Uncaught exception: ", t)
        }
      }
    }
    info("fetch started");
    thread.start()
    testSemaphore.release()
    thread
  }

  @Ignore
  @Test
  def testDiskLoadProtector() {
    var t1 = Time.SYSTEM.milliseconds
    try {
      testSemaphore = new Semaphore(0)

      testcase3()

      testSemaphore.acquire(numFetch + numProduce)
      var num = numProduce + numFetch
      while(num > 0) {
        val success = testSemaphore.tryAcquire(1000, TimeUnit.MILLISECONDS)
        if (success) {
          num = num - 1;
        }

      }
      info("test finishied: " + (Time.SYSTEM.milliseconds-t1)/1000.0 + "s")
      Thread.sleep(300000)
    } catch {
      case t: Throwable => error("Uncaught exception: ", t)
    } finally {
      replicaManager.logManager.shutdown()
      replicaManager.shutdown()
    }
  }

  def testcase0(): Unit = {
    totalRecords = 60000000
    val topicPartitions = Seq(new TopicPartition(topic, 0));
    createTestTopic(topicPartitions)

    startProducer(-1, topicPartitions, 0);
    startProducer(0, topicPartitions, 0);

    val fetchinfo = mutable.HashMap[TopicPartition, PartitionData]();
    topicPartitions.foreach(tp =>fetchinfo.put(tp, new PartitionData(0, -1,  1000000, Optional.of(0))))
    startFetcher(-1, fetchinfo);
  }

  def testcase1(): Unit = {
    totalRecords = 60000000
    //val topicPartitions = Seq(new TopicPartition(topic, 0), new TopicPartition(topic, 1));
    var topicPartitions = Seq(new TopicPartition(topic, 0), new TopicPartition(topic, 1), new TopicPartition(topic, 2));
    createTestTopic(topicPartitions)

    fastProduce = true;
    startProducer(-1, topicPartitions, 0);
    startProducer(0, topicPartitions, 0);

    val fetchinfo = mutable.HashMap[TopicPartition, PartitionData]();
    topicPartitions.foreach(tp =>fetchinfo.put(tp, new PartitionData(0, -1,  1000000, Optional.of(0))))
    startFetcher(-1, fetchinfo);

    Thread.sleep(900000)
    OSUtil.instance().dropPageCache();
    info("========== drop page cache");
    Thread.sleep(1000)

    topicPartitions.foreach(tp =>fetchinfo.put(tp, new PartitionData(0, -1,  1000000, Optional.of(0))))
    startFetcher(-2, fetchinfo);

    topicPartitions.foreach(tp =>fetchinfo.put(tp, new PartitionData(20000000, -1,  1000000, Optional.of(0))))
    startFetcher(-1, fetchinfo);

    topicPartitions.foreach(tp =>fetchinfo.put(tp, new PartitionData(80000000, -1,  1000000, Optional.of(0))))
    startFetcher(2, fetchinfo);
  }

  def testcase2(): Unit = {
    totalRecords = 40000000
    var topicPartitions = Seq(
      new TopicPartition(topic, 0),
      new TopicPartition(topic, 1),
      new TopicPartition(topic, 2),
      new TopicPartition(topic, 3),
      new TopicPartition(topic, 4),
      new TopicPartition(topic, 5),
      new TopicPartition(topic, 6),
      new TopicPartition(topic, 7),
      new TopicPartition(topic, 8),
      new TopicPartition(topic, 9),
      new TopicPartition(topic, 10),
      new TopicPartition(topic, 11)
    );
    createTestTopic(topicPartitions)

    info("========== drop page cache");
    OSUtil.instance().dropPageCache();

    startProducer(-1, topicPartitions, 0);
    startProducer(0, topicPartitions, 0);
    startProducer(-2, topicPartitions, 0);
    startProducer(1, topicPartitions, 0);

    val fetchinfo = mutable.HashMap[TopicPartition, PartitionData]();
    topicPartitions.foreach(tp =>fetchinfo.put(tp, new PartitionData(logStartOffsets(tp), -1,  1000000, Optional.of(0))))

    startFetcher(1, fetchinfo);
    startFetcher(-1, fetchinfo);


    Thread.sleep(60000)

    topicPartitions = Seq(
      new TopicPartition(topic, 0),
      new TopicPartition(topic, 1)
    )
    fetchinfo.clear()
    topicPartitions.foreach(tp =>fetchinfo.put(tp, new PartitionData(0, -1,  1000000, Optional.of(0))))
    startFetcher(2, fetchinfo);

    topicPartitions = Seq(
      new TopicPartition(topic, 2),
      new TopicPartition(topic, 3)
    )
    fetchinfo.clear()
    topicPartitions.foreach(tp =>fetchinfo.put(tp, new PartitionData(0, -1,  1000000, Optional.of(0))))
    startFetcher(2, fetchinfo);

    diskLoadProtector.addMaintainTopicPartitions(new TopicPartitionAndReplica(new TopicPartition(topic, 4), 2));
    topicPartitions = Seq(
      new TopicPartition(topic, 4),
      new TopicPartition(topic, 5)
    )
    fetchinfo.clear()
    topicPartitions.foreach(tp =>fetchinfo.put(tp, new PartitionData(0, -1,  1000000, Optional.of(0))))
    startFetcher(2, fetchinfo);

    topicPartitions = Seq(
      new TopicPartition(topic, 6),
      new TopicPartition(topic, 7)
    )
    fetchinfo.clear()
    topicPartitions.foreach(tp =>fetchinfo.put(tp, new PartitionData(0, -1,  1000000, Optional.of(0))))
    startFetcher(-2, fetchinfo);

    topicPartitions = Seq(
      new TopicPartition(topic, 8),
      new TopicPartition(topic, 9)
    )
    fetchinfo.clear()
    topicPartitions.foreach(tp =>fetchinfo.put(tp, new PartitionData(0, -1,  1000000, Optional.of(0))))
    startFetcher(-3, fetchinfo);

    topicPartitions = Seq(
      new TopicPartition(topic, 10),
      new TopicPartition(topic, 11)
    )
    fetchinfo.clear()
    topicPartitions.foreach(tp =>fetchinfo.put(tp, new PartitionData(0, -1,  1000000, Optional.of(0))))
    startFetcher(-4, fetchinfo);
  }

  def testcase3(): Unit = {
    totalRecords = 60000000
    var topicPartitions = List[TopicPartition]()
    for (i  <- 0 until 500) {
      topicPartitions = topicPartitions ::: List(new TopicPartition(topic, i))
    }
    createTestTopic(topicPartitions)

    fastProduce = false
    val fetchinfo = mutable.HashMap[TopicPartition, PartitionData]();
    topicPartitions.foreach(tp =>fetchinfo.put(tp, new PartitionData(0, -1,  1000000, Optional.of(0))))
    startFetcher(-1, fetchinfo);
    startFetcher(-1, fetchinfo);
    startFetcher(-1, fetchinfo);
    startFetcher(-1, fetchinfo);
    startFetcher(-1, fetchinfo);
    startFetcher(-1, fetchinfo);
    startFetcher(-1, fetchinfo);
    startFetcher(-1, fetchinfo);
    startFetcher(-1, fetchinfo);
    startFetcher(-1, fetchinfo);
    startFetcher(-1, fetchinfo);
    startFetcher(-1, fetchinfo);
    startFetcher(-1, fetchinfo);
    startFetcher(-1, fetchinfo);
    startFetcher(-1, fetchinfo);
    startFetcher(-1, fetchinfo);
    startFetcher(-1, fetchinfo);
    startFetcher(-1, fetchinfo);
    startFetcher(-1, fetchinfo);
    startFetcher(-1, fetchinfo);
  }

  def initTestData(): util.ArrayList[MemoryRecords] = {
    var size = 0;
    val testData = new util.ArrayList[MemoryRecords];
    val records = new util.ArrayList[Record]();
    /*
    for (i <- 0 until batchRecords) {
      val record =  new DefaultRecord(getRandomString.getBytes())

      records.add(record);
      size = size + record.sizeInBytes();
    }

    //val memoryRecords = MemoryRecords.withRecords(CompressionType.LZ4, batchRecords - 1, records);
    val memoryRecords = MemoryRecords.withRecords(CompressionType.NONE, batchRecords - 1, records);
    info(s"total recordsize = $size memory record size = ${memoryRecords.sizeInBytes()}, ");

    testData.add(memoryRecords);
     */
    testData
  }

  def generateTestData(topicPartitions : Seq[TopicPartition], style : Int,
                       currentData: util.ArrayList[MemoryRecords]) : Map[TopicPartition, MemoryRecords] = {
    val records = currentData.get(style)
    val entry = records.batches.iterator.next()
    entry.setLastOffset(batchRecords - 1)
    Map(topicPartitions(seed.nextInt(topicPartitions.length)) -> records)
  }

  def getRandomString:String = {
    import scala.io.Source
    val filename = classOf[ReplicaManagerTest].getResource("/sample.log").getFile
    val lines = Source.fromFile(filename).getLines.toArray;
    var line = ""
    for (_  <- 0 until 20) {
      val index  = seed.nextInt(lines.length);
      line = line.concat(lines.apply(index))
    }
    line
  }

  private class ByteBufferChannel(val size: Long) extends GatheringByteChannel {

    var buf:ByteBuffer = null
    private var closed = false

    if (size > Integer.MAX_VALUE) throw new IllegalArgumentException("size should be not be greater than Integer.MAX_VALUE")
    this.buf = ByteBuffer.allocate(size.toInt)

    @throws[IOException]
    override def write(srcs: Array[ByteBuffer], offset: Int, length: Int): Long = {
      assert(false,"should not come here")
      0
    }

    @throws[IOException]
    override def write(srcs: Array[ByteBuffer]): Long = write(srcs, 0, srcs.length)

    @throws[IOException]
    override def write(src: ByteBuffer): Int = {
      val position:Int = buf.position()
      buf.put(src)
      buf.position() - position
    }

    override def isOpen: Boolean = !closed

    @throws[IOException]
    override def close(): Unit = {
      buf.flip
      closed = true
    }
  }
}
