package com.xiaojukeji.kafka.manager.common.entity.dto;

import java.util.Date;

/**
 * @author zengqiao
 * @date 19/4/22
 */
public class ControllerDTO {
    private String  clusterName;

    private Integer brokerId;

    private String host;

    private Integer controllerVersion;

    private Date controllerTimestamp;

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

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

    public Integer getControllerVersion() {
        return controllerVersion;
    }

    public void setControllerVersion(Integer controllerVersion) {
        this.controllerVersion = controllerVersion;
    }

    public Date getControllerTimestamp() {
        return controllerTimestamp;
    }

    public void setControllerTimestamp(Date controllerTimestamp) {
        this.controllerTimestamp = controllerTimestamp;
    }

    @Override
    public String toString() {
        return "ControllerInfoDTO{" +
                "clusterName='" + clusterName + '\'' +
                ", brokerId=" + brokerId +
                ", host='" + host + '\'' +
                ", controllerVersion=" + controllerVersion +
                ", controllerTimestamp=" + controllerTimestamp +
                '}';
    }
}
