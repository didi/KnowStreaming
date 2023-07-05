package com.xiaojukeji.know.streaming.km.common.constant;


import kafka.log.LogConfig$;
import kafka.server.KafkaConfig$;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.internals.Topic;
import scala.jdk.javaapi.CollectionConverters;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zengqiao
 * @date 20/5/20
 */
public class KafkaConstant {
    public static final List<String> KAFKA_INTERNAL_TOPICS = Arrays.asList(
            Topic.TRANSACTION_STATE_TOPIC_NAME,
            Topic.GROUP_METADATA_TOPIC_NAME,
            "__cluster_metadata"
    );

    public static final String  VERSION             = "version";

    public static final String  PARTITIONS             = "partitions";

    public static final String EXTERNAL_KEY = "EXTERNAL";

    public static final String INTERNAL_KEY = "INTERNAL";

    public static final String WITHOUT_RACK_INFO_NAME = "";

    public static final Integer DATA_VERSION_ONE = 1;

    public static final Integer ADMIN_CLIENT_REQUEST_TIME_OUT_UNIT_MS = 10000;

    public static final Integer KAFKA_SASL_SCRAM_ITERATIONS = 8192;

    public static final Integer NO_LEADER = -1;

    public static final Long POLL_ONCE_TIMEOUT_UNIT_MS = 2000L;

    public static final String CONTROLLER_ROLE = "controller";

    public static final String DEFAULT_CONNECT_VERSION = "2.5.0";

    public static final List<String> CONFIG_SIMILAR_IGNORED_CONFIG_KEY_LIST = Arrays.asList("broker.id", "listeners", "name", "value", "advertised.listeners", "node.id");

    public static final Map<String, ConfigDef.ConfigKey> KAFKA_ALL_CONFIG_DEF_MAP = new ConcurrentHashMap<>();

    public static final Integer TOPICK_TRUNCATE_DEFAULT_OFFSET = -1;

    static {
        try {
            KAFKA_ALL_CONFIG_DEF_MAP.putAll(CollectionConverters.asJava(LogConfig$.MODULE$.configKeys()));
        } catch (Exception e) {
            // ignore
        }

        try {
            KAFKA_ALL_CONFIG_DEF_MAP.putAll(CollectionConverters.asJava(KafkaConfig$.MODULE$.configKeys()));
        } catch (Exception e) {
            // ignore
        }
    }

    private KafkaConstant() {
    }
}