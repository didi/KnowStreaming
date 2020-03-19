package com.xiaojukeji.kafka.manager.service.service;

import com.xiaojukeji.kafka.manager.common.entity.zookeeper.PartitionState;
import com.xiaojukeji.kafka.manager.common.exception.ConfigException;

import java.util.List;
import java.util.Properties;

public interface ZookeeperService {
    /**
     * 获取Topic的Properties
     */
    Properties getTopicProperties(Long clusterId, String topicName) throws ConfigException;

    /**
     * 获取PartitionState信息
     */
    List<PartitionState> getTopicPartitionState(Long clusterId, String topicName) throws ConfigException;

    Long getRetentionTime(Long clusterId, String topicName);
}
