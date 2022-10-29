package com.xiaojukeji.know.streaming.km.core.service.health.state;

import com.xiaojukeji.know.streaming.km.common.bean.entity.health.HealthScoreResult;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.*;
import com.xiaojukeji.know.streaming.km.common.enums.health.HealthCheckDimensionEnum;

import java.util.List;


public interface HealthStateService {
    /**
     * 集群健康指标
     */
    ClusterMetrics calClusterHealthMetrics(Long clusterPhyId);

    /**
     * 获取Broker健康指标
     */
    BrokerMetrics calBrokerHealthMetrics(Long clusterPhyId, Integer brokerId);

    /**
     * 获取Topic健康指标
     */
    TopicMetrics calTopicHealthMetrics(Long clusterPhyId, String topicName);

    /**
     * 获取Group健康指标
     */
    GroupMetrics calGroupHealthMetrics(Long clusterPhyId, String groupName);

    /**
     * 获取Zookeeper健康指标
     */
    ZookeeperMetrics calZookeeperHealthMetrics(Long clusterPhyId);

    /**
     * 获取集群健康检查结果
     */
    List<HealthScoreResult> getClusterHealthResult(Long clusterPhyId);

    /**
     * 获取集群某个维度健康检查结果
     */
    List<HealthScoreResult> getDimensionHealthResult(Long clusterPhyId, HealthCheckDimensionEnum dimensionEnum);

    /**
     * 获取集群某个资源的健康检查结果
     */
    List<HealthScoreResult> getResHealthResult(Long clusterPhyId, Integer dimension, String resNme);
}
