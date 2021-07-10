package com.xiaojukeji.kafka.manager.common.entity.pojo;

import java.util.Date;

/**
 * migrate topic task do
 * @author zengqiao
 * @date 19/4/16
 */
public class ReassignTaskDO {
    private Long id;

    private Integer status;

    private Date gmtCreate;

    private Date gmtModify;

    private Long taskId;

    private Long clusterId;

    private String topicName;

    private String partitions;

    private String reassignmentJson;

    private Long realThrottle;

    private Long maxThrottle;

    private Long minThrottle;

    private Date beginTime;

    private Long originalRetentionTime;

    private Long reassignRetentionTime;

    private String srcBrokers;

    private String destBrokers;

    private String description;

    private String operator;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
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

    public String getPartitions() {
        return partitions;
    }

    public void setPartitions(String partitions) {
        this.partitions = partitions;
    }

    public String getReassignmentJson() {
        return reassignmentJson;
    }

    public void setReassignmentJson(String reassignmentJson) {
        this.reassignmentJson = reassignmentJson;
    }

    public Long getRealThrottle() {
        return realThrottle;
    }

    public void setRealThrottle(Long realThrottle) {
        this.realThrottle = realThrottle;
    }

    public Long getMaxThrottle() {
        return maxThrottle;
    }

    public void setMaxThrottle(Long maxThrottle) {
        this.maxThrottle = maxThrottle;
    }

    public Long getMinThrottle() {
        return minThrottle;
    }

    public void setMinThrottle(Long minThrottle) {
        this.minThrottle = minThrottle;
    }

    public Date getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    public Long getOriginalRetentionTime() {
        return originalRetentionTime;
    }

    public void setOriginalRetentionTime(Long originalRetentionTime) {
        this.originalRetentionTime = originalRetentionTime;
    }

    public Long getReassignRetentionTime() {
        return reassignRetentionTime;
    }

    public void setReassignRetentionTime(Long reassignRetentionTime) {
        this.reassignRetentionTime = reassignRetentionTime;
    }

    public String getSrcBrokers() {
        return srcBrokers;
    }

    public void setSrcBrokers(String srcBrokers) {
        this.srcBrokers = srcBrokers;
    }

    public String getDestBrokers() {
        return destBrokers;
    }

    public void setDestBrokers(String destBrokers) {
        this.destBrokers = destBrokers;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    @Override
    public String toString() {
        return "ReassignTaskDO{" +
                "id=" + id +
                ", status=" + status +
                ", gmtCreate=" + gmtCreate +
                ", gmtModify=" + gmtModify +
                ", taskId=" + taskId +
                ", clusterId=" + clusterId +
                ", topicName='" + topicName + '\'' +
                ", partitions='" + partitions + '\'' +
                ", reassignmentJson='" + reassignmentJson + '\'' +
                ", realThrottle=" + realThrottle +
                ", maxThrottle=" + maxThrottle +
                ", minThrottle=" + minThrottle +
                ", beginTime=" + beginTime +
                ", originalRetentionTime=" + originalRetentionTime +
                ", reassignRetentionTime=" + reassignRetentionTime +
                ", srcBrokers='" + srcBrokers + '\'' +
                ", destBrokers='" + destBrokers + '\'' +
                ", description='" + description + '\'' +
                ", operator='" + operator + '\'' +
                '}';
    }
}
