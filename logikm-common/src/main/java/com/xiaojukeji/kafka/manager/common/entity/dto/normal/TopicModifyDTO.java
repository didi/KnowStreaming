package com.xiaojukeji.kafka.manager.common.entity.dto.normal;

import com.xiaojukeji.kafka.manager.common.entity.dto.ClusterTopicDTO;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author zengqiao
 * @date 20/4/8
 */
public class TopicModifyDTO extends ClusterTopicDTO {
    @ApiModelProperty(value = "描述")
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "TopicModifyDTO{" +
                "description='" + description + '\'' +
                ", clusterId=" + clusterId +
                ", topicName='" + topicName + '\'' +
                '}';
    }

    @Override
    public boolean paramLegal() {
        if (ValidateUtils.isNull(clusterId)
                || ValidateUtils.isExistBlank(topicName)
                || ValidateUtils.isNull(description)) {
            return false;
        }
        return true;
    }
}