package com.xiaojukeji.kafka.manager.web.vo.broker;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

/**
 * @author zengqiao
 * @date 19/4/22
 */
@ApiModel(value = "BrokerStatusVO", description = "Broker流量信息")
public class BrokerStatusVO implements Serializable {
    @ApiModelProperty(value = "消息数")
    private List<Double> messageIn;

    @ApiModelProperty(value = "流入流量(B)")
    private List<Double> byteIn;

    @ApiModelProperty(value = "流出流量(B)")
    private List<Double> byteOut;

    @ApiModelProperty(value = "被拒绝流量(B)")
    private List<Double> byteRejected;

    @ApiModelProperty(value = "Fetch失败请求数")
    private List<Double> failedFetchRequest;

    @ApiModelProperty(value = "Produce失败请求数")
    private List<Double> failedProduceRequest;

    @ApiModelProperty(value = "Fetch请求数")
    private List<Double> fetchConsumerRequest;

    @ApiModelProperty(value = "Produce请求数")
    private List<Double> produceRequest;

    public List<Double> getMessageIn() {
        return messageIn;
    }

    public void setMessageIn(List<Double> messageIn) {
        this.messageIn = messageIn;
    }

    public List<Double> getByteIn() {
        return byteIn;
    }

    public void setByteIn(List<Double> byteIn) {
        this.byteIn = byteIn;
    }

    public List<Double> getByteOut() {
        return byteOut;
    }

    public void setByteOut(List<Double> byteOut) {
        this.byteOut = byteOut;
    }

    public List<Double> getByteRejected() {
        return byteRejected;
    }

    public void setByteRejected(List<Double> byteRejected) {
        this.byteRejected = byteRejected;
    }

    public List<Double> getFailedFetchRequest() {
        return failedFetchRequest;
    }

    public void setFailedFetchRequest(List<Double> failedFetchRequest) {
        this.failedFetchRequest = failedFetchRequest;
    }

    public List<Double> getFailedProduceRequest() {
        return failedProduceRequest;
    }

    public void setFailedProduceRequest(List<Double> failedProduceRequest) {
        this.failedProduceRequest = failedProduceRequest;
    }

    public List<Double> getFetchConsumerRequest() {
        return fetchConsumerRequest;
    }

    public void setFetchConsumerRequest(List<Double> fetchConsumerRequest) {
        this.fetchConsumerRequest = fetchConsumerRequest;
    }

    public List<Double> getProduceRequest() {
        return produceRequest;
    }

    public void setProduceRequest(List<Double> produceRequest) {
        this.produceRequest = produceRequest;
    }

    @Override
    public String toString() {
        return "BrokerStatusVO{" +
                "messageIn=" + messageIn +
                ", byteIn=" + byteIn +
                ", byteOut=" + byteOut +
                ", byteRejected=" + byteRejected +
                ", failedFetchRequest=" + failedFetchRequest +
                ", failedProduceRequest=" + failedProduceRequest +
                ", fetchConsumerRequest=" + fetchConsumerRequest +
                ", produceRequest=" + produceRequest +
                '}';
    }
}
