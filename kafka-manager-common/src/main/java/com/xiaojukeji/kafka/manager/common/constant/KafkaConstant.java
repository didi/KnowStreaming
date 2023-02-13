package com.xiaojukeji.kafka.manager.common.constant;

/**
 * @author zengqiao
 * @date 20/5/20
 */
public class KafkaConstant {
    public static final String COORDINATOR_TOPIC_NAME = "__consumer_offsets";

    public static final String TRANSACTION_TOPIC_NAME = "__transaction_state";

    public static final String BROKER_HOST_NAME_SUFFIX = ".diditaxi.com";

    public static final String CLIENT_VERSION_CODE_UNKNOWN = "-1";

    public static final String CLIENT_VERSION_NAME_UNKNOWN = "unknown";

    public static final String RETENTION_MS_KEY = "retention.ms";

    public static final String EXTERNAL_KEY = "EXTERNAL";

    public static final String INTERNAL_KEY = "INTERNAL";

    public static final String BOOTSTRAP_SERVERS = "bootstrap.servers";


    /**
     * HA
     */

    public static final String DIDI_KAFKA_ENABLE = "didi.kafka.enable";

    public static final String DIDI_HA_REMOTE_CLUSTER = "didi.ha.remote.cluster";

    // TODO 平台来管理配置，不需要底层来管理，因此可以删除该配置
    public static final String DIDI_HA_SYNC_TOPIC_CONFIGS_ENABLED = "didi.ha.sync.topic.configs.enabled";

    public static final String DIDI_HA_ACTIVE_CLUSTER = "didi.ha.active.cluster";

    public static final String DIDI_HA_REMOTE_TOPIC = "didi.ha.remote.topic";

    public static final String SECURITY_PROTOCOL = "security.protocol";

    public static final String SASL_MECHANISM = "sasl.mechanism";

    public static final String SASL_JAAS_CONFIG = "sasl.jaas.config";

    public static final String NONE = "None";

    private KafkaConstant() {
    }
}