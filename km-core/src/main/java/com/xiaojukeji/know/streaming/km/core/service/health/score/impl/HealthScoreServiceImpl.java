package com.xiaojukeji.know.streaming.km.core.service.health.score.impl;

import com.xiaojukeji.know.streaming.km.common.bean.entity.config.healthcheck.BaseClusterHealthConfig;
import com.xiaojukeji.know.streaming.km.common.bean.entity.health.HealthScoreResult;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.BrokerMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.ClusterMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.GroupMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.TopicMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.po.health.HealthCheckResultPO;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import com.xiaojukeji.know.streaming.km.common.enums.health.HealthCheckNameEnum;
import com.xiaojukeji.know.streaming.km.common.enums.health.HealthCheckDimensionEnum;
import com.xiaojukeji.know.streaming.km.common.utils.ValidateUtils;
import com.xiaojukeji.know.streaming.km.core.service.health.checkresult.HealthCheckResultService;
import com.xiaojukeji.know.streaming.km.core.service.health.score.HealthScoreService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.xiaojukeji.know.streaming.km.core.service.version.metrics.BrokerMetricVersionItems.*;
import static com.xiaojukeji.know.streaming.km.core.service.version.metrics.TopicMetricVersionItems.*;
import static com.xiaojukeji.know.streaming.km.core.service.version.metrics.ClusterMetricVersionItems.*;
import static com.xiaojukeji.know.streaming.km.core.service.version.metrics.GroupMetricVersionItems.*;


@Service
public class HealthScoreServiceImpl implements HealthScoreService {
    @Autowired
    private HealthCheckResultService healthCheckResultService;

    @Override
    public ClusterMetrics calClusterHealthScore(Long clusterPhyId) {
        List<HealthScoreResult> allHealthScoreResultList = this.getClusterHealthScoreResult(clusterPhyId);
        Map<Integer, List<HealthScoreResult>> healthScoreResultMap = new HashMap<>();
        for (HealthScoreResult healthScoreResult: allHealthScoreResultList) {
            healthScoreResultMap.putIfAbsent(healthScoreResult.getCheckNameEnum().getDimensionEnum().getDimension(), new ArrayList<>());
            healthScoreResultMap.get(healthScoreResult.getCheckNameEnum().getDimensionEnum().getDimension()).add(healthScoreResult);
        }

        Float healthScore = 0f;
        Float healthCheckPassed = 0f;
        Float healthCheckTotal = 0f;

        ClusterMetrics metrics = new ClusterMetrics(clusterPhyId);

        // cluster维度
        List<HealthScoreResult> healthScoreResultList = healthScoreResultMap.get(HealthCheckDimensionEnum.CLUSTER.getDimension());
        if (ValidateUtils.isEmptyList(healthScoreResultList)) {
            metrics.getMetrics().put(CLUSTER_METRIC_HEALTH_SCORE_CLUSTER, Constant.MIN_HEALTH_SCORE);
            metrics.getMetrics().put(CLUSTER_METRIC_HEALTH_CHECK_PASSED_CLUSTER, 0.0f);
            metrics.getMetrics().put(CLUSTER_METRIC_HEALTH_CHECK_TOTAL_CLUSTER, 0.0f);

            healthScore += 0;
            healthCheckPassed += 0;
            healthCheckTotal += 0;
        } else {
            metrics.getMetrics().put(CLUSTER_METRIC_HEALTH_SCORE_CLUSTER, Math.max(this.getDimensionHealthScore(healthScoreResultList), Constant.MIN_HEALTH_SCORE));
            metrics.getMetrics().put(CLUSTER_METRIC_HEALTH_CHECK_PASSED_CLUSTER, this.getHealthCheckPassed(healthScoreResultList));
            metrics.getMetrics().put(CLUSTER_METRIC_HEALTH_CHECK_TOTAL_CLUSTER, Float.valueOf(healthScoreResultList.size()));

            healthScore += this.getAllHealthScore(healthScoreResultList);
            healthCheckPassed += metrics.getMetrics().get(CLUSTER_METRIC_HEALTH_CHECK_PASSED_CLUSTER);
            healthCheckTotal += metrics.getMetrics().get(CLUSTER_METRIC_HEALTH_CHECK_TOTAL_CLUSTER);
        }

        // broker维度
        healthScoreResultList = healthScoreResultMap.get(HealthCheckDimensionEnum.BROKER.getDimension());
        if (ValidateUtils.isEmptyList(healthScoreResultList)) {
            metrics.getMetrics().put(CLUSTER_METRIC_HEALTH_SCORE_BROKERS, Constant.MIN_HEALTH_SCORE);
            metrics.getMetrics().put(CLUSTER_METRIC_HEALTH_CHECK_PASSED_BROKERS, 0.0f);
            metrics.getMetrics().put(CLUSTER_METRIC_HEALTH_CHECK_TOTAL_BROKERS, 0.0f);

            healthScore += 0;
            healthCheckPassed += 0;
            healthCheckTotal += 0;
        } else {
            metrics.getMetrics().put(CLUSTER_METRIC_HEALTH_SCORE_BROKERS, Math.max(this.getDimensionHealthScore(healthScoreResultList), Constant.MIN_HEALTH_SCORE));
            metrics.getMetrics().put(CLUSTER_METRIC_HEALTH_CHECK_PASSED_BROKERS, this.getHealthCheckPassed(healthScoreResultList));
            metrics.getMetrics().put(CLUSTER_METRIC_HEALTH_CHECK_TOTAL_BROKERS, Float.valueOf(healthScoreResultList.size()));

            healthScore += this.getAllHealthScore(healthScoreResultList);
            healthCheckPassed += metrics.getMetrics().get(CLUSTER_METRIC_HEALTH_CHECK_PASSED_BROKERS);
            healthCheckTotal += metrics.getMetrics().get(CLUSTER_METRIC_HEALTH_CHECK_TOTAL_BROKERS);
        }


        // topic维度
        healthScoreResultList = healthScoreResultMap.get(HealthCheckDimensionEnum.TOPIC.getDimension());
        if (ValidateUtils.isEmptyList(healthScoreResultList)) {
            metrics.getMetrics().put(CLUSTER_METRIC_HEALTH_SCORE_TOPICS, Constant.MIN_HEALTH_SCORE);
            metrics.getMetrics().put(CLUSTER_METRIC_HEALTH_CHECK_PASSED_TOPICS, 0.0f);
            metrics.getMetrics().put(CLUSTER_METRIC_HEALTH_CHECK_TOTAL_TOPICS, 0.0f);

            healthScore += 0;
            healthCheckPassed += 0;
            healthCheckTotal += 0;
        } else {
            metrics.getMetrics().put(CLUSTER_METRIC_HEALTH_SCORE_TOPICS, Math.max(this.getDimensionHealthScore(healthScoreResultList), Constant.MIN_HEALTH_SCORE));
            metrics.getMetrics().put(CLUSTER_METRIC_HEALTH_CHECK_PASSED_TOPICS, this.getHealthCheckPassed(healthScoreResultList));
            metrics.getMetrics().put(CLUSTER_METRIC_HEALTH_CHECK_TOTAL_TOPICS, Float.valueOf(healthScoreResultList.size()));

            healthScore += this.getAllHealthScore(healthScoreResultList);
            healthCheckPassed += metrics.getMetrics().get(CLUSTER_METRIC_HEALTH_CHECK_PASSED_TOPICS);
            healthCheckTotal += metrics.getMetrics().get(CLUSTER_METRIC_HEALTH_CHECK_TOTAL_TOPICS);
        }

        // group维度
        healthScoreResultList = healthScoreResultMap.get(HealthCheckDimensionEnum.GROUP.getDimension());
        if (ValidateUtils.isEmptyList(healthScoreResultList)) {
            metrics.getMetrics().put(CLUSTER_METRIC_HEALTH_SCORE_GROUPS, Constant.MIN_HEALTH_SCORE);
            metrics.getMetrics().put(CLUSTER_METRIC_HEALTH_CHECK_PASSED_GROUPS, 0.0f);
            metrics.getMetrics().put(CLUSTER_METRIC_HEALTH_CHECK_TOTAL_GROUPS, 0.0f);

            healthScore += 0;
            healthCheckPassed += 0;
            healthCheckTotal += 0;
        } else {
            metrics.getMetrics().put(CLUSTER_METRIC_HEALTH_SCORE_GROUPS, Math.max(this.getDimensionHealthScore(healthScoreResultList), Constant.MIN_HEALTH_SCORE));
            metrics.getMetrics().put(CLUSTER_METRIC_HEALTH_CHECK_PASSED_GROUPS, this.getHealthCheckPassed(healthScoreResultList));
            metrics.getMetrics().put(CLUSTER_METRIC_HEALTH_CHECK_TOTAL_GROUPS, Float.valueOf(healthScoreResultList.size()));

            healthScore += this.getAllHealthScore(healthScoreResultList);
            healthCheckPassed += metrics.getMetrics().get(CLUSTER_METRIC_HEALTH_CHECK_PASSED_GROUPS);
            healthCheckTotal += metrics.getMetrics().get(CLUSTER_METRIC_HEALTH_CHECK_TOTAL_GROUPS);
        }

        // 集群最终的
        metrics.getMetrics().put(CLUSTER_METRIC_HEALTH_SCORE, Math.max(healthScore, Constant.MIN_HEALTH_SCORE));
        metrics.getMetrics().put(CLUSTER_METRIC_HEALTH_CHECK_PASSED, healthCheckPassed);
        metrics.getMetrics().put(CLUSTER_METRIC_HEALTH_CHECK_TOTAL, healthCheckTotal);
        return metrics;
    }

    @Override
    public ClusterMetrics calClusterTopicsHealthScore(Long clusterPhyId) {
        List<HealthScoreResult> healthScoreResultList = this.getDimensionHealthScoreResult(clusterPhyId, HealthCheckDimensionEnum.TOPIC);

        ClusterMetrics metrics = new ClusterMetrics(clusterPhyId);
        if (ValidateUtils.isEmptyList(healthScoreResultList)) {
            metrics.getMetrics().put(CLUSTER_METRIC_HEALTH_SCORE_TOPICS, Constant.MIN_HEALTH_SCORE);
            metrics.getMetrics().put(CLUSTER_METRIC_HEALTH_CHECK_PASSED_TOPICS, 0.0f);
            metrics.getMetrics().put(CLUSTER_METRIC_HEALTH_CHECK_TOTAL_TOPICS, 0.0f);
        } else {
            metrics.getMetrics().put(CLUSTER_METRIC_HEALTH_SCORE_TOPICS, Math.max(this.getDimensionHealthScore(healthScoreResultList), Constant.MIN_HEALTH_SCORE));
            metrics.getMetrics().put(CLUSTER_METRIC_HEALTH_CHECK_PASSED_TOPICS, getHealthCheckPassed(healthScoreResultList));
            metrics.getMetrics().put(CLUSTER_METRIC_HEALTH_CHECK_TOTAL_TOPICS, Float.valueOf(healthScoreResultList.size()));
        }

        return metrics;
    }

    @Override
    public ClusterMetrics calClusterBrokersHealthScore(Long clusterPhyId) {
        List<HealthScoreResult> healthScoreResultList = this.getDimensionHealthScoreResult(clusterPhyId, HealthCheckDimensionEnum.BROKER);

        ClusterMetrics metrics = new ClusterMetrics(clusterPhyId);
        if (ValidateUtils.isEmptyList(healthScoreResultList)) {
            metrics.getMetrics().put(CLUSTER_METRIC_HEALTH_SCORE_BROKERS, Constant.MIN_HEALTH_SCORE);
            metrics.getMetrics().put(CLUSTER_METRIC_HEALTH_CHECK_PASSED_BROKERS, 0.0f);
            metrics.getMetrics().put(CLUSTER_METRIC_HEALTH_CHECK_TOTAL_BROKERS, 0.0f);
        } else {
            metrics.getMetrics().put(CLUSTER_METRIC_HEALTH_SCORE_BROKERS, Math.max(this.getDimensionHealthScore(healthScoreResultList), Constant.MIN_HEALTH_SCORE));
            metrics.getMetrics().put(CLUSTER_METRIC_HEALTH_CHECK_PASSED_BROKERS, getHealthCheckPassed(healthScoreResultList));
            metrics.getMetrics().put(CLUSTER_METRIC_HEALTH_CHECK_TOTAL_BROKERS, Float.valueOf(healthScoreResultList.size()));
        }

        return metrics;
    }

    @Override
    public ClusterMetrics calClusterGroupsHealthScore(Long clusterPhyId) {
        List<HealthScoreResult> healthScoreResultList = this.getDimensionHealthScoreResult(clusterPhyId, HealthCheckDimensionEnum.GROUP);

        ClusterMetrics metrics = new ClusterMetrics(clusterPhyId);
        if (ValidateUtils.isEmptyList(healthScoreResultList)) {
            metrics.getMetrics().put(CLUSTER_METRIC_HEALTH_SCORE_GROUPS, Constant.MIN_HEALTH_SCORE);
            metrics.getMetrics().put(CLUSTER_METRIC_HEALTH_CHECK_PASSED_GROUPS, 0.0f);
            metrics.getMetrics().put(CLUSTER_METRIC_HEALTH_CHECK_TOTAL_GROUPS, 0.0f);
        } else {
            metrics.getMetrics().put(CLUSTER_METRIC_HEALTH_SCORE_GROUPS, Math.max(this.getDimensionHealthScore(healthScoreResultList), Constant.MIN_HEALTH_SCORE));
            metrics.getMetrics().put(CLUSTER_METRIC_HEALTH_CHECK_PASSED_GROUPS, this.getHealthCheckPassed(healthScoreResultList));
            metrics.getMetrics().put(CLUSTER_METRIC_HEALTH_CHECK_TOTAL_GROUPS, Float.valueOf(healthScoreResultList.size()));
        }

        return metrics;
    }

    @Override
    public TopicMetrics calTopicHealthScore(Long clusterPhyId, String topicName) {
        List<HealthScoreResult> healthScoreResultList = this.getResHealthScoreResult(clusterPhyId, HealthCheckDimensionEnum.TOPIC.getDimension(), topicName);

        TopicMetrics metrics = new TopicMetrics(topicName, clusterPhyId,true);
        if (ValidateUtils.isEmptyList(healthScoreResultList)) {
            metrics.getMetrics().put(TOPIC_METRIC_HEALTH_SCORE, Constant.MIN_HEALTH_SCORE);
            metrics.getMetrics().put(TOPIC_METRIC_HEALTH_CHECK_PASSED, 0.0f);
            metrics.getMetrics().put(TOPIC_METRIC_HEALTH_CHECK_TOTAL, 0.0f);
        } else {
            metrics.getMetrics().put(TOPIC_METRIC_HEALTH_SCORE, Math.max(this.getDimensionHealthScore(healthScoreResultList), Constant.MIN_HEALTH_SCORE));
            metrics.getMetrics().put(TOPIC_METRIC_HEALTH_CHECK_PASSED, getHealthCheckPassed(healthScoreResultList));
            metrics.getMetrics().put(TOPIC_METRIC_HEALTH_CHECK_TOTAL, Float.valueOf(healthScoreResultList.size()));
        }

        return metrics;
    }

    @Override
    public BrokerMetrics calBrokerHealthScore(Long clusterPhyId, Integer brokerId) {
        List<HealthScoreResult> healthScoreResultList = this.getResHealthScoreResult(clusterPhyId, HealthCheckDimensionEnum.BROKER.getDimension(), String.valueOf(brokerId));

        BrokerMetrics metrics = new BrokerMetrics(clusterPhyId, brokerId);
        if (ValidateUtils.isEmptyList(healthScoreResultList)) {
            metrics.getMetrics().put(BROKER_METRIC_HEALTH_SCORE, Constant.MIN_HEALTH_SCORE);
            metrics.getMetrics().put(BROKER_METRIC_HEALTH_CHECK_PASSED, 0.0f);
            metrics.getMetrics().put(BROKER_METRIC_HEALTH_CHECK_TOTAL, 0.0f);
        } else {
            metrics.getMetrics().put(BROKER_METRIC_HEALTH_SCORE, Math.max(this.getDimensionHealthScore(healthScoreResultList), Constant.MIN_HEALTH_SCORE));
            metrics.getMetrics().put(BROKER_METRIC_HEALTH_CHECK_PASSED, getHealthCheckPassed(healthScoreResultList));
            metrics.getMetrics().put(BROKER_METRIC_HEALTH_CHECK_TOTAL, Float.valueOf(healthScoreResultList.size()));
        }

        return metrics;
    }

    @Override
    public GroupMetrics calGroupHealthScore(Long clusterPhyId, String groupName) {
        List<HealthScoreResult> healthScoreResultList = this.getResHealthScoreResult(clusterPhyId, HealthCheckDimensionEnum.GROUP.getDimension(), groupName);

        GroupMetrics metrics = new GroupMetrics(clusterPhyId, groupName, true);
        if (ValidateUtils.isEmptyList(healthScoreResultList)) {
            metrics.getMetrics().put(GROUP_METRIC_HEALTH_SCORE, Constant.MIN_HEALTH_SCORE);
            metrics.getMetrics().put(GROUP_METRIC_HEALTH_CHECK_PASSED, 0.0f);
            metrics.getMetrics().put(GROUP_METRIC_HEALTH_CHECK_TOTAL, 0.0f);
        } else {
            metrics.getMetrics().put(GROUP_METRIC_HEALTH_SCORE, Math.max(this.getDimensionHealthScore(healthScoreResultList),Constant.MIN_HEALTH_SCORE));
            metrics.getMetrics().put(GROUP_METRIC_HEALTH_CHECK_PASSED, getHealthCheckPassed(healthScoreResultList));
            metrics.getMetrics().put(GROUP_METRIC_HEALTH_CHECK_TOTAL, Float.valueOf(healthScoreResultList.size()));
        }

        return metrics;
    }

    @Override
    public List<HealthScoreResult> getClusterHealthScoreResult(Long clusterPhyId) {
        List<HealthCheckResultPO> poList = healthCheckResultService.getClusterHealthCheckResult(clusterPhyId);

        // <检查项，<检查结果>>
        Map<String, List<HealthCheckResultPO>> checkResultMap = new HashMap<>();
        for (HealthCheckResultPO po: poList) {
            checkResultMap.putIfAbsent(po.getConfigName(), new ArrayList<>());
            checkResultMap.get(po.getConfigName()).add(po);
        }

        // 每个维度的权重和
        Map<String, Float> dimensionTotalWeightMap = new HashMap<>();
        Float allDimensionTotalWeight = 0f;

        Map<String, BaseClusterHealthConfig> configMap = healthCheckResultService.getClusterHealthConfig(clusterPhyId);
        for (HealthCheckNameEnum nameEnum: HealthCheckNameEnum.values()) {
            BaseClusterHealthConfig baseConfig = configMap.get(nameEnum.getConfigName());
            if (baseConfig == null) {
                continue;
            }

            allDimensionTotalWeight += baseConfig.getWeight();

            Float totalWeight = dimensionTotalWeightMap.getOrDefault(nameEnum.getDimensionEnum().name(), 0f);
            dimensionTotalWeightMap.put(nameEnum.getDimensionEnum().name(), totalWeight + baseConfig.getWeight());
        }

        List<HealthScoreResult> healthScoreResultList = new ArrayList<>();
        for (HealthCheckNameEnum nameEnum: HealthCheckNameEnum.values()) {
            BaseClusterHealthConfig baseConfig = configMap.get(nameEnum.getConfigName());
            if (baseConfig == null) {
                continue;
            }

            healthScoreResultList.add(new HealthScoreResult(
                    nameEnum,
                    dimensionTotalWeightMap.getOrDefault(nameEnum.getDimensionEnum().name(), 0f),
                    allDimensionTotalWeight,
                    baseConfig,
                    checkResultMap.getOrDefault(nameEnum.getConfigName(), new ArrayList<>()))
            );
        }

        return healthScoreResultList;
    }

    @Override
    public List<HealthScoreResult> getDimensionHealthScoreResult(Long clusterPhyId, HealthCheckDimensionEnum dimensionEnum) {
        List<HealthCheckResultPO> poList = healthCheckResultService.getClusterResourcesHealthCheckResult(clusterPhyId, dimensionEnum.getDimension());

        // <检查项，<通过的数量，不通过的数量>>
        Map<String, List<HealthCheckResultPO>> checkResultMap = new HashMap<>();
        for (HealthCheckResultPO po: poList) {
            checkResultMap.putIfAbsent(po.getConfigName(), new ArrayList<>());
            checkResultMap.get(po.getConfigName()).add(po);
        }

        // 每个维度的权重和
        Float totalWeight = 0f;

        Map<String, BaseClusterHealthConfig> configMap = healthCheckResultService.getClusterHealthConfig(clusterPhyId);
        for (HealthCheckNameEnum nameEnum: HealthCheckNameEnum.getByDimension(dimensionEnum)) {
            BaseClusterHealthConfig baseConfig = configMap.get(nameEnum.getConfigName());
            if (baseConfig == null) {
                continue;
            }

            totalWeight += baseConfig.getWeight();
        }

        Float allDimensionTotalWeight = configMap.values().stream().map(elem -> elem.getWeight()).reduce(Float::sum).get();

        List<HealthScoreResult> healthScoreResultList = new ArrayList<>();
        for (HealthCheckNameEnum nameEnum: HealthCheckNameEnum.getByDimension(dimensionEnum)) {
            BaseClusterHealthConfig baseConfig = configMap.get(nameEnum.getConfigName());
            if (baseConfig == null) {
                continue;
            }

            healthScoreResultList.add(new HealthScoreResult(nameEnum, totalWeight, allDimensionTotalWeight, baseConfig, checkResultMap.getOrDefault(nameEnum.getConfigName(), new ArrayList<>())));
        }

        return healthScoreResultList;
    }

    @Override
    public List<HealthScoreResult> getResHealthScoreResult(Long clusterPhyId, Integer dimension, String resNme) {
        List<HealthCheckResultPO> poList = healthCheckResultService.getClusterResourcesHealthCheckResult(clusterPhyId, dimension);
        Map<String, List<HealthCheckResultPO>> checkResultMap = new HashMap<>();
        for (HealthCheckResultPO po: poList) {
            checkResultMap.putIfAbsent(po.getConfigName(), new ArrayList<>());
            checkResultMap.get(po.getConfigName()).add(po);
        }

        // 每个维度的权重和
        Float totalWeight = 0f;

        Map<String, BaseClusterHealthConfig> configMap = healthCheckResultService.getClusterHealthConfig(clusterPhyId);
        for (HealthCheckNameEnum nameEnum: HealthCheckNameEnum.getByDimensionCode(dimension)) {
            BaseClusterHealthConfig baseConfig = configMap.get(nameEnum.getConfigName());
            if (baseConfig == null) {
                continue;
            }

            totalWeight += baseConfig.getWeight();
        }

        List<HealthScoreResult> healthScoreResultList = new ArrayList<>();
        for (HealthCheckNameEnum nameEnum: HealthCheckNameEnum.getByDimensionCode(dimension)) {
            BaseClusterHealthConfig baseConfig = configMap.get(nameEnum.getConfigName());
            if (baseConfig == null) {
                continue;
            }

            healthScoreResultList.add(new HealthScoreResult(nameEnum, totalWeight, null, baseConfig, checkResultMap.getOrDefault(nameEnum.getConfigName(), new ArrayList<>())));
        }

        return healthScoreResultList;
    }

    private float getAllHealthScore(List<HealthScoreResult> healthScoreResultList){
        if(CollectionUtils.isEmpty(healthScoreResultList)){return 0f;}

        return healthScoreResultList.stream().map(elem -> elem.calAllWeightHealthScore()).reduce(Float::sum).get();
    }

    private float getDimensionHealthScore(List<HealthScoreResult> healthScoreResultList){
        if(CollectionUtils.isEmpty(healthScoreResultList)){return 0f;}

        return healthScoreResultList.stream().map(elem -> elem.calDimensionWeightHealthScore()).reduce(Float::sum).get();
    }

    private float getHealthCheckPassed(List<HealthScoreResult> healthScoreResultList){
        if(CollectionUtils.isEmpty(healthScoreResultList)){return 0f;}

        return Float.valueOf(healthScoreResultList.stream().filter(elem -> elem.getPassed()).count());
    }
}
