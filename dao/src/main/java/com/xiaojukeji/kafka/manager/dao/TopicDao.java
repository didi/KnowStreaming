package com.xiaojukeji.kafka.manager.dao;

import com.xiaojukeji.kafka.manager.common.entity.po.TopicDO;

import java.util.List;

public interface TopicDao {
    int replace(TopicDO topicDO);

    int deleteById(Long id);

    int deleteByName(Long clusterId, String topicName);

    TopicDO getByTopicName(Long clusterId, String topicName);

    List<TopicDO> getByClusterId(Long clusterId);

    List<TopicDO> list();
}