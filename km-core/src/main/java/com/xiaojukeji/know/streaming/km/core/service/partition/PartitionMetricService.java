package com.xiaojukeji.know.streaming.km.core.service.partition;

import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.PartitionMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;

import java.util.List;

/**
 * @author didi
 */
public interface PartitionMetricService {

    /**
     * 从Kafka获取分区指标
     */
    Result<List<PartitionMetrics>> collectPartitionsMetricsFromKafkaWithCache(Long clusterPhyId, String topicName, String metricName);

    Result<List<PartitionMetrics>> collectPartitionsMetricsFromKafka(Long clusterPhyId, String topicName, List<String> metricNameList);
    Result<List<PartitionMetrics>> collectPartitionsMetricsFromKafka(Long clusterPhyId, String topicName, String metricName);

    Result<PartitionMetrics> collectPartitionMetricsFromKafka(Long clusterPhyId, String topicName, Integer partitionId, String metricName);



    /**
     * 从ES获取指标
     */
    PartitionMetrics getLatestMetricsFromES(Long clusterPhyId, String topic, Integer brokerId, Integer partitionId, List<String> metricNameList);
    Result<List<PartitionMetrics>> getLatestMetricsFromES(Long clusterPhyId, String topicName, List<String> metricNameList);
}
