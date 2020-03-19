package com.xiaojukeji.kafka.manager.common.entity.po;

import com.xiaojukeji.kafka.manager.common.entity.metrics.BrokerMetrics;

public class ClusterMetricsDO extends BaseEntryDO {
    private Long clusterId;

    private Integer topicNum = 0;

    private Integer partitionNum = 0;

    private Integer brokerNum = 0;

    private Double bytesInPerSec = 0.0;

    private Double bytesOutPerSec = 0.0;

    private Double bytesRejectedPerSec = 0.0;

    private Double messagesInPerSec = 0.0;

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    public Integer getTopicNum() {
        return topicNum;
    }

    public void setTopicNum(Integer topicNum) {
        this.topicNum = topicNum;
    }

    public Integer getPartitionNum() {
        return partitionNum;
    }

    public void setPartitionNum(Integer partitionNum) {
        this.partitionNum = partitionNum;
    }

    public Integer getBrokerNum() {
        return brokerNum;
    }

    public void setBrokerNum(Integer brokerNum) {
        this.brokerNum = brokerNum;
    }

    public Double getBytesInPerSec() {
        return bytesInPerSec;
    }

    public void setBytesInPerSec(Double bytesInPerSec) {
        this.bytesInPerSec = bytesInPerSec;
    }

    public Double getBytesOutPerSec() {
        return bytesOutPerSec;
    }

    public void setBytesOutPerSec(Double bytesOutPerSec) {
        this.bytesOutPerSec = bytesOutPerSec;
    }

    public Double getBytesRejectedPerSec() {
        return bytesRejectedPerSec;
    }

    public void setBytesRejectedPerSec(Double bytesRejectedPerSec) {
        this.bytesRejectedPerSec = bytesRejectedPerSec;
    }

    public Double getMessagesInPerSec() {
        return messagesInPerSec;
    }

    public void setMessagesInPerSec(Double messagesInPerSec) {
        this.messagesInPerSec = messagesInPerSec;
    }

    public void addBrokerMetrics(BrokerMetrics brokerMetrics) {
        this.clusterId = brokerMetrics.getClusterId();
        this.brokerNum += 1;
        this.bytesInPerSec += brokerMetrics.getBytesInPerSec();
        this.bytesOutPerSec += brokerMetrics.getBytesOutPerSec();
        this.bytesRejectedPerSec += brokerMetrics.getBytesRejectedPerSec();
        this.messagesInPerSec += brokerMetrics.getMessagesInPerSec();
    }

    @Override
    public String toString() {
        return "ClusterMetricsDO{" +
                "clusterId=" + clusterId +
                ", topicNum=" + topicNum +
                ", partitionNum=" + partitionNum +
                ", brokerNum=" + brokerNum +
                ", bytesInPerSec=" + bytesInPerSec +
                ", bytesOutPerSec=" + bytesOutPerSec +
                ", bytesRejectedPerSec=" + bytesRejectedPerSec +
                ", messagesInPerSec=" + messagesInPerSec +
                ", id=" + id +
                ", gmtCreate=" + gmtCreate +
                '}';
    }
}
