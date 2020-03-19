package com.xiaojukeji.kafka.manager.common.utils.jmx;

import java.util.HashMap;
import java.util.Map;

/**
 * kafka集群的mbean的object name集合
 * @author tukun, zengqiao
 * @date 2015/11/5.
 */
public class MbeanNameUtil {

    //broker监控参数
    private static final String MESSAGE_IN_PER_SEC = "kafka.server:type=BrokerTopicMetrics,name=MessagesInPerSec";
    private static final String BYTES_IN_PER_SEC = "kafka.server:type=BrokerTopicMetrics,name=BytesInPerSec";
    private static final String BYTES_OUT_PER_SEC = "kafka.server:type=BrokerTopicMetrics,name=BytesOutPerSec";
    private static final String BYTES_REJECTED_PER_SEC = "kafka.server:type=BrokerTopicMetrics,name=BytesRejectedPerSec";
    private static final String FAILED_FETCH_REQUEST_PER_SEC = "kafka.server:type=BrokerTopicMetrics,name=FailedFetchRequestsPerSec";
    private static final String FAILED_PRODUCE_REQUEST_PER_SEC = "kafka.server:type=BrokerTopicMetrics,name=FailedProduceRequestsPerSec";
    private static final String PRODUCE_REQUEST_PER_SEC = "kafka.network:type=RequestMetrics,name=RequestsPerSec,request=Produce";
    private static final String CONSUMER_REQUEST_PER_SEC = "kafka.network:type=RequestMetrics,name=RequestsPerSec,request=FetchConsumer";
    private static final String TOTAL_PRODUCE_REQUEST_PER_SEC = "kafka.server:type=BrokerTopicMetrics,name=TotalProduceRequestsPerSec";
    private static final String TOTAL_FETCH_REQUEST_PER_SEC = "kafka.server:type=BrokerTopicMetrics,name=TotalFetchRequestsPerSec";

    private static final String REQUEST_HANDLER_AVG_IDLE_PERCENT = "kafka.server:type=KafkaRequestHandlerPool,name=RequestHandlerAvgIdlePercent";
    private static final String NETWORK_PROCESSOR_AVG_IDLE_PERCENT = "kafka.network:type=SocketServer,name=NetworkProcessorAvgIdlePercent";
    private static final String REQUEST_QUEUE_SIZE = "kafka.network:type=RequestChannel,name=RequestQueueSize";
    private static final String RESPONSE_QUEUE_SIZE = "kafka.network:type=RequestChannel,name=ResponseQueueSize";
    private static final String LOG_FLUSH_RATE_AND_TIME_MS = "kafka.log:type=LogFlushStats,name=LogFlushRateAndTimeMs";
    private static final String TOTAL_TIME_PRODUCE = "kafka.network:type=RequestMetrics,name=TotalTimeMs,request=Produce";
    private static final String TOTAL_TIME_FETCH_CONSUMER = "kafka.network:type=RequestMetrics,name=TotalTimeMs,request=FetchConsumer";

    private static final String PART_COUNT = "kafka.server:type=ReplicaManager,name=PartitionCount";
    private static final String PARTITION_OFFSET_PULL = "kafka.log:type=Log,name=LogEndOffset,topic=${topic},partition=${partition}";
    private static final String UNDER_REPLICATED_PARTITIONS = "kafka.server:type=ReplicaManager,name=UnderReplicatedPartitions";
    private static final String LEADER_COUNT = "kafka.server:type=ReplicaManager,name=LeaderCount";


//    private static final String PRODUCE_REQUEST_TIME = "kafka.network:type=TopicRequestMetrics,name=TotalTimeMs,request=Produce";
//    private static final String FETCH_REQUEST_TIME = "kafka.network:type=TopicRequestMetrics,name=TotalTimeMs,request=FetchConsumer";


    //存储监控的参数name到获取的object_name的映射关系图
    private static Map<String, Mbean> mbeanNameMap = new HashMap<String, Mbean>();
    static {
        //监控参数配置，object_name和监控的属性名
        mbeanNameMap.put("MessagesInPerSec", new Mbean(MESSAGE_IN_PER_SEC,"OneMinuteRate", Double.class));
        mbeanNameMap.put("BytesInPerSec", new Mbean(BYTES_IN_PER_SEC,"OneMinuteRate", Double.class));
        mbeanNameMap.put("BytesOutPerSec", new Mbean(BYTES_OUT_PER_SEC,"OneMinuteRate", Double.class));
        mbeanNameMap.put("BytesRejectedPerSec", new Mbean(BYTES_REJECTED_PER_SEC,"OneMinuteRate", Double.class));
        mbeanNameMap.put("FailFetchRequestPerSec", new Mbean(FAILED_FETCH_REQUEST_PER_SEC,"OneMinuteRate", Double.class));
        mbeanNameMap.put("FailProduceRequestPerSec", new Mbean(FAILED_PRODUCE_REQUEST_PER_SEC,"OneMinuteRate", Double.class));
        mbeanNameMap.put("ProduceRequestPerSec", new Mbean(PRODUCE_REQUEST_PER_SEC,"OneMinuteRate", Double.class));
        mbeanNameMap.put("FetchConsumerRequestPerSec", new Mbean(CONSUMER_REQUEST_PER_SEC,"OneMinuteRate", Double.class));
        mbeanNameMap.put("TotalProduceRequestsPerSec", new Mbean(TOTAL_PRODUCE_REQUEST_PER_SEC,"OneMinuteRate", Double.class));
        mbeanNameMap.put("TotalFetchRequestsPerSec", new Mbean(TOTAL_FETCH_REQUEST_PER_SEC,"OneMinuteRate", Double.class));


        mbeanNameMap.put("PartitionOffset", new Mbean(PARTITION_OFFSET_PULL,"Value", int.class));

        mbeanNameMap.put("PartitionCount", new Mbean(PART_COUNT,"Value", int.class));
        mbeanNameMap.put("UnderReplicatedPartitions", new Mbean(UNDER_REPLICATED_PARTITIONS,"Value", int.class));
        mbeanNameMap.put("LeaderCount", new Mbean(LEADER_COUNT,"Value", int.class));

        mbeanNameMap.put("RequestHandlerAvgIdlePercent", new Mbean(REQUEST_HANDLER_AVG_IDLE_PERCENT,"OneMinuteRate", Double.class));
        mbeanNameMap.put("NetworkProcessorAvgIdlePercent", new Mbean(NETWORK_PROCESSOR_AVG_IDLE_PERCENT,"Value", Double.class));
        mbeanNameMap.put("RequestQueueSize", new Mbean(REQUEST_QUEUE_SIZE,"Value", int.class));
        mbeanNameMap.put("ResponseQueueSize", new Mbean(RESPONSE_QUEUE_SIZE, "Value", int.class));
        mbeanNameMap.put("LogFlushRateAndTimeMs", new Mbean(LOG_FLUSH_RATE_AND_TIME_MS,"OneMinuteRate", Double.class));
        mbeanNameMap.put("TotalTimeProduceMean", new Mbean(TOTAL_TIME_PRODUCE,"Mean", Double.class));
        mbeanNameMap.put("TotalTimeProduce99Th", new Mbean(TOTAL_TIME_PRODUCE,"99thPercentile", Double.class));
        mbeanNameMap.put("TotalTimeFetchConsumerMean", new Mbean(TOTAL_TIME_FETCH_CONSUMER,"Mean", Double.class));
        mbeanNameMap.put("TotalTimeFetchConsumer99Th", new Mbean(TOTAL_TIME_FETCH_CONSUMER,"99thPercentile", Double.class));

//        mbeanNameMap.put("ProduceRequestTime", new Mbean(PRODUCE_REQUEST_TIME,"Value"));
//        mbeanNameMap.put("FetchRequestTime", new Mbean(FETCH_REQUEST_TIME,"Value"));
    }

    /**
     * 根据属性名，kafka版本，topic获取相应的Mbean
     */
    public static Mbean getMbean(String name, String topic) {
        Mbean mbean = mbeanNameMap.get(name);
        if (mbean == null) {
            return null;
        }
        if (topic != null && !topic.isEmpty()) {
            return new Mbean(mbean.getObjectName() + ",topic=" + topic, mbean.getProperty(), mbean.getPropertyClass());
        }
        return mbean;
    }

}
