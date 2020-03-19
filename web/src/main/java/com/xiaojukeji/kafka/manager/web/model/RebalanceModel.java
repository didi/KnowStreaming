package com.xiaojukeji.kafka.manager.web.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * rebalance请求参数
 * @author zengqiao
 * @date 19/7/8
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(value = "RebalanceModel", description = "rebalance模型")
public class RebalanceModel {
    @ApiModelProperty(value = "集群Id")
    private Long clusterId;

    @ApiModelProperty(value = "brokerId")
    private Integer brokerId;

    @ApiModelProperty(value = "Topic名称")
    private String topicName;

    @ApiModelProperty(value = "维度[0: 集群维度, 1: broker维度]")
    private Integer dimension;

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

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public Integer getDimension() {
        return dimension;
    }

    public void setDimension(Integer dimension) {
        this.dimension = dimension;
    }

    @Override
    public String toString() {
        return "RebalanceModel{" +
                "clusterId=" + clusterId +
                ", brokerId=" + brokerId +
                ", topicName=" + topicName +
                ", dimension=" + dimension +
                '}';
    }

    public boolean legal() {
        if (dimension == null || clusterId == null) {
            return false;
        }
        if (dimension.equals(1) && brokerId == null) {
            return false;
        }
        return true;
    }
}