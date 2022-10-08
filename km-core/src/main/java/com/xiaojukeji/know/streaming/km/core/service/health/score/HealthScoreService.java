package com.xiaojukeji.know.streaming.km.core.service.health.score;

import com.xiaojukeji.know.streaming.km.common.bean.entity.health.HealthScoreResult;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.BrokerMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.ClusterMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.GroupMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.TopicMetrics;
import com.xiaojukeji.know.streaming.km.common.enums.health.HealthCheckDimensionEnum;

import java.util.List;

public interface HealthScoreService {
    /**
     * 获取集群健康分指标
     * @param clusterPhyId 集群ID
     * @return
     */
    @Deprecated
    ClusterMetrics calClusterHealthScore(Long clusterPhyId);

    /**
     * 获取集群健康分指标
     * @param clusterPhyId 集群ID
     * @param topicName Topic名称
     * @return
     */
    @Deprecated
    TopicMetrics calTopicHealthScore(Long clusterPhyId, String topicName);

    /**
     * 获取集群健康分指标
     * @param clusterPhyId 集群ID
     * @param brokerId brokerId
     * @return
     */
    @Deprecated
    BrokerMetrics calBrokerHealthScore(Long clusterPhyId, Integer brokerId);

    /**
     * 获取集群健康分指标
     * @param clusterPhyId 集群ID
     * @param groupName group名称
     * @return
     */
    @Deprecated
    GroupMetrics calGroupHealthScore(Long clusterPhyId, String groupName);

    /**
     * 获取集群健康分结果
     * @param clusterPhyId 集群ID
     * @return
     */
    List<HealthScoreResult> getClusterHealthScoreResult(Long clusterPhyId);

    /**
     * 获取集群某个维度健康分结果
     * @param clusterPhyId 集群ID
     * @param dimensionEnum 维度
     * @return
     */
    List<HealthScoreResult> getDimensionHealthScoreResult(Long clusterPhyId, HealthCheckDimensionEnum dimensionEnum);

    /**
     * 获取集群某个资源的健康分结果
     * @param clusterPhyId 集群ID
     * @param dimension 维度
     * @param resNme 资源名称
     * @return
     */
    List<HealthScoreResult> getResHealthScoreResult(Long clusterPhyId, Integer dimension, String resNme);
}
