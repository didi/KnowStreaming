package com.xiaojukeji.kafka.manager.common.constant;

/**
 * 配置的常量KEY
 * @author zengqiao
 * @date 20/7/1
 */
public class ConfigConstant {
    /**
     * 专家服务
     */
    public static final String REGION_HOT_TOPIC_CONFIG_KEY = "REGION_HOT_TOPIC_CONFIG";
    public static final String TOPIC_INSUFFICIENT_PARTITION_CONFIG_KEY = "TOPIC_INSUFFICIENT_PARTITION_CONFIG";
    public static final String EXPIRED_TOPIC_CONFIG_KEY = "EXPIRED_TOPIC_CONFIG";

    /**
     *
     */
    public static final String PRODUCE_CONSUMER_METRICS_CONFIG_KEY = "PRODUCE_CONSUMER_METRICS_CONFIG_KEY";

    public static final String PRODUCE_TOPIC_METRICS_CONFIG_KEY = "PRODUCE_TOPIC_METRICS_CONFIG_KEY";

    public static final long MAX_LIMIT_NUM = 200L;

    /**
     * broker 默认最大峰值流量 100M
     */
    public static final Long DEFAULT_BROKER_CAPACITY_LIMIT = 100 * 1024 * 1024L;

    public static final String BROKER_CAPACITY_LIMIT_CONFIG_KEY = "BROKER_CAPACITY_LIMIT_CONFIG";

    public static final String KAFKA_CLUSTER_DO_CONFIG_KEY = "KAFKA_CLUSTER_DO_CONFIG";

    private ConfigConstant() {
    }
}
