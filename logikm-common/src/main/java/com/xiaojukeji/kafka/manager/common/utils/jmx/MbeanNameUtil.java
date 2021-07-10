package com.xiaojukeji.kafka.manager.common.utils.jmx;

import java.util.HashMap;
import java.util.Map;

/**
 * kafka集群的mbean的object name集合
 * @author tukun, zengqiao
 * @date 2015/11/5.
 */
public class MbeanNameUtil {
    /**
     * 社区Metrics指标
     */

    /**
     * 滴滴Metrics指标
     */
    private static final String PRODUCE_BYTES_IN_PER_SEC = "kafka.server:type=DiskMetrics,name=ProducerBytesInPerSec";

    /**
     * 限流指标
     */
    private static final String PRODUCE_THROTTLE_TIME = "kafka.server:type=Produce,client-id=*";
    private static final String FETCH_THROTTLE_TIME = "kafka.server:type=Fetch,client-id=*";


    private static final String TOPIC_PARTITION_LOG_SIZE = "kafka.log:type=Log,name=Size,topic=%s,partition=%d";


    /**
     * 存储监控的参数name到获取的object_name的映射关系图
     */
    private static Map<String, Mbean> MBEAN_NAME_MAP = new HashMap<>();
    static {

        MBEAN_NAME_MAP.put("ProduceThrottleTime", new Mbean(MbeanNameUtil.PRODUCE_THROTTLE_TIME, "throttle-time", Double.class));
        MBEAN_NAME_MAP.put("FetchThrottleTime", new Mbean(MbeanNameUtil.FETCH_THROTTLE_TIME, "throttle-time", Double.class));



        /**
         * 滴滴Metrics指标
         */
        MBEAN_NAME_MAP.put("ProducerBytesInPerSec", new Mbean(PRODUCE_BYTES_IN_PER_SEC,"OneMinuteRate", Double.class));

        MBEAN_NAME_MAP.put("TopicPartitionLogSize", new Mbean(TOPIC_PARTITION_LOG_SIZE, "Value", Long.class));

    }

    /**
     * 根据属性名，kafka版本，topic获取相应的Mbean
     */
    public static Mbean getMbean(String mbeanName) {
        return MBEAN_NAME_MAP.get(mbeanName);
    }

    public static Mbean getMbean(String mbeanName, String topicName) {
        Mbean mbean = MBEAN_NAME_MAP.get(mbeanName);
        if (mbean == null) {
            return null;
        }
        if (topicName == null || topicName.isEmpty()) {
            return mbean;
        }
        return new Mbean(mbean.getObjectName() + ",topic=" + topicName, mbean.getProperty(), mbean.getPropertyClass());
    }

    public static Mbean getMbean(String mbeanName, Integer brokerId) {
        Mbean mbean = MBEAN_NAME_MAP.get(mbeanName);
        if (mbean == null) {
            return null;
        }
        if (brokerId == null) {
            return mbean;
        }
        return new Mbean(mbean.getObjectName() + ",id=" + brokerId, mbean.getProperty(), mbean.getPropertyClass());
    }
}
