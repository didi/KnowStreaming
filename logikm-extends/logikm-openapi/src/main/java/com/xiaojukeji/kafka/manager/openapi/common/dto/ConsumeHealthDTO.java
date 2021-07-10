package com.xiaojukeji.kafka.manager.openapi.common.dto;

import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author zengqiao
 * @date 20/6/2
 */
@ApiModel(description = "消费健康")
public class ConsumeHealthDTO {
    @ApiModelProperty(value = "集群ID")
    private Long clusterId;

    @ApiModelProperty(value = "Topic名称")
    private List<String> topicNameList;

    @ApiModelProperty(value = "消费组")
    private String consumerGroup;

    @ApiModelProperty(value = "允许最大延迟(ms)")
    private Long maxDelayTime;

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    public List<String> getTopicNameList() {
        return topicNameList;
    }

    public void setTopicNameList(List<String> topicNameList) {
        this.topicNameList = topicNameList;
    }

    public String getConsumerGroup() {
        return consumerGroup;
    }

    public void setConsumerGroup(String consumerGroup) {
        this.consumerGroup = consumerGroup;
    }

    public Long getMaxDelayTime() {
        return maxDelayTime;
    }

    public void setMaxDelayTime(Long maxDelayTime) {
        this.maxDelayTime = maxDelayTime;
    }

    @Override
    public String toString() {
        return "ConsumeHealthDTO{" +
                "clusterId=" + clusterId +
                ", topicNameList=" + topicNameList +
                ", consumerGroup='" + consumerGroup + '\'' +
                ", maxDelayTime=" + maxDelayTime +
                '}';
    }

    public boolean paramLegal() {
        if (ValidateUtils.isNull(clusterId)
                || ValidateUtils.isEmptyList(topicNameList)
                || ValidateUtils.isBlank(consumerGroup)
                || ValidateUtils.isNullOrLessThanZero(maxDelayTime)) {
            return false;
        }
        for (String topicName: topicNameList) {
            if (ValidateUtils.isExistBlank(topicName)) {
                return false;
            }
        }
        return true;
    }
}