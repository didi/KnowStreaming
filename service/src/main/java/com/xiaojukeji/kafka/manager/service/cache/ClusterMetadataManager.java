package com.xiaojukeji.kafka.manager.service.cache;

import com.xiaojukeji.kafka.manager.common.entity.po.ClusterDO;
import com.xiaojukeji.kafka.manager.common.entity.po.ControllerDO;
import com.xiaojukeji.kafka.manager.common.entity.zookeeper.BrokerMetadata;
import com.xiaojukeji.kafka.manager.common.entity.zookeeper.ControllerData;
import com.xiaojukeji.kafka.manager.common.entity.zookeeper.PartitionMap;
import com.xiaojukeji.kafka.manager.common.entity.zookeeper.TopicMetadata;
import com.xiaojukeji.kafka.manager.common.exception.ConfigException;
import com.xiaojukeji.kafka.manager.common.utils.zk.StateChangeListener;
import com.xiaojukeji.kafka.manager.common.utils.zk.ZkConfigImpl;
import com.xiaojukeji.kafka.manager.dao.ControllerDao;
import com.xiaojukeji.kafka.manager.common.utils.jmx.JmxConnectorWrap;
import com.xiaojukeji.kafka.manager.service.service.ClusterService;
import com.xiaojukeji.kafka.manager.common.utils.zk.ZkPathUtil;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 集群缓存信息
 * @author zengqiao
 * @date 19/4/3
 */
@Service
public class ClusterMetadataManager {
    private final static Logger LOGGER = LoggerFactory.getLogger(ClusterMetadataManager.class);

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private ControllerDao controllerDao;

    private final static Map<Long, ClusterDO> CLUSTER_MAP = new ConcurrentHashMap<>();

    private final static Map<Long, ControllerData> CONTROLLER_DATA_MAP = new ConcurrentHashMap<>();

    private final static Map<Long, ZkConfigImpl> ZK_CONFIG_MAP = new ConcurrentHashMap<>();

    private final static Map<Long, Map<String, TopicMetadata>> TOPIC_METADATA_MAP = new ConcurrentHashMap<>();

    private final static Map<Long, Map<Integer, BrokerMetadata>> BROKER_METADATA_MAP = new ConcurrentHashMap<>();

    private final static Map<Long, Map<Integer, JmxConnectorWrap>> JMX_CONNECTOR_MAP = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        LOGGER.info("ClusterMetadataManager, init start ...");

        List<ClusterDO> clusterDOList = clusterService.listAll();
        for (ClusterDO clusterDO : clusterDOList) {
            addNew(clusterDO);
        }
        LOGGER.info("ClusterMetadataManager, init finish ...");
    }

    public boolean reload(ClusterDO clusterDO) {
        remove(clusterDO.getId());
        return addNew(clusterDO);
    }

    public boolean addNew(ClusterDO clusterDO) {
        try {
            CLUSTER_MAP.put(clusterDO.getId(), clusterDO);
            TOPIC_METADATA_MAP.put(clusterDO.getId(), new ConcurrentHashMap<>());
            BROKER_METADATA_MAP.put(clusterDO.getId(), new ConcurrentHashMap<>());
            JMX_CONNECTOR_MAP.put(clusterDO.getId(), new ConcurrentHashMap<>());

            ZkConfigImpl zkConfig = new ZkConfigImpl(clusterDO.getZookeeper());
            ZK_CONFIG_MAP.put(clusterDO.getId(), zkConfig);

            //增加Broker监控
            zkConfig.watchChildren(ZkPathUtil.BROKER_IDS_ROOT, new BrokerStateListener(clusterDO.getId(), zkConfig));

            //增加Topic监控
            zkConfig.watchChildren(ZkPathUtil.BROKER_TOPICS_ROOT, new TopicStateListener(clusterDO.getId(), zkConfig));

            //增加Controller监控
            zkConfig.watch(ZkPathUtil.CONTROLLER_ROOT_NODE, new ControllerStateListener(clusterDO.getId(), zkConfig));
        } catch (ConfigException e) {
            LOGGER.error("addNew failed, clusterId:{}.", clusterDO.getId(), e);
            return false;
        }
        return true;
    }

    private void remove(Long clusterId) {
        try {
            ZkConfigImpl zkConfig = ZK_CONFIG_MAP.remove(clusterId);
            if (zkConfig != null) {
                zkConfig.cancelWatchChildren(ZkPathUtil.BROKER_IDS_ROOT);
                zkConfig.cancelWatchChildren(ZkPathUtil.BROKER_TOPICS_ROOT);
                zkConfig.cancelWatchChildren(ZkPathUtil.CONTROLLER_ROOT_NODE);
                zkConfig.close();
            }
        } catch (Exception e) {
            LOGGER.error("remove failed, clusterId:{}.", clusterId, e);
        }
        CLUSTER_MAP.remove(clusterId);
        CONTROLLER_DATA_MAP.remove(clusterId);
        BROKER_METADATA_MAP.remove(clusterId);
        TOPIC_METADATA_MAP.remove(clusterId);
    }

    /**
     * topic更新监听器
     */
    class TopicStateListener implements StateChangeListener {
        private Long clusterId;

        private ZkConfigImpl zkConfig;

        private TopicStateListener(Long clusterId, ZkConfigImpl zkConfig) {
            this.clusterId = clusterId;
            this.zkConfig = zkConfig;
        }

        @Override
        public void onChange(State state, String topicPath) {
            try {
                String topicName = ZkPathUtil.parseLastPartFromZkPath(topicPath);
                switch (state) {
                    case CHILD_ADDED:
                    case CHILD_UPDATED:
                        processTopicAdded(topicName);
                        break;
                    case CHILD_DELETED:
                        processTopicDelete(topicName);
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                LOGGER.warn("topic state {} for path {} process failed", state, topicPath, e);
            }
        }

        private void processTopicDelete(String topicName) {
            LOGGER.warn("delete topic, clusterId:{} topicName:{}", clusterId, topicName);
            TOPIC_METADATA_MAP.get(clusterId).remove(topicName);
        }

        private void processTopicAdded(String topicName) {
            LOGGER.info("create topic, clusterId:{} topicName:{}", clusterId, topicName);
            try {
                TopicMetadata topicMetadata = TOPIC_METADATA_MAP.get(clusterId).getOrDefault(topicName, new TopicMetadata());
                topicMetadata.setTopic(topicName);

                // 获取 version 和 partitions 及 副本所在 BROKER id
                String topicNodePath = ZkPathUtil.getBrokerTopicRoot(topicName);

                Stat stat = zkConfig.getNodeStat(topicNodePath);
                topicMetadata.setCreateTime(stat.getCtime());
                topicMetadata.setModifyTime(stat.getMtime());

                PartitionMap partitionMap = zkConfig.get(topicNodePath, PartitionMap.class);
                topicMetadata.setPartitionMap(partitionMap);
                topicMetadata.setReplicaNum(partitionMap.getPartitions().values().iterator().next().size());
                topicMetadata.setPartitionNum(partitionMap.getPartitions().size());

                Set<Integer> brokerIdSet = new HashSet<>();
                Map<Integer, List<Integer>> topicBrokers = partitionMap.getPartitions();
                for (Map.Entry<Integer, List<Integer>> entry : topicBrokers.entrySet()) {
                    brokerIdSet.addAll(entry.getValue());
                }
                topicMetadata.setBrokerIdSet(brokerIdSet);
                TOPIC_METADATA_MAP.get(clusterId).put(topicName, topicMetadata);
            } catch (ConfigException e) {
                LOGGER.error("create topic, add cache failed, clusterId:{} topicName:{}.", clusterId, topicName, e);
            }
        }
    }


    /**
     * broker更新监听器
     */
    private static class BrokerStateListener implements StateChangeListener {
        private Long clusterId;

        private ZkConfigImpl zkConfig;

        private BrokerStateListener(Long clusterId, ZkConfigImpl zkConfig) {
            this.clusterId = clusterId;
            this.zkConfig = zkConfig;
        }

        @Override
        public void onChange(State state, String brokerPath) {
            try {
                String brokerId = ZkPathUtil.parseLastPartFromZkPath(brokerPath);
                switch (state) {
                    case CHILD_ADDED:
                    case CHILD_UPDATED:
                        processBrokerAdded(Integer.valueOf(brokerId));
                        break;
                    case CHILD_DELETED:
                        processBrokerDelete(Integer.valueOf(brokerId));
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                LOGGER.error("BROKER state {} for path {} process failed", state, brokerPath, e);
            }
        }

        private void processBrokerDelete(Integer brokerId) {
            BROKER_METADATA_MAP.get(clusterId).remove(brokerId);
            JmxConnectorWrap jmxConnectorWrap = JMX_CONNECTOR_MAP.get(clusterId).remove(brokerId);
            if (jmxConnectorWrap == null) {
                return;
            }
            jmxConnectorWrap.close();
        }

        private void processBrokerAdded(Integer brokerId) throws ConfigException {
            BrokerMetadata brokerMetadata = zkConfig.get(ZkPathUtil.getBrokerIdNodePath(brokerId), BrokerMetadata.class);
            if (!brokerMetadata.getEndpoints().isEmpty()) {
                String endpoint = brokerMetadata.getEndpoints().get(0);
                int idx = endpoint.indexOf("://");
                endpoint = endpoint.substring(idx + "://".length());
                idx = endpoint.indexOf(":");

                brokerMetadata.setHost(endpoint.substring(0, idx));
                brokerMetadata.setPort(Integer.parseInt(endpoint.substring(idx + 1)));
            }
            brokerMetadata.setBrokerId(brokerId);
            BROKER_METADATA_MAP.get(clusterId).put(brokerId, brokerMetadata);
            JMX_CONNECTOR_MAP.get(clusterId).put(brokerId, new JmxConnectorWrap(brokerMetadata.getHost(), brokerMetadata.getJmxPort()));
        }
    }


    /**
      * controller更新监听器
      */
    class ControllerStateListener implements StateChangeListener {
        private Long clusterId;

        private ZkConfigImpl zkConfig;

        private ControllerStateListener(Long clusterId, ZkConfigImpl zkConfig) {
            this.clusterId = clusterId;
            this.zkConfig = zkConfig;
        }

        @Override
        public void onChange(State state, String controller) {
            try {
                switch (state) {
                    case NODE_DATA_CHANGED:
                        processControllerChange(zkConfig);
                    default:
                        break;
                }
            } catch (Exception e) {
                LOGGER.error("controller state {} for path {} process failed", state, ZkPathUtil.getControllerRootNode(), e);
            }
        }

        private void processControllerChange(ZkConfigImpl zkConfig) throws ConfigException{
            String controllerRootNode = ZkPathUtil.getControllerRootNode();
            ControllerData controllerData = zkConfig.get(controllerRootNode, ControllerData.class);
            if (controllerData == null) {
                return;
            }
            CONTROLLER_DATA_MAP.put(clusterId, controllerData);

            BrokerMetadata brokerMetadata = BROKER_METADATA_MAP.get(clusterId).get(controllerData.getBrokerid());
            ControllerDO controllerDO = ControllerDO.newInstance(clusterId,
                    controllerData.getBrokerid(),
                    brokerMetadata != null? brokerMetadata.getHost(): "",
                    controllerData.getTimestamp(),
                    controllerData.getVersion()
            );
            try {
                controllerDao.insert(controllerDO);
            } catch (DuplicateKeyException e) {
                LOGGER.info("processControllerChange@ClusterMetadataManager, ignore controller change, controller:{}.", controllerDO);
            } catch (Exception e) {
                LOGGER.error("processControllerChange@ClusterMetadataManager, insert controller change failed, controller:{}.", controllerDO, e);
            }
        }
    }

    /**
     * 获取集群信息
     */
    public static ClusterDO getClusterFromCache(Long clusterId) {
        return CLUSTER_MAP.get(clusterId);
    }

    /**
     * 获取集群信息
     */
    public static ControllerData getControllerData(Long clusterId) {
        return CONTROLLER_DATA_MAP.get(clusterId);
    }

    /**
     * 获取集群信息
     */
    public static JmxConnectorWrap getJmxConnectorWrap(Long clusterId, Integer brokerId) {
        Map<Integer, JmxConnectorWrap> jmxConnectorWrapMap = JMX_CONNECTOR_MAP.get(clusterId);
        if (jmxConnectorWrapMap == null) {
            return null;
        }
        return jmxConnectorWrapMap.get(brokerId);
    }

    /**
     * 获取Topic的元信息
     */
    public static TopicMetadata getTopicMetaData(Long clusterId, String topicName) {
        if (!TOPIC_METADATA_MAP.containsKey(clusterId)) {
            return null;
        }
        if (!TOPIC_METADATA_MAP.get(clusterId).containsKey(topicName)) {
            return null;
        }
        return TOPIC_METADATA_MAP.get(clusterId).get(topicName);
    }

    /**
     * 获取集群的Topic列表
     */
    public static List<String> getTopicNameList(Long clusterId) {
        if (clusterId == null || !TOPIC_METADATA_MAP.containsKey(clusterId)) {
            return new ArrayList<>();
        }
        return new ArrayList<>(TOPIC_METADATA_MAP.get(clusterId).keySet());
    }

    /**
     * 获取集群的Topic列表
     */
    public static boolean isTopicExist(Long clusterId, String topicName) {
        if (!TOPIC_METADATA_MAP.containsKey(clusterId)) {
            return false;
        }
        if (!TOPIC_METADATA_MAP.get(clusterId).containsKey(topicName)) {
            return false;
        }
        return true;
    }

    /**
     * 获取Broker的元信息
     */
    public static BrokerMetadata getBrokerMetadata(Long clusterId, Integer brokerId) {
        if (!BROKER_METADATA_MAP.containsKey(clusterId)) {
            return null;
        }
        if (!BROKER_METADATA_MAP.get(clusterId).containsKey(brokerId)) {
            return null;
        }
        return (BrokerMetadata) BROKER_METADATA_MAP.get(clusterId).get(brokerId).clone();
    }

    /**
     * 获取BrokerId列表
     */
    public static List<Integer> getBrokerIdList(Long clusterId) {
        if (clusterId == null || !BROKER_METADATA_MAP.containsKey(clusterId)) {
            return new ArrayList<>();
        }
        return new ArrayList<>(BROKER_METADATA_MAP.get(clusterId).keySet());
    }

    public static ZkConfigImpl getZKConfig(Long clusterId) {
        if (!ZK_CONFIG_MAP.containsKey(clusterId)) {
            return null;
        }
        return ZK_CONFIG_MAP.get(clusterId);
    }
}
