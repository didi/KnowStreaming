package com.xiaojukeji.kafka.manager.dao;

import com.xiaojukeji.kafka.manager.common.entity.pojo.*;

import java.util.Date;
import java.util.List;

/**
 * @author zengqiao
 * @date 20/3/30
 */
public interface TopicExpiredDao {
    List<TopicExpiredDO> getExpiredTopics(Integer expiredDay);

    int modifyTopicExpiredTime(Long clusterId, String topicName, Date gmtRetain);

    int replace(TopicExpiredDO expiredDO);

    TopicExpiredDO getByTopic(Long clusterId, String topicName);
}