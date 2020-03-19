package com.xiaojukeji.kafka.manager.common.constant;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zengqiao
 * @date 20/2/28
 */
public class Constant {
    public static final String KAFKA_MANAGER_INNER_ERROR = "kafka-manager inner error";

    public final static Map<Integer, List<String>> BROKER_METRICS_TYPE_MBEAN_NAME_MAP = new ConcurrentHashMap<>();

    public final static Map<Integer, List<String>> TOPIC_METRICS_TYPE_MBEAN_NAME_MAP = new ConcurrentHashMap<>();

    public static final String COLLECTOR_METRICS_LOGGER = "COLLECTOR_METRICS_LOGGER";

    public static final String API_METRICS_LOGGER = "API_METRICS_LOGGER";
}
