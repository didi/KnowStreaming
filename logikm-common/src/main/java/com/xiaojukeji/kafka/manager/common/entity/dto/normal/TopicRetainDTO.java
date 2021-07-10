package com.xiaojukeji.kafka.manager.common.entity.dto.normal;

import com.xiaojukeji.kafka.manager.common.entity.dto.ClusterTopicDTO;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author zengqiao
 * @date 20/4/8
 */
@ApiModel(description = "Topic延长保留")
public class TopicRetainDTO extends ClusterTopicDTO{

    @ApiModelProperty(value = "延期天数")
    private Integer retainDays;

    public Integer getRetainDays() {
        return retainDays;
    }

    public void setRetainDays(Integer retainDays) {
        this.retainDays = retainDays;
    }

    @Override
    public String toString() {
        return "TopicRetainDTO{" +
                "retainDays=" + retainDays +
                ", clusterId=" + clusterId +
                ", topicName='" + topicName + '\'' +
                '}';
    }

    @Override
    public boolean paramLegal() {
        if (ValidateUtils.isNull(clusterId)
                || ValidateUtils.isNull(topicName)
                || ValidateUtils.isNull(retainDays)) {
            return false;
        }
        return true;
    }
}