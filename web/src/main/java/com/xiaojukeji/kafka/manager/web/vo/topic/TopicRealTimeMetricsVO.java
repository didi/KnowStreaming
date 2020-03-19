package com.xiaojukeji.kafka.manager.web.vo.topic;

import io.swagger.annotations.ApiModel;

import java.util.List;

/**
 * Topic实时流量信息
 * @author zengqiao
 * @date 19/4/1
 */
@ApiModel(value = "Topic实时流量信息")
public class TopicRealTimeMetricsVO {
    private List<Double> messageIn;

    private List<Double> byteIn;

    private List<Double> byteOut;

    private List<Double> byteRejected;

    private List<Double> failedFetchRequest;

    private List<Double> failedProduceRequest;

    private List<Double> totalProduceRequest;

    private List<Double> totalFetchRequest;

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

    public List<Double> getTotalProduceRequest() {
        return totalProduceRequest;
    }

    public void setTotalProduceRequest(List<Double> totalProduceRequest) {
        this.totalProduceRequest = totalProduceRequest;
    }

    public List<Double> getTotalFetchRequest() {
        return totalFetchRequest;
    }

    public void setTotalFetchRequest(List<Double> totalFetchRequest) {
        this.totalFetchRequest = totalFetchRequest;
    }

    @Override
    public String toString() {
        return "TopicRealTimeMetricsVO{" +
                "messageIn=" + messageIn +
                ", byteIn=" + byteIn +
                ", byteOut=" + byteOut +
                ", byteRejected=" + byteRejected +
                ", failedFetchRequest=" + failedFetchRequest +
                ", failedProduceRequest=" + failedProduceRequest +
                ", totalProduceRequest=" + totalProduceRequest +
                ", totalFetchRequest=" + totalFetchRequest +
                '}';
    }
}
