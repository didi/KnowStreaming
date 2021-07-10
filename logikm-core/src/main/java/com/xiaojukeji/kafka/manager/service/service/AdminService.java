package com.xiaojukeji.kafka.manager.service.service;

import com.xiaojukeji.kafka.manager.common.bizenum.TaskStatusEnum;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.TopicDO;

import java.util.List;
import java.util.Properties;

public interface AdminService {
    /**
     * 创建Topic
     * @param clusterDO 集群DO
     * @param topicDO TopicDO
     * @param partitionNum 分区数
     * @param replicaNum 副本数
     * @param regionId RegionID
     * @param brokerIdList BrokerId
     * @param properties Topic属性
     * @param applicant 申请人
     * @param operator 操作人
     * @return 操作状态
     */
    ResultStatus createTopic(ClusterDO clusterDO,
                             TopicDO topicDO,
                             Integer partitionNum,
                             Integer replicaNum,
                             Long regionId,
                             List<Integer> brokerIdList,
                             Properties properties,
                             String applicant,
                             String operator);

    /**
     * 删除Topic
     * @param clusterDO 集群DO
     * @param topicName Topic名称
     * @param operator 操作人
     * @return 操作状态
     */
    ResultStatus deleteTopic(ClusterDO clusterDO,
                             String topicName,
                             String operator);

    /**
     * 优先副本选举状态
     * @param clusterDO 集群DO
     * @return 任务状态
     */
    TaskStatusEnum preferredReplicaElectionStatus(ClusterDO clusterDO);

    /**
     * 集群纬度优先副本选举
     * @param clusterDO 集群DO
     * @return 任务状态
     */
    ResultStatus preferredReplicaElection(ClusterDO clusterDO, String operator);

    /**
     * Broker纬度优先副本选举
     * @param clusterDO 集群DO
     * @param brokerId BrokerID
     * @param operator 操作人
     * @return 任务状态
     */
    ResultStatus preferredReplicaElection(ClusterDO clusterDO, Integer brokerId, String operator);

    /**
     * Topic纬度优先副本选举
     * @param clusterDO 集群DO
     * @param topicName Topic名称
     * @param operator 操作人
     * @return 任务状态
     */
    ResultStatus preferredReplicaElection(ClusterDO clusterDO, String topicName, String operator);

    /**
     * 分区纬度优先副本选举
     * @param clusterDO 集群DO
     * @param topicName Topic名称
     * @param partitionId 分区ID
     * @param operator 操作人
     * @return 任务状态
     */
    ResultStatus preferredReplicaElection(ClusterDO clusterDO, String topicName, Integer partitionId, String operator);

    /**
     * Topic扩分区
     * @param clusterDO 集群DO
     * @param topicName Topic名称
     * @param partitionNum 新增？  分区数
     * @param regionId RegionID
     * @param brokerIdList 集群ID
     * @param operator 操作人
     * @return 任务状态
     */
    ResultStatus expandPartitions(ClusterDO clusterDO, String topicName, Integer partitionNum, Long regionId, List<Integer> brokerIdList, String operator);

    /**
     * 获取Topic配置
     * @param clusterDO 集群DO
     * @param topicName Topic名称
     * @return 任务状态
     */
    Properties getTopicConfig(ClusterDO clusterDO, String topicName);

    /**
     * 修改Topic配置
     * @param clusterDO 集群DO
     * @param topicName Topic名称
     * @param properties 新的属性
     * @param operator 操作人
     * @return 任务状态
     */
    ResultStatus modifyTopicConfig(ClusterDO clusterDO, String topicName, Properties properties, String operator);
}
