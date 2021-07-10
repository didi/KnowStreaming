package com.xiaojukeji.kafka.manager.common.entity.vo.rd.cluster;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * ClusterMetricsVO
 * @author zengqiao
 * @date 19/4/3
 */
@ApiModel(description="集群流量信息")
public class RdClusterMetricsVO {
    @ApiModelProperty(value="集群Id")
    private Long clusterId;

    @ApiModelProperty(value="Topic数量")
    private Object topicNum;

    @ApiModelProperty(value="Partition数量")
    private Object partitionNum;

    @ApiModelProperty(value="Broker数量")
    private Object brokerNum;

    @ApiModelProperty(value="每秒流入的字节数")
    private Object bytesInPerSec;

    @ApiModelProperty(value="每秒流出的字节数")
    private Object bytesOutPerSec;

    @ApiModelProperty(value="每秒拒绝的字节数")
    private Object bytesRejectedPerSec;

    @ApiModelProperty(value="每秒流入的消息数")
    private Object messagesInPerSec;

    @ApiModelProperty(value="创建时间")
    private Long gmtCreate;

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    public Object getTopicNum() {
        return topicNum;
    }

    public void setTopicNum(Object topicNum) {
        this.topicNum = topicNum;
    }

    public Object getPartitionNum() {
        return partitionNum;
    }

    public void setPartitionNum(Object partitionNum) {
        this.partitionNum = partitionNum;
    }

    public Object getBrokerNum() {
        return brokerNum;
    }

    public void setBrokerNum(Object brokerNum) {
        this.brokerNum = brokerNum;
    }

    public Object getBytesInPerSec() {
        return bytesInPerSec;
    }

    public void setBytesInPerSec(Object bytesInPerSec) {
        this.bytesInPerSec = bytesInPerSec;
    }

    public Object getBytesOutPerSec() {
        return bytesOutPerSec;
    }

    public void setBytesOutPerSec(Object bytesOutPerSec) {
        this.bytesOutPerSec = bytesOutPerSec;
    }

    public Object getBytesRejectedPerSec() {
        return bytesRejectedPerSec;
    }

    public void setBytesRejectedPerSec(Object bytesRejectedPerSec) {
        this.bytesRejectedPerSec = bytesRejectedPerSec;
    }

    public Object getMessagesInPerSec() {
        return messagesInPerSec;
    }

    public void setMessagesInPerSec(Object messagesInPerSec) {
        this.messagesInPerSec = messagesInPerSec;
    }

    public Long getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Long gmtCreate) {
        this.gmtCreate = gmtCreate;
    }
}
