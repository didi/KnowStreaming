package com.xiaojukeji.kafka.manager.common.entity.pojo.gateway;

import java.util.Date;

/**
 * Topic连接信息
 * @author zengqiao
 * @date 20/7/6
 */
public class TopicConnectionDO {
    private Long id;

    private String appId;

    private Long clusterId;

    private String topicName;

    private String type;

    private String ip;

    private String clientVersion;

    private Date createTime;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getClientVersion() {
        return clientVersion;
    }

    public void setClientVersion(String clientVersion) {
        this.clientVersion = clientVersion;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "TopicConnectionDO{" +
                "id=" + id +
                ", clusterId=" + clusterId +
                ", topicName='" + topicName + '\'' +
                ", type='" + type + '\'' +
                ", appId='" + appId + '\'' +
                ", ip='" + ip + '\'' +
                ", clientVersion='" + clientVersion + '\'' +
                ", createTime=" + createTime +
                '}';
    }

    public String uniqueKey() {
        return appId + clusterId + topicName + type + ip;
    }
}