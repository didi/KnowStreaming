package com.xiaojukeji.kafka.manager.common.entity.ao.cluster;

public class ControllerPreferredCandidate {
    private Integer brokerId;

    private String host;

    private Long startTime;

    private Integer status;

    public Integer getBrokerId() {
        return brokerId;
    }

    public void setBrokerId(Integer brokerId) {
        this.brokerId = brokerId;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ControllerPreferredBroker{" +
                "brokerId=" + brokerId +
                ", host='" + host + '\'' +
                ", startTime=" + startTime +
                ", status=" + status +
                '}';
    }
}
