package com.xiaojukeji.kafka.manager.common.constant;

/**
 *
 * @author zengqiao
 * @date 20/4/22
 */
public class KafkaMetricsCollections {
    public static final int COMMON_DETAIL_METRICS = 0;

    /**
     * Broker流量详情
     */
    public static final int BROKER_TO_DB_METRICS = 101; // Broker入DB的Metrics指标
    public static final int BROKER_OVERVIEW_PAGE_METRICS = 103; // Broker状态概览的指标
    public static final int BROKER_ANALYSIS_METRICS = 105; // Broker分析的指标
    public static final int BROKER_TOPIC_ANALYSIS_METRICS = 106; // Broker分析的指标
    public static final int BROKER_BASIC_PAGE_METRICS = 107; // Broker基本信息页面的指标
    public static final int BROKER_STATUS_PAGE_METRICS = 108; // Broker状态
    public static final int BROKER_HEALTH_SCORE_METRICS = 109; // Broker健康分

    /**
     * Topic流量详情
     */
    public static final int TOPIC_FLOW_OVERVIEW = 201;
    public static final int TOPIC_METRICS_TO_DB = 202;
    public static final int TOPIC_REQUEST_TIME_METRICS_TO_DB = 203;
    public static final int TOPIC_BASIC_PAGE_METRICS = 204;
    public static final int TOPIC_REQUEST_TIME_DETAIL_PAGE_METRICS = 205;
    public static final int TOPIC_THROTTLED_METRICS_TO_DB = 206;


    /**
     * App+Topic流量详情
     */
    public static final int APP_TOPIC_METRICS_TO_DB = 300;

    /**
     * Broker信息
     */
    public static final int BROKER_VERSION = 400;

    private KafkaMetricsCollections() {
    }
}
