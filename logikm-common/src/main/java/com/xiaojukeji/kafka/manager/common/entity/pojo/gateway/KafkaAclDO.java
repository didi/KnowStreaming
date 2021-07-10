package com.xiaojukeji.kafka.manager.common.entity.pojo.gateway;

import java.util.Date;

/**
 * @author zengqiao
 * @date 20/7/21
 */
public class KafkaAclDO {
    private Long id;

    private String appId;

    private Long clusterId;

    private String topicName;

    private Integer access;

    private Integer operation;

    private Date createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
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

    public Integer getAccess() {
        return access;
    }

    public void setAccess(Integer access) {
        this.access = access;
    }

    public Integer getOperation() {
        return operation;
    }

    public void setOperation(Integer operation) {
        this.operation = operation;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "KafkaAclDO{" +
                "id=" + id +
                ", appId='" + appId + '\'' +
                ", clusterId=" + clusterId +
                ", topicName='" + topicName + '\'' +
                ", access=" + access +
                ", operation=" + operation +
                ", createTime=" + createTime +
                '}';
    }
}