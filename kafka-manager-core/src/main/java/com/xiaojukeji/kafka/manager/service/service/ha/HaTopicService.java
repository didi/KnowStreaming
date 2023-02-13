package com.xiaojukeji.kafka.manager.service.service.ha;

import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterDO;

import java.util.List;
import java.util.Map;

/**
 * Topic主备关系管理
 * 不包括ACL，Gateway等信息
 */
public interface HaTopicService {
    /**
     * 创建主备关系
     */
    Result<Void> createHA(Long activeClusterPhyId, Long standbyClusterPhyId, String topicName, String operator);
    Result<Void> activeHAInKafkaNotCheck(ClusterDO activeClusterDO, String activeTopicName, ClusterDO standbyClusterDO, String standbyTopicName, String operator);
    Result<Void> activeHAInKafka(ClusterDO activeClusterDO, String activeTopicName, ClusterDO standbyClusterDO, String standbyTopicName, String operator);

    /**
     * 删除主备关系
     */
    Result<Void> deleteHA(Long activeClusterPhyId, Long standbyClusterPhyId, String topicName, String operator);
    Result<Void> stopHAInKafka(ClusterDO standbyClusterDO, String standbyTopicName, String operator);

    /**
     * 获取集群topic的主备关系
     */
    Map<String, Integer> getRelation(Long clusterId);

    /**
     * 获取所有集群的备topic名称
     */
    Map<Long, List<String>> getClusterStandbyTopicMap();

    /**
     * 激活kafkaUserHA
     */
    Result<Void> activeUserHAInKafka(ClusterDO activeClusterDO, ClusterDO standbyClusterDO, String kafkaUser, String operator);

    Result<Long> getStandbyTopicFetchLag(Long standbyClusterPhyId, String topicName);
}
