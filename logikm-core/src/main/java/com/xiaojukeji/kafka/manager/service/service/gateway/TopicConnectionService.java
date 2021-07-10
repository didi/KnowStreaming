package com.xiaojukeji.kafka.manager.service.service.gateway;

import com.xiaojukeji.kafka.manager.common.entity.ao.topic.TopicConnection;
import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.TopicConnectionDO;

import java.util.Date;
import java.util.List;

/**
 * @author zhongyuankai
 * @date 20/4/13
 */
public interface TopicConnectionService {
    void batchAdd(List<TopicConnectionDO> doList);

    /**
     * 查询连接信息
     */
    List<TopicConnection> getByTopicName(Long clusterId,
                                         String topicName,
                                         Date startTime,
                                         Date endTime);

    /**
     * 查询连接信息
     */
    List<TopicConnection> getByTopicName(Long clusterId,
                                         String topicName,
                                         String appId,
                                         Date startTime,
                                         Date endTime);

    /**
     * 查询连接信息
     */
    List<TopicConnection> getByAppId(String appId,
                                     Date startTime,
                                     Date endTime);

    /**
     * 判断topic是否存在连接
     */
    boolean isExistConnection(Long clusterId,
                              String topicName,
                              Date startTime,
                              Date endTime);

    /**
     * 判断app是否对topic存在连接
     */
    boolean isExistConnection(Long clusterId,
                              String topicName,
                              String appId,
                              Date startTime,
                              Date endTime);
}
