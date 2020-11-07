package com.xiaojukeji.kafka.manager.openapi.common.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author zengqiao
 * @date 20/9/9
 */
@ApiModel(description="第三方-Broker概览")
public class ThirdPartBrokerOverviewVO {
    @ApiModelProperty(value = "集群ID")
    private Long clusterId;

    @ApiModelProperty(value = "BrokerId")
    private Integer brokerId;

    @ApiModelProperty(value = "处于同步状态 false:已同步, true:未同步")
    private Boolean underReplicated;

    public ThirdPartBrokerOverviewVO(Long clusterId, Integer brokerId, Boolean underReplicated) {
        this.clusterId = clusterId;
        this.brokerId = brokerId;
        this.underReplicated = underReplicated;
    }

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    public Integer getBrokerId() {
        return brokerId;
    }

    public void setBrokerId(Integer brokerId) {
        this.brokerId = brokerId;
    }

    public Boolean getUnderReplicated() {
        return underReplicated;
    }

    public void setUnderReplicated(Boolean underReplicated) {
        this.underReplicated = underReplicated;
    }

    @Override
    public String toString() {
        return "ThirdPartBrokerOverviewVO{" +
                "clusterId=" + clusterId +
                ", brokerId=" + brokerId +
                ", underReplicated=" + underReplicated +
                '}';
    }
}