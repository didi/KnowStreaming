package com.xiaojukeji.kafka.manager.common.entity.pojo;

import java.util.Date;

/**
 * @author zengqiao
 * @date 20/4/21
 */
public class ClusterTaskDO {
    private Long id;

    private String uuid;

    private Long clusterId;

    private String taskType;

    private String kafkaPackage;

    private String kafkaPackageMd5;

    private String serverProperties;

    private String serverPropertiesMd5;

    private Long agentTaskId;

    private Long agentRollbackTaskId;

    private String hostList;

    private String pauseHostList;

    private String rollbackHostList;

    private String rollbackPauseHostList;

    private String operator;

    private Integer taskStatus;

    private Date createTime;

    private Date modifyTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getKafkaPackage() {
        return kafkaPackage;
    }

    public void setKafkaPackage(String kafkaPackage) {
        this.kafkaPackage = kafkaPackage;
    }

    public String getKafkaPackageMd5() {
        return kafkaPackageMd5;
    }

    public void setKafkaPackageMd5(String kafkaPackageMd5) {
        this.kafkaPackageMd5 = kafkaPackageMd5;
    }

    public String getServerProperties() {
        return serverProperties;
    }

    public void setServerProperties(String serverProperties) {
        this.serverProperties = serverProperties;
    }

    public String getServerPropertiesMd5() {
        return serverPropertiesMd5;
    }

    public void setServerPropertiesMd5(String serverPropertiesMd5) {
        this.serverPropertiesMd5 = serverPropertiesMd5;
    }

    public Long getAgentTaskId() {
        return agentTaskId;
    }

    public void setAgentTaskId(Long agentTaskId) {
        this.agentTaskId = agentTaskId;
    }

    public Long getAgentRollbackTaskId() {
        return agentRollbackTaskId;
    }

    public void setAgentRollbackTaskId(Long agentRollbackTaskId) {
        this.agentRollbackTaskId = agentRollbackTaskId;
    }

    public String getHostList() {
        return hostList;
    }

    public void setHostList(String hostList) {
        this.hostList = hostList;
    }

    public String getPauseHostList() {
        return pauseHostList;
    }

    public void setPauseHostList(String pauseHostList) {
        this.pauseHostList = pauseHostList;
    }

    public String getRollbackHostList() {
        return rollbackHostList;
    }

    public void setRollbackHostList(String rollbackHostList) {
        this.rollbackHostList = rollbackHostList;
    }

    public String getRollbackPauseHostList() {
        return rollbackPauseHostList;
    }

    public void setRollbackPauseHostList(String rollbackPauseHostList) {
        this.rollbackPauseHostList = rollbackPauseHostList;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Integer getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(Integer taskStatus) {
        this.taskStatus = taskStatus;
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
        return "ClusterTaskDO{" +
                "id=" + id +
                ", uuid='" + uuid + '\'' +
                ", clusterId=" + clusterId +
                ", taskType='" + taskType + '\'' +
                ", kafkaPackage='" + kafkaPackage + '\'' +
                ", kafkaPackageMd5='" + kafkaPackageMd5 + '\'' +
                ", serverProperties='" + serverProperties + '\'' +
                ", serverPropertiesMd5='" + serverPropertiesMd5 + '\'' +
                ", agentTaskId=" + agentTaskId +
                ", agentRollbackTaskId=" + agentRollbackTaskId +
                ", hostList='" + hostList + '\'' +
                ", pauseHostList='" + pauseHostList + '\'' +
                ", rollbackHostList='" + rollbackHostList + '\'' +
                ", rollbackPauseHostList='" + rollbackPauseHostList + '\'' +
                ", operator='" + operator + '\'' +
                ", taskStatus=" + taskStatus +
                ", createTime=" + createTime +
                ", modifyTime=" + modifyTime +
                '}';
    }
}