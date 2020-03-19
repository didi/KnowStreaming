package com.xiaojukeji.kafka.manager.web.vo.cluster;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * ClusterMetricsVO
 * @author zengqiao
 * @date 19/4/3
 */
@ApiModel(value="ClusterMetricsVO", description="集群流量信息")
public class ClusterMetricsVO {
    @ApiModelProperty(value="集群Id")
    private Long clusterId;

    @ApiModelProperty(value="Topic数量")
    private Integer topicNum;

    @ApiModelProperty(value="Partition数量")
    private Integer partitionNum;

    @ApiModelProperty(value="Broker数量")
    private Integer brokerNum;

    @ApiModelProperty(value="每秒流入的字节数")
    private Double bytesInPerSec;

    @ApiModelProperty(value="每秒流出的字节数")
    private Double bytesOutPerSec;

    @ApiModelProperty(value="每秒拒绝的字节数")
    private Double bytesRejectedPerSec;

    @ApiModelProperty(value="每秒流入的消息数")
    private Double messagesInPerSec;

    @ApiModelProperty(value="创建时间")
    private Long gmtCreate;

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

    public Long getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Long gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    @Override
    public String toString() {
        return "ClusterMetricsVO{" +
                "clusterId=" + clusterId +
                ", topicNum=" + topicNum +
                ", partitionNum=" + partitionNum +
                ", brokerNum=" + brokerNum +
                ", bytesInPerSec=" + bytesInPerSec +
                ", bytesOutPerSec=" + bytesOutPerSec +
                ", bytesRejectedPerSec=" + bytesRejectedPerSec +
                ", messagesInPerSec=" + messagesInPerSec +
                ", gmtCreate=" + gmtCreate +
                '}';
    }
}
