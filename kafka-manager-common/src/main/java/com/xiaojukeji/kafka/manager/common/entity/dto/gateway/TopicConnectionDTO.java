package com.xiaojukeji.kafka.manager.common.entity.dto.gateway;

import java.util.Date;

/**
 * @author zengqiao
 * @date 20/7/6
 */
public class TopicConnectionDTO {
    private Long id;

    private Long clusterId;

    private String topicName;

    // producer or consumer
    private String type;

    // appId#ip#clientVersion
    private String clientInfo;

    private String appId;

    private String ip;

    private String clientVersion;

    private Date gmtCreate;

    private Date gmtModify;

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

    public String getClientInfo() {
        return clientInfo;
    }

    public void setClientInfo(String clientInfo) {
        this.clientInfo = clientInfo;
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

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtModify() {
        return gmtModify;
    }

    public void setGmtModify(Date gmtModify) {
        this.gmtModify = gmtModify;
    }

    @Override
    public String toString() {
        return "TopicConnectionDTO{" +
                "id=" + id +
                ", clusterId=" + clusterId +
                ", topicName='" + topicName + '\'' +
                ", type='" + type + '\'' +
                ", clientInfo='" + clientInfo + '\'' +
                ", appId='" + appId + '\'' +
                ", ip='" + ip + '\'' +
                ", clientVersion='" + clientVersion + '\'' +
                ", gmtCreate=" + gmtCreate +
                ", gmtModify=" + gmtModify +
                '}';
    }
}