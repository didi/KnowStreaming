package com.xiaojukeji.kafka.manager.common.entity.ao.topic;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author zengqiao
 * @date 20/5/11
 */
public class TopicMetricsDTO {
    @ApiModelProperty(value = "每秒流入消息数")
    private Object messagesInPerSec;

    @ApiModelProperty(value = "每秒流入字节数")
    private Object bytesInPerSec;

    @ApiModelProperty(value = "每秒流出字节数")
    private Object bytesOutPerSec;

    @ApiModelProperty(value = "每秒拒绝字节数")
    private Object bytesRejectedPerSec;

    @ApiModelProperty(value = "每秒请求数")
    private Object totalProduceRequestsPerSec;

    @ApiModelProperty(value = "appId维度每秒流入消息数")
    private Object appIdMessagesInPerSec;

    @ApiModelProperty(value = "appId维度每秒流入字节数")
    private Object appIdBytesInPerSec;

    @ApiModelProperty(value = "appId维度每秒流出字节数")
    private Object appIdBytesOutPerSec;

    @ApiModelProperty(value = "produce限流")
    private Boolean produceThrottled;

    @ApiModelProperty(value = "consume限流")
    private Boolean consumeThrottled;

    @ApiModelProperty(value = "创建时间")
    private Long gmtCreate;

    public Object getMessagesInPerSec() {
        return messagesInPerSec;
    }

    public void setMessagesInPerSec(Object messagesInPerSec) {
        this.messagesInPerSec = messagesInPerSec;
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

    public Object getTotalProduceRequestsPerSec() {
        return totalProduceRequestsPerSec;
    }

    public void setTotalProduceRequestsPerSec(Object totalProduceRequestsPerSec) {
        this.totalProduceRequestsPerSec = totalProduceRequestsPerSec;
    }

    public Object getAppIdMessagesInPerSec() {
        return appIdMessagesInPerSec;
    }

    public void setAppIdMessagesInPerSec(Object appIdMessagesInPerSec) {
        this.appIdMessagesInPerSec = appIdMessagesInPerSec;
    }

    public Object getAppIdBytesInPerSec() {
        return appIdBytesInPerSec;
    }

    public void setAppIdBytesInPerSec(Object appIdBytesInPerSec) {
        this.appIdBytesInPerSec = appIdBytesInPerSec;
    }

    public Object getAppIdBytesOutPerSec() {
        return appIdBytesOutPerSec;
    }

    public void setAppIdBytesOutPerSec(Object appIdBytesOutPerSec) {
        this.appIdBytesOutPerSec = appIdBytesOutPerSec;
    }

    public Boolean getProduceThrottled() {
        return produceThrottled;
    }

    public void setProduceThrottled(Boolean produceThrottled) {
        this.produceThrottled = produceThrottled;
    }

    public Boolean getConsumeThrottled() {
        return consumeThrottled;
    }

    public void setConsumeThrottled(Boolean consumeThrottled) {
        this.consumeThrottled = consumeThrottled;
    }

    public Long getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Long gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    @Override
    public String toString() {
        return "TopicMetricsDTO{" +
                "messagesInPerSec=" + messagesInPerSec +
                ", bytesInPerSec=" + bytesInPerSec +
                ", bytesOutPerSec=" + bytesOutPerSec +
                ", bytesRejectedPerSec=" + bytesRejectedPerSec +
                ", totalProduceRequestsPerSec=" + totalProduceRequestsPerSec +
                ", appIdMessagesInPerSec=" + appIdMessagesInPerSec +
                ", appIdBytesInPerSec=" + appIdBytesInPerSec +
                ", appIdBytesOutPerSec=" + appIdBytesOutPerSec +
                ", produceThrottled=" + produceThrottled +
                ", consumeThrottled=" + consumeThrottled +
                ", gmtCreate=" + gmtCreate +
                '}';
    }
}