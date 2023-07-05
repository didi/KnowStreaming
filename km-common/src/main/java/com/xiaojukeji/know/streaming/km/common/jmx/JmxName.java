package com.xiaojukeji.know.streaming.km.common.jmx;

public class JmxName {

    /*********************************************************** server ***********************************************************/
    public static final String JMX_SERVER_BROKER_BYTE_IN                = "kafka.server:type=BrokerTopicMetrics,name=BytesInPerSec";

    public static final String JMX_SERVER_BROKER_REASSIGNMENT_BYTE_IN   = "kafka.server:type=BrokerTopicMetrics,name=ReassignmentBytesInPerSec";

    public static final String JMX_SERVER_BROKER_BYTE_OUT               = "kafka.server:type=BrokerTopicMetrics,name=BytesOutPerSec";

    public static final String JMX_SERVER_BROKER_REASSIGNMENT_BYTE_OUT  = "kafka.server:type=BrokerTopicMetrics,name=ReassignmentBytesOutPerSec";

    public static final String JMX_SERVER_BROKER_MESSAGES_IN            = "kafka.server:type=BrokerTopicMetrics,name=MessagesInPerSec";

    public static final String JMX_SERVER_BROKER_PRODUCES_REQUEST       = "kafka.server:type=BrokerTopicMetrics,name=TotalProduceRequestsPerSec";

    public static final String JMX_SERVER_BROKER_BYTES_REJECTED         = "kafka.server:type=BrokerTopicMetrics,name=BytesRejectedPerSec";

    public static final String JMX_SERVER_BROKER_FAILED_FETCH_REQUEST   = "kafka.server:type=BrokerTopicMetrics,name=FailedFetchRequestsPerSec";

    public static final String JMX_SERVER_BROKER_FAILED_PRODUCE_REQUEST = "kafka.server:type=BrokerTopicMetrics,name=FailedProduceRequestsPerSec";

    public static final String JMX_SERVER_BROKER_REPLICATION_BYTES_IN   = "kafka.server:type=BrokerTopicMetrics,name=ReplicationBytesInPerSec";

    public static final String JMX_SERVER_BROKER_REPLICATION_BYTES_OUT  = "kafka.server:type=BrokerTopicMetrics,name=ReplicationBytesOutPerSec";

    public static final String JMX_SERVER_SOCKET_CONNECTIONS            = "kafka.server:type=socket-server-metrics";

    public static final String JMX_SERVER_REQUEST_AVG_IDLE              = "kafka.server:type=KafkaRequestHandlerPool,name=RequestHandlerAvgIdlePercent";

    public static final String JMX_SERVER_UNDER_REP_PARTITIONS          = "kafka.server:type=ReplicaManager,name=UnderReplicatedPartitions";

    public static final String JMX_SERVER_AT_MIN_ISR_PARTITIONS         = "kafka.server:type=ReplicaManager,name=AtMinIsrPartitionCount";

    public static final String JMX_SERVER_UNDER_MIN_ISR_PARTITIONS      = "kafka.server:type=ReplicaManager,name=UnderMinIsrPartitionCount";

    public static final String JMX_SERVER_PARTITIONS                    = "kafka.server:type=ReplicaManager,name=PartitionCount";

    public static final String JMX_SERVER_LEADERS                       = "kafka.server:type=ReplicaManager,name=LeaderCount";

    public static final String JMX_SERVER_APP_INFO                      ="kafka.server:type=app-info";

    public static final String JMX_SERVER_TOPIC_MIRROR                  ="kafka.server:type=FetcherLagMetrics,name=ConsumerLag,clientId=*,topic=%s,partition=*";

    /*********************************************************** controller ***********************************************************/
    public static final String JMX_CONTROLLER_ACTIVE_COUNT              = "kafka.controller:type=KafkaController,name=ActiveControllerCount";

    public static final String JMX_CONTROLLER_EVENT_QUEUE_SIZE          = "kafka.controller:type=ControllerEventManager,name=EventQueueSize";

    /*********************************************************** log ***********************************************************/
    public static final String JMX_LOG_LOG_END_OFFSET                   = "kafka.log:type=Log,name=LogEndOffset";

    public static final String JMX_LOG_LOG_START_OFFSET                 = "kafka.log:type=Log,name=LogStartOffset";

    public static final String JMX_LOG_LOG_SIZE                         = "kafka.log:type=Log,name=Size";

    /*********************************************************** network ***********************************************************/
    public static final String JMX_NETWORK_REQUEST_QUEUE                = "kafka.network:type=RequestChannel,name=RequestQueueSize";

    public static final String JMX_NETWORK_RESPONSE_QUEUE               = "kafka.network:type=RequestChannel,name=ResponseQueueSize";

    public static final String JMX_NETWORK_PROCESSOR_AVG_IDLE           = "kafka.network:type=SocketServer,name=NetworkProcessorAvgIdlePercent";

    /*********************************************************** cluster ***********************************************************/
    public static final String JMX_CLUSTER_PARTITION_UNDER_REPLICATED   = "kafka.cluster:type=Partition,name=UnderReplicated";

    /*********************************************************** zookeeper ***********************************************************/

    public static final String JMX_ZK_REQUEST_LATENCY_MS                = "kafka.server:type=ZooKeeperClientMetrics,name=ZooKeeperRequestLatencyMs";
    public static final String JMX_ZK_SYNC_CONNECTS_PER_SEC             = "kafka.server:type=SessionExpireListener,name=ZooKeeperSyncConnectsPerSec";
    public static final String JMX_ZK_DISCONNECTORS_PER_SEC             = "kafka.server:type=SessionExpireListener,name=ZooKeeperDisconnectsPerSec";

    /*********************************************************** connect ***********************************************************/
    public static final String JMX_CONNECT_WORKER_METRIC                = "kafka.connect:type=connect-worker-metrics";

    public static final String JMX_CONNECT_WORKER_CONNECTOR_METRIC      = "kafka.connect:type=connect-worker-metrics,connector=%s";

    public static final String JMX_CONNECTOR_TASK_CONNECTOR_METRIC      = "kafka.connect:type=connector-task-metrics,connector=%s,task=%s";

    public static final String JMX_CONNECTOR_SOURCE_TASK_METRICS        = "kafka.connect:type=source-task-metrics,connector=%s,task=%s";

    public static final String JMX_CONNECTOR_SINK_TASK_METRICS          = "kafka.connect:type=sink-task-metrics,connector=%s,task=%s";

    public static final String JMX_CONNECTOR_TASK_ERROR_METRICS         = "kafka.connect:type=task-error-metrics,connector=%s,task=%s";

    /*********************************************************** mm2 ***********************************************************/

    public static final String JMX_MIRROR_MAKER_SOURCE                  = "kafka.connect.mirror:type=MirrorSourceConnector,target=%s,topic=%s,partition=%s";


    private JmxName() {
    }
}
