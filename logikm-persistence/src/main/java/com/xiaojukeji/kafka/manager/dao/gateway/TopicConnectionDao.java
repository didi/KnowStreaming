package com.xiaojukeji.kafka.manager.dao.gateway;

import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.TopicConnectionDO;

import java.util.Date;
import java.util.List;

/**
 * Topic连接信息
 * @author zengqiao
 * @date 20/7/6
 */
public interface TopicConnectionDao {
    int batchReplace(List<TopicConnectionDO> doList);

    List<TopicConnectionDO> getByTopicName(Long clusterId, String topicName, Date startTime, Date endTime);

    List<TopicConnectionDO> getByAppId(String appId, Date startTime, Date endTime);
}