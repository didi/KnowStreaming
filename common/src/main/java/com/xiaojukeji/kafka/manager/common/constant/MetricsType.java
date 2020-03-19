package com.xiaojukeji.kafka.manager.common.constant;

public class MetricsType {
    /**
     * Broker流量详情
     */
    public static final int BROKER_FLOW_DETAIL = 0;
    public static final int BROKER_TO_DB_METRICS = 1; // Broker入DB的Metrics指标
    public static final int BROKER_REAL_TIME_METRICS = 2; // Broker入DB的Metrics指标
    public static final int BROKER_OVER_VIEW_METRICS = 3; // Broker状态概览的指标
    public static final int BROKER_OVER_ALL_METRICS = 4; // Broker状态总揽的指标
    public static final int BROKER_ANALYSIS_METRICS = 5; // Broker分析的指标
    public static final int BROKER_TOPIC_ANALYSIS_METRICS = 6; // Broker分析的指标

    /**
     * Topic流量详情
     */
    public static final int TOPIC_FLOW_DETAIL = 100;
    public static final int TOPIC_FLOW_OVERVIEW = 101;
    public static final int TOPIC_METRICS_TO_DB = 102;


}
