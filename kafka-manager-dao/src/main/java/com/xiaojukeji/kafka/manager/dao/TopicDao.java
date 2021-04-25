package com.xiaojukeji.kafka.manager.dao;

import com.xiaojukeji.kafka.manager.common.entity.pojo.*;

import java.util.List;

public interface TopicDao {
    int insert(TopicDO topicDO);

    int deleteById(Long id);

    int deleteByName(Long clusterId, String topicName);

    int updateByName(TopicDO topicDO);

    TopicDO getByTopicName(Long clusterId, String topicName);

    List<TopicDO> getByClusterIdFromCache(Long clusterId);

    List<TopicDO> getByClusterId(Long clusterId);

    List<TopicDO> getByAppId(String appId);

    List<TopicDO> listAll();

    TopicDO getTopic(Long clusterId, String topicName, String appId);
}