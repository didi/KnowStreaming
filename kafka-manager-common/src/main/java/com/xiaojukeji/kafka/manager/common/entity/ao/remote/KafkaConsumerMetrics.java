package com.xiaojukeji.kafka.manager.common.entity.ao.remote;

import java.util.List;

/**
 * @author zengqiao
 * @date 20/8/25
 */
public class KafkaConsumerMetrics {
    private Long clusterId;

    private String topicName;

    private String consumerGroup;

    private String location;

    private Integer partitionNum;

    private List<KafkaConsumerMetricsElem> consumeDetailList;

    private Long createTime;

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getConsumerGroup() {
        return consumerGroup;
    }

    public void setConsumerGroup(String consumerGroup) {
        this.consumerGroup = consumerGroup;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getPartitionNum() {
        return partitionNum;
    }

    public void setPartitionNum(Integer partitionNum) {
        this.partitionNum = partitionNum;
    }

    public List<KafkaConsumerMetricsElem> getConsumeDetailList() {
        return consumeDetailList;
    }

    public void setConsumeDetailList(List<KafkaConsumerMetricsElem> consumeDetailList) {
        this.consumeDetailList = consumeDetailList;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "KafkaConsumerMetrics{" +
                "clusterId=" + clusterId +
                ", topicName='" + topicName + '\'' +
                ", consumerGroup='" + consumerGroup + '\'' +
                ", location='" + location + '\'' +
                ", partitionNum=" + partitionNum +
                ", consumeDetailList=" + consumeDetailList +
                ", createTime=" + createTime +
                '}';
    }
}