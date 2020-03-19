package com.xiaojukeji.kafka.manager.web.model.topic;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author huangyiminghappy@163.com
 * @date 2019-04-21
 */
public class TopicDeleteModel {
    @ApiModelProperty(value = "topicName名称列表")
    private List<String> topicNameList;

    @ApiModelProperty(value = "集群id")
    private Long clusterId;

    public List<String> getTopicNameList() {
        return topicNameList;
    }

    public void setTopicNameList(List<String> topicNameList) {
        this.topicNameList = topicNameList;
    }

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    @Override
    public String toString() {
        return "TopicDeleteModel{" +
                "topicNameList=" + topicNameList +
                ", clusterId=" + clusterId +
                '}';
    }

    public boolean legal() {
        if (topicNameList == null || clusterId == null) {
            return false;
        }
        return true;
    }
}
