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

    private KafkaConstant() {
    }
}