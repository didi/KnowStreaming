package com.xiaojukeji.kafka.manager.common.entity.vo.rd.broker;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author zengqiao
 * @date 2019-06-03
 */
@ApiModel(description = "Broker指标")
public class BrokerMetricsVO {
    @ApiModelProperty(value = "健康分[0-100]")
    private Integer healthScore;

    @ApiModelProperty(value = "流入流量(B/s)")
    private Object bytesInPerSec;

    @ApiModelProperty(value = "流出流量(B/s)")
    private Object bytesOutPerSec;

    @ApiModelProperty(value = "被拒绝流量(B/s)")
    private Object bytesRejectedPerSec;

    @ApiModelProperty(value = "消息数")
    private Object messagesInPerSec;

    @ApiModelProperty(value = "发送请求数")
    private Object produceRequestPerSec;

    @ApiModelProperty(value = "消费请求数")
    private Object fetchConsumerRequestPerSec;

    @ApiModelProperty(value = "请求处理器空闲百分比")
    private Object requestHandlerIdlPercent;

    @ApiModelProperty(value = "网络处理器空闲百分比")
    private Object networkProcessorIdlPercent;

    @ApiModelProperty(value = "请求队列大小")
    private Integer requestQueueSize;

    @ApiModelProperty(value = "响应队列大小")
    private Integer responseQueueSize;

    @ApiModelProperty(value = "刷日志事件")
    private Object logFlushTime;

    @ApiModelProperty(value = "每秒消费失败数")
    private Object failFetchRequestPerSec;

    @ApiModelProperty(value = "每秒发送失败数")
    private Object failProduceRequestPerSec;

    @ApiModelProperty(value = "发送耗时99分位")
    private Object totalTimeProduce99Th;

    @ApiModelProperty(value = "消费耗时99分位")
    private Object totalTimeFetchConsumer99Th;

    @ApiModelProperty(value = "创建时间")
    private Long gmtCreate;

    public Integer getHealthScore() {
        return healthScore;
    }

    public void setHealthScore(Integer healthScore) {
        this.healthScore = healthScore;
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

    public Object getProduceRequestPerSec() {
        return produceRequestPerSec;
    }

    public void setProduceRequestPerSec(Object produceRequestPerSec) {
        this.produceRequestPerSec = produceRequestPerSec;
    }

    public Object getFetchConsumerRequestPerSec() {
        return fetchConsumerRequestPerSec;
    }

    public void setFetchConsumerRequestPerSec(Object fetchConsumerRequestPerSec) {
        this.fetchConsumerRequestPerSec = fetchConsumerRequestPerSec;
    }

    public Object getRequestHandlerIdlPercent() {
        return requestHandlerIdlPercent;
    }

    public void setRequestHandlerIdlPercent(Object requestHandlerIdlPercent) {
        this.requestHandlerIdlPercent = requestHandlerIdlPercent;
    }

    public Object getNetworkProcessorIdlPercent() {
        return networkProcessorIdlPercent;
    }

    public void setNetworkProcessorIdlPercent(Object networkProcessorIdlPercent) {
        this.networkProcessorIdlPercent = networkProcessorIdlPercent;
    }

    public Integer getRequestQueueSize() {
        return requestQueueSize;
    }

    public void setRequestQueueSize(Integer requestQueueSize) {
        this.requestQueueSize = requestQueueSize;
    }

    public Integer getResponseQueueSize() {
        return responseQueueSize;
    }

    public void setResponseQueueSize(Integer responseQueueSize) {
        this.responseQueueSize = responseQueueSize;
    }

    public Object getLogFlushTime() {
        return logFlushTime;
    }

    public void setLogFlushTime(Object logFlushTime) {
        this.logFlushTime = logFlushTime;
    }

    public Object getFailFetchRequestPerSec() {
        return failFetchRequestPerSec;
    }

    public void setFailFetchRequestPerSec(Object failFetchRequestPerSec) {
        this.failFetchRequestPerSec = failFetchRequestPerSec;
    }

    public Object getFailProduceRequestPerSec() {
        return failProduceRequestPerSec;
    }

    public void setFailProduceRequestPerSec(Object failProduceRequestPerSec) {
        this.failProduceRequestPerSec = failProduceRequestPerSec;
    }

    public Object getTotalTimeProduce99Th() {
        return totalTimeProduce99Th;
    }

    public void setTotalTimeProduce99Th(Object totalTimeProduce99Th) {
        this.totalTimeProduce99Th = totalTimeProduce99Th;
    }

    public Object getTotalTimeFetchConsumer99Th() {
        return totalTimeFetchConsumer99Th;
    }

    public void setTotalTimeFetchConsumer99Th(Object totalTimeFetchConsumer99Th) {
        this.totalTimeFetchConsumer99Th = totalTimeFetchConsumer99Th;
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
                "healthScore=" + healthScore +
                ", bytesInPerSec=" + bytesInPerSec +
                ", bytesOutPerSec=" + bytesOutPerSec +
                ", bytesRejectedPerSec=" + bytesRejectedPerSec +
                ", messagesInPerSec=" + messagesInPerSec +
                ", produceRequestPerSec=" + produceRequestPerSec +
                ", fetchConsumerRequestPerSec=" + fetchConsumerRequestPerSec +
                ", requestHandlerIdlPercent=" + requestHandlerIdlPercent +
                ", networkProcessorIdlPercent=" + networkProcessorIdlPercent +
                ", requestQueueSize=" + requestQueueSize +
                ", responseQueueSize=" + responseQueueSize +
                ", logFlushTime=" + logFlushTime +
                ", failFetchRequestPerSec=" + failFetchRequestPerSec +
                ", failProduceRequestPerSec=" + failProduceRequestPerSec +
                ", totalTimeProduce99Th=" + totalTimeProduce99Th +
                ", totalTimeFetchConsumer99Th=" + totalTimeFetchConsumer99Th +
                ", gmtCreate=" + gmtCreate +
                '}';
    }
}
