package com.xiaojukeji.know.streaming.km.biz.version.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.didiglobal.logi.security.common.dto.config.ConfigDTO;
import com.didiglobal.logi.security.service.ConfigService;
import com.xiaojukeji.know.streaming.km.biz.version.VersionControlManager;
import com.xiaojukeji.know.streaming.km.common.bean.dto.metrices.MetricDetailDTO;
import com.xiaojukeji.know.streaming.km.common.bean.dto.metrices.UserMetricConfigDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.config.metric.UserMetricConfig;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.version.VersionControlItem;
import com.xiaojukeji.know.streaming.km.common.bean.vo.config.metric.UserMetricConfigVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.version.VersionItemVO;
import com.xiaojukeji.know.streaming.km.common.enums.version.VersionEnum;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import com.xiaojukeji.know.streaming.km.common.utils.VersionUtil;
import com.xiaojukeji.know.streaming.km.core.service.cluster.ClusterPhyService;
import com.xiaojukeji.know.streaming.km.core.service.version.VersionControlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.xiaojukeji.know.streaming.km.common.enums.version.VersionEnum.V_MAX;
import static com.xiaojukeji.know.streaming.km.common.enums.version.VersionItemTypeEnum.*;
import static com.xiaojukeji.know.streaming.km.core.service.version.metrics.kafka.BrokerMetricVersionItems.*;
import static com.xiaojukeji.know.streaming.km.core.service.version.metrics.kafka.ClusterMetricVersionItems.*;
import static com.xiaojukeji.know.streaming.km.core.service.version.metrics.kafka.GroupMetricVersionItems.*;
import static com.xiaojukeji.know.streaming.km.core.service.version.metrics.kafka.TopicMetricVersionItems.*;
import static com.xiaojukeji.know.streaming.km.core.service.version.metrics.connect.MirrorMakerMetricVersionItems.*;
import static com.xiaojukeji.know.streaming.km.core.service.version.metrics.connect.ConnectClusterMetricVersionItems.*;
import static com.xiaojukeji.know.streaming.km.core.service.version.metrics.connect.ConnectorMetricVersionItems.*;
import static com.xiaojukeji.know.streaming.km.core.service.version.metrics.kafka.ZookeeperMetricVersionItems.*;

@Service
public class VersionControlManagerImpl implements VersionControlManager {
    protected static final ILog LOGGER = LogFactory.getLog(VersionControlManagerImpl.class);

    private static final String NOT_SUPPORT_DESC  = "，(该指标只支持%s及以上的版本)";
    private static final String NOT_SUPPORT_DESC1 = "，(该指标只支持%s及以上和%s以下的版本)";

    private static final String CONFIG_GROUP      = "UserMetricConfig";

    Set<UserMetricConfig> defaultMetrics = new HashSet<>();

    @PostConstruct
    public void init(){
        // topic
        defaultMetrics.add(new UserMetricConfig(METRIC_TOPIC.getCode(), TOPIC_METRIC_HEALTH_STATE, true));
        defaultMetrics.add(new UserMetricConfig(METRIC_TOPIC.getCode(), TOPIC_METRIC_FAILED_FETCH_REQ, true));
        defaultMetrics.add(new UserMetricConfig(METRIC_TOPIC.getCode(), TOPIC_METRIC_FAILED_PRODUCE_REQ, true));
        defaultMetrics.add(new UserMetricConfig(METRIC_TOPIC.getCode(), TOPIC_METRIC_UNDER_REPLICA_PARTITIONS, true));
        defaultMetrics.add(new UserMetricConfig(METRIC_TOPIC.getCode(), TOPIC_METRIC_TOTAL_PRODUCE_REQUESTS, true));
        defaultMetrics.add(new UserMetricConfig(METRIC_TOPIC.getCode(), TOPIC_METRIC_BYTES_IN, true));
        defaultMetrics.add(new UserMetricConfig(METRIC_TOPIC.getCode(), TOPIC_METRIC_BYTES_OUT, true));
        defaultMetrics.add(new UserMetricConfig(METRIC_TOPIC.getCode(), TOPIC_METRIC_BYTES_REJECTED, true));
        defaultMetrics.add(new UserMetricConfig(METRIC_TOPIC.getCode(), TOPIC_METRIC_MESSAGE_IN, true));

        // cluster
        defaultMetrics.add(new UserMetricConfig(METRIC_CLUSTER.getCode(), CLUSTER_METRIC_HEALTH_STATE, true));
        defaultMetrics.add(new UserMetricConfig(METRIC_CLUSTER.getCode(), CLUSTER_METRIC_ACTIVE_CONTROLLER_COUNT, true));
        defaultMetrics.add(new UserMetricConfig(METRIC_CLUSTER.getCode(), CLUSTER_METRIC_BYTES_IN, true));
        defaultMetrics.add(new UserMetricConfig(METRIC_CLUSTER.getCode(), CLUSTER_METRIC_BYTES_OUT, true));
        defaultMetrics.add(new UserMetricConfig(METRIC_CLUSTER.getCode(), CLUSTER_METRIC_CONNECTIONS, true));
        defaultMetrics.add(new UserMetricConfig(METRIC_CLUSTER.getCode(), CLUSTER_METRIC_MESSAGES_IN, true));
        defaultMetrics.add(new UserMetricConfig(METRIC_CLUSTER.getCode(), CLUSTER_METRIC_PARTITIONS_NO_LEADER, true));
        defaultMetrics.add(new UserMetricConfig(METRIC_CLUSTER.getCode(), CLUSTER_METRIC_PARTITION_URP, true));
        defaultMetrics.add(new UserMetricConfig(METRIC_CLUSTER.getCode(), CLUSTER_METRIC_TOTAL_LOG_SIZE, true));
        defaultMetrics.add(new UserMetricConfig(METRIC_CLUSTER.getCode(), CLUSTER_METRIC_TOTAL_PRODUCE_REQ, true));
        defaultMetrics.add(new UserMetricConfig(METRIC_CLUSTER.getCode(), CLUSTER_METRIC_TOTAL_REQ_QUEUE_SIZE, true));
        defaultMetrics.add(new UserMetricConfig(METRIC_CLUSTER.getCode(), CLUSTER_METRIC_TOTAL_RES_QUEUE_SIZE, true));
        defaultMetrics.add(new UserMetricConfig(METRIC_CLUSTER.getCode(), CLUSTER_METRIC_GROUP_REBALANCES, true));
        defaultMetrics.add(new UserMetricConfig(METRIC_CLUSTER.getCode(), CLUSTER_METRIC_JOB_RUNNING, true));

        // group
        defaultMetrics.add(new UserMetricConfig(METRIC_GROUP.getCode(), GROUP_METRIC_OFFSET_CONSUMED, true));
        defaultMetrics.add(new UserMetricConfig(METRIC_GROUP.getCode(), GROUP_METRIC_LAG, true));
        defaultMetrics.add(new UserMetricConfig(METRIC_GROUP.getCode(), GROUP_METRIC_STATE, true));
        defaultMetrics.add(new UserMetricConfig(METRIC_GROUP.getCode(), GROUP_METRIC_HEALTH_STATE, true));

        // broker
        defaultMetrics.add(new UserMetricConfig(METRIC_BROKER.getCode(), BROKER_METRIC_HEALTH_STATE, true));
        defaultMetrics.add(new UserMetricConfig(METRIC_BROKER.getCode(), BROKER_METRIC_CONNECTION_COUNT, true));
        defaultMetrics.add(new UserMetricConfig(METRIC_BROKER.getCode(), BROKER_METRIC_MESSAGE_IN, true));
        defaultMetrics.add(new UserMetricConfig(METRIC_BROKER.getCode(), BROKER_METRIC_NETWORK_RPO_AVG_IDLE, true));
        defaultMetrics.add(new UserMetricConfig(METRIC_BROKER.getCode(), BROKER_METRIC_REQ_AVG_IDLE, true));
        defaultMetrics.add(new UserMetricConfig(METRIC_BROKER.getCode(), BROKER_METRIC_TOTAL_PRODUCE_REQ, true));
        defaultMetrics.add(new UserMetricConfig(METRIC_BROKER.getCode(), BROKER_METRIC_TOTAL_REQ_QUEUE, true));
        defaultMetrics.add(new UserMetricConfig(METRIC_BROKER.getCode(), BROKER_METRIC_TOTAL_RES_QUEUE, true));
        defaultMetrics.add(new UserMetricConfig(METRIC_BROKER.getCode(), BROKER_METRIC_LEADERS_SKEW, true));
        defaultMetrics.add(new UserMetricConfig(METRIC_BROKER.getCode(), BROKER_METRIC_UNDER_REPLICATE_PARTITION, true));
        defaultMetrics.add(new UserMetricConfig(METRIC_BROKER.getCode(), BROKER_METRIC_PARTITIONS_SKEW, true));
        defaultMetrics.add(new UserMetricConfig(METRIC_BROKER.getCode(), BROKER_METRIC_BYTES_IN, true));
        defaultMetrics.add(new UserMetricConfig(METRIC_BROKER.getCode(), BROKER_METRIC_BYTES_OUT, true));

        // zookeeper
        defaultMetrics.add(new UserMetricConfig(METRIC_ZOOKEEPER.getCode(), ZOOKEEPER_METRIC_HEALTH_STATE, true));
        defaultMetrics.add(new UserMetricConfig(METRIC_ZOOKEEPER.getCode(), ZOOKEEPER_METRIC_HEALTH_CHECK_PASSED, true));
        defaultMetrics.add(new UserMetricConfig(METRIC_ZOOKEEPER.getCode(), ZOOKEEPER_METRIC_HEALTH_CHECK_TOTAL, true));
        defaultMetrics.add(new UserMetricConfig(METRIC_ZOOKEEPER.getCode(), ZOOKEEPER_METRIC_MAX_REQUEST_LATENCY, true));
        defaultMetrics.add(new UserMetricConfig(METRIC_ZOOKEEPER.getCode(), ZOOKEEPER_METRIC_OUTSTANDING_REQUESTS, true));
        defaultMetrics.add(new UserMetricConfig(METRIC_ZOOKEEPER.getCode(), ZOOKEEPER_METRIC_NODE_COUNT, true));
        defaultMetrics.add(new UserMetricConfig(METRIC_ZOOKEEPER.getCode(), ZOOKEEPER_METRIC_WATCH_COUNT, true));
        defaultMetrics.add(new UserMetricConfig(METRIC_ZOOKEEPER.getCode(), ZOOKEEPER_METRIC_NUM_ALIVE_CONNECTIONS, true));
        defaultMetrics.add(new UserMetricConfig(METRIC_ZOOKEEPER.getCode(), ZOOKEEPER_METRIC_PACKETS_RECEIVED, true));
        defaultMetrics.add(new UserMetricConfig(METRIC_ZOOKEEPER.getCode(), ZOOKEEPER_METRIC_PACKETS_SENT, true));
        defaultMetrics.add(new UserMetricConfig(METRIC_ZOOKEEPER.getCode(), ZOOKEEPER_METRIC_EPHEMERALS_COUNT, true));
        defaultMetrics.add(new UserMetricConfig(METRIC_ZOOKEEPER.getCode(), ZOOKEEPER_METRIC_APPROXIMATE_DATA_SIZE, true));
        defaultMetrics.add(new UserMetricConfig(METRIC_ZOOKEEPER.getCode(), ZOOKEEPER_METRIC_OPEN_FILE_DESCRIPTOR_COUNT, true));
        defaultMetrics.add(new UserMetricConfig(METRIC_ZOOKEEPER.getCode(), ZOOKEEPER_METRIC_KAFKA_ZK_DISCONNECTS_PER_SEC, true));
        defaultMetrics.add(new UserMetricConfig(METRIC_ZOOKEEPER.getCode(), ZOOKEEPER_METRIC_KAFKA_ZK_SYNC_CONNECTS_PER_SEC, true));
        defaultMetrics.add(new UserMetricConfig(METRIC_ZOOKEEPER.getCode(), ZOOKEEPER_METRIC_KAFKA_ZK_REQUEST_LATENCY_99TH, true));

        // mm2
        defaultMetrics.add(new UserMetricConfig(METRIC_CONNECT_MIRROR_MAKER.getCode(), MIRROR_MAKER_METRIC_BYTE_COUNT, true));
        defaultMetrics.add(new UserMetricConfig(METRIC_CONNECT_MIRROR_MAKER.getCode(), MIRROR_MAKER_METRIC_BYTE_RATE, true));
        defaultMetrics.add(new UserMetricConfig(METRIC_CONNECT_MIRROR_MAKER.getCode(), MIRROR_MAKER_METRIC_RECORD_AGE_MS_MAX, true));
        defaultMetrics.add(new UserMetricConfig(METRIC_CONNECT_MIRROR_MAKER.getCode(), MIRROR_MAKER_METRIC_RECORD_COUNT, true));
        defaultMetrics.add(new UserMetricConfig(METRIC_CONNECT_MIRROR_MAKER.getCode(), MIRROR_MAKER_METRIC_RECORD_RATE, true));
        defaultMetrics.add(new UserMetricConfig(METRIC_CONNECT_MIRROR_MAKER.getCode(), MIRROR_MAKER_METRIC_REPLICATION_LATENCY_MS_MAX, true));

        // Connect Cluster
        defaultMetrics.add(new UserMetricConfig(METRIC_CONNECT_CLUSTER.getCode(), CONNECT_CLUSTER_METRIC_CONNECTOR_COUNT, true));
        defaultMetrics.add(new UserMetricConfig(METRIC_CONNECT_CLUSTER.getCode(), CONNECT_CLUSTER_METRIC_TASK_COUNT, true));
        defaultMetrics.add(new UserMetricConfig(METRIC_CONNECT_CLUSTER.getCode(), CONNECT_CLUSTER_METRIC_CONNECTOR_STARTUP_ATTEMPTS_TOTAL, true));
        defaultMetrics.add(new UserMetricConfig(METRIC_CONNECT_CLUSTER.getCode(), CONNECT_CLUSTER_METRIC_CONNECTOR_STARTUP_FAILURE_PERCENTAGE, true));
        defaultMetrics.add(new UserMetricConfig(METRIC_CONNECT_CLUSTER.getCode(), CONNECT_CLUSTER_METRIC_CONNECTOR_STARTUP_FAILURE_TOTAL, true));
        defaultMetrics.add(new UserMetricConfig(METRIC_CONNECT_CLUSTER.getCode(), CONNECT_CLUSTER_METRIC_TASK_STARTUP_ATTEMPTS_TOTAL, true));
        defaultMetrics.add(new UserMetricConfig(METRIC_CONNECT_CLUSTER.getCode(), CONNECT_CLUSTER_METRIC_TASK_STARTUP_FAILURE_PERCENTAGE, true));
        defaultMetrics.add(new UserMetricConfig(METRIC_CONNECT_CLUSTER.getCode(), CONNECT_CLUSTER_METRIC_TASK_STARTUP_FAILURE_TOTAL, true));
        defaultMetrics.add(new UserMetricConfig(METRIC_CONNECT_CLUSTER.getCode(), CONNECT_CLUSTER_METRIC_COLLECT_COST_TIME, true));


        // Connect Connector
        defaultMetrics.add(new UserMetricConfig(METRIC_CONNECT_CONNECTOR.getCode(), CONNECTOR_METRIC_HEALTH_STATE, true));
        defaultMetrics.add(new UserMetricConfig(METRIC_CONNECT_CONNECTOR.getCode(), CONNECTOR_METRIC_HEALTH_CHECK_PASSED, true));
        defaultMetrics.add(new UserMetricConfig(METRIC_CONNECT_CONNECTOR.getCode(), CONNECTOR_METRIC_HEALTH_CHECK_TOTAL, true));
        defaultMetrics.add(new UserMetricConfig(METRIC_CONNECT_CONNECTOR.getCode(), CONNECTOR_METRIC_COLLECT_COST_TIME, true));
        defaultMetrics.add(new UserMetricConfig(METRIC_CONNECT_CONNECTOR.getCode(), CONNECTOR_METRIC_CONNECTOR_TOTAL_TASK_COUNT, true));
        defaultMetrics.add(new UserMetricConfig(METRIC_CONNECT_CONNECTOR.getCode(), CONNECTOR_METRIC_CONNECTOR_RUNNING_TASK_COUNT, true));
        defaultMetrics.add(new UserMetricConfig(METRIC_CONNECT_CONNECTOR.getCode(), CONNECTOR_METRIC_CONNECTOR_FAILED_TASK_COUNT, true));
        defaultMetrics.add(new UserMetricConfig(METRIC_CONNECT_CONNECTOR.getCode(), CONNECTOR_METRIC_SOURCE_RECORD_ACTIVE_COUNT, true));
        defaultMetrics.add(new UserMetricConfig(METRIC_CONNECT_CONNECTOR.getCode(), CONNECTOR_METRIC_SOURCE_RECORD_POLL_TOTAL, true));
        defaultMetrics.add(new UserMetricConfig(METRIC_CONNECT_CONNECTOR.getCode(), CONNECTOR_METRIC_SOURCE_RECORD_WRITE_TOTAL, true));
        defaultMetrics.add(new UserMetricConfig(METRIC_CONNECT_CONNECTOR.getCode(), CONNECTOR_METRIC_SINK_RECORD_ACTIVE_COUNT, true));
        defaultMetrics.add(new UserMetricConfig(METRIC_CONNECT_CONNECTOR.getCode(), CONNECTOR_METRIC_SINK_RECORD_READ_TOTAL, true));
        defaultMetrics.add(new UserMetricConfig(METRIC_CONNECT_CONNECTOR.getCode(), CONNECTOR_METRIC_SINK_RECORD_SEND_TOTAL, true));
        defaultMetrics.add(new UserMetricConfig(METRIC_CONNECT_CONNECTOR.getCode(), CONNECTOR_METRIC_DEADLETTERQUEUE_PRODUCE_FAILURES, true));
        defaultMetrics.add(new UserMetricConfig(METRIC_CONNECT_CONNECTOR.getCode(), CONNECTOR_METRIC_DEADLETTERQUEUE_PRODUCE_REQUESTS, true));
        defaultMetrics.add(new UserMetricConfig(METRIC_CONNECT_CONNECTOR.getCode(), CONNECTOR_METRIC_TOTAL_ERRORS_LOGGED, true));
        defaultMetrics.add(new UserMetricConfig(METRIC_CONNECT_CONNECTOR.getCode(), CONNECTOR_METRIC_SOURCE_RECORD_POLL_RATE, true));
        defaultMetrics.add(new UserMetricConfig(METRIC_CONNECT_CONNECTOR.getCode(), CONNECTOR_METRIC_SOURCE_RECORD_WRITE_RATE, true));
        defaultMetrics.add(new UserMetricConfig(METRIC_CONNECT_CONNECTOR.getCode(), CONNECTOR_METRIC_SINK_RECORD_READ_RATE, true));
        defaultMetrics.add(new UserMetricConfig(METRIC_CONNECT_CONNECTOR.getCode(), CONNECTOR_METRIC_SINK_RECORD_SEND_RATE, true));


    }

    @Autowired
    private ClusterPhyService clusterPhyService;

    @Autowired
    private VersionControlService versionControlService;

    @Autowired
    private ConfigService configService;

    @Override
    public Result<Map<String, VersionItemVO>> listAllVersionItem() {
        List<VersionItemVO> allVersionItemVO = new ArrayList<>();
        allVersionItemVO.addAll(ConvertUtil.list2List(versionControlService.listVersionControlItem(METRIC_TOPIC.getCode()), VersionItemVO.class));
        allVersionItemVO.addAll(ConvertUtil.list2List(versionControlService.listVersionControlItem(METRIC_CLUSTER.getCode()), VersionItemVO.class));
        allVersionItemVO.addAll(ConvertUtil.list2List(versionControlService.listVersionControlItem(METRIC_GROUP.getCode()), VersionItemVO.class));
        allVersionItemVO.addAll(ConvertUtil.list2List(versionControlService.listVersionControlItem(METRIC_BROKER.getCode()), VersionItemVO.class));
        allVersionItemVO.addAll(ConvertUtil.list2List(versionControlService.listVersionControlItem(METRIC_PARTITION.getCode()), VersionItemVO.class));
        allVersionItemVO.addAll(ConvertUtil.list2List(versionControlService.listVersionControlItem(METRIC_REPLICATION.getCode()), VersionItemVO.class));

        allVersionItemVO.addAll(ConvertUtil.list2List(versionControlService.listVersionControlItem(METRIC_ZOOKEEPER.getCode()), VersionItemVO.class));

        allVersionItemVO.addAll(ConvertUtil.list2List(versionControlService.listVersionControlItem(METRIC_CONNECT_CLUSTER.getCode()), VersionItemVO.class));
        allVersionItemVO.addAll(ConvertUtil.list2List(versionControlService.listVersionControlItem(METRIC_CONNECT_CONNECTOR.getCode()), VersionItemVO.class));
        allVersionItemVO.addAll(ConvertUtil.list2List(versionControlService.listVersionControlItem(METRIC_CONNECT_MIRROR_MAKER.getCode()), VersionItemVO.class));

        allVersionItemVO.addAll(ConvertUtil.list2List(versionControlService.listVersionControlItem(WEB_OP.getCode()), VersionItemVO.class));

        Map<String, VersionItemVO> map = allVersionItemVO.stream().collect(
                Collectors.toMap(
                        u -> u.getType() + "@" + u.getName(),
                        Function.identity(),
                        (v1, v2) -> v1)
        );

        return Result.buildSuc(map);
    }

    @Override
    public Result<Map<String, Long>> listAllKafkaVersions() {
        return Result.buildSuc(VersionEnum.allVersionsWithOutMax());
    }

    @Override
    public Result<List<VersionItemVO>> listKafkaClusterVersionControlItem(Long clusterId, Integer type) {
        List<VersionControlItem> allItem   = versionControlService.listVersionControlItem(type);
        List<VersionItemVO> versionItemVOS = new ArrayList<>();

        String versionStr  = clusterPhyService.getVersionFromCacheFirst(clusterId);

        for (VersionControlItem item : allItem){
            VersionItemVO itemVO = ConvertUtil.obj2Obj(item, VersionItemVO.class);
            boolean      support = versionControlService.isClusterSupport(versionStr, item);

            itemVO.setSupport(support);
            itemVO.setDesc(itemSupportDesc(item, support));

            versionItemVOS.add(itemVO);
        }

        return Result.buildSuc(versionItemVOS);
    }

    @Override
    public Result<List<UserMetricConfigVO>> listUserMetricItem(Long clusterId, Integer type, String operator) {
        Result<List<VersionItemVO>> ret = listKafkaClusterVersionControlItem(clusterId, type);
        if(null == ret || ret.failed()){
            return Result.buildFail();
        }

        List<UserMetricConfigVO> userMetricConfigVOS = new ArrayList<>();
        List<VersionItemVO>      allVersionItemVOs   = ret.getData();
        Set<UserMetricConfig>    userMetricConfigs   = getUserMetricConfig(operator);

        Map<String, UserMetricConfig> userMetricConfigMap = userMetricConfigs.stream().collect(
                Collectors.toMap(u -> u.getType() + "@" + u.getMetric(), Function.identity() ));

        for(VersionItemVO itemVO : allVersionItemVOs){
            UserMetricConfigVO userMetricConfigVO = new UserMetricConfigVO();

            int    itemType = itemVO.getType();
            String metric   = itemVO.getName();

            UserMetricConfig umc = userMetricConfigMap.get(itemType + "@" + metric);
            userMetricConfigVO.setSet(null != umc && umc.isSet());
            if (umc != null) {
                userMetricConfigVO.setRank(umc.getRank());
            }
            userMetricConfigVO.setName(itemVO.getName());
            userMetricConfigVO.setType(itemVO.getType());
            userMetricConfigVO.setDesc(itemVO.getDesc());
            userMetricConfigVO.setMinVersion(itemVO.getMinVersion());
            userMetricConfigVO.setMaxVersion(itemVO.getMaxVersion());
            userMetricConfigVO.setSupport(itemVO.getSupport());

            userMetricConfigVOS.add(userMetricConfigVO);
        }

        LOGGER.debug("method=listUserMetricItem||clusterId={}||type={}||operator={}||userMetricConfigs={}||userMetricConfigVO={}",
                clusterId, type, operator, JSON.toJSONString(userMetricConfigs), JSON.toJSONString(userMetricConfigVOS));

        return Result.buildSuc(userMetricConfigVOS);
    }

    @Override
    public Result<Void> updateUserMetricItem(Long clusterId, Integer type, UserMetricConfigDTO dto, String operator) {
        Map<String, Boolean> metricsSetMap = dto.getMetricsSet();

        //转换metricDetailDTOList
        List<MetricDetailDTO> metricDetailDTOList = dto.getMetricDetailDTOList();
        Map<String, MetricDetailDTO> metricDetailMap = new HashMap<>();
        if (metricDetailDTOList != null && !metricDetailDTOList.isEmpty()) {
            metricDetailMap = metricDetailDTOList.stream().collect(Collectors.toMap(MetricDetailDTO::getMetric, Function.identity()));
        }

        //转换metricsSetMap
        if (metricsSetMap != null && !metricsSetMap.isEmpty()) {
            for (Map.Entry<String, Boolean> metricAndShowEntry : metricsSetMap.entrySet()) {
                if (metricDetailMap.containsKey(metricAndShowEntry.getKey())) continue;
                metricDetailMap.put(metricAndShowEntry.getKey(), new MetricDetailDTO(metricAndShowEntry.getKey(), metricAndShowEntry.getValue(), null));
            }
        }

        if (metricDetailMap.isEmpty()) {
            return Result.buildSuc();
        }

        Set<UserMetricConfig> userMetricConfigs = getUserMetricConfig(operator);
        for (MetricDetailDTO metricDetailDTO : metricDetailMap.values()) {
            UserMetricConfig userMetricConfig = new UserMetricConfig(type, metricDetailDTO.getMetric(), metricDetailDTO.getSet(), metricDetailDTO.getRank());
            userMetricConfigs.remove(userMetricConfig);
            userMetricConfigs.add(userMetricConfig);
        }

        ConfigDTO configDTO = new ConfigDTO();
        configDTO.setValueGroup(CONFIG_GROUP);
        configDTO.setValueName(operator);
        configDTO.setValue(JSON.toJSONString(userMetricConfigs));
        configDTO.setOperator(operator);
        configDTO.setStatus(1);

        com.didiglobal.logi.security.common.Result<Void> result = configService.editConfig(configDTO, operator);


        LOGGER.debug("method=updateUserMetricItem||clusterId={}||type={}||operator={}||userMetricConfigs={}||metricsSetMap={}",
                clusterId, type, operator, JSON.toJSONString(userMetricConfigs), JSON.toJSONString(metricsSetMap));

        return Result.build(result.successed());
    }

    /**************************************************** private method ****************************************************/

    private String itemSupportDesc(VersionControlItem item, boolean support){
        if(support){return item.getDesc();}

        boolean bMaxVersion = (item.getMaxVersion() == V_MAX.getVersionL().longValue());

        String minVersion = VersionUtil.dNormailze(item.getMinVersion());
        String maxVersion = VersionUtil.dNormailze(item.getMaxVersion());

        if(bMaxVersion){
            return item.getDesc() + String.format(NOT_SUPPORT_DESC, minVersion);
        }

        return item.getDesc() + String.format(NOT_SUPPORT_DESC1, minVersion, maxVersion);
    }

    private Set<UserMetricConfig> getUserMetricConfig(String operator){
        String value = configService.stringSetting(CONFIG_GROUP, operator, "");
        if(StringUtils.isEmpty(value)){
            return defaultMetrics;
        }

        return JSON.parseObject(value, new TypeReference<Set<UserMetricConfig>>() {});
    }

    public static void main(String[] args){
        Set<UserMetricConfig> defaultMetrics = new HashSet<>();

        defaultMetrics.add(new UserMetricConfig(METRIC_TOPIC.getCode(), TOPIC_METRIC_BYTES_IN, true));
        defaultMetrics.add(new UserMetricConfig(METRIC_TOPIC.getCode(), TOPIC_METRIC_MESSAGES, true));
        defaultMetrics.add(new UserMetricConfig(METRIC_TOPIC.getCode(), TOPIC_METRIC_MESSAGES, true));

        String value = JSON.toJSONString(defaultMetrics);

        Set<UserMetricConfig> userMetricConfigs = JSON.parseObject(value, new TypeReference<Set<UserMetricConfig>>(){});

        System.out.println(value);
    }
}
