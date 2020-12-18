package com.xiaojukeji.kafka.manager.common.entity.vo.normal.topic;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author zengqiao
 * @date 20/4/8
 */
@ApiModel(value = "Topic请求耗时详情")
public class TopicRequestTimeDetailVO {
    @ApiModelProperty(value = "请求耗时类型")
    private String requestTimeType;

    @ApiModelProperty(value = "responseQueueTimeMs")
    private Object responseQueueTimeMs;

    @ApiModelProperty(value = "localTimeMs")
    private Object localTimeMs;

    @ApiModelProperty(value = "requestQueueTimeMs")
    private Object requestQueueTimeMs;

    @ApiModelProperty(value = "throttleTimeMs")
    private Object throttleTimeMs;

    @ApiModelProperty(value = "responseSendTimeMs")
    private Object responseSendTimeMs;

    @ApiModelProperty(value = "remoteTimeMs")
    private Object remoteTimeMs;

    @ApiModelProperty(value = "totalTimeMs")
    private Object totalTimeMs;

    private List<TopicBrokerRequestTimeVO> brokerRequestTimeList;

    public String getRequestTimeType() {
        return requestTimeType;
    }

    public void setRequestTimeType(String requestTimeType) {
        this.requestTimeType = requestTimeType;
    }

    public Object getResponseQueueTimeMs() {
        return responseQueueTimeMs;
    }

    public void setResponseQueueTimeMs(Object responseQueueTimeMs) {
        this.responseQueueTimeMs = responseQueueTimeMs;
    }

    public Object getLocalTimeMs() {
        return localTimeMs;
    }

    public void setLocalTimeMs(Object localTimeMs) {
        this.localTimeMs = localTimeMs;
    }

    public Object getRequestQueueTimeMs() {
        return requestQueueTimeMs;
    }

    public void setRequestQueueTimeMs(Object requestQueueTimeMs) {
        this.requestQueueTimeMs = requestQueueTimeMs;
    }

    public Object getThrottleTimeMs() {
        return throttleTimeMs;
    }

    public void setThrottleTimeMs(Object throttleTimeMs) {
        this.throttleTimeMs = throttleTimeMs;
    }

    public Object getResponseSendTimeMs() {
        return responseSendTimeMs;
    }

    public void setResponseSendTimeMs(Object responseSendTimeMs) {
        this.responseSendTimeMs = responseSendTimeMs;
    }

    public Object getRemoteTimeMs() {
        return remoteTimeMs;
    }

    public void setRemoteTimeMs(Object remoteTimeMs) {
        this.remoteTimeMs = remoteTimeMs;
    }

    public Object getTotalTimeMs() {
        return totalTimeMs;
    }

    public void setTotalTimeMs(Object totalTimeMs) {
        this.totalTimeMs = totalTimeMs;
    }

    public List<TopicBrokerRequestTimeVO> getBrokerRequestTimeList() {
        return brokerRequestTimeList;
    }

    public void setBrokerRequestTimeList(List<TopicBrokerRequestTimeVO> brokerRequestTimeList) {
        this.brokerRequestTimeList = brokerRequestTimeList;
    }

    @Override
    public String toString() {
        return "TopicRequestTimeDetailVO{" +
                "requestTimeType='" + requestTimeType + '\'' +
                ", responseQueueTimeMs=" + responseQueueTimeMs +
                ", localTimeMs=" + localTimeMs +
                ", requestQueueTimeMs=" + requestQueueTimeMs +
                ", throttleTimeMs=" + throttleTimeMs +
                ", responseSendTimeMs=" + responseSendTimeMs +
                ", remoteTimeMs=" + remoteTimeMs +
                ", totalTimeMs=" + totalTimeMs +
                '}';
    }
}
