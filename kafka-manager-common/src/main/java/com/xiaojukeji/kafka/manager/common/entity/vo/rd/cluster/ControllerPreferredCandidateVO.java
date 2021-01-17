package com.xiaojukeji.kafka.manager.common.entity.vo.rd.cluster;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "Broker基本信息")
public class ControllerPreferredCandidateVO {
    @ApiModelProperty(value = "brokerId")
    private Integer brokerId;

    @ApiModelProperty(value = "主机名")
    private String host;

    @ApiModelProperty(value = "启动时间")
    private Long startTime;

    @ApiModelProperty(value = "broker状态[0:在线, -1:不在线]")
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
        return "ControllerPreferredBrokerVO{" +
                "brokerId=" + brokerId +
                ", host='" + host + '\'' +
                ", startTime=" + startTime +
                ", status=" + status +
                '}';
    }
}
