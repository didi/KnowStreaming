package com.xiaojukeji.kafka.manager.common.entity.vo.normal.cluster;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author zengqiao
 * @date 20/5/13
 */
public class NormalClusterMetricsVO {

    @ApiModelProperty(value="每秒总共发送的请求")
    private Double totalProduceRequestsPerSec;

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

    public Double getTotalProduceRequestsPerSec() {
        return totalProduceRequestsPerSec;
    }

    public void setTotalProduceRequestsPerSec(Double totalProduceRequestsPerSec) {
        this.totalProduceRequestsPerSec = totalProduceRequestsPerSec;
    }

    @Override
    public String toString() {
        return "NormalClusterMetricsVO{" +
                "totalProduceRequestsPerSec=" + totalProduceRequestsPerSec +
                ", bytesInPerSec=" + bytesInPerSec +
                ", bytesOutPerSec=" + bytesOutPerSec +
                ", bytesRejectedPerSec=" + bytesRejectedPerSec +
                ", messagesInPerSec=" + messagesInPerSec +
                ", gmtCreate=" + gmtCreate +
                '}';
    }
}