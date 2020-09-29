package com.xiaojukeji.kafka.manager.service.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xiaojukeji.kafka.manager.common.constant.TopicCreationConstant;
import com.xiaojukeji.kafka.manager.common.entity.ao.gateway.TopicQuota;
import com.xiaojukeji.kafka.manager.common.utils.NumberUtils;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.common.zookeeper.ZkConfigImpl;
import com.xiaojukeji.kafka.manager.common.zookeeper.ZkPathUtil;
import com.xiaojukeji.kafka.manager.common.zookeeper.znode.ReassignmentElemData;
import com.xiaojukeji.kafka.manager.common.zookeeper.znode.ReassignmentJsonData;
import com.xiaojukeji.kafka.manager.common.zookeeper.znode.brokers.PartitionState;
import com.xiaojukeji.kafka.manager.common.zookeeper.znode.config.ChangeData;
import com.xiaojukeji.kafka.manager.common.zookeeper.znode.config.TopicQuotaData;
import com.xiaojukeji.kafka.manager.common.zookeeper.znode.config.ConfigNodeData;
import com.xiaojukeji.kafka.manager.common.zookeeper.znode.didi.JmxSwitchDataConstant;
import com.xiaojukeji.kafka.manager.common.zookeeper.znode.didi.TopicJmxSwitch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Kafka ZK 工具类
 * @author zengqiao
 * @date 20/4/8
 */
public class KafkaZookeeperUtils {
    private final static Logger LOGGER = LoggerFactory.getLogger(KafkaZookeeperUtils.class);

    private static final Integer DATA_VERSION_ONE = 1;

    public static List<ReassignmentElemData> getReassignmentElemDataList(String reassignmentJson) {
        try {
            ReassignmentJsonData data = JSONObject.parseObject(reassignmentJson, ReassignmentJsonData.class);
            return data.getPartitions();
        } catch (Exception e) {
        }
        return new ArrayList<>();
    }

    public static Long getTopicRetentionTime(Properties properties) {
        if (ValidateUtils.isNull(properties)
                || !properties.containsKey(TopicCreationConstant.TOPIC_RETENTION_TIME_KEY_NAME)) {
            return null;
        }
        return NumberUtils.string2Long(properties.getProperty(TopicCreationConstant.TOPIC_RETENTION_TIME_KEY_NAME));
    }

    public static Long getTopicRetentionTime(ZkConfigImpl zkConfig, String topicName) {
        Properties properties = getTopicProperties(zkConfig, topicName);
        if (ValidateUtils.isNull(properties)
                || !properties.containsKey(TopicCreationConstant.TOPIC_RETENTION_TIME_KEY_NAME)) {
            return null;
        }
        return NumberUtils.string2Long(properties.getProperty(TopicCreationConstant.TOPIC_RETENTION_TIME_KEY_NAME));
    }

    public static Properties getTopicProperties(ZkConfigImpl zkConfig, String topicName) {
        return getConfigData(zkConfig, ZkPathUtil.getConfigTopicNode(topicName));
    }

    public static List<PartitionState> getTopicPartitionState(ZkConfigImpl zkConfig,
                                                              String topicName,
                                                              List<Integer> partitionIdList) {
        List<PartitionState> partitionStateList = new ArrayList<>();
        for (Integer partitionId: partitionIdList) {
            try {
                PartitionState partitionState = zkConfig.get(
                        ZkPathUtil.getBrokerTopicPartitionStatePath(topicName, partitionId),
                        PartitionState.class
                );
                partitionState.setPartitionId(partitionId);
                partitionStateList.add(partitionState);
            } catch (Exception e) {
                LOGGER.error("get partition state failed, topicName:{} partitionId:{}."
                        ,topicName, partitionId, e);
            }
        }
        return partitionStateList;
    }

    public static boolean openBrokerTopicJmx(ZkConfigImpl zkConfig,
                                             Integer brokerId,
                                             String topicName,
                                             TopicJmxSwitch jmxSwitch) {
        String dataPath = ZkPathUtil.getKafkaExtraMetricsPath(brokerId);

        // 获取旧的配置
        Properties properties = getConfigData(zkConfig, dataPath);
        if (ValidateUtils.isNull(properties)) {
            properties = new Properties();
        }

        // 合并新旧的配置
        if (jmxSwitch.getOpenTopicRequestMetrics()) {
            properties.put(JmxSwitchDataConstant.TOPIC_REQUEST_METRICS + topicName, Boolean.TRUE);
        }
        if (jmxSwitch.getOpenAppIdTopicMetrics()) {
            properties.put(JmxSwitchDataConstant.APP_ID_TOPIC_METRICS + topicName, Boolean.TRUE);
        }
        if (jmxSwitch.getOpenClientRequestMetrics()) {
            properties.put(JmxSwitchDataConstant.CLIENT_REQUEST_METRICS + topicName, Boolean.TRUE);
        }

        // 新配置写ZK
        ConfigNodeData<Properties> configNodeData = new ConfigNodeData<>();
        configNodeData.setVersion(ConfigNodeData.CONFIGDATA_VERSION);
        configNodeData.setConfig(properties);
        return setConfigNodeData(zkConfig, configNodeData, dataPath);
    }

    public static boolean closeBrokerTopicJmx(ZkConfigImpl zkConfig,
                                              Integer brokerId,
                                              String topicName) {
        String dataPath = ZkPathUtil.getKafkaExtraMetricsPath(brokerId);

        // 获取旧的配置
        Properties properties = getConfigData(zkConfig, dataPath);
        if (ValidateUtils.isNull(properties) || properties.isEmpty()) {
            return true;
        }

        // 移除该Topic的配置
        properties.remove(JmxSwitchDataConstant.TOPIC_REQUEST_METRICS + topicName);
        properties.remove(JmxSwitchDataConstant.APP_ID_TOPIC_METRICS + topicName);
        properties.remove(JmxSwitchDataConstant.CLIENT_REQUEST_METRICS + topicName);

        // 新配置写ZK
        ConfigNodeData<Properties> configNodeData = new ConfigNodeData<>();
        configNodeData.setVersion(ConfigNodeData.CONFIGDATA_VERSION);
        configNodeData.setConfig(properties);
        return setConfigNodeData(zkConfig, configNodeData, dataPath);
    }

    public static TopicQuotaData getTopicQuota(ZkConfigImpl zkConfig, String appId, String topicName) {
        ConfigNodeData configNodeData =
                getConfigNodeData(zkConfig, ZkPathUtil.getConfigClientNodePath(appId, topicName));
        if (ValidateUtils.isNull(configNodeData) || ValidateUtils.isNull(configNodeData.getConfig())) {
            return null;
        }
        return JSONObject.parseObject(JSON.toJSONString(configNodeData.getConfig()), TopicQuotaData.class);
    }

    public static boolean setTopicQuota(ZkConfigImpl zkConfig, TopicQuota topicQuotaDO) {
        ConfigNodeData<TopicQuotaData> configNodeData = new ConfigNodeData<>();
        TopicQuotaData clientData =
                TopicQuotaData.getClientData(topicQuotaDO.getProduceQuota(), topicQuotaDO.getConsumeQuota());
        configNodeData.setVersion(ConfigNodeData.CONFIGDATA_VERSION);
        configNodeData.setConfig(clientData);
        return setConfigNodeData(zkConfig,
                configNodeData,
                ZkPathUtil.getConfigClientNodePath(topicQuotaDO.getAppId(), topicQuotaDO.getTopicName())
        );
    }

    private static Properties getConfigData(ZkConfigImpl zkConfig, String path) {
        ConfigNodeData configNodeData = getConfigNodeData(zkConfig, path);
        if (ValidateUtils.isNull(configNodeData) || ValidateUtils.isNull(configNodeData.getConfig())) {
            return null;
        }
        return JSON.parseObject(JSON.toJSONString(configNodeData.getConfig()), Properties.class);
    }

    /**
     * 获取config node节点的数据
     */
    private static ConfigNodeData getConfigNodeData(ZkConfigImpl zkConfig, String path) {
        try {
            if (!zkConfig.checkPathExists(path)) {
                return null;
            }
            return zkConfig.get(path, ConfigNodeData.class);
        } catch (Exception e) {
            LOGGER.error("get config data failed, path:{}.", path, e);
        }
        return null;
    }

    /**
     * 设置config node节点的数据
     */
    private static boolean setConfigNodeData(ZkConfigImpl zkConfig,
                                             ConfigNodeData configNodeData,
                                             String dataPath) {
        try {
            String entityPath = dataPath.substring(ZkPathUtil.CONFIG_ROOT_NODE.length() + 1);
            zkConfig.setOrCreatePersistentNodeStat(dataPath, JSON.toJSONString(configNodeData));
            zkConfig.createPersistentSequential(
                    ZkPathUtil.CONFIG_ENTITY_CHANGES_ROOT_NODE,
                    JSON.toJSONString(ChangeData.getChangeData(entityPath))
            );
            return true;
        } catch (Exception e) {
            LOGGER.error("set config data failed, dataPath:{} configNodeData:{}."
                    , dataPath, configNodeData, e);
        }
        return false;
    }
}