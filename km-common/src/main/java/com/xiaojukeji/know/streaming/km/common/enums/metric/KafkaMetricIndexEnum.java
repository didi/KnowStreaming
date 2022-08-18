package com.xiaojukeji.know.streaming.km.common.enums.metric;

/**
 * @author: D10865
 * @description:
 * @date: Create on 2019/3/11 下午2:19
 * @modified By D10865
 *
 * 不同维度的es监控数据
 */
public enum KafkaMetricIndexEnum {

    /**
     * topic 维度
     */
    TOPIC_INFO("ks_kafka_topic_metric"),

    /**
     * 集群 维度
     */
    CLUSTER_INFO("ks_kafka_cluster_metric"),

    /**
     * broker 维度
     */
    BROKER_INFO("ks_kafka_broker_metric"),

    /**
     * partition 维度
     */
    PARTITION_INFO("ks_kafka_partition_metric"),

    /**
     * group 维度
     */
    GROUP_INFO("ks_kafka_group_metric"),

    /**
     * replication 维度
     */
    REPLICATION_INFO("ks_kafka_replication_metric"),

    ;

    private String index;

    KafkaMetricIndexEnum(String index) {
        this.index = index;
    }

    public String getIndex() {
        return index;
    }
}
