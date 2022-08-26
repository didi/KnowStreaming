package com.xiaojukeji.know.streaming.km.core.service.replica;

import com.xiaojukeji.know.streaming.km.common.bean.dto.metrices.MetricDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.ReplicationMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.vo.metrics.point.MetricPointVO;

import java.util.List;

public interface ReplicaMetricService {

    /**
     * 从kafka中采集指标
     */
    Result<ReplicationMetrics> collectReplicaMetricsFromKafka(Long clusterId, String topic, Integer partitionId, Integer brokerId, String metric);
    Result<ReplicationMetrics> collectReplicaMetricsFromKafkaWithCache(Long clusterPhyId, String topic, Integer brokerId, Integer partitionId, String metric);

    /**
     * 从ES中获取指标
     */
    Result<List<MetricPointVO>> getMetricPointsFromES(Long clusterPhyId, Integer brokerId, String topicName, Integer partitionId, MetricDTO dto);

    Result<ReplicationMetrics> getLatestMetricsFromES(Long clusterPhyId, Integer brokerId, String topicName, Integer partitionId, List<String> metricNames);
}
