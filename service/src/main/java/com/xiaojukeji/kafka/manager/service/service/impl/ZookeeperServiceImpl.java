package com.xiaojukeji.kafka.manager.service.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.xiaojukeji.kafka.manager.common.entity.zookeeper.PartitionState;
import com.xiaojukeji.kafka.manager.common.exception.ConfigException;
import com.xiaojukeji.kafka.manager.common.utils.zk.ZkConfigImpl;
import com.xiaojukeji.kafka.manager.service.cache.ClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.service.ZookeeperService;
import com.xiaojukeji.kafka.manager.common.utils.zk.ZkPathUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("zookeeperService")
public class ZookeeperServiceImpl implements ZookeeperService {
    private final static Logger logger = LoggerFactory.getLogger(ZookeeperServiceImpl.class);

    @Override
    public Properties getTopicProperties(Long clusterId, String topicName) throws ConfigException {
        ZkConfigImpl zkConfig = ClusterMetadataManager.getZKConfig(clusterId);
        String path = ZkPathUtil.getConfigTopicNode(topicName);
        Properties properties = zkConfig.get(path, Properties.class);
        JSONObject jsonObject = (JSONObject) properties.get("config");
        return JSONObject.parseObject(jsonObject.toJSONString(), Properties.class);
    }

    @Override
    public List<PartitionState> getTopicPartitionState(Long clusterId, String topicName) throws ConfigException {
        ZkConfigImpl zkConfig = ClusterMetadataManager.getZKConfig(clusterId);
        Integer partitionNum = ClusterMetadataManager.getTopicMetaData(clusterId, topicName).getPartitionNum();

        List<PartitionState> partitionStateList = new ArrayList<>();
        for (int partitionId = 0; partitionId < partitionNum; ++partitionId) {
            String partitionStatePath = ZkPathUtil.getBrokerTopicPartitionStatePath(topicName, partitionId);
            PartitionState partitionState = zkConfig.get(partitionStatePath, PartitionState.class);
            partitionState.setPartitionId(partitionId);
            partitionStateList.add(partitionState);
        }
        return partitionStateList;
    }

    @Override
    public Long getRetentionTime(Long clusterId, String topicName) {
        try {
            Properties properties = getTopicProperties(clusterId, topicName);
            if (properties == null || !properties.containsKey("retention.ms")) {
                return null;
            }
            if (properties.get("retention.ms") instanceof String) {
                return Long.valueOf(properties.getProperty("retention.ms"));
            } else if (properties.get("retention.ms") instanceof Integer) {
                return Long.valueOf((Integer)properties.get("retention.ms"));
            } else if (properties.get("retention.ms") instanceof Long) {
                return (Long) properties.get("retention.ms");
            }
        } catch (Exception e) {
            logger.error("get retentionTime failed, clusterId:{} topicName:{}.", clusterId, topicName, e);
        }
        return null;
    }
}
