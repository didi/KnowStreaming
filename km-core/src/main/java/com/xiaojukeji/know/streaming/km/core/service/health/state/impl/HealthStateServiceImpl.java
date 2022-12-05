package com.xiaojukeji.know.streaming.km.core.service.health.state.impl;

import com.xiaojukeji.know.streaming.km.common.bean.entity.broker.Broker;
import com.xiaojukeji.know.streaming.km.common.bean.entity.config.healthcheck.BaseClusterHealthConfig;
import com.xiaojukeji.know.streaming.km.common.bean.entity.health.HealthCheckAggResult;
import com.xiaojukeji.know.streaming.km.common.bean.entity.health.HealthScoreResult;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.*;
import com.xiaojukeji.know.streaming.km.common.bean.po.health.HealthCheckResultPO;
import com.xiaojukeji.know.streaming.km.common.enums.health.HealthCheckDimensionEnum;
import com.xiaojukeji.know.streaming.km.common.enums.health.HealthCheckNameEnum;
import com.xiaojukeji.know.streaming.km.common.enums.health.HealthStateEnum;
import com.xiaojukeji.know.streaming.km.common.utils.ValidateUtils;
import com.xiaojukeji.know.streaming.km.core.service.broker.BrokerService;
import com.xiaojukeji.know.streaming.km.core.service.health.checkresult.HealthCheckResultService;
import com.xiaojukeji.know.streaming.km.core.service.health.state.HealthStateService;
import com.xiaojukeji.know.streaming.km.core.service.zookeeper.ZookeeperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.xiaojukeji.know.streaming.km.core.service.version.metrics.kafka.BrokerMetricVersionItems.*;
import static com.xiaojukeji.know.streaming.km.core.service.version.metrics.kafka.ClusterMetricVersionItems.*;
import static com.xiaojukeji.know.streaming.km.core.service.version.metrics.kafka.GroupMetricVersionItems.*;
import static com.xiaojukeji.know.streaming.km.core.service.version.metrics.kafka.TopicMetricVersionItems.*;
import static com.xiaojukeji.know.streaming.km.core.service.version.metrics.kafka.ZookeeperMetricVersionItems.*;


@Service
public class HealthStateServiceImpl implements HealthStateService {
    @Autowired
    private HealthCheckResultService healthCheckResultService;

    @Autowired
    private ZookeeperService zookeeperService;

    @Autowired
    private BrokerService brokerService;

    @Override
    public ClusterMetrics calClusterHealthMetrics(Long clusterPhyId) {
        ClusterMetrics metrics = new ClusterMetrics(clusterPhyId);

        // 集群维度指标
        List<HealthCheckAggResult> resultList = healthCheckResultService.getHealthCheckAggResult(clusterPhyId, HealthCheckDimensionEnum.CLUSTER);
        if (ValidateUtils.isEmptyList(resultList)) {
            metrics.getMetrics().put(CLUSTER_METRIC_HEALTH_CHECK_PASSED_CLUSTER, 0.0f);
            metrics.getMetrics().put(CLUSTER_METRIC_HEALTH_CHECK_TOTAL_CLUSTER, 0.0f);
        } else {
            metrics.getMetrics().put(CLUSTER_METRIC_HEALTH_CHECK_PASSED_CLUSTER, this.getHealthCheckPassed(resultList));
            metrics.getMetrics().put(CLUSTER_METRIC_HEALTH_CHECK_TOTAL_CLUSTER, (float)resultList.size());
        }

        metrics.getMetrics().put(CLUSTER_METRIC_HEALTH_STATE_CLUSTER, (float)this.calHealthState(resultList).getDimension());

        // 获取指标
        metrics.putMetric(this.calClusterBrokersHealthMetrics(clusterPhyId).getMetrics());
        metrics.putMetric(this.calClusterTopicsHealthMetrics(clusterPhyId).getMetrics());
        metrics.putMetric(this.calClusterGroupsHealthMetrics(clusterPhyId).getMetrics());
        metrics.putMetric(this.calZookeeperHealthMetrics(clusterPhyId).getMetrics());

        // 统计最终结果
        Float passed = 0.0f;
        passed += metrics.getMetric(ZOOKEEPER_METRIC_HEALTH_CHECK_PASSED);
        passed += metrics.getMetric(CLUSTER_METRIC_HEALTH_CHECK_PASSED_TOPICS);
        passed += metrics.getMetric(CLUSTER_METRIC_HEALTH_CHECK_PASSED_BROKERS);
        passed += metrics.getMetric(CLUSTER_METRIC_HEALTH_CHECK_PASSED_GROUPS);
        passed += metrics.getMetric(CLUSTER_METRIC_HEALTH_CHECK_PASSED_CLUSTER);

        Float total = 0.0f;
        total += metrics.getMetric(ZOOKEEPER_METRIC_HEALTH_CHECK_TOTAL);
        total += metrics.getMetric(CLUSTER_METRIC_HEALTH_CHECK_TOTAL_TOPICS);
        total += metrics.getMetric(CLUSTER_METRIC_HEALTH_CHECK_TOTAL_BROKERS);
        total += metrics.getMetric(CLUSTER_METRIC_HEALTH_CHECK_TOTAL_GROUPS);
        total += metrics.getMetric(CLUSTER_METRIC_HEALTH_CHECK_TOTAL_CLUSTER);

        // 状态
        Float state = 0.0f;
        state = Math.max(state, metrics.getMetric(ZOOKEEPER_METRIC_HEALTH_STATE));
        state = Math.max(state, metrics.getMetric(CLUSTER_METRIC_HEALTH_STATE_TOPICS));
        state = Math.max(state, metrics.getMetric(CLUSTER_METRIC_HEALTH_STATE_BROKERS));
        state = Math.max(state, metrics.getMetric(CLUSTER_METRIC_HEALTH_STATE_GROUPS));
        state = Math.max(state, metrics.getMetric(CLUSTER_METRIC_HEALTH_STATE_CLUSTER));

        metrics.getMetrics().put(CLUSTER_METRIC_HEALTH_CHECK_PASSED, passed);
        metrics.getMetrics().put(CLUSTER_METRIC_HEALTH_CHECK_TOTAL, total);
        metrics.getMetrics().put(CLUSTER_METRIC_HEALTH_STATE, state);

        return metrics;
    }

    @Override
    public BrokerMetrics calBrokerHealthMetrics(Long clusterPhyId, Integer brokerId) {
        List<HealthCheckAggResult> aggResultList = healthCheckResultService.getHealthCheckAggResult(clusterPhyId, HealthCheckDimensionEnum.BROKER, String.valueOf(brokerId));

        BrokerMetrics metrics = new BrokerMetrics(clusterPhyId, brokerId);
        if (ValidateUtils.isEmptyList(aggResultList)) {
            metrics.getMetrics().put(BROKER_METRIC_HEALTH_STATE, (float)HealthStateEnum.GOOD.getDimension());
            metrics.getMetrics().put(BROKER_METRIC_HEALTH_CHECK_PASSED, 0.0f);
            metrics.getMetrics().put(BROKER_METRIC_HEALTH_CHECK_TOTAL, 0.0f);
        } else {
            metrics.getMetrics().put(BROKER_METRIC_HEALTH_CHECK_PASSED, this.getHealthCheckPassed(aggResultList));
            metrics.getMetrics().put(BROKER_METRIC_HEALTH_CHECK_TOTAL, (float)aggResultList.size());

            // 计算健康状态
            Broker broker = brokerService.getBrokerFromCacheFirst(clusterPhyId, brokerId);
            if (broker == null) {
                // DB中不存在，则默认是存活的
                metrics.getMetrics().put(BROKER_METRIC_HEALTH_STATE, (float)HealthStateEnum.GOOD.getDimension());
            } else if (!broker.alive()) {
                metrics.getMetrics().put(BROKER_METRIC_HEALTH_STATE, (float)HealthStateEnum.DEAD.getDimension());
            } else {
                metrics.getMetrics().put(BROKER_METRIC_HEALTH_STATE, (float)this.calHealthState(aggResultList).getDimension());
            }
        }

        return metrics;
    }

    @Override
    public TopicMetrics calTopicHealthMetrics(Long clusterPhyId, String topicName) {
        List<HealthCheckAggResult> aggResultList = healthCheckResultService.getHealthCheckAggResult(clusterPhyId, HealthCheckDimensionEnum.TOPIC, topicName);

        TopicMetrics metrics = new TopicMetrics(topicName, clusterPhyId,true);
        if (ValidateUtils.isEmptyList(aggResultList)) {
            metrics.getMetrics().put(TOPIC_METRIC_HEALTH_STATE, (float)HealthStateEnum.GOOD.getDimension());
            metrics.getMetrics().put(TOPIC_METRIC_HEALTH_CHECK_PASSED, 0.0f);
            metrics.getMetrics().put(TOPIC_METRIC_HEALTH_CHECK_TOTAL, 0.0f);
        } else {
            metrics.getMetrics().put(TOPIC_METRIC_HEALTH_STATE, (float)this.calHealthState(aggResultList).getDimension());
            metrics.getMetrics().put(TOPIC_METRIC_HEALTH_CHECK_PASSED, this.getHealthCheckPassed(aggResultList));
            metrics.getMetrics().put(TOPIC_METRIC_HEALTH_CHECK_TOTAL, (float)aggResultList.size());
        }

        return metrics;
    }

    @Override
    public GroupMetrics calGroupHealthMetrics(Long clusterPhyId, String groupName) {
        List<HealthCheckAggResult> aggResultList = healthCheckResultService.getHealthCheckAggResult(clusterPhyId, HealthCheckDimensionEnum.GROUP, groupName);

        GroupMetrics metrics = new GroupMetrics(clusterPhyId, groupName, true);
        if (ValidateUtils.isEmptyList(aggResultList)) {
            metrics.getMetrics().put(GROUP_METRIC_HEALTH_STATE, (float)HealthStateEnum.GOOD.getDimension());
            metrics.getMetrics().put(GROUP_METRIC_HEALTH_CHECK_PASSED, 0.0f);
            metrics.getMetrics().put(GROUP_METRIC_HEALTH_CHECK_TOTAL, 0.0f);
        } else {
            metrics.getMetrics().put(GROUP_METRIC_HEALTH_STATE, (float)this.calHealthState(aggResultList).getDimension());
            metrics.getMetrics().put(GROUP_METRIC_HEALTH_CHECK_PASSED, this.getHealthCheckPassed(aggResultList));
            metrics.getMetrics().put(GROUP_METRIC_HEALTH_CHECK_TOTAL, (float)aggResultList.size());
        }

        return metrics;
    }

    @Override
    public ZookeeperMetrics calZookeeperHealthMetrics(Long clusterPhyId) {
        List<HealthCheckAggResult> aggResultList = healthCheckResultService.getHealthCheckAggResult(clusterPhyId, HealthCheckDimensionEnum.ZOOKEEPER);

        ZookeeperMetrics metrics = new ZookeeperMetrics(clusterPhyId);
        if (ValidateUtils.isEmptyList(aggResultList)) {
            metrics.getMetrics().put(ZOOKEEPER_METRIC_HEALTH_CHECK_PASSED, 0.0f);
            metrics.getMetrics().put(ZOOKEEPER_METRIC_HEALTH_CHECK_TOTAL, 0.0f);
        } else {
            metrics.getMetrics().put(ZOOKEEPER_METRIC_HEALTH_CHECK_PASSED, this.getHealthCheckPassed(aggResultList));
            metrics.getMetrics().put(ZOOKEEPER_METRIC_HEALTH_CHECK_TOTAL, (float)aggResultList.size());
        }

        if (zookeeperService.allServerDown(clusterPhyId)) {
            // 所有服务挂掉
            metrics.getMetrics().put(ZOOKEEPER_METRIC_HEALTH_STATE, (float)HealthStateEnum.DEAD.getDimension());
            return metrics;
        }

        if (zookeeperService.existServerDown(clusterPhyId)) {
            // 存在服务挂掉
            metrics.getMetrics().put(ZOOKEEPER_METRIC_HEALTH_STATE, (float)HealthStateEnum.POOR.getDimension());
            return metrics;
        }

        // 服务未挂时，依据检查结果计算状态
        metrics.getMetrics().put(ZOOKEEPER_METRIC_HEALTH_STATE, (float)this.calHealthState(aggResultList).getDimension());
        return metrics;
    }

    @Override
    public List<HealthScoreResult> getClusterHealthResult(Long clusterPhyId) {
        List<HealthCheckResultPO> poList = healthCheckResultService.listCheckResult(clusterPhyId);

        return this.convert2HealthScoreResultList(clusterPhyId, poList, null);
    }

    @Override
    public List<HealthScoreResult> getDimensionHealthResult(Long clusterPhyId, HealthCheckDimensionEnum dimensionEnum) {
        List<HealthCheckResultPO> poList = healthCheckResultService.listCheckResult(clusterPhyId, dimensionEnum.getDimension());

        return this.convert2HealthScoreResultList(clusterPhyId, poList, dimensionEnum.getDimension());
    }

    @Override
    public List<HealthScoreResult> getResHealthResult(Long clusterPhyId, Integer dimension, String resNme) {
        List<HealthCheckResultPO> poList = healthCheckResultService.listCheckResult(clusterPhyId, dimension, resNme);

        return this.convert2HealthScoreResultList(clusterPhyId, poList, dimension);
    }


    /**************************************************** private method ****************************************************/


    private ClusterMetrics calClusterTopicsHealthMetrics(Long clusterPhyId) {
        List<HealthCheckAggResult> resultList = healthCheckResultService.getHealthCheckAggResult(clusterPhyId, HealthCheckDimensionEnum.TOPIC);

        ClusterMetrics metrics = new ClusterMetrics(clusterPhyId);
        if (ValidateUtils.isEmptyList(resultList)) {
            metrics.getMetrics().put(CLUSTER_METRIC_HEALTH_CHECK_PASSED_TOPICS, 0.0f);
            metrics.getMetrics().put(CLUSTER_METRIC_HEALTH_CHECK_TOTAL_TOPICS, 0.0f);
        } else {
            metrics.getMetrics().put(CLUSTER_METRIC_HEALTH_CHECK_PASSED_TOPICS, this.getHealthCheckPassed(resultList));
            metrics.getMetrics().put(CLUSTER_METRIC_HEALTH_CHECK_TOTAL_TOPICS, (float)resultList.size());
        }

        // 服务未挂时，依据检查结果计算状态
        metrics.getMetrics().put(CLUSTER_METRIC_HEALTH_STATE_TOPICS, (float)this.calHealthState(resultList).getDimension());
        return metrics;
    }

    private ClusterMetrics calClusterGroupsHealthMetrics(Long clusterPhyId) {
        List<HealthCheckAggResult> resultList = healthCheckResultService.getHealthCheckAggResult(clusterPhyId, HealthCheckDimensionEnum.GROUP);

        ClusterMetrics metrics = new ClusterMetrics(clusterPhyId);
        if (ValidateUtils.isEmptyList(resultList)) {
            metrics.getMetrics().put(CLUSTER_METRIC_HEALTH_CHECK_PASSED_GROUPS, 0.0f);
            metrics.getMetrics().put(CLUSTER_METRIC_HEALTH_CHECK_TOTAL_GROUPS, 0.0f);
        } else {
            metrics.getMetrics().put(CLUSTER_METRIC_HEALTH_CHECK_PASSED_GROUPS, this.getHealthCheckPassed(resultList));
            metrics.getMetrics().put(CLUSTER_METRIC_HEALTH_CHECK_TOTAL_GROUPS, (float)resultList.size());
        }

        // 服务未挂时，依据检查结果计算状态
        metrics.getMetrics().put(CLUSTER_METRIC_HEALTH_STATE_GROUPS, (float)this.calHealthState(resultList).getDimension());
        return metrics;
    }

    private ClusterMetrics calClusterBrokersHealthMetrics(Long clusterPhyId) {
        List<HealthCheckAggResult> resultList = healthCheckResultService.getHealthCheckAggResult(clusterPhyId, HealthCheckDimensionEnum.BROKER);

        ClusterMetrics metrics = new ClusterMetrics(clusterPhyId);
        if (ValidateUtils.isEmptyList(resultList)) {
            metrics.getMetrics().put(CLUSTER_METRIC_HEALTH_CHECK_PASSED_BROKERS, 0.0f);
            metrics.getMetrics().put(CLUSTER_METRIC_HEALTH_CHECK_TOTAL_BROKERS, 0.0f);
        } else {
            metrics.getMetrics().put(CLUSTER_METRIC_HEALTH_CHECK_PASSED_BROKERS, this.getHealthCheckPassed(resultList));
            metrics.getMetrics().put(CLUSTER_METRIC_HEALTH_CHECK_TOTAL_BROKERS, (float)resultList.size());
        }

        if (brokerService.allServerDown(clusterPhyId)) {
            // 所有服务挂掉
            metrics.getMetrics().put(CLUSTER_METRIC_HEALTH_STATE_BROKERS, (float)HealthStateEnum.DEAD.getDimension());
            return metrics;
        }

        if (brokerService.existServerDown(clusterPhyId)) {
            // 存在服务挂掉
            metrics.getMetrics().put(CLUSTER_METRIC_HEALTH_STATE_BROKERS, (float)HealthStateEnum.POOR.getDimension());
            return metrics;
        }

        // 服务未挂时，依据检查结果计算状态
        metrics.getMetrics().put(CLUSTER_METRIC_HEALTH_STATE_BROKERS, (float)this.calHealthState(resultList).getDimension());
        return metrics;
    }


    /**************************************************** 聚合数据 ****************************************************/

    public List<HealthScoreResult> convert2HealthScoreResultList(Long clusterPhyId, List<HealthCheckResultPO> poList, Integer dimensionCode) {
        Map<String, List<HealthCheckResultPO>> checkResultMap = new HashMap<>();
        for (HealthCheckResultPO po: poList) {
            checkResultMap.putIfAbsent(po.getConfigName(), new ArrayList<>());
            checkResultMap.get(po.getConfigName()).add(po);
        }

        Map<String, BaseClusterHealthConfig> configMap = healthCheckResultService.getClusterHealthConfig(clusterPhyId);

        List<HealthCheckNameEnum> nameEnums =
                dimensionCode == null?
                Arrays.stream(HealthCheckNameEnum.values()).collect(Collectors.toList()): HealthCheckNameEnum.getByDimensionCode(dimensionCode);

        List<HealthScoreResult> resultList = new ArrayList<>();
        for (HealthCheckNameEnum nameEnum: nameEnums) {
            BaseClusterHealthConfig baseConfig = configMap.get(nameEnum.getConfigName());
            if (baseConfig == null) {
                continue;
            }

            resultList.add(new HealthScoreResult(nameEnum, baseConfig, checkResultMap.getOrDefault(nameEnum.getConfigName(), new ArrayList<>())));
        }

        return resultList;
    }


    /**************************************************** 计算指标 ****************************************************/


    private float getHealthCheckPassed(List<HealthCheckAggResult> aggResultList){
        if(ValidateUtils.isEmptyList(aggResultList)) {
            return 0f;
        }

        return Float.valueOf(aggResultList.stream().filter(elem -> elem.getPassed()).count());
    }

    private HealthStateEnum calHealthState(List<HealthCheckAggResult> resultList) {
        if(ValidateUtils.isEmptyList(resultList)) {
            return HealthStateEnum.GOOD;
        }

        boolean existNotPassed = false;
        for (HealthCheckAggResult aggResult: resultList) {
            if (aggResult.getCheckNameEnum().isAvailableChecker() && !aggResult.getPassed()) {
                return HealthStateEnum.POOR;
            }

            if (!aggResult.getPassed()) {
                existNotPassed = true;
            }
        }

        return existNotPassed? HealthStateEnum.MEDIUM: HealthStateEnum.GOOD;
    }
}
