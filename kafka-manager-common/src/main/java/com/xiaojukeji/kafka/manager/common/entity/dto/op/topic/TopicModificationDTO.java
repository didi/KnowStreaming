package com.xiaojukeji.kafka.manager.common.entity.dto.op.topic;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.xiaojukeji.kafka.manager.common.entity.dto.ClusterTopicDTO;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Properties;

/**
 * @author zengqiao
 * @date 20/4/24
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(value = "Topic修改")
public class TopicModificationDTO extends ClusterTopicDTO {
    @ApiModelProperty(value = "AppID")
    private String appId;

    @ApiModelProperty(value = "消息保存时间(ms)")
    private Long retentionTime;

    @ApiModelProperty(value = "备注")
    private String description;

    @ApiModelProperty(value = "Topic属性列表")
    private Properties properties;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public Long getRetentionTime() {
        return retentionTime;
    }

    public void setRetentionTime(Long retentionTime) {
        this.retentionTime = retentionTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    @Override
    public String toString() {
        return "TopicModificationDTO{" +
                "appId='" + appId + '\'' +
                ", retentionTime=" + retentionTime +
                ", description='" + description + '\'' +
                ", properties='" + properties + '\'' +
                ", clusterId=" + clusterId +
                ", topicName='" + topicName + '\'' +
                '}';
    }

    @Override
    public boolean paramLegal() {
        if (ValidateUtils.isNull(clusterId)
                || ValidateUtils.isNull(topicName)
                || ValidateUtils.isNull(appId)
                || ValidateUtils.isNull(retentionTime)
                || ValidateUtils.isNull(description)) {
            return false;
        }
        return true;
    }
}