package com.xiaojukeji.kafka.manager.common.entity.po;

public class OrderPartitionDO extends BaseDO{
    private Long clusterId;

    private String clusterName;

    private String topicName;

    private String applicant;

    private Integer partitionNum;

    private String brokerList;

    private Long peakBytesIn;

    private String description;

    private Integer orderStatus;

    private String approver;

    private String opinion;

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getApplicant() {
        return applicant;
    }

    public void setApplicant(String applicant) {
        this.applicant = applicant;
    }

    public Integer getPartitionNum() {
        return partitionNum;
    }

    public void setPartitionNum(Integer partitionNum) {
        this.partitionNum = partitionNum;
    }

    public String getBrokerList() {
        return brokerList;
    }

    public void setBrokerList(String brokerList) {
        this.brokerList = brokerList;
    }

    public Long getPeakBytesIn() {
        return peakBytesIn;
    }

    public void setPeakBytesIn(Long peakBytesIn) {
        this.peakBytesIn = peakBytesIn;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(Integer orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getApprover() {
        return approver;
    }

    public void setApprover(String approver) {
        this.approver = approver;
    }

    public String getOpinion() {
        return opinion;
    }

    public void setOpinion(String opinion) {
        this.opinion = opinion;
    }

    @Override
    public String toString() {
        return "OrderPartitionDO{" +
                "clusterId=" + clusterId +
                ", clusterName='" + clusterName + '\'' +
                ", topicName='" + topicName + '\'' +
                ", applicant='" + applicant + '\'' +
                ", partitionNum=" + partitionNum +
                ", brokerList='" + brokerList + '\'' +
                ", peakBytesIn=" + peakBytesIn +
                ", description='" + description + '\'' +
                ", orderStatus=" + orderStatus +
                ", approver='" + approver + '\'' +
                ", opinion='" + opinion + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", gmtCreate=" + gmtCreate +
                ", gmtModify=" + gmtModify +
                '}';
    }
}