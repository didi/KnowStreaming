package com.xiaojukeji.kafka.manager.web.model.topic;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author zengqiao
 * @date 19/7/11
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(value = "TopicFavorite", description = "Topic收藏,取消收藏")
public class TopicFavorite {
    @ApiModelProperty(value = "集群ID")
    private Long clusterId;

    @ApiModelProperty(value = "Topic名称")
    private String topicName;

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
        return "TopicFavorite{" +
                "clusterId=" + clusterId +
                ", topicName='" + topicName + '\'' +
                '}';
    }
}