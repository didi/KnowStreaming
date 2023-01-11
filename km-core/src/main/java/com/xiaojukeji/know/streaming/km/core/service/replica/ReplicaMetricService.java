package com.xiaojukeji.know.streaming.km.core.service.replica;

import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.ReplicationMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;

import java.util.List;

public interface ReplicaMetricService {

    /**
     * 从kafka中采集指标
     */
    Result<ReplicationMetrics> collectReplicaMetricsFromKafka(Long clusterId, String topic, Integer partitionId, Integer brokerId, String metric);
    Result<ReplicationMetrics> collectReplicaMetricsFromKafka(Long clusterId, String topicName, Integer partitionId, Integer brokerId, List<String> metricNameList);
}
