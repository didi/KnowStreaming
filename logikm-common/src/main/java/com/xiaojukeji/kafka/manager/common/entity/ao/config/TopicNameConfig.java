package com.xiaojukeji.kafka.manager.common.entity.ao.config;

import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;

/**
 * @author zengqiao
 * @date 20/8/31
 */
public class TopicNameConfig {
    private Long clusterId;

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
        return "TopicNameConfig{" +
                "clusterId=" + clusterId +
                ", topicName='" + topicName + '\'' +
                '}';
    }

    public boolean legal() {
        if (ValidateUtils.isNull(clusterId) || ValidateUtils.isBlank(topicName)) {
            return false;
        }
        return true;
    }
}