package com.xiaojukeji.kafka.manager.web.vo.broker;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Broker关键指标
 * @date 2019-06-03
 */
@ApiModel(value = "BrokerKeyMetricsVO", description = "Broker关键指标")
public class BrokerKeyMetricsVO {
    @ApiModelProperty(value = "DB的ID")
    private Long id;

    @ApiModelProperty(value = "请求处理器空闲百分比")
    private Double requestHandlerIdlPercent;

    @ApiModelProperty(value = "网络处理器空闲百分比")
    private Double networkProcessorIdlPercent;

    @ApiModelProperty(value = "请求队列大小")
    private Integer requestQueueSize;

    @ApiModelProperty(value = "响应队列大小")
    private Integer responseQueueSize;

    @ApiModelProperty(value = "刷日志事件")
    private Double logFlushTime;

    @ApiModelProperty(value = "每秒消费失败数")
    private Double failFetchRequest;

    @ApiModelProperty(value = "每秒发送失败数")
    private Double failProduceRequest;

    @ApiModelProperty(value = "发送耗时均值")
    private Double totalTimeProduceMean;

    @ApiModelProperty(value = "发送耗时99分位")
    private Double totalTimeProduce99Th;

    @ApiModelProperty(value = "消费耗时均值")
    private Double totalTimeFetchConsumerMean;

    @ApiModelProperty(value = "消费耗时99分位")
    private Double totalTimeFetchConsumer99Th;

    @ApiModelProperty(value = "创建事件")
    private Long gmtCreate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getRequestHandlerIdlPercent() {
        return requestHandlerIdlPercent;
    }

    public void setRequestHandlerIdlPercent(Double requestHandlerIdlPercent) {
        this.requestHandlerIdlPercent = requestHandlerIdlPercent;
    }

    public Double getNetworkProcessorIdlPercent() {
        return networkProcessorIdlPercent;
    }

    public void setNetworkProcessorIdlPercent(Double networkProcessorIdlPercent) {
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

    public Double getLogFlushTime() {
        return logFlushTime;
    }

    public void setLogFlushTime(Double logFlushTime) {
        this.logFlushTime = logFlushTime;
    }

    public Double getFailFetchRequest() {
        return failFetchRequest;
    }

    public void setFailFetchRequest(Double failFetchRequest) {
        this.failFetchRequest = failFetchRequest;
    }

    public Double getFailProduceRequest() {
        return failProduceRequest;
    }

    public void setFailProduceRequest(Double failProduceRequest) {
        this.failProduceRequest = failProduceRequest;
    }

    public Double getTotalTimeProduceMean() {
        return totalTimeProduceMean;
    }

    public void setTotalTimeProduceMean(Double totalTimeProduceMean) {
        this.totalTimeProduceMean = totalTimeProduceMean;
    }

    public Double getTotalTimeProduce99Th() {
        return totalTimeProduce99Th;
    }

    public void setTotalTimeProduce99Th(Double totalTimeProduce99Th) {
        this.totalTimeProduce99Th = totalTimeProduce99Th;
    }

    public Double getTotalTimeFetchConsumerMean() {
        return totalTimeFetchConsumerMean;
    }

    public void setTotalTimeFetchConsumerMean(Double totalTimeFetchConsumerMean) {
        this.totalTimeFetchConsumerMean = totalTimeFetchConsumerMean;
    }

    public Double getTotalTimeFetchConsumer99Th() {
        return totalTimeFetchConsumer99Th;
    }

    public void setTotalTimeFetchConsumer99Th(Double totalTimeFetchConsumer99Th) {
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
        return "BrokerKeyMetricsVO{" +
                "id=" + id +
                ", requestHandlerIdlPercent=" + requestHandlerIdlPercent +
                ", networkProcessorIdlPercent=" + networkProcessorIdlPercent +
                ", requestQueueSize=" + requestQueueSize +
                ", responseQueueSize=" + responseQueueSize +
                ", logFlushTime=" + logFlushTime +
                ", failFetchRequest=" + failFetchRequest +
                ", failProduceRequest=" + failProduceRequest +
                ", totalTimeProduceMean=" + totalTimeProduceMean +
                ", totalTimeProduce99Th=" + totalTimeProduce99Th +
                ", totalTimeFetchConsumerMean=" + totalTimeFetchConsumerMean +
                ", totalTimeFetchConsumer99Th=" + totalTimeFetchConsumer99Th +
                ", gmtCreate=" + gmtCreate +
                '}';
    }
}
