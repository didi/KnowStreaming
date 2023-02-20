package com.xiaojukeji.know.streaming.km.persistence.es.template;

/**
 * @author didi
 */
public class TemplateConstant {
    public static final String TOPIC_INDEX              = "ks_kafka_topic_metric";
    public static final String CLUSTER_INDEX            = "ks_kafka_cluster_metric";
    public static final String BROKER_INDEX             = "ks_kafka_broker_metric";
    public static final String PARTITION_INDEX          = "ks_kafka_partition_metric";
    public static final String GROUP_INDEX              = "ks_kafka_group_metric";
    public static final String ZOOKEEPER_INDEX          = "ks_kafka_zookeeper_metric";
    public static final String CONNECT_CLUSTER_INDEX    = "ks_kafka_connect_cluster_metric";
    public static final String CONNECT_CONNECTOR_INDEX  = "ks_kafka_connect_connector_metric";
    public static final String CONNECT_MM2_INDEX        = "ks_kafka_connect_mirror_maker_metric";

    private TemplateConstant() {
    }
}
