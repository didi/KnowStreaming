package com.xiaojukeji.kafka.manager.service.service.gateway;

import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ao.topic.TopicConnection;
import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.TopicConnectionDO;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    Result<Map<String/*KafkaUser*/, Set<String>/*ClientID*/>> getHaKafkaUserAndClientIdByTopicName(Long firstClusterId,
                                                                                                 Long secondClusterId,
                                                                                                 String topicName,
                                                                                                 Date startTime,
                                                                                                 Date endTime);

    Set<String> getKafkaUserAndClientIdTopicNames(Set<Long> clusterIdSet, String kafkaUser, String clientId, Date startTime, Date endTime);

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

    Result<List<TopicConnectionDO>> getByClusterAndAppId(Long firstClusterId, Long secondClusterId, String appId, Date startTime, Date endTime);

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
