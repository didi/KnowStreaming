package com.xiaojukeji.know.streaming.km.core.service.health.state;

import com.xiaojukeji.know.streaming.km.common.bean.entity.health.HealthScoreResult;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.*;
import com.xiaojukeji.know.streaming.km.common.enums.health.HealthCheckDimensionEnum;

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

    /**
     * 获取集群健康检查结果
     */
    List<HealthScoreResult> getClusterHealthResult(Long clusterPhyId);
    List<HealthScoreResult> getDimensionHealthResult(Long clusterPhyId, HealthCheckDimensionEnum dimensionEnum);
    List<HealthScoreResult> getResHealthResult(Long clusterPhyId, Integer dimension, String resNme);
}
