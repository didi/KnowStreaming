package com.xiaojukeji.kafka.manager.service.service;

import com.xiaojukeji.kafka.manager.common.bizenum.TaskStatusEnum;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.TopicDO;

import java.util.List;
import java.util.Properties;

public interface AdminService {
    ResultStatus createTopic(ClusterDO clusterDO,
                             TopicDO topicDO,
                             Integer partitionNum,
                             Integer replicaNum,
                             Long regionId,
                             List<Integer> brokerIdList,
                             Properties properties,
                             String applicant,
                             String operator);

    ResultStatus deleteTopic(ClusterDO clusterDO,
                             String topicName,
                             String operator);

    TaskStatusEnum preferredReplicaElectionStatus(ClusterDO clusterDO);

    ResultStatus preferredReplicaElection(ClusterDO clusterDO, String operator);

    ResultStatus preferredReplicaElection(ClusterDO clusterDO, Integer brokerId, String operator);

    ResultStatus expandPartitions(ClusterDO clusterDO, String topicName, Integer partitionNum, Long regionId, List<Integer> brokerIdList, String operator);

    Properties getTopicConfig(ClusterDO clusterDO, String topicName);

    ResultStatus modifyTopicConfig(ClusterDO clusterDO, String topicName, Properties properties, String operator);
}
