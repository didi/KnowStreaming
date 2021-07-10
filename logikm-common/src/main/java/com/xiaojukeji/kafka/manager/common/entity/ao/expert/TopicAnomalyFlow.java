package com.xiaojukeji.kafka.manager.common.entity.ao.expert;

/**
 * @author zengqiao
 * @date 20/3/30
 */
public class TopicAnomalyFlow {
    private Long clusterId;

    private String clusterName;

    private String topicName;

    private Double bytesIn;

    private Double bytesInIncr;

    private Double iops;

    private Double iopsIncr;

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public Double getBytesIn() {
        return bytesIn;
    }

    public void setBytesIn(Double bytesIn) {
        this.bytesIn = bytesIn;
    }

    public Double getBytesInIncr() {
        return bytesInIncr;
    }

    public void setBytesInIncr(Double bytesInIncr) {
        this.bytesInIncr = bytesInIncr;
    }

    public Double getIops() {
        return iops;
    }

    public void setIops(Double iops) {
        this.iops = iops;
    }

    public Double getIopsIncr() {
        return iopsIncr;
    }

    public void setIopsIncr(Double iopsIncr) {
        this.iopsIncr = iopsIncr;
    }

    @Override
    public String toString() {
        return "AnomalyFlowTopicDTO{" +
                "clusterId=" + clusterId +
                ", clusterName='" + clusterName + '\'' +
                ", topicName='" + topicName + '\'' +
                ", bytesIn=" + bytesIn +
                ", bytesInIncr=" + bytesInIncr +
                ", iops=" + iops +
                ", iopsIncr=" + iopsIncr +
                '}';
    }
}