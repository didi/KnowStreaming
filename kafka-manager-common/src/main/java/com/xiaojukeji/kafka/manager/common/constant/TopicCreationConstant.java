package com.xiaojukeji.kafka.manager.common.constant;

import java.util.Properties;

/**
 * @author zengqiao
 * @date 20/7/28
 */
public class TopicCreationConstant {
    /**
     * LogX创建Topic配置KEY
     */
    public static final String LOG_X_CREATE_TOPIC_CONFIG_KEY_NAME = "LOG_X_CREATE_TOPIC_CONFIG";

    /**
     * 治理平台创建Topic配置KEY
     */
    public static final String CHORUS_CREATE_TOPIC_CONFIG_KEY_NAME = "CHORUS_CREATE_TOPIC_CONFIG";

    /**
     * 内部创建Topic配置KEY
     */
    public static final String INNER_CREATE_TOPIC_CONFIG_KEY = "INNER_CREATE_TOPIC_CONFIG_KEY";

    public static final Integer DEFAULT_REPLICA = 3;

    public static final Integer DEFAULT_PARTITION_NUM = 1;

    public static final Integer DEFAULT_RETENTION_TIME_UNIT_HOUR = 24;

    public static final String TOPIC_RETENTION_TIME_KEY_NAME = "retention.ms";

    public static Properties createNewProperties(Long retentionTime) {
        Properties properties = new Properties();
        properties.put(TOPIC_RETENTION_TIME_KEY_NAME, String.valueOf(retentionTime));
        return properties;
    }

    public static final Long AUTO_EXEC_MAX_BYTES_IN_UNIT_B = 30 * 1024 * 1024L;

    /**
     * Topic 前缀
     */
    public static final String TOPIC_NAME_PREFIX_US = "us01_";

    public static final String TOPIC_NAME_PREFIX_RU = "ru01_";

    public static final Integer TOPIC_NAME_MAX_LENGTH = 255;
}