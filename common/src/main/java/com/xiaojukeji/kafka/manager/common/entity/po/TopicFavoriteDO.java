package com.xiaojukeji.kafka.manager.common.entity.po;

public class TopicFavoriteDO extends BaseDO{
    private String username;

    private Long clusterId;

    private String topicName;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

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
        return "TopicFavoriteDO{" +
                "username='" + username + '\'' +
                ", clusterId=" + clusterId +
                ", topicName='" + topicName + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", gmtCreate=" + gmtCreate +
                ", gmtModify=" + gmtModify +
                '}';
    }
}