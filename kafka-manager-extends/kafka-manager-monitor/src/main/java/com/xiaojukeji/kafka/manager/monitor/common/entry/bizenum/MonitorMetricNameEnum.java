package com.xiaojukeji.kafka.manager.monitor.common.entry.bizenum;

import com.xiaojukeji.kafka.manager.monitor.common.MonitorSinkConstant;

/**
 * 监控指标名
 * @author zengqiao
 * @date 20/5/24
 */
public enum MonitorMetricNameEnum {
    TOPIC_MSG_IN(MonitorSinkConstant.METRIC_NAME_PRE_STR + "kafka-topic-msgIn", "条"),

    TOPIC_BYTES_IN(MonitorSinkConstant.METRIC_NAME_PRE_STR + "kafka-topic-bytesIn", "字节/秒"),

    TOPIC_BYTES_REJECTED(MonitorSinkConstant.METRIC_NAME_PRE_STR + "kafka-topic-bytesRejected", "字节/秒"),

    TOPIC_APP_PRODUCE_THROTTLE(MonitorSinkConstant.METRIC_NAME_PRE_STR + "kafka-topic-produce-throttled", "1:被限流"),

    TOPIC_APP_FETCH_THROTTLE(MonitorSinkConstant.METRIC_NAME_PRE_STR + "kafka-topic-fetch-throttled", "1:被限流"),

    CONSUMER_MAX_LAG(MonitorSinkConstant.METRIC_NAME_PRE_STR + "kafka-consumer-maxLag", "条"),

    CONSUMER_PARTITION_LAG(MonitorSinkConstant.METRIC_NAME_PRE_STR + "kafka-consumer-lag", "条"),

    CONSUMER_MAX_DELAY_TIME(MonitorSinkConstant.METRIC_NAME_PRE_STR + "kafka-consumer-maxDelayTime", "秒"),

    ;

    private String metricName;

    private String unit;

    MonitorMetricNameEnum(String metricName, String unit) {
        this.metricName = metricName;
        this.unit = unit;
    }

    public String getMetricName() {
        return metricName;
    }

    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    @Override
    public String toString() {
        return "MonitorMetricNameEnum{" +
                "metricName='" + metricName + '\'' +
                ", unit='" + unit + '\'' +
                '}';
    }
}