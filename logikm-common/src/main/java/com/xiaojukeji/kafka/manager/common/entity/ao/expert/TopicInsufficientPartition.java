package com.xiaojukeji.kafka.manager.common.entity.ao.expert;

import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterDO;

import java.util.List;

/**
 * @author zengqiao
 * @date 20/3/30
 */
public class TopicInsufficientPartition {
    private ClusterDO clusterDO;

    private String topicName;

    private Integer presentPartitionNum;

    private Integer suggestedPartitionNum;

    private List<Double> maxAvgBytesInList;

    private Double bytesInPerPartition;

    private List<Integer> brokerIdList;

    public TopicInsufficientPartition(
            ClusterDO clusterDO,
            String topicName,
            Integer presentPartitionNum,
            Integer suggestedPartitionNum,
            List<Double> maxAvgBytesInList,
            Double bytesInPerPartition,
            List<Integer> brokerIdList) {
        this.clusterDO = clusterDO;
        this.topicName = topicName;
        this.presentPartitionNum = presentPartitionNum;
        this.suggestedPartitionNum = suggestedPartitionNum;
        this.maxAvgBytesInList = maxAvgBytesInList;
        this.bytesInPerPartition = bytesInPerPartition;
        this.brokerIdList = brokerIdList;
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

    public Integer getPresentPartitionNum() {
        return presentPartitionNum;
    }

    public void setPresentPartitionNum(Integer presentPartitionNum) {
        this.presentPartitionNum = presentPartitionNum;
    }

    public Integer getSuggestedPartitionNum() {
        return suggestedPartitionNum;
    }

    public void setSuggestedPartitionNum(Integer suggestedPartitionNum) {
        this.suggestedPartitionNum = suggestedPartitionNum;
    }

    public List<Double> getMaxAvgBytesInList() {
        return maxAvgBytesInList;
    }

    public void setMaxAvgBytesInList(List<Double> maxAvgBytesInList) {
        this.maxAvgBytesInList = maxAvgBytesInList;
    }

    public Double getBytesInPerPartition() {
        return bytesInPerPartition;
    }

    public void setBytesInPerPartition(Double bytesInPerPartition) {
        this.bytesInPerPartition = bytesInPerPartition;
    }

    public List<Integer> getBrokerIdList() {
        return brokerIdList;
    }

    public void setBrokerIdList(List<Integer> brokerIdList) {
        this.brokerIdList = brokerIdList;
    }

    @Override
    public String toString() {
        return "TopicInsufficientPartition{" +
                "clusterDO=" + clusterDO +
                ", topicName='" + topicName + '\'' +
                ", presentPartitionNum=" + presentPartitionNum +
                ", suggestedPartitionNum=" + suggestedPartitionNum +
                ", maxAvgBytesInList=" + maxAvgBytesInList +
                ", bytesInPerPartition=" + bytesInPerPartition +
                ", brokerIdList=" + brokerIdList +
                '}';
    }
}