package com.xiaojukeji.kafka.manager.common.entity.pojo.gateway;

import java.util.Date;

/**
 * @author zengqiao
 * @date 20/7/21
 */
public class AuthorityDO {
    private Long id;

    private String appId;

    private Long clusterId;

    private String topicName;

    private Integer access;

    private Date createTime;

    private Date modifyTime;

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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    @Override
    public String toString() {
        return "AuthorityDO{" +
                "id=" + id +
                ", appId='" + appId + '\'' +
                ", clusterId=" + clusterId +
                ", topicName='" + topicName + '\'' +
                ", access=" + access +
                ", createTime=" + createTime +
                ", modifyTime=" + modifyTime +
                '}';
    }
}