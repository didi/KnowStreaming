package com.xiaojukeji.know.streaming.km.core.service.health.state;

import com.xiaojukeji.know.streaming.km.common.bean.entity.health.HealthScoreResult;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.*;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.connect.ConnectorMetrics;

import java.util.List;


public interface HealthStateService {
    /**
     * 健康指标
     */
    ClusterMetrics calClusterHealthMetrics(Long clusterPhyId);
    BrokerMetrics calBrokerHealthMetrics(Long clusterPhyId, Integer brokerId);
    TopicMetrics calTopicHealthMetrics(Long clusterPhyId, String topicName);
    GroupMetrics calGroupHealthMetrics(Long clusterPhyId, String groupName);
    ZookeeperMetrics calZookeeperHealthMetrics(Long clusterPhyId);
    ConnectorMetrics calConnectorHealthMetrics(Long connectClusterId, String connectorName);

    /**
     * 获取集群健康检查结果
     */
    List<HealthScoreResult> getAllDimensionHealthResult(Long clusterPhyId);
    List<HealthScoreResult> getDimensionHealthResult(Long clusterPhyId, List<Integer> dimensionCodeList);
    List<HealthScoreResult> getResHealthResult(Long clusterPhyId, Long clusterId, Integer dimension, String resNme);
}
