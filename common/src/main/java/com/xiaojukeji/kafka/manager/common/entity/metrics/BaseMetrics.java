package com.xiaojukeji.kafka.manager.common.entity.metrics;

import com.xiaojukeji.kafka.manager.common.constant.MetricsType;
import com.xiaojukeji.kafka.manager.common.entity.annotations.FieldSelector;
import com.xiaojukeji.kafka.manager.common.entity.po.BaseEntryDO;

/**
 * @author zengqiao
 * @date 19/11/25
 */
public class BaseMetrics extends BaseEntryDO {
    /**
     * 每秒流入的近一分钟的均值、平均字节数、近五分钟均值、近十五分钟均值
     */
    @FieldSelector(types = {
            MetricsType.BROKER_FLOW_DETAIL,
            MetricsType.BROKER_TO_DB_METRICS,
            MetricsType.BROKER_REAL_TIME_METRICS,
            MetricsType.BROKER_OVER_VIEW_METRICS,
            MetricsType.BROKER_ANALYSIS_METRICS,
            MetricsType.BROKER_TOPIC_ANALYSIS_METRICS,
            MetricsType.TOPIC_FLOW_DETAIL,
            MetricsType.TOPIC_FLOW_OVERVIEW,
            MetricsType.TOPIC_METRICS_TO_DB
    })
    protected Double bytesInPerSec = 0.0;
    protected Double bytesInPerSecMeanRate = 0.0;
    protected Double bytesInPerSecFiveMinuteRate = 0.0;
    protected Double bytesInPerSecFifteenMinuteRate = 0.0;

    /**
     * 每秒流出的近一分钟的均值、平均字节数、近五分钟均值、近十五分钟均值
     */
    @FieldSelector(types = {
            MetricsType.BROKER_FLOW_DETAIL,
            MetricsType.BROKER_TO_DB_METRICS,
            MetricsType.BROKER_REAL_TIME_METRICS,
            MetricsType.BROKER_OVER_VIEW_METRICS,
            MetricsType.BROKER_ANALYSIS_METRICS,
            MetricsType.BROKER_TOPIC_ANALYSIS_METRICS,
            MetricsType.TOPIC_FLOW_DETAIL,
            MetricsType.TOPIC_METRICS_TO_DB
    })
    protected Double bytesOutPerSec = 0.0;
    protected Double bytesOutPerSecMeanRate = 0.0;
    protected Double bytesOutPerSecFiveMinuteRate = 0.0;
    protected Double bytesOutPerSecFifteenMinuteRate = 0.0;

    /**
     * 每秒流入的近一分钟的均值、平均字节数、近五分钟均值、近十五分钟均值
     */
    @FieldSelector(types = {
            MetricsType.BROKER_FLOW_DETAIL,
            MetricsType.BROKER_TO_DB_METRICS,
            MetricsType.BROKER_REAL_TIME_METRICS,
            MetricsType.BROKER_ANALYSIS_METRICS,
            MetricsType.BROKER_TOPIC_ANALYSIS_METRICS,
            MetricsType.TOPIC_FLOW_DETAIL,
            MetricsType.TOPIC_METRICS_TO_DB
    })
    protected Double messagesInPerSec = 0.0;
    protected Double messagesInPerSecMeanRate = 0.0;
    protected Double messagesInPerSecFiveMinuteRate = 0.0;
    protected Double messagesInPerSecFifteenMinuteRate = 0.0;

    /**
     * 每秒拒绝的近一分钟的均值、平均字节数、近五分钟均值、近十五分钟均值
     */
    @FieldSelector(types = {
            MetricsType.BROKER_FLOW_DETAIL,
            MetricsType.BROKER_TO_DB_METRICS,
            MetricsType.BROKER_REAL_TIME_METRICS,
            MetricsType.TOPIC_FLOW_DETAIL,
            MetricsType.TOPIC_METRICS_TO_DB
    })
    protected Double bytesRejectedPerSec = 0.0;
    protected Double bytesRejectedPerSecMeanRate = 0.0;
    protected Double bytesRejectedPerSecFiveMinuteRate = 0.0;
    protected Double bytesRejectedPerSecFifteenMinuteRate = 0.0;

    /**
     * 每秒失败的Produce请求数的近一分钟的均值、平均字节数、近五分钟均值、近十五分钟均值
     */
    @FieldSelector(types = {
            MetricsType.BROKER_FLOW_DETAIL,
            MetricsType.BROKER_TO_DB_METRICS,
            MetricsType.BROKER_REAL_TIME_METRICS,
            MetricsType.TOPIC_FLOW_DETAIL
    })
    protected Double failProduceRequestPerSec = 0.0;
    protected Double failProduceRequestPerSecMeanRate = 0.0;
    protected Double failProduceRequestPerSecFiveMinuteRate = 0.0;
    protected Double failProduceRequestPerSecFifteenMinuteRate = 0.0;

    /**
     * 每秒失败的Fetch请求数的近一分钟的均值、平均字节数、近五分钟均值、近十五分钟均值
     */
    @FieldSelector(types = {
            MetricsType.BROKER_FLOW_DETAIL,
            MetricsType.BROKER_TO_DB_METRICS,
            MetricsType.BROKER_REAL_TIME_METRICS,
            MetricsType.TOPIC_FLOW_DETAIL
    })
    protected Double failFetchRequestPerSec = 0.0;
    protected Double failFetchRequestPerSecMeanRate = 0.0;
    protected Double failFetchRequestPerSecFiveMinuteRate = 0.0;
    protected Double failFetchRequestPerSecFifteenMinuteRate = 0.0;

    /**
     * 每秒总Produce请求数的近一分钟的均值、平均字节数、近五分钟均值、近十五分钟均值
     */
    @FieldSelector(types = {
            MetricsType.BROKER_FLOW_DETAIL,
            MetricsType.BROKER_ANALYSIS_METRICS,
            MetricsType.BROKER_TOPIC_ANALYSIS_METRICS,
            MetricsType.TOPIC_FLOW_DETAIL,
            MetricsType.TOPIC_METRICS_TO_DB,
            MetricsType.TOPIC_FLOW_OVERVIEW
    })
    protected Double totalProduceRequestsPerSec = 0.0;
    protected Double totalProduceRequestsPerSecMeanRate = 0.0;
    protected Double totalProduceRequestsPerSecFiveMinuteRate = 0.0;
    protected Double totalProduceRequestsPerSecFifteenMinuteRate = 0.0;

    /**
     * 每秒总Fetch请求数的近一分钟的均值、平均字节数、近五分钟均值、近十五分钟均值
     */
    @FieldSelector(types = {
            MetricsType.BROKER_FLOW_DETAIL,
            MetricsType.BROKER_ANALYSIS_METRICS,
            MetricsType.BROKER_TOPIC_ANALYSIS_METRICS,
            MetricsType.TOPIC_FLOW_DETAIL
    })
    protected Double totalFetchRequestsPerSec = 0.0;
    protected Double totalFetchRequestsPerSecMeanRate = 0.0;
    protected Double totalFetchRequestsPerSecFiveMinuteRate = 0.0;
    protected Double totalFetchRequestsPerSecFifteenMinuteRate = 0.0;

    public Double getBytesInPerSec() {
        return bytesInPerSec;
    }

    public void setBytesInPerSec(Double bytesInPerSec) {
        this.bytesInPerSec = bytesInPerSec;
    }

    public Double getBytesInPerSecMeanRate() {
        return bytesInPerSecMeanRate;
    }

    public void setBytesInPerSecMeanRate(Double bytesInPerSecMeanRate) {
        this.bytesInPerSecMeanRate = bytesInPerSecMeanRate;
    }

    public Double getBytesInPerSecFiveMinuteRate() {
        return bytesInPerSecFiveMinuteRate;
    }

    public void setBytesInPerSecFiveMinuteRate(Double bytesInPerSecFiveMinuteRate) {
        this.bytesInPerSecFiveMinuteRate = bytesInPerSecFiveMinuteRate;
    }

    public Double getBytesInPerSecFifteenMinuteRate() {
        return bytesInPerSecFifteenMinuteRate;
    }

    public void setBytesInPerSecFifteenMinuteRate(Double bytesInPerSecFifteenMinuteRate) {
        this.bytesInPerSecFifteenMinuteRate = bytesInPerSecFifteenMinuteRate;
    }

    public Double getBytesOutPerSec() {
        return bytesOutPerSec;
    }

    public void setBytesOutPerSec(Double bytesOutPerSec) {
        this.bytesOutPerSec = bytesOutPerSec;
    }

    public Double getBytesOutPerSecMeanRate() {
        return bytesOutPerSecMeanRate;
    }

    public void setBytesOutPerSecMeanRate(Double bytesOutPerSecMeanRate) {
        this.bytesOutPerSecMeanRate = bytesOutPerSecMeanRate;
    }

    public Double getBytesOutPerSecFiveMinuteRate() {
        return bytesOutPerSecFiveMinuteRate;
    }

    public void setBytesOutPerSecFiveMinuteRate(Double bytesOutPerSecFiveMinuteRate) {
        this.bytesOutPerSecFiveMinuteRate = bytesOutPerSecFiveMinuteRate;
    }

    public Double getBytesOutPerSecFifteenMinuteRate() {
        return bytesOutPerSecFifteenMinuteRate;
    }

    public void setBytesOutPerSecFifteenMinuteRate(Double bytesOutPerSecFifteenMinuteRate) {
        this.bytesOutPerSecFifteenMinuteRate = bytesOutPerSecFifteenMinuteRate;
    }

    public Double getMessagesInPerSec() {
        return messagesInPerSec;
    }

    public void setMessagesInPerSec(Double messagesInPerSec) {
        this.messagesInPerSec = messagesInPerSec;
    }

    public Double getMessagesInPerSecMeanRate() {
        return messagesInPerSecMeanRate;
    }

    public void setMessagesInPerSecMeanRate(Double messagesInPerSecMeanRate) {
        this.messagesInPerSecMeanRate = messagesInPerSecMeanRate;
    }

    public Double getMessagesInPerSecFiveMinuteRate() {
        return messagesInPerSecFiveMinuteRate;
    }

    public void setMessagesInPerSecFiveMinuteRate(Double messagesInPerSecFiveMinuteRate) {
        this.messagesInPerSecFiveMinuteRate = messagesInPerSecFiveMinuteRate;
    }

    public Double getMessagesInPerSecFifteenMinuteRate() {
        return messagesInPerSecFifteenMinuteRate;
    }

    public void setMessagesInPerSecFifteenMinuteRate(Double messagesInPerSecFifteenMinuteRate) {
        this.messagesInPerSecFifteenMinuteRate = messagesInPerSecFifteenMinuteRate;
    }

    public Double getBytesRejectedPerSec() {
        return bytesRejectedPerSec;
    }

    public void setBytesRejectedPerSec(Double bytesRejectedPerSec) {
        this.bytesRejectedPerSec = bytesRejectedPerSec;
    }

    public Double getBytesRejectedPerSecMeanRate() {
        return bytesRejectedPerSecMeanRate;
    }

    public void setBytesRejectedPerSecMeanRate(Double bytesRejectedPerSecMeanRate) {
        this.bytesRejectedPerSecMeanRate = bytesRejectedPerSecMeanRate;
    }

    public Double getBytesRejectedPerSecFiveMinuteRate() {
        return bytesRejectedPerSecFiveMinuteRate;
    }

    public void setBytesRejectedPerSecFiveMinuteRate(Double bytesRejectedPerSecFiveMinuteRate) {
        this.bytesRejectedPerSecFiveMinuteRate = bytesRejectedPerSecFiveMinuteRate;
    }

    public Double getBytesRejectedPerSecFifteenMinuteRate() {
        return bytesRejectedPerSecFifteenMinuteRate;
    }

    public void setBytesRejectedPerSecFifteenMinuteRate(Double bytesRejectedPerSecFifteenMinuteRate) {
        this.bytesRejectedPerSecFifteenMinuteRate = bytesRejectedPerSecFifteenMinuteRate;
    }

    public Double getFailProduceRequestPerSec() {
        return failProduceRequestPerSec;
    }

    public void setFailProduceRequestPerSec(Double failProduceRequestPerSec) {
        this.failProduceRequestPerSec = failProduceRequestPerSec;
    }

    public Double getFailProduceRequestPerSecMeanRate() {
        return failProduceRequestPerSecMeanRate;
    }

    public void setFailProduceRequestPerSecMeanRate(Double failProduceRequestPerSecMeanRate) {
        this.failProduceRequestPerSecMeanRate = failProduceRequestPerSecMeanRate;
    }

    public Double getFailProduceRequestPerSecFiveMinuteRate() {
        return failProduceRequestPerSecFiveMinuteRate;
    }

    public void setFailProduceRequestPerSecFiveMinuteRate(Double failProduceRequestPerSecFiveMinuteRate) {
        this.failProduceRequestPerSecFiveMinuteRate = failProduceRequestPerSecFiveMinuteRate;
    }

    public Double getFailProduceRequestPerSecFifteenMinuteRate() {
        return failProduceRequestPerSecFifteenMinuteRate;
    }

    public void setFailProduceRequestPerSecFifteenMinuteRate(Double failProduceRequestPerSecFifteenMinuteRate) {
        this.failProduceRequestPerSecFifteenMinuteRate = failProduceRequestPerSecFifteenMinuteRate;
    }

    public Double getFailFetchRequestPerSec() {
        return failFetchRequestPerSec;
    }

    public void setFailFetchRequestPerSec(Double failFetchRequestPerSec) {
        this.failFetchRequestPerSec = failFetchRequestPerSec;
    }

    public Double getFailFetchRequestPerSecMeanRate() {
        return failFetchRequestPerSecMeanRate;
    }

    public void setFailFetchRequestPerSecMeanRate(Double failFetchRequestPerSecMeanRate) {
        this.failFetchRequestPerSecMeanRate = failFetchRequestPerSecMeanRate;
    }

    public Double getFailFetchRequestPerSecFiveMinuteRate() {
        return failFetchRequestPerSecFiveMinuteRate;
    }

    public void setFailFetchRequestPerSecFiveMinuteRate(Double failFetchRequestPerSecFiveMinuteRate) {
        this.failFetchRequestPerSecFiveMinuteRate = failFetchRequestPerSecFiveMinuteRate;
    }

    public Double getFailFetchRequestPerSecFifteenMinuteRate() {
        return failFetchRequestPerSecFifteenMinuteRate;
    }

    public void setFailFetchRequestPerSecFifteenMinuteRate(Double failFetchRequestPerSecFifteenMinuteRate) {
        this.failFetchRequestPerSecFifteenMinuteRate = failFetchRequestPerSecFifteenMinuteRate;
    }

    public Double getTotalProduceRequestsPerSec() {
        return totalProduceRequestsPerSec;
    }

    public void setTotalProduceRequestsPerSec(Double totalProduceRequestsPerSec) {
        this.totalProduceRequestsPerSec = totalProduceRequestsPerSec;
    }

    public Double getTotalProduceRequestsPerSecMeanRate() {
        return totalProduceRequestsPerSecMeanRate;
    }

    public void setTotalProduceRequestsPerSecMeanRate(Double totalProduceRequestsPerSecMeanRate) {
        this.totalProduceRequestsPerSecMeanRate = totalProduceRequestsPerSecMeanRate;
    }

    public Double getTotalProduceRequestsPerSecFiveMinuteRate() {
        return totalProduceRequestsPerSecFiveMinuteRate;
    }

    public void setTotalProduceRequestsPerSecFiveMinuteRate(Double totalProduceRequestsPerSecFiveMinuteRate) {
        this.totalProduceRequestsPerSecFiveMinuteRate = totalProduceRequestsPerSecFiveMinuteRate;
    }

    public Double getTotalProduceRequestsPerSecFifteenMinuteRate() {
        return totalProduceRequestsPerSecFifteenMinuteRate;
    }

    public void setTotalProduceRequestsPerSecFifteenMinuteRate(Double totalProduceRequestsPerSecFifteenMinuteRate) {
        this.totalProduceRequestsPerSecFifteenMinuteRate = totalProduceRequestsPerSecFifteenMinuteRate;
    }

    public Double getTotalFetchRequestsPerSec() {
        return totalFetchRequestsPerSec;
    }

    public void setTotalFetchRequestsPerSec(Double totalFetchRequestsPerSec) {
        this.totalFetchRequestsPerSec = totalFetchRequestsPerSec;
    }

    public Double getTotalFetchRequestsPerSecMeanRate() {
        return totalFetchRequestsPerSecMeanRate;
    }

    public void setTotalFetchRequestsPerSecMeanRate(Double totalFetchRequestsPerSecMeanRate) {
        this.totalFetchRequestsPerSecMeanRate = totalFetchRequestsPerSecMeanRate;
    }

    public Double getTotalFetchRequestsPerSecFiveMinuteRate() {
        return totalFetchRequestsPerSecFiveMinuteRate;
    }

    public void setTotalFetchRequestsPerSecFiveMinuteRate(Double totalFetchRequestsPerSecFiveMinuteRate) {
        this.totalFetchRequestsPerSecFiveMinuteRate = totalFetchRequestsPerSecFiveMinuteRate;
    }

    public Double getTotalFetchRequestsPerSecFifteenMinuteRate() {
        return totalFetchRequestsPerSecFifteenMinuteRate;
    }

    public void setTotalFetchRequestsPerSecFifteenMinuteRate(Double totalFetchRequestsPerSecFifteenMinuteRate) {
        this.totalFetchRequestsPerSecFifteenMinuteRate = totalFetchRequestsPerSecFifteenMinuteRate;
    }
}