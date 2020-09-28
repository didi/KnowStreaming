package com.xiaojukeji.kafka.manager.common.entity.vo.op.expert;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author zengqiao
 * @date 20/3/30
 */
@ApiModel(description = "流量异常Topic")
public class AnomalyFlowTopicVO {
    @ApiModelProperty(value = "集群ID")
    private Long clusterId;

    @ApiModelProperty(value = "集群名称")
    private String clusterName;

    @ApiModelProperty(value = "Topic名称")
    private String topicName;

    @ApiModelProperty(value = "bytesIn(B/s)")
    private Double bytesIn;

    @ApiModelProperty(value = "bytesIn增加(B/s)")
    private Double bytesInIncr;

    @ApiModelProperty(value = "iops(Q/s)")
    private Double iops;

    @ApiModelProperty(value = "iops增加(Q/s)")
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
        return "AnomalyFlowTopic{" +
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