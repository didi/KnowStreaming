package com.xiaojukeji.kafka.manager.common.entity.pojo;

import java.util.Date;

/**
 * @author zhongyuankai
 * @date 20/4/3
 */
public class TopicThrottledMetricsDO {
    private Long id;

    private Long clusterId;

    private String topicName;

    private String appId;

    private Integer produceThrottled;

    private Integer fetchThrottled;

    private Date gmtCreate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public Integer getProduceThrottled() {
        return produceThrottled;
    }

    public void setProduceThrottled(Integer produceThrottled) {
        this.produceThrottled = produceThrottled;
    }

    public Integer getFetchThrottled() {
        return fetchThrottled;
    }

    public void setFetchThrottled(Integer fetchThrottled) {
        this.fetchThrottled = fetchThrottled;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    @Override
    public String toString() {
        return "TopicThrottleDO{" +
                "id=" + id +
                ", clusterId=" + clusterId +
                ", topicName='" + topicName + '\'' +
                ", appId='" + appId + '\'' +
                ", produceThrottled=" + produceThrottled +
                ", fetchThrottled=" + fetchThrottled +
                ", gmtCreate=" + gmtCreate +
                '}';
    }
}
