package com.xiaojukeji.kafka.manager.common.entity.ao.topic;

/**
 * @author zhongyuankai
 * @date 20/09/08
 */
public class TopicBusinessInfo {
    private String appId;

    private String appName;

    private String principals;

    private Long clusterId;

    private String topicName;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPrincipals() {
        return principals;
    }

    public void setPrincipals(String principals) {
        this.principals = principals;
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
        return "TopicBusinessInfoVO{" +
                "appId='" + appId + '\'' +
                ", appName='" + appName + '\'' +
                ", principals='" + principals + '\'' +
                ", clusterId=" + clusterId +
                ", topicName='" + topicName + '\'' +
                '}';
    }
}
