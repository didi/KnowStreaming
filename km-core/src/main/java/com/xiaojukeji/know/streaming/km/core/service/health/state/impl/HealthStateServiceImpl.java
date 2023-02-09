package com.xiaojukeji.know.streaming.km.core.service.health.state.impl;

import com.xiaojukeji.know.streaming.km.common.bean.entity.broker.Broker;
import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
import com.xiaojukeji.know.streaming.km.common.bean.entity.config.healthcheck.BaseClusterHealthConfig;
import com.xiaojukeji.know.streaming.km.common.bean.entity.connect.ConnectCluster;
import com.xiaojukeji.know.streaming.km.common.bean.entity.health.HealthCheckAggResult;
import com.xiaojukeji.know.streaming.km.common.bean.entity.health.HealthScoreResult;
import com.xiaojukeji.know.streaming.km.common.bean.entity.kafkacontroller.KafkaController;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.*;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.connect.ConnectorMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.mm2.MirrorMakerMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.po.health.HealthCheckResultPO;
import com.xiaojukeji.know.streaming.km.common.component.SpringTool;
import com.xiaojukeji.know.streaming.km.common.enums.health.HealthCheckDimensionEnum;
import com.xiaojukeji.know.streaming.km.common.enums.health.HealthCheckNameEnum;
import com.xiaojukeji.know.streaming.km.common.enums.health.HealthStateEnum;
import com.xiaojukeji.know.streaming.km.common.utils.ValidateUtils;
import com.xiaojukeji.know.streaming.km.core.service.broker.BrokerService;
import com.xiaojukeji.know.streaming.km.core.service.connect.cluster.ConnectClusterService;
import com.xiaojukeji.know.streaming.km.core.service.health.checker.AbstractHealthCheckService;
import com.xiaojukeji.know.streaming.km.core.service.health.checkresult.HealthCheckResultService;
import com.xiaojukeji.know.streaming.km.core.service.health.state.HealthStateService;
import com.xiaojukeji.know.streaming.km.core.service.kafkacontroller.KafkaControllerService;
import com.xiaojukeji.know.streaming.km.core.service.zookeeper.ZookeeperService;
import com.xiaojukeji.know.streaming.km.persistence.cache.LoadedClusterPhyCache;
import com.xiaojukeji.know.streaming.km.persistence.connect.cache.LoadedConnectClusterCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.List;

import static com.xiaojukeji.know.streaming.km.common.enums.health.HealthCheckDimensionEnum.*;
import static com.xiaojukeji.know.streaming.km.common.enums.health.HealthStateEnum.DEAD;
import static com.xiaojukeji.know.streaming.km.core.service.version.metrics.connect.ConnectorMetricVersionItems.*;
import static com.xiaojukeji.know.streaming.km.core.service.version.metrics.connect.MirrorMakerMetricVersionItems.*;
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

    @Autowired
    private ConnectClusterService connectClusterService;

    @Autowired
    private KafkaControllerService kafkaControllerService;

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
        metrics.putMetric(this.calClusterConnectsHealthMetrics(clusterPhyId).getMetrics());
        metrics.putMetric(this.calClusterMirrorMakersHealthMetrics(clusterPhyId).getMetrics());

        // 统计最终结果
        Float passed = 0.0f;
        passed += metrics.getMetric(ZOOKEEPER_METRIC_HEALTH_CHECK_PASSED);
        passed += metrics.getMetric(CLUSTER_METRIC_HEALTH_CHECK_PASSED_TOPICS);
        passed += metrics.getMetric(CLUSTER_METRIC_HEALTH_CHECK_PASSED_BROKERS);
        passed += metrics.getMetric(CLUSTER_METRIC_HEALTH_CHECK_PASSED_GROUPS);
        passed += metrics.getMetric(CLUSTER_METRIC_HEALTH_CHECK_PASSED_CLUSTER);
        passed += metrics.getMetric(CLUSTER_METRIC_HEALTH_CHECK_PASSED_CONNECTOR);
        passed += metrics.getMetric(CLUSTER_METRIC_HEALTH_CHECK_PASSED_MIRROR_MAKER);

        Float total = 0.0f;
        total += metrics.getMetric(ZOOKEEPER_METRIC_HEALTH_CHECK_TOTAL);
        total += metrics.getMetric(CLUSTER_METRIC_HEALTH_CHECK_TOTAL_TOPICS);
        total += metrics.getMetric(CLUSTER_METRIC_HEALTH_CHECK_TOTAL_BROKERS);
        total += metrics.getMetric(CLUSTER_METRIC_HEALTH_CHECK_TOTAL_GROUPS);
        total += metrics.getMetric(CLUSTER_METRIC_HEALTH_CHECK_TOTAL_CLUSTER);
        total += metrics.getMetric(CLUSTER_METRIC_HEALTH_CHECK_TOTAL_CONNECTOR);
        total += metrics.getMetric(CLUSTER_METRIC_HEALTH_CHECK_TOTAL_MIRROR_MAKER);

        // 状态
        Float state = 0.0f;
        state = Math.max(state, metrics.getMetric(ZOOKEEPER_METRIC_HEALTH_STATE));
        state = Math.max(state, metrics.getMetric(CLUSTER_METRIC_HEALTH_STATE_TOPICS));
        state = Math.max(state, metrics.getMetric(CLUSTER_METRIC_HEALTH_STATE_BROKERS));
        state = Math.max(state, metrics.getMetric(CLUSTER_METRIC_HEALTH_STATE_GROUPS));
        state = Math.max(state, metrics.getMetric(CLUSTER_METRIC_HEALTH_STATE_CLUSTER));
        state = Math.max(state, metrics.getMetric(CLUSTER_METRIC_HEALTH_STATE_CONNECTOR));
        state = Math.max(state, metrics.getMetric(CLUSTER_METRIC_HEALTH_STATE_MIRROR_MAKER));

        if (isKafkaClusterDown(clusterPhyId)) {
            state = Float.valueOf(HealthStateEnum.DEAD.getDimension());
        }

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
    public ConnectorMetrics calConnectorHealthMetrics(Long connectClusterId, String connectorName) {
        ConnectCluster connectCluster = LoadedConnectClusterCache.getByPhyId(connectClusterId);
        ConnectorMetrics metrics = new ConnectorMetrics(connectClusterId, connectorName);

        // 找不到connect集群
        if (connectCluster == null) {
            metrics.putMetric(CONNECTOR_METRIC_HEALTH_STATE, (float) HealthStateEnum.DEAD.getDimension());
            return metrics;
        }

        List<HealthCheckAggResult> resultList = healthCheckResultService.getHealthCheckAggResult(connectClusterId, HealthCheckDimensionEnum.CONNECTOR, connectorName);

        if (ValidateUtils.isEmptyList(resultList)) {
            metrics.getMetrics().put(CONNECTOR_METRIC_HEALTH_CHECK_PASSED, 0.0f);
            metrics.getMetrics().put(CONNECTOR_METRIC_HEALTH_CHECK_TOTAL, 0.0f);
        } else {
            metrics.getMetrics().put(CONNECTOR_METRIC_HEALTH_CHECK_PASSED, this.getHealthCheckPassed(resultList));
            metrics.getMetrics().put(CONNECTOR_METRIC_HEALTH_CHECK_TOTAL, (float) resultList.size());
        }

        metrics.putMetric(CONNECTOR_METRIC_HEALTH_STATE, (float) this.calHealthState(resultList).getDimension());
        return metrics;
    }

    @Override
    public MirrorMakerMetrics calMirrorMakerHealthMetrics(Long connectClusterId, String mirrorMakerName) {
        ConnectCluster connectCluster = LoadedConnectClusterCache.getByPhyId(connectClusterId);
        MirrorMakerMetrics metrics = new MirrorMakerMetrics(connectClusterId, mirrorMakerName);

        if (connectCluster == null) {
            metrics.putMetric(MIRROR_MAKER_METRIC_HEALTH_STATE, (float) HealthStateEnum.DEAD.getDimension());
            return metrics;
        }

        List<HealthCheckAggResult> resultList = healthCheckResultService.getHealthCheckAggResult(connectClusterId, HealthCheckDimensionEnum.MIRROR_MAKER, mirrorMakerName);

        if (ValidateUtils.isEmptyList(resultList)) {
            metrics.getMetrics().put(MIRROR_MAKER_METRIC_HEALTH_CHECK_PASSED, 0.0f);
            metrics.getMetrics().put(MIRROR_MAKER_METRIC_HEALTH_CHECK_TOTAL, 0.0f);
        } else {
            metrics.getMetrics().put(MIRROR_MAKER_METRIC_HEALTH_CHECK_PASSED, this.getHealthCheckPassed(resultList));
            metrics.getMetrics().put(MIRROR_MAKER_METRIC_HEALTH_CHECK_TOTAL, (float) resultList.size());
        }

        metrics.putMetric(MIRROR_MAKER_METRIC_HEALTH_STATE, (float) this.calHealthState(resultList).getDimension());
        return metrics;
    }


    @Override
    public List<HealthScoreResult> getAllDimensionHealthResult(Long clusterPhyId) {
        List<Integer> supportedDimensionCodeList = new ArrayList<>();

        // 获取支持的code
        for (AbstractHealthCheckService service: SpringTool.getBeansOfType(AbstractHealthCheckService.class).values()) {
            Integer dimensionCode = service.getDimensionCodeIfSupport(clusterPhyId);
            if (dimensionCode == null) {
                continue;
            }

            supportedDimensionCodeList.add(dimensionCode);
        }

        return this.getDimensionHealthResult(clusterPhyId, supportedDimensionCodeList);
    }

    @Override
    public List<HealthScoreResult> getDimensionHealthResult(Long clusterPhyId, List<Integer> dimensionCodeList) {
        //查找健康巡查结果
        List<HealthCheckResultPO> poList = new ArrayList<>();
        for (Integer dimensionCode : dimensionCodeList) {
            if (dimensionCode.equals(HealthCheckDimensionEnum.CONNECTOR.getDimension())) {
                poList.addAll(healthCheckResultService.getConnectorHealthCheckResult(clusterPhyId));
            } else if (dimensionCode.equals(HealthCheckDimensionEnum.MIRROR_MAKER.getDimension())) {
                poList.addAll(healthCheckResultService.getMirrorMakerHealthCheckResult(clusterPhyId));
            } else {
                poList.addAll(healthCheckResultService.listCheckResult(clusterPhyId, dimensionCode));
            }
        }

        return this.getResHealthResult(clusterPhyId, dimensionCodeList, poList);
    }

    @Override
    public List<HealthScoreResult> getResHealthResult(Long clusterPhyId, Long clusterId, Integer dimension, String resNme) {
        List<HealthCheckResultPO> poList = healthCheckResultService.listCheckResult(clusterId, dimension, resNme);
        Map<String, List<HealthCheckResultPO>> checkResultMap = new HashMap<>();
        for (HealthCheckResultPO po: poList) {
            checkResultMap.putIfAbsent(po.getConfigName(), new ArrayList<>());
            checkResultMap.get(po.getConfigName()).add(po);
        }

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

    private ClusterMetrics calClusterConnectsHealthMetrics(Long clusterPhyId) {
        //获取健康巡检结果
        List<HealthCheckResultPO> connectHealthCheckResult = healthCheckResultService.getConnectorHealthCheckResult(clusterPhyId);

        connectHealthCheckResult.addAll(healthCheckResultService.listCheckResult(clusterPhyId, CONNECT_CLUSTER.getDimension()));

        List<Integer> dimensionCodeList = Arrays.asList(CONNECTOR.getDimension(), CONNECT_CLUSTER.getDimension());

        List<HealthCheckAggResult> resultList = this.getDimensionHealthCheckAggResult(connectHealthCheckResult, dimensionCodeList);

        ClusterMetrics metrics = new ClusterMetrics(clusterPhyId);

        if (ValidateUtils.isEmptyList(resultList)) {
            metrics.getMetrics().put(CLUSTER_METRIC_HEALTH_CHECK_PASSED_CONNECTOR, 0.0f);
            metrics.getMetrics().put(CLUSTER_METRIC_HEALTH_CHECK_TOTAL_CONNECTOR, 0.0f);
        } else {
            metrics.getMetrics().put(CLUSTER_METRIC_HEALTH_CHECK_PASSED_CONNECTOR, this.getHealthCheckPassed(resultList));
            metrics.getMetrics().put(CLUSTER_METRIC_HEALTH_CHECK_TOTAL_CONNECTOR, (float) resultList.size());
        }

        // 先根据connect集群状态判断
        if (connectClusterService.existConnectClusterDown(clusterPhyId)) {
            metrics.putMetric(CLUSTER_METRIC_HEALTH_STATE_CONNECTOR, (float) HealthStateEnum.POOR.getDimension());
            return metrics;
        }

        metrics.putMetric(CLUSTER_METRIC_HEALTH_STATE_CONNECTOR, (float) this.calHealthState(resultList).getDimension());
        return metrics;
    }

    private ClusterMetrics calClusterMirrorMakersHealthMetrics(Long clusterPhyId){
        List<HealthCheckResultPO> mirrorMakerHealthCheckResult = healthCheckResultService.getMirrorMakerHealthCheckResult(clusterPhyId);
        List<HealthCheckAggResult> resultList = this.getDimensionHealthCheckAggResult(mirrorMakerHealthCheckResult, Arrays.asList(MIRROR_MAKER.getDimension()));

        ClusterMetrics metrics = new ClusterMetrics(clusterPhyId);

        if (ValidateUtils.isEmptyList(resultList)) {
            metrics.getMetrics().put(CLUSTER_METRIC_HEALTH_CHECK_PASSED_MIRROR_MAKER, 0.0f);
            metrics.getMetrics().put(CLUSTER_METRIC_HEALTH_CHECK_TOTAL_MIRROR_MAKER, 0.0f);
        } else {
            metrics.getMetrics().put(CLUSTER_METRIC_HEALTH_CHECK_PASSED_MIRROR_MAKER, this.getHealthCheckPassed(resultList));
            metrics.getMetrics().put(CLUSTER_METRIC_HEALTH_CHECK_TOTAL_MIRROR_MAKER, (float) resultList.size());
        }

        metrics.putMetric(CLUSTER_METRIC_HEALTH_STATE_MIRROR_MAKER, (float) this.calHealthState(resultList).getDimension());
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


    private List<HealthCheckAggResult> getDimensionHealthCheckAggResult(List<HealthCheckResultPO> poList, List<Integer> dimensionCodeList) {
        Map<String /*检查名*/, List<HealthCheckResultPO> /*检查结果列表*/> checkResultMap = new HashMap<>();

        for (HealthCheckResultPO po : poList) {
            checkResultMap.putIfAbsent(po.getConfigName(), new ArrayList<>());
            checkResultMap.get(po.getConfigName()).add(po);
        }

        List<HealthCheckAggResult> stateList = new ArrayList<>();
        for (Integer dimensionCode : dimensionCodeList) {
            HealthCheckDimensionEnum dimensionEnum = HealthCheckDimensionEnum.getByCode(dimensionCode);

            if (dimensionEnum.equals(UNKNOWN)) {
                continue;
            }

            for (HealthCheckNameEnum nameEnum : HealthCheckNameEnum.getByDimension(dimensionEnum)) {
                stateList.add(new HealthCheckAggResult(nameEnum, checkResultMap.getOrDefault(nameEnum.getConfigName(), new ArrayList<>())));
            }
        }
        return stateList;
    }

    private List<HealthScoreResult> getResHealthResult(Long clusterPhyId, List<Integer> dimensionCodeList, List<HealthCheckResultPO> poList) {
        Map<String /*检查名*/, List<HealthCheckResultPO> /*检查结果列表*/> checkResultMap = new HashMap<>();

        for (HealthCheckResultPO po : poList) {
            checkResultMap.putIfAbsent(po.getConfigName(), new ArrayList<>());
            checkResultMap.get(po.getConfigName()).add(po);
        }

        Map<String, BaseClusterHealthConfig> configMap = healthCheckResultService.getClusterHealthConfig(clusterPhyId);

        List<HealthScoreResult> healthScoreResultList = new ArrayList<>();
        for (Integer dimensionCode : dimensionCodeList) {
            HealthCheckDimensionEnum dimensionEnum = HealthCheckDimensionEnum.getByCode(dimensionCode);

            //该维度不存在，则跳过
            if (dimensionEnum.equals(HealthCheckDimensionEnum.UNKNOWN)){
                continue;
            }

            for (HealthCheckNameEnum nameEnum : HealthCheckNameEnum.getByDimension(dimensionEnum)) {
                BaseClusterHealthConfig baseConfig = configMap.get(nameEnum.getConfigName());
                if (baseConfig == null) {
                    continue;
                }

                healthScoreResultList.add(new HealthScoreResult(nameEnum, baseConfig, checkResultMap.getOrDefault(nameEnum.getConfigName(), new ArrayList<>())));

            }
        }
        return healthScoreResultList;
    }

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

    private boolean isKafkaClusterDown(Long clusterPhyId) {
        ClusterPhy clusterPhy = LoadedClusterPhyCache.getByPhyId(clusterPhyId);
        KafkaController kafkaController = kafkaControllerService.getKafkaControllerFromDB(clusterPhyId);
        if (kafkaController != null && !kafkaController.alive()) {
            return true;
        } else if ((System.currentTimeMillis() - clusterPhy.getCreateTime().getTime() >= 5 * 60 * 1000) && kafkaController == null) {
            // 集群接入时间是在近5分钟内，同时kafkaController信息不存在，则设置为down
            return true;
        }
        return false;
    }
}
