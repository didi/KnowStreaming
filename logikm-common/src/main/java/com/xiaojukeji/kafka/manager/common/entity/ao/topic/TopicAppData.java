package com.xiaojukeji.kafka.manager.common.entity.ao.topic;

/**
 * @author zhongyuankai
 * @date 2020/6/8
 */
public class TopicAppData {
    private Long clusterId;

    private String topicName;

    private String appId;

    private String appName;

    private String appPrincipals;

    private Long produceQuota;

    private Long consumerQuota;

    private Boolean produceThrottled;

    private Boolean fetchThrottled;

    private Integer access;

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

    public String getAppPrincipals() {
        return appPrincipals;
    }

    public void setAppPrincipals(String appPrincipals) {
        this.appPrincipals = appPrincipals;
    }

    public Long getProduceQuota() {
        return produceQuota;
    }

    public void setProduceQuota(Long produceQuota) {
        this.produceQuota = produceQuota;
    }

    public Long getConsumerQuota() {
        return consumerQuota;
    }

    public void setConsumerQuota(Long consumerQuota) {
        this.consumerQuota = consumerQuota;
    }

    public Boolean getProduceThrottled() {
        return produceThrottled;
    }

    public void setProduceThrottled(Boolean produceThrottled) {
        this.produceThrottled = produceThrottled;
    }

    public Boolean getFetchThrottled() {
        return fetchThrottled;
    }

    public void setFetchThrottled(Boolean fetchThrottled) {
        this.fetchThrottled = fetchThrottled;
    }

    public Integer getAccess() {
        return access;
    }

    public void setAccess(Integer access) {
        this.access = access;
    }

    @Override
    public String toString() {
        return "TopicAppDTO{" +
                "clusterId=" + clusterId +
                ", topicName='" + topicName + '\'' +
                ", appId='" + appId + '\'' +
                ", appName='" + appName + '\'' +
                ", appPrincipals='" + appPrincipals + '\'' +
                ", produceQuota=" + produceQuota +
                ", consumerQuota=" + consumerQuota +
                ", produceThrottled=" + produceThrottled +
                ", fetchThrottled=" + fetchThrottled +
                ", access=" + access +
                '}';
    }
}
