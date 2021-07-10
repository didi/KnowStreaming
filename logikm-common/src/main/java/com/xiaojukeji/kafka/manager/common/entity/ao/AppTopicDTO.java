package com.xiaojukeji.kafka.manager.common.entity.ao;

/**
 * AppTopic信息
 * @author zengqiao
 * @date 20/5/11
 */
public class AppTopicDTO {
    private Long logicalClusterId;

    private String logicalClusterName;

    private Long physicalClusterId;

    private String topicName;

    private Integer access;

    private String operator;

    private Long gmtCreate;

    public Long getLogicalClusterId() {
        return logicalClusterId;
    }

    public void setLogicalClusterId(Long logicalClusterId) {
        this.logicalClusterId = logicalClusterId;
    }

    public String getLogicalClusterName() {
        return logicalClusterName;
    }

    public void setLogicalClusterName(String logicalClusterName) {
        this.logicalClusterName = logicalClusterName;
    }

    public Long getPhysicalClusterId() {
        return physicalClusterId;
    }

    public void setPhysicalClusterId(Long physicalClusterId) {
        this.physicalClusterId = physicalClusterId;
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

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Long getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Long gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    @Override
    public String toString() {
        return "AppTopicDTO{" +
                "logicalClusterId=" + logicalClusterId +
                ", logicalClusterName='" + logicalClusterName + '\'' +
                ", physicalClusterId=" + physicalClusterId +
                ", topicName='" + topicName + '\'' +
                ", access=" + access +
                ", operator='" + operator + '\'' +
                ", gmtCreate=" + gmtCreate +
                '}';
    }
}