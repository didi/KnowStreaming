package com.xiaojukeji.kafka.manager.bpm.common.entry.apply;

import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;

/**
 * @author zengqiao
 * @date 20/5/12
 */
public class OrderExtensionDeleteTopicDTO {
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

    public boolean paramLegal() {
        if (ValidateUtils.isNull(clusterId) ||
                ValidateUtils.isNull(topicName)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "OrderDeleteTopicExtensionDTO{" +
                "clusterId=" + clusterId +
                ", topicName='" + topicName + '\'' +
                '}';
    }
}