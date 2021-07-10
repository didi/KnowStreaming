package com.xiaojukeji.kafka.manager.common.entity.dto;

import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author zengqiao
 * @date 20/4/23
 */
@ApiModel(description="Topic信息")
public class ClusterTopicDTO {
    @ApiModelProperty(value = "集群ID")
    protected Long clusterId;

    @ApiModelProperty(value = "Topic名称")
    protected String topicName;

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    @Override
    public String toString() {
        return "ClusterTopicDTO{" +
                "clusterId=" + clusterId +
                ", topicName='" + topicName + '\'' +
                '}';
    }

    public boolean paramLegal() {
        if (ValidateUtils.isNull(clusterId)
                || ValidateUtils.isNull(topicName)) {
            return false;
        }
        return true;
    }
}