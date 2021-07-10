package com.xiaojukeji.kafka.manager.common.entity.pojo.gateway;

import java.util.Date;

/**
 * @author zengqiao
 * @date 20/7/21
 */
public class KafkaUserDO {
    private Long id;

    private String appId;

    private String password;

    private Integer userType;

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getUserType() {
        return userType;
    }

    public void setUserType(Integer userType) {
        this.userType = userType;
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
        return "KafkaUserDO{" +
                "id=" + id +
                ", appId='" + appId + '\'' +
                ", password='" + password + '\'' +
                ", userType=" + userType +
                ", operation=" + operation +
                ", createTime=" + createTime +
                '}';
    }
}