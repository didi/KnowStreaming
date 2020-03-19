package com.xiaojukeji.kafka.manager.web.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * @author huangyiminghappy@163.com
 * @date 2019-03-27
 */
@ApiModel(value = "controller信息")
public class KafkaControllerVO implements Serializable {
    private static final long serialVersionUID = 7402869683834994137L;

    @ApiModelProperty(value = "节点ID")
    private Integer brokerId;

    @ApiModelProperty(value = "节点地址")
    private String host;

    @ApiModelProperty(value = "controller版本")
    private Integer controllerVersion;

    @ApiModelProperty(value = "controller变更时间")
    private Long controllerTimestamp;

    public static long getSerialVersionUID() {
        return serialVersionUID;
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

    public Long getControllerTimestamp() {
        return controllerTimestamp;
    }

    public void setControllerTimestamp(Long controllerTimestamp) {
        this.controllerTimestamp = controllerTimestamp;
    }

    @Override
    public String toString() {
        return "KafkaControllerVO{" +
                "brokerId=" + brokerId +
                ", host='" + host + '\'' +
                ", controllerVersion=" + controllerVersion +
                ", controllerTimestamp=" + controllerTimestamp +
                '}';
    }
}