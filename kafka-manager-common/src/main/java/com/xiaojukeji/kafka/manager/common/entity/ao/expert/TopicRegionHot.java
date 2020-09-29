package com.xiaojukeji.kafka.manager.common.entity.ao.expert;

import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterDO;

import java.util.Map;

/**
 * Region内热点Topic
 * @author zengqiao
 * @date 20/3/27
 */
public class TopicRegionHot {
    private ClusterDO clusterDO;

    private String topicName;

    private Long retentionTime;

    private Map<Integer, Integer> brokerIdPartitionNumMap;

    public TopicRegionHot(ClusterDO clusterDO, String topicName, Long retentionTime, Map<Integer, Integer>
            brokerIdPartitionNumMap) {
        this.clusterDO = clusterDO;
        this.topicName = topicName;
        this.retentionTime = retentionTime;
        this.brokerIdPartitionNumMap = brokerIdPartitionNumMap;
    }

    public ClusterDO getClusterDO() {
        return clusterDO;
    }

    public void setClusterDO(ClusterDO clusterDO) {
        this.clusterDO = clusterDO;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public Long getRetentionTime() {
        return retentionTime;
    }

    public void setRetentionTime(Long retentionTime) {
        this.retentionTime = retentionTime;
    }

    public Map<Integer, Integer> getBrokerIdPartitionNumMap() {
        return brokerIdPartitionNumMap;
    }

    public void setBrokerIdPartitionNumMap(Map<Integer, Integer> brokerIdPartitionNumMap) {
        this.brokerIdPartitionNumMap = brokerIdPartitionNumMap;
    }

    @Override
    public String toString() {
        return "ExpertRegionTopicHot{" +
                "clusterDO=" + clusterDO +
                ", topicName='" + topicName + '\'' +
                ", retentionTime=" + retentionTime +
                ", brokerIdPartitionNumMap=" + brokerIdPartitionNumMap +
                '}';
    }
}