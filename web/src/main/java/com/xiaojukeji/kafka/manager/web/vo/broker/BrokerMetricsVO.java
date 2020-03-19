package com.xiaojukeji.kafka.manager.web.vo.broker;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * BROKER metrics
 * @author zengqiao
 * @date 19/3/18
 */
@ApiModel(value = "BrokerMetricsVO", description = "Broker流量信息")
public class BrokerMetricsVO {
    @ApiModelProperty(value = "消息数")
    private Double messagesInPerSec;

    @ApiModelProperty(value = "流入流量(B)")
    private Double bytesInPerSec;

    @ApiModelProperty(value = "流出流量(B)")
    private Double bytesOutPerSec;

    @ApiModelProperty(value = "被拒绝流量(B)")
    private Double bytesRejectedPerSec;

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

    public Long getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Long gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    @Override
    public String toString() {
        return "BrokerMetricsVO{" +
                "messagesInPerSec=" + messagesInPerSec +
                ", bytesInPerSec=" + bytesInPerSec +
                ", bytesOutPerSec=" + bytesOutPerSec +
                ", bytesRejectedPerSec=" + bytesRejectedPerSec +
                ", gmtCreate=" + gmtCreate +
                '}';
    }
}
