package com.xiaojukeji.know.streaming.km.common.constant;

/**
 * 信息模版Constant
 * @author zengqiao
 * @date 22/03/03
 */
public class MsgConstant {
    private MsgConstant() {
    }

    /**************************************************** Cluster ****************************************************/

    public static String getClusterBizStr(Long clusterPhyId, String clusterName){
        return String.format("集群ID:[%d] 集群名称:[%s]", clusterPhyId, clusterName);
    }

    public static String getClusterPhyNotExist(Long clusterPhyId) {
        return String.format("集群ID:[%d] 不存在或者未加载", clusterPhyId);
    }



    /**************************************************** Broker ****************************************************/

    public static String getBrokerNotExist(Long clusterPhyId, Integer brokerId) {
        return String.format("集群ID:[%d] brokerId:[%d] 不存在或未存活", clusterPhyId, brokerId);
    }

    public static String getBrokerBizStr(Long clusterPhyId, Integer brokerId) {
        return String.format("集群ID:[%d] brokerId:[%d]", clusterPhyId, brokerId);
    }


    /**************************************************** Topic ****************************************************/

    public static String getTopicNotExist(Long clusterPhyId, String topicName) {
        return String.format("集群ID:[%d] Topic名称:[%s] 不存在", clusterPhyId, topicName);
    }

    public static String getTopicBizStr(Long clusterPhyId, String topicName) {
        return String.format("集群ID:[%d] Topic名称:[%s]", clusterPhyId, topicName);
    }

    public static String getTopicExtend(Long existPartitionNum, Long totalPartitionNum,String expandParam){
        return  String.format("新增分区, 从:[%d] 增加到:[%d], 详细参数信息:[%s]", existPartitionNum,totalPartitionNum,expandParam);
    }

    public static String getClusterTopicKey(Long clusterPhyId, String topicName) {
        return String.format("%d@%s", clusterPhyId, topicName);
    }

    /**************************************************** Partition ****************************************************/

    public static String getPartitionNoLeader(Long clusterPhyId) {
        return String.format("集群ID:[%d] 所有分区NoLeader", clusterPhyId);
    }

    public static String getPartitionNoLeader(Long clusterPhyId, String topicName) {
        return String.format("集群ID:[%d] Topic名称:[%s] 所有分区NoLeader", clusterPhyId, topicName);
    }

    public static String getPartitionNotExist(Long clusterPhyId, String topicName) {
        return String.format("集群ID:[%d] Topic名称:[%s] 存在非法的分区ID", clusterPhyId, topicName);
    }

    public static String getPartitionNotExist(Long clusterPhyId, String topicName, Integer partitionId) {
        return String.format("集群ID:[%d] Topic名称:[%s] 分区Id:[%d] 不存在", clusterPhyId, topicName, partitionId);
    }

    /**************************************************** KafkaUser ****************************************************/

    public static String getKafkaUserBizStr(Long clusterPhyId, String kafkaUser) {
        return String.format("集群ID:[%d] kafkaUser:[%s]", clusterPhyId, kafkaUser);
    }

    public static String getKafkaUserNotExist(Long clusterPhyId, String kafkaUser) {
        return String.format("集群ID:[%d] kafkaUser:[%s] 不存在", clusterPhyId, kafkaUser);
    }

    public static String getKafkaUserDuplicate(Long clusterPhyId, String kafkaUser) {
        return String.format("集群ID:[%d] kafkaUser:[%s] 已存在", clusterPhyId, kafkaUser);
    }

    /**************************************************** reassign ****************************************************/

    public static String getReassignJobBizStr(Long jobId, Long clusterPhyId) {
        return String.format("任务Id:[%d] 集群ID:[%s]", jobId, clusterPhyId);
    }

    public static String getJobIdCanNotNull() {
        return "jobId不允许为空";
    }

    public static String getJobNotExist(Long jobId) {
        return String.format("jobId:[%d] 不存在", jobId);
    }


    /**************************************************** Connect-Cluster ****************************************************/

    public static String getConnectClusterBizStr(Long clusterId, String clusterName){
        return String.format("Connect集群ID:[%d] 集群名称:[%s]", clusterId, clusterName);
    }

    public static String getConnectClusterNotExist(Long clusterId) {
        return String.format("Connect集群ID:[%d] 不存在或者未加载", clusterId);
    }

    public static String getConnectorBizStr(Long clusterPhyId, String topicName) {
        return String.format("Connect集群ID:[%d] Connector名称:[%s]", clusterPhyId, topicName);
    }


    /**************************************************** Connector ****************************************************/

    public static String getConnectorNotExist(Long connectClusterId, String connectorName) {
        return String.format("Connect集群ID:[%d] Connector名称:[%s] 不存在", connectClusterId, connectorName);
    }
}
