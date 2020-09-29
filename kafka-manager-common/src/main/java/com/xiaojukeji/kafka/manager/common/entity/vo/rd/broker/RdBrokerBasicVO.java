package com.xiaojukeji.kafka.manager.common.entity.vo.rd.broker;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author zhongyuankai
 * @date 2020/6/11
 */
@ApiModel(description = "Broker基本信息")
public class RdBrokerBasicVO {
    @ApiModelProperty(value = "brokerId")
    private Integer brokerId;

    @ApiModelProperty(value = "主机名")
    private String host;

    @ApiModelProperty(value = "逻辑集群")
    private Long logicClusterId;

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

    public Long getLogicClusterId() {
        return logicClusterId;
    }

    public void setLogicClusterId(Long logicClusterId) {
        this.logicClusterId = logicClusterId;
    }

    @Override
    public String toString() {
        return "RdBrokerBasicVO{" +
                "brokerId=" + brokerId +
                ", host='" + host + '\'' +
                ", logicClusterId=" + logicClusterId +
                '}';
    }
}
