package com.xiaojukeji.kafka.manager.common.entity.metrics;

import com.xiaojukeji.kafka.manager.common.constant.Constant;
import com.xiaojukeji.kafka.manager.common.constant.MetricsType;
import com.xiaojukeji.kafka.manager.common.entity.annotations.FieldSelector;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 需要定时拉取的broker数据
 * @author tukun
 * @date 2015/11/6.
 */
public class BrokerMetrics extends BaseMetrics {
    /**
     * 集群ID
     */
    private Long clusterId;

    /**
     * Topic名称
     */
    private Integer brokerId;

    /**
     * 每秒Produce请求数的近一分钟的均值、平均字节数、近五分钟均值、近十五分钟均值
     */
    @FieldSelector(types = {
            MetricsType.BROKER_FLOW_DETAIL,
            MetricsType.BROKER_TO_DB_METRICS,
            MetricsType.BROKER_REAL_TIME_METRICS
    })
    private Double produceRequestPerSec = 0.0;
    private Double produceRequestPerSecMeanRate = 0.0;
    private Double produceRequestPerSecFiveMinuteRate = 0.0;
    private Double produceRequestPerSecFifteenMinuteRate = 0.0;

    /**
     * 每秒Fetch请求数的近一分钟的均值、平均字节数、近五分钟均值、近十五分钟均值
     */
    @FieldSelector(types = {
            MetricsType.BROKER_FLOW_DETAIL,
            MetricsType.BROKER_TO_DB_METRICS,
            MetricsType.BROKER_REAL_TIME_METRICS
    })
    private Double fetchConsumerRequestPerSec = 0.0;
    private Double fetchConsumerRequestPerSecMeanRate = 0.0;
    private Double fetchConsumerRequestPerSecFiveMinuteRate = 0.0;
    private Double fetchConsumerRequestPerSecFifteenMinuteRate = 0.0;

    /**
     * Broker分区数量
     */
    @FieldSelector(types = {MetricsType.BROKER_OVER_ALL_METRICS, 5})
    private int partitionCount;

    /**
     * Broker已同步分区数量
     */
    @FieldSelector(types = {MetricsType.BROKER_OVER_ALL_METRICS})
    private int underReplicatedPartitions;

    /**
     * Broker Leader的数量
     */
    @FieldSelector(types = {MetricsType.BROKER_OVER_ALL_METRICS, 5})
    private int leaderCount;

    /**
     * Broker请求处理器空闲百分比
     */
    @FieldSelector(types = {MetricsType.BROKER_TO_DB_METRICS})
    private Double requestHandlerAvgIdlePercent = 0.0;

    /**
     * 网络处理器空闲百分比
     */
    @FieldSelector(types = {MetricsType.BROKER_TO_DB_METRICS})
    private Double networkProcessorAvgIdlePercent = 0.0;

    /**
     * 请求列表大小
     */
    @FieldSelector(types = {MetricsType.BROKER_TO_DB_METRICS})
    private Integer requestQueueSize = 0;

    /**
     * 响应列表大小
     */
    @FieldSelector(types = {MetricsType.BROKER_TO_DB_METRICS})
    private Integer responseQueueSize = 0;

    /**
     * 刷日志时间
     */
    @FieldSelector(types = {MetricsType.BROKER_TO_DB_METRICS})
    private Double logFlushRateAndTimeMs = 0.0;

    /**
     * produce请求总时间-平均值
     */
    @FieldSelector(types = {MetricsType.BROKER_TO_DB_METRICS})
    private Double totalTimeProduceMean = 0.0;

    /**
     * produce请求总时间-99th
     */
    @FieldSelector(types = {MetricsType.BROKER_TO_DB_METRICS})
    private Double totalTimeProduce99Th = 0.0;

    /**
     * fetch consumer请求总时间-平均值
     */
    @FieldSelector(types = {MetricsType.BROKER_TO_DB_METRICS})
    private Double totalTimeFetchConsumerMean = 0.0;

    /**
     * fetch consumer请求总时间-99th
     */
    @FieldSelector(types = {MetricsType.BROKER_TO_DB_METRICS})
    private Double totalTimeFetchConsumer99Th = 0.0;

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    public Integer getBrokerId() {
        return brokerId;
    }

    public void setBrokerId(Integer brokerId) {
        this.brokerId = brokerId;
    }

    public Double getProduceRequestPerSec() {
        return produceRequestPerSec;
    }

    public void setProduceRequestPerSec(Double produceRequestPerSec) {
        this.produceRequestPerSec = produceRequestPerSec;
    }

    public Double getProduceRequestPerSecMeanRate() {
        return produceRequestPerSecMeanRate;
    }

    public void setProduceRequestPerSecMeanRate(Double produceRequestPerSecMeanRate) {
        this.produceRequestPerSecMeanRate = produceRequestPerSecMeanRate;
    }

    public Double getProduceRequestPerSecFiveMinuteRate() {
        return produceRequestPerSecFiveMinuteRate;
    }

    public void setProduceRequestPerSecFiveMinuteRate(Double produceRequestPerSecFiveMinuteRate) {
        this.produceRequestPerSecFiveMinuteRate = produceRequestPerSecFiveMinuteRate;
    }

    public Double getProduceRequestPerSecFifteenMinuteRate() {
        return produceRequestPerSecFifteenMinuteRate;
    }

    public void setProduceRequestPerSecFifteenMinuteRate(Double produceRequestPerSecFifteenMinuteRate) {
        this.produceRequestPerSecFifteenMinuteRate = produceRequestPerSecFifteenMinuteRate;
    }

    public Double getFetchConsumerRequestPerSec() {
        return fetchConsumerRequestPerSec;
    }

    public void setFetchConsumerRequestPerSec(Double fetchConsumerRequestPerSec) {
        this.fetchConsumerRequestPerSec = fetchConsumerRequestPerSec;
    }

    public Double getFetchConsumerRequestPerSecMeanRate() {
        return fetchConsumerRequestPerSecMeanRate;
    }

    public void setFetchConsumerRequestPerSecMeanRate(Double fetchConsumerRequestPerSecMeanRate) {
        this.fetchConsumerRequestPerSecMeanRate = fetchConsumerRequestPerSecMeanRate;
    }

    public Double getFetchConsumerRequestPerSecFiveMinuteRate() {
        return fetchConsumerRequestPerSecFiveMinuteRate;
    }

    public void setFetchConsumerRequestPerSecFiveMinuteRate(Double fetchConsumerRequestPerSecFiveMinuteRate) {
        this.fetchConsumerRequestPerSecFiveMinuteRate = fetchConsumerRequestPerSecFiveMinuteRate;
    }

    public Double getFetchConsumerRequestPerSecFifteenMinuteRate() {
        return fetchConsumerRequestPerSecFifteenMinuteRate;
    }

    public void setFetchConsumerRequestPerSecFifteenMinuteRate(Double fetchConsumerRequestPerSecFifteenMinuteRate) {
        this.fetchConsumerRequestPerSecFifteenMinuteRate = fetchConsumerRequestPerSecFifteenMinuteRate;
    }

    public int getPartitionCount() {
        return partitionCount;
    }

    public void setPartitionCount(int partitionCount) {
        this.partitionCount = partitionCount;
    }

    public int getUnderReplicatedPartitions() {
        return underReplicatedPartitions;
    }

    public void setUnderReplicatedPartitions(int underReplicatedPartitions) {
        this.underReplicatedPartitions = underReplicatedPartitions;
    }

    public int getLeaderCount() {
        return leaderCount;
    }

    public void setLeaderCount(int leaderCount) {
        this.leaderCount = leaderCount;
    }

    public Double getRequestHandlerAvgIdlePercent() {
        return requestHandlerAvgIdlePercent;
    }

    public void setRequestHandlerAvgIdlePercent(Double requestHandlerAvgIdlePercent) {
        this.requestHandlerAvgIdlePercent = requestHandlerAvgIdlePercent;
    }

    public Double getNetworkProcessorAvgIdlePercent() {
        return networkProcessorAvgIdlePercent;
    }

    public void setNetworkProcessorAvgIdlePercent(Double networkProcessorAvgIdlePercent) {
        this.networkProcessorAvgIdlePercent = networkProcessorAvgIdlePercent;
    }

    public Integer getRequestQueueSize() {
        return requestQueueSize;
    }

    public void setRequestQueueSize(Integer requestQueueSize) {
        this.requestQueueSize = requestQueueSize;
    }

    public Integer getResponseQueueSize() {
        return responseQueueSize;
    }

    public void setResponseQueueSize(Integer responseQueueSize) {
        this.responseQueueSize = responseQueueSize;
    }

    public Double getLogFlushRateAndTimeMs() {
        return logFlushRateAndTimeMs;
    }

    public void setLogFlushRateAndTimeMs(Double logFlushRateAndTimeMs) {
        this.logFlushRateAndTimeMs = logFlushRateAndTimeMs;
    }

    public Double getTotalTimeProduceMean() {
        return totalTimeProduceMean;
    }

    public void setTotalTimeProduceMean(Double totalTimeProduceMean) {
        this.totalTimeProduceMean = totalTimeProduceMean;
    }

    public Double getTotalTimeProduce99Th() {
        return totalTimeProduce99Th;
    }

    public void setTotalTimeProduce99Th(Double totalTimeProduce99Th) {
        this.totalTimeProduce99Th = totalTimeProduce99Th;
    }

    public Double getTotalTimeFetchConsumerMean() {
        return totalTimeFetchConsumerMean;
    }

    public void setTotalTimeFetchConsumerMean(Double totalTimeFetchConsumerMean) {
        this.totalTimeFetchConsumerMean = totalTimeFetchConsumerMean;
    }

    public Double getTotalTimeFetchConsumer99Th() {
        return totalTimeFetchConsumer99Th;
    }

    public void setTotalTimeFetchConsumer99Th(Double totalTimeFetchConsumer99Th) {
        this.totalTimeFetchConsumer99Th = totalTimeFetchConsumer99Th;
    }

    private static void initialization(Field[] fields){
        for(Field field : fields){
            FieldSelector annotation = field.getAnnotation(FieldSelector.class);
            if(annotation ==null){
                continue;
            }

            String fieldName;
            if("".equals(annotation.name())) {
                fieldName = field.getName().substring(0,1).toUpperCase() + field.getName().substring(1);
            } else{
                fieldName = annotation.name();
            }
            for(int type: annotation.types()){
                List<String> list = Constant.BROKER_METRICS_TYPE_MBEAN_NAME_MAP.getOrDefault(type, new ArrayList<>());
                list.add(fieldName);
                Constant.BROKER_METRICS_TYPE_MBEAN_NAME_MAP.put(type, list);
            }
        }
    }

    public static List<String> getFieldNameList(int metricsType){
        synchronized (BrokerMetrics.class) {
            if (Constant.BROKER_METRICS_TYPE_MBEAN_NAME_MAP.isEmpty()) {
                initialization(BrokerMetrics.class.getDeclaredFields());
                initialization(BaseMetrics.class.getDeclaredFields());
            }
        }
        return Constant.BROKER_METRICS_TYPE_MBEAN_NAME_MAP.getOrDefault(metricsType, new ArrayList<>());
    }
}
