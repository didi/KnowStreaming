package com.xiaojukeji.know.streaming.km.common.constant.connect;

/**
 * @author zengqiao
 * @date 20/5/20
 */
public class KafkaConnectConstant {
    public static final String CONNECTOR_CLASS_FILED_NAME               = "connector.class";

    public static final String CONNECTOR_TOPICS_FILED_NAME              = "topics";
    public static final String CONNECTOR_TOPICS_FILED_ERROR_VALUE       = "know-streaming-connect-illegal-value";

    public static final String MIRROR_MAKER_TOPIC_PARTITION_PATTERN     = "kafka.connect.mirror:type=MirrorSourceConnector,target=*,topic=*,partition=*";

    public static final String MIRROR_MAKER_SOURCE_CONNECTOR_TYPE       = "org.apache.kafka.connect.mirror.MirrorSourceConnector";

    public static final String MIRROR_MAKER_HEARTBEAT_CONNECTOR_TYPE    = "org.apache.kafka.connect.mirror.MirrorHeartbeatConnector";

    public static final String MIRROR_MAKER_CHECKPOINT_CONNECTOR_TYPE   = "org.apache.kafka.connect.mirror.MirrorCheckpointConnector";

    public static final String MIRROR_MAKER_TARGET_CLUSTER_BOOTSTRAP_SERVERS_FIELD_NAME     = "target.cluster.bootstrap.servers";
    public static final String MIRROR_MAKER_TARGET_CLUSTER_ALIAS_FIELD_NAME                 = "target.cluster.alias";
    public static final String MIRROR_MAKER_TARGET_CLUSTER_FIELD_NAME                       = "target.cluster";

    public static final String MIRROR_MAKER_SOURCE_CLUSTER_BOOTSTRAP_SERVERS_FIELD_NAME     = "source.cluster.bootstrap.servers";
    public static final String MIRROR_MAKER_SOURCE_CLUSTER_ALIAS_FIELD_NAME                 = "source.cluster.alias";
    public static final String MIRROR_MAKER_SOURCE_CLUSTER_FIELD_NAME                       = "source.cluster";

    public static final String MIRROR_MAKER_NAME_FIELD_NAME                                 = "name";
    private KafkaConnectConstant() {
    }
}