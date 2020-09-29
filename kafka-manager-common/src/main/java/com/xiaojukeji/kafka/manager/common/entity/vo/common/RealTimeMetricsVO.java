package com.xiaojukeji.kafka.manager.common.entity.vo.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 实时流量信息
 * @author zengqiao
 * @date 19/4/1
 */
@ApiModel(value = "实时流量信息(Cluster/broker/Topic)")
public class RealTimeMetricsVO {
    @ApiModelProperty(value = "每秒进入消息条")
    private List<Double> messageIn;

    @ApiModelProperty(value = "每秒字节流入")
    private List<Double> byteIn;

    @ApiModelProperty(value = "每秒字节流出")
    private List<Double> byteOut;

    @ApiModelProperty(value = "每秒拒绝字节")
    private List<Double> byteRejected;

    @ApiModelProperty(value = "失败fetch的请求")
    private List<Double> failedFetchRequest;

    @ApiModelProperty(value = "失败produce的请求")
    private List<Double> failedProduceRequest;

    @ApiModelProperty(value = "总的produce请求")
    private List<Double> totalProduceRequest;

    @ApiModelProperty(value = "总的fetch请求")
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
        return "RealTimeMetricsVO{" +
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
