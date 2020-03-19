package com.xiaojukeji.kafka.manager.web.vo.topic;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author huangyiminghappy@163.com
 * @date 2019-03-20
 */
@ApiModel(value = "Topic流量信息")
public class TopicMetricsVO {
    @ApiModelProperty(value = "每秒流入消息数")
    private Double messagesInPerSec = 0.0;

    @ApiModelProperty(value = "每秒流入字节数")
    private Double bytesInPerSec = 0.0;

    @ApiModelProperty(value = "每秒流出字节数")
    private Double bytesOutPerSec = 0.0;

    @ApiModelProperty(value = "每秒拒绝字节数")
    private Double bytesRejectedPerSec = 0.0;

    @ApiModelProperty(value = "每秒请求数")
    private Double totalProduceRequestsPerSec = 0.0;

    @ApiModelProperty(value = "创建时间")
    private Long gmtCreate;

    public Double getMessagesInPerSec() {
        return messagesInPerSec;
    }

    public void setMessagesInPerSec(Double messagesInPerSec) {
        this.messagesInPerSec = messagesInPerSec;
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

    public Double getTotalProduceRequestsPerSec() {
        return totalProduceRequestsPerSec;
    }

    public void setTotalProduceRequestsPerSec(Double totalProduceRequestsPerSec) {
        this.totalProduceRequestsPerSec = totalProduceRequestsPerSec;
    }

    public Long getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Long gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    @Override
    public String toString() {
        return "TopicMetricsVO{" +
                "messagesInPerSec=" + messagesInPerSec +
                ", bytesInPerSec=" + bytesInPerSec +
                ", bytesOutPerSec=" + bytesOutPerSec +
                ", bytesRejectedPerSec=" + bytesRejectedPerSec +
                ", totalProduceRequestsPerSec=" + totalProduceRequestsPerSec +
                ", gmtCreate=" + gmtCreate +
                '}';
    }
}
