package com.xiaojukeji.kafka.manager.common.entity.dto.op.topic;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.xiaojukeji.kafka.manager.common.entity.dto.ClusterTopicDTO;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author zengqiao
 * @date 20/4/2
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(value = "Topic删除")
public class TopicDeletionDTO extends ClusterTopicDTO {
    @ApiModelProperty(value = "不强制")
    private Boolean unForce;

    public Boolean getUnForce() {
        return unForce;
    }

    public void setUnForce(Boolean unForce) {
        this.unForce = unForce;
    }

    @Override
    public String toString() {
        return "TopicDeletionDTO{" +
                "unForce=" + unForce +
                ", clusterId=" + clusterId +
                ", topicName='" + topicName + '\'' +
                '}';
    }

    public boolean paramLegal() {
        if (ValidateUtils.isNull(clusterId)
                || ValidateUtils.isNull(topicName)
                || ValidateUtils.isNull(unForce)) {
            return false;
        }
        return true;
    }
}