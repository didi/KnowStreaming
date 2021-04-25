package com.xiaojukeji.kafka.manager.service.cache;

import com.xiaojukeji.kafka.manager.common.bizenum.KafkaBrokerRoleEnum;
import com.xiaojukeji.kafka.manager.common.constant.Constant;
import com.xiaojukeji.kafka.manager.common.constant.KafkaConstant;
import com.xiaojukeji.kafka.manager.common.entity.KafkaVersion;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterDO;
import com.xiaojukeji.kafka.manager.common.utils.JsonUtils;
import com.xiaojukeji.kafka.manager.common.utils.ListUtils;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.common.utils.jmx.JmxConfig;
import com.xiaojukeji.kafka.manager.common.utils.jmx.JmxConnectorWrap;
import com.xiaojukeji.kafka.manager.common.zookeeper.ZkConfigImpl;
import com.xiaojukeji.kafka.manager.common.zookeeper.ZkPathUtil;
import com.xiaojukeji.kafka.manager.common.zookeeper.znode.ControllerData;
import com.xiaojukeji.kafka.manager.common.zookeeper.znode.brokers.BrokerMetadata;
import com.xiaojukeji.kafka.manager.common.zookeeper.znode.brokers.TopicMetadata;
import com.xiaojukeji.kafka.manager.dao.ControllerDao;
import com.xiaojukeji.kafka.manager.service.service.ClusterService;
import com.xiaojukeji.kafka.manager.service.service.JmxService;
import com.xiaojukeji.kafka.manager.service.zookeeper.BrokerStateListener;
import com.xiaojukeji.kafka.manager.service.zookeeper.ControllerStateListener;
import com.xiaojukeji.kafka.manager.service.zookeeper.TopicStateListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 物理集群元信息
 * @author zengqiao
 * @date 19/4/3
 */
@Service
public class PhysicalClusterMetadataManager {
    private final static Logger LOGGER = LoggerFactory.getLogger(PhysicalClusterMetadataManager.class);

    @Autowired
    private ControllerDao controllerDao;

    @Autowired
    private JmxService jmxService;

    @Autowired
    private ClusterService clusterService;

    private final static Map<Long, ClusterDO> CLUSTER_MAP = new ConcurrentHashMap<>();

    private final static Map<Long, ControllerData> CONTROLLER_DATA_MAP = new ConcurrentHashMap<>();

    private final static Map<Long, ZkConfigImpl> ZK_CONFIG_MAP = new ConcurrentHashMap<>();

    private final static Map<Long, Map<String, TopicMetadata>> TOPIC_METADATA_MAP = new ConcurrentHashMap<>();

    private final static Map<Long, Map<String, Long>> TOPIC_RETENTION_TIME_MAP = new ConcurrentHashMap<>();

    private final static Map<Long, Map<Integer, BrokerMetadata>> BROKER_METADATA_MAP = new ConcurrentHashMap<>();

    /**
     * JXM连接, 延迟连接
     */
    private final static Map<Long, Map<Integer, JmxConnectorWrap>> JMX_CONNECTOR_MAP = new ConcurrentHashMap<>();

    /**
     * KafkaBroker版本, 延迟获取
     */
    private static final Map<Long, Map<Integer, KafkaVersion>> KAFKA_VERSION_MAP = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        LOGGER.info("cluster metadata initialization start.");
        List<ClusterDO> doList = clusterService.list();
        for (ClusterDO elem : doList) {
            LOGGER.info("cluster metadata initializing, clusterId:{}.", elem.getId());
            addNew(elem);
        }
        LOGGER.info("cluster metadata initialization finished.");
    }

    public synchronized void addNew(ClusterDO clusterDO) {
        try {
            if (ZK_CONFIG_MAP.containsKey(clusterDO.getId())) {
                return;
            }
            ZkConfigImpl zkConfig = new ZkConfigImpl(clusterDO.getZookeeper());

            // 初始化broker-map
            BROKER_METADATA_MAP.put(clusterDO.getId(), new ConcurrentHashMap<>());
            JMX_CONNECTOR_MAP.put(clusterDO.getId(), new ConcurrentHashMap<>());
            KAFKA_VERSION_MAP.put(clusterDO.getId(), new ConcurrentHashMap<>());

            // 初始化topic-map
            TOPIC_METADATA_MAP.put(clusterDO.getId(), new ConcurrentHashMap<>());
            TOPIC_RETENTION_TIME_MAP.put(clusterDO.getId(), new ConcurrentHashMap<>());

            // 初始化cluster-map
            CLUSTER_MAP.put(clusterDO.getId(), clusterDO);

            if (!zkConfig.checkPathExists(ZkPathUtil.BROKER_ROOT_NODE)) {
                LOGGER.info("ignore add cluster, zk path=/brokers not exist, clusterId:{}.", clusterDO.getId());
                try {
                    zkConfig.close();
                } catch (Exception e) {
                    LOGGER.warn("ignore add cluster, close zk connection failed, cluster:{}.", clusterDO, e);
                }
                return;
            }

            JmxConfig jmxConfig = null;
            try {
                jmxConfig = JsonUtils.stringToObj(clusterDO.getJmxProperties(), JmxConfig.class);
            } catch (Exception e) {
                LOGGER.error("class=PhysicalClusterMetadataManager||method=addNew||clusterDO={}||msg=parse jmx properties failed", JsonUtils.toJSONString(clusterDO));
            }

            //增加Broker监控
            BrokerStateListener brokerListener = new BrokerStateListener(clusterDO.getId(), zkConfig, jmxConfig);
            brokerListener.init();
            zkConfig.watchChildren(ZkPathUtil.BROKER_IDS_ROOT, brokerListener);

            //增加Topic监控
            TopicStateListener topicListener = new TopicStateListener(clusterDO.getId(), zkConfig);
            topicListener.init();
            zkConfig.watchChildren(ZkPathUtil.BROKER_TOPICS_ROOT, topicListener);

            //增加Controller监控
            ControllerStateListener controllerListener =
                    new ControllerStateListener(clusterDO.getId(), zkConfig, controllerDao);
            controllerListener.init();
            zkConfig.watch(ZkPathUtil.CONTROLLER_ROOT_NODE, controllerListener);

            ZK_CONFIG_MAP.put(clusterDO.getId(), zkConfig);
        } catch (Exception e) {
            LOGGER.error("add cluster failed, cluster:{}.", clusterDO, e);
        }
    }

    public void remove(Long clusterId) {
        try {
            ZkConfigImpl zkConfig = ZK_CONFIG_MAP.remove(clusterId);
            if (zkConfig != null) {
                zkConfig.cancelWatchChildren(ZkPathUtil.BROKER_IDS_ROOT);
                zkConfig.cancelWatchChildren(ZkPathUtil.BROKER_TOPICS_ROOT);
                zkConfig.cancelWatchChildren(ZkPathUtil.CONTROLLER_ROOT_NODE);
                zkConfig.close();
            }
        } catch (Exception e) {
            LOGGER.error("remove cluster metadata failed, clusterId:{}.", clusterId, e);
        }
        CONTROLLER_DATA_MAP.remove(clusterId);

        BROKER_METADATA_MAP.remove(clusterId);
        JMX_CONNECTOR_MAP.remove(clusterId);
        KAFKA_VERSION_MAP.remove(clusterId);

        TOPIC_METADATA_MAP.remove(clusterId);
        TOPIC_RETENTION_TIME_MAP.remove(clusterId);
        CLUSTER_MAP.remove(clusterId);
    }

    public static Map<Long, ClusterDO> getClusterMap() {
        return CLUSTER_MAP;
    }

    public static void updateClusterMap(ClusterDO clusterDO) {
        CLUSTER_MAP.put(clusterDO.getId(), clusterDO);
    }

    public static ClusterDO getClusterFromCache(Long clusterId) {
        return CLUSTER_MAP.get(clusterId);
    }

    //---------------------------Controller元信息相关--------------

    public static ControllerData removeControllerData(Long clusterId) {
        return CONTROLLER_DATA_MAP.remove(clusterId);
    }

    public static void putControllerData(Long clusterId, ControllerData controllerData) {
        CONTROLLER_DATA_MAP.put(clusterId, controllerData);
    }

    public static Integer getControllerId(Long clusterId) {
        ControllerData data = CONTROLLER_DATA_MAP.get(clusterId);
        if (data == null) {
            return null;
        }
        return data.getBrokerid();
    }


    //---------------------------Topic元信息相关--------------

    public static void putTopicMetadata(Long clusterId, String topicName, TopicMetadata topicMetadata) {
        Map<String, TopicMetadata> metadataMap = TOPIC_METADATA_MAP.get(clusterId);
        if (metadataMap == null) {
            return;
        }
        metadataMap.put(topicName, topicMetadata);
    }

    public static TopicMetadata removeTopicMetadata(Long clusterId, String topicName) {
        Map<String, TopicMetadata> metadataMap = TOPIC_METADATA_MAP.get(clusterId);
        if (metadataMap == null) {
            return null;
        }
        return metadataMap.remove(topicName);
    }

    public static TopicMetadata getTopicMetadata(Long clusterId, String topicName) {
        Map<String, TopicMetadata> metadataMap = TOPIC_METADATA_MAP.get(clusterId);
        if (metadataMap == null) {
            return null;
        }
        return metadataMap.get(topicName);
    }

    public static List<String> getTopicNameList(Long clusterId) {
        Map<String, TopicMetadata> metadataMap = TOPIC_METADATA_MAP.get(clusterId);
        if (metadataMap == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(metadataMap.keySet());
    }

    public static boolean isTopicExist(Long clusterId, String topicName) {
        Map<String, TopicMetadata> metadataMap = TOPIC_METADATA_MAP.get(clusterId);
        return metadataMap != null && metadataMap.containsKey(topicName);
    }

    public static boolean isTopicExistStrictly(Long clusterId, String topicName) {
        Map<String, TopicMetadata> metadataMap = TOPIC_METADATA_MAP.get(clusterId);
        if (metadataMap == null) {
            // 集群不存在, 直接false
            return false;
        }
        if (metadataMap.containsKey(topicName)) {
            // 存在则一定存在
            return true;
        }

        // 不存在则可能是因为重启导致的
        try {
            ZkConfigImpl zkConfig = ZK_CONFIG_MAP.get(clusterId);
            if (zkConfig == null) {
                return false;
            }
            if (zkConfig.checkPathExists(ZkPathUtil.getConfigTopicNode(topicName))) {
                return true;
            }
        } catch (Exception e) {
            LOGGER.error("unknown whether topic exist, clusterId:{} topicName:{}.", clusterId, topicName, e);
        }
        return false;
    }



    //---------------------------配置相关元信息--------------

    public static void putTopicRetentionTime(Long clusterId, String topicName, Long retentionTime) {
        Map<String, Long> timeMap = TOPIC_RETENTION_TIME_MAP.get(clusterId);
        if (timeMap == null) {
            return;
        }
        timeMap.put(topicName, retentionTime);
    }

    public static Long getTopicRetentionTime(Long clusterId, String topicName) {
        Map<String, Long> timeMap = TOPIC_RETENTION_TIME_MAP.get(clusterId);
        if (timeMap == null) {
            return null;
        }
        return timeMap.get(topicName);
    }




    //---------------------------Broker元信息相关--------------

    public static void putBrokerMetadata(Long clusterId, Integer brokerId, BrokerMetadata brokerMetadata, JmxConfig jmxConfig) {
        Map<Integer, BrokerMetadata> metadataMap = BROKER_METADATA_MAP.get(clusterId);
        if (metadataMap == null) {
            return;
        }
        metadataMap.put(brokerId, brokerMetadata);

        Map<Integer, JmxConnectorWrap> jmxMap = JMX_CONNECTOR_MAP.getOrDefault(clusterId, new ConcurrentHashMap<>());
        jmxMap.put(brokerId, new JmxConnectorWrap(brokerMetadata.getHost(), brokerMetadata.getJmxPort(), jmxConfig));
        JMX_CONNECTOR_MAP.put(clusterId, jmxMap);

        Map<Integer, KafkaVersion> versionMap = KAFKA_VERSION_MAP.getOrDefault(clusterId, new ConcurrentHashMap<>());
        versionMap.put(brokerId, new KafkaVersion());
        KAFKA_VERSION_MAP.put(clusterId, versionMap);
    }

    public static void removeBrokerMetadata(Long clusterId, Integer brokerId) {
        Map<Integer, JmxConnectorWrap> jmxMap = JMX_CONNECTOR_MAP.get(clusterId);
        JmxConnectorWrap jmxConnectorWrap = jmxMap == null? null: jmxMap.remove(brokerId);

        Map<Integer, KafkaVersion> versionMap = KAFKA_VERSION_MAP.get(clusterId);
        if (versionMap != null) {
            versionMap.remove(brokerId);
        }

        Map<Integer, BrokerMetadata> metadataMap = BROKER_METADATA_MAP.get(clusterId);
        if (metadataMap == null) {
            return;
        }
        metadataMap.remove(brokerId);

        if (jmxConnectorWrap != null) {
            jmxConnectorWrap.close();
        }
    }

    public static boolean isBrokerAlive(Long clusterId, Integer brokerId) {
        Map<Integer, BrokerMetadata> metadataMap = BROKER_METADATA_MAP.get(clusterId);
        if (metadataMap == null) {
            return false;
        }
        return metadataMap.containsKey(brokerId);
    }

    public static BrokerMetadata getBrokerMetadata(Long clusterId, Integer brokerId) {
        Map<Integer, BrokerMetadata> metadataMap = BROKER_METADATA_MAP.get(clusterId);
        if (metadataMap == null) {
            return null;
        }
        return metadataMap.get(brokerId);
    }

    public static BrokerMetadata getBrokerMetadata(Long clusterId, String hostname) {
        Map<Integer, BrokerMetadata> metadataMap = BROKER_METADATA_MAP.get(clusterId);
        if (metadataMap == null) {
            return null;
        }
        for (BrokerMetadata brokerMetadata: metadataMap.values()) {
            if (brokerMetadata.getHost().equals(hostname)) {
                return brokerMetadata;
            }
        }
        return null;
    }

    public static Map<String, List<String>> getBrokerHostKafkaRoleMap(Long clusterId) {
        Map<String, List<String>> hostRoleMap = new HashMap<>();
        ControllerData controllerData = CONTROLLER_DATA_MAP.get(clusterId);
        if (controllerData != null) {
            supplyHostRoleData(hostRoleMap, clusterId, controllerData.getBrokerid(), KafkaBrokerRoleEnum.CONTROLLER);
        }

        TopicMetadata topicMetadata =
                PhysicalClusterMetadataManager.getTopicMetadata(clusterId, KafkaConstant.COORDINATOR_TOPIC_NAME);
        if (topicMetadata != null) {
            for (Integer brokerId: topicMetadata.getBrokerIdSet()) {
                supplyHostRoleData(hostRoleMap, clusterId, brokerId, KafkaBrokerRoleEnum.COORDINATOR);
            }
        }
        List<Integer> brokerIdList = PhysicalClusterMetadataManager.getBrokerIdList(clusterId);
        for (Integer brokerId: brokerIdList) {
            supplyHostRoleData(hostRoleMap, clusterId, brokerId, KafkaBrokerRoleEnum.NORMAL);
        }
        return hostRoleMap;
    }

    private static void supplyHostRoleData(Map<String, List<String>> hostRoleMap,
                                           Long clusterId,
                                           Integer brokerId,
                                           KafkaBrokerRoleEnum roleEnum) {
        BrokerMetadata brokerMetadata =
                PhysicalClusterMetadataManager.getBrokerMetadata(clusterId, brokerId);
        if (ValidateUtils.isNull(brokerMetadata)) {
            return;
        }
        String hostname = brokerMetadata.getHost().replace(KafkaConstant.BROKER_HOST_NAME_SUFFIX, "");
        if (KafkaBrokerRoleEnum.NORMAL.equals(roleEnum) && hostRoleMap.containsKey(hostname)) {
            return;
        }

        List<String> roleList = hostRoleMap.getOrDefault(brokerMetadata.getHost(), new ArrayList<>());
        roleList.add(roleEnum.getRole());
        hostRoleMap.put(hostname, roleList);
    }

    public static Map<String, List<String>> getKafkaRoleBrokerHostMap(Long clusterId) {
        Map<String, List<String>> roleHostMap = new HashMap<>(3);
        ControllerData controllerData = CONTROLLER_DATA_MAP.get(clusterId);
        if (controllerData != null) {
            supplyRoleHostData(roleHostMap, clusterId, controllerData.getBrokerid(), KafkaBrokerRoleEnum.CONTROLLER);
        }

        TopicMetadata topicMetadata =
                PhysicalClusterMetadataManager.getTopicMetadata(clusterId, KafkaConstant.COORDINATOR_TOPIC_NAME);
        if (topicMetadata != null) {
            for (Integer brokerId: topicMetadata.getBrokerIdSet()) {
                supplyRoleHostData(roleHostMap, clusterId, brokerId, KafkaBrokerRoleEnum.COORDINATOR);
            }
        }
        List<Integer> brokerIdList = PhysicalClusterMetadataManager.getBrokerIdList(clusterId);
        for (Integer brokerId: brokerIdList) {
            supplyRoleHostData(roleHostMap, clusterId, brokerId, KafkaBrokerRoleEnum.NORMAL);
        }
        return roleHostMap;
    }

    private static void supplyRoleHostData(Map<String, List<String>> roleHostMap,
                                           Long clusterId,
                                           Integer brokerId,
                                           KafkaBrokerRoleEnum roleEnum) {
        BrokerMetadata brokerMetadata =
                PhysicalClusterMetadataManager.getBrokerMetadata(clusterId, brokerId);
        if (ValidateUtils.isNull(brokerMetadata)) {
            return;
        }

        List<String> hostList = roleHostMap.getOrDefault(roleEnum.getRole(), new ArrayList<>());
        hostList.add(brokerMetadata.getHost().replace(KafkaConstant.BROKER_HOST_NAME_SUFFIX, ""));
        roleHostMap.put(roleEnum.getRole(), hostList);
    }

    public static List<Integer> getBrokerIdList(Long clusterId) {
        Map<Integer, BrokerMetadata> metadataMap = BROKER_METADATA_MAP.get(clusterId);
        if (metadataMap == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(metadataMap.keySet());
    }

    public static Set<String> getBrokerHostnameSet(Long clusterId) {
        Map<Integer, BrokerMetadata> metadataMap = BROKER_METADATA_MAP.get(clusterId);
        if (metadataMap == null) {
            return new HashSet<>();
        }
        Set<String> hostnameSet = new HashSet<>();
        for (Map.Entry<Integer, BrokerMetadata> entry: metadataMap.entrySet()) {
            hostnameSet.add(entry.getValue().getHost().replace(KafkaConstant.BROKER_HOST_NAME_SUFFIX, ""));
        }
        return hostnameSet;
    }

    public static JmxConnectorWrap getJmxConnectorWrap(Long clusterId, Integer brokerId) {
        Map<Integer, JmxConnectorWrap> jmxConnectorWrapMap = JMX_CONNECTOR_MAP.get(clusterId);
        if (jmxConnectorWrapMap == null) {
            return null;
        }
        return jmxConnectorWrapMap.get(brokerId);
    }

    public KafkaVersion getKafkaVersion(Long clusterId, Integer brokerId) {
        Map<Integer, KafkaVersion> versionMap = KAFKA_VERSION_MAP.get(clusterId);
        if (versionMap == null) {
            return new KafkaVersion();
        }
        KafkaVersion kafkaVersion = versionMap.get(brokerId);
        if (kafkaVersion == null) {
            return new KafkaVersion();
        }
        if (kafkaVersion.initialized()) {
            return kafkaVersion;
        }
        kafkaVersion.init(jmxService.getBrokerVersion(clusterId, brokerId));
        return kafkaVersion;
    }

    public String getKafkaVersionFromCache(Long clusterId) {
        Set<String> kafkaVersionSet = new HashSet<>();
        for (Integer brokerId: PhysicalClusterMetadataManager.getBrokerIdList(clusterId)) {
            String kafkaVersion = this.getKafkaVersionFromCache(clusterId, brokerId);
            if (ValidateUtils.isBlank(kafkaVersion)) {
                continue;
            }
            kafkaVersionSet.add(kafkaVersion);
        }
        return ListUtils.strList2String(new ArrayList<>(kafkaVersionSet));
    }

    public String getKafkaVersion(Long clusterId, List<Integer> brokerIdList) {
        Set<String> kafkaVersionSet = new HashSet<>();
        for (Integer brokerId: brokerIdList) {
            KafkaVersion kafkaVersion = this.getKafkaVersion(clusterId, brokerId);
            if (kafkaVersion == null) {
                kafkaVersionSet.add(Constant.UNKNOWN_VERSION);
                continue;
            }
            kafkaVersionSet.add(kafkaVersion.getVersion());
        }
        return ListUtils.strList2String(new ArrayList<>(kafkaVersionSet));
    }

    public String getKafkaVersionFromCache(Long clusterId, Integer brokerId) {
        Map<Integer, KafkaVersion> versionMap = KAFKA_VERSION_MAP.get(clusterId);
        if (versionMap == null) {
            return null;
        }
        KafkaVersion kafkaVersion = versionMap.get(brokerId);
        if (kafkaVersion == null) {
            return null;
        }
        if (kafkaVersion.initialized()) {
            return kafkaVersion.getVersion();
        }
        return null;
    }

    public static ZkConfigImpl getZKConfig(Long clusterId) {
        if (!ZK_CONFIG_MAP.containsKey(clusterId)) {
            return null;
        }
        return ZK_CONFIG_MAP.get(clusterId);
    }

    public static Set<String> getBrokerTopicNum(Long clusterId, Set<Integer> brokerIdSet) {
        Set<String> topicNameSet = new HashSet<>();

        Map<String, TopicMetadata> metadataMap = TOPIC_METADATA_MAP.get(clusterId);
        for (String topicName: metadataMap.keySet()) {
            try {
                TopicMetadata tm = metadataMap.get(topicName);
                for (Integer brokerId: tm.getBrokerIdSet()) {
                    if (!brokerIdSet.contains(brokerId)) {
                        continue;
                    }
                    topicNameSet.add(topicName);
                }
            } catch (Exception e) {
            }
        }
        return topicNameSet;
    }

    public static long getNotAliveBrokerNum(Long clusterId, List<Integer> brokerIdList) {
        Set<Integer> aliveBrokerIdSet = new HashSet<>(getBrokerIdList(clusterId));
        return brokerIdList.stream()
                .filter(brokerId -> !aliveBrokerIdSet.contains(brokerId))
                .count();
    }
}
