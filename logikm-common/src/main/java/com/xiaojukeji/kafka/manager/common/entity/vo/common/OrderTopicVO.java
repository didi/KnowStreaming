package com.xiaojukeji.kafka.manager.common.entity.vo.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author zengqiao
 * @date 19/6/18
 */
@ApiModel(value = "Topic工单")
public class OrderTopicVO {
    @ApiModelProperty(value = "工单ID")
    private Long orderId;

    @ApiModelProperty(value = "集群Id")
    private Long clusterId;

    @ApiModelProperty(value = "集群名称")
    private String clusterName;

    @ApiModelProperty(value = "Topic名称")
    private String topicName;

    @ApiModelProperty(value = "流量上限(KB)")
    private Long peakBytesIn;

    @ApiModelProperty(value = "保留时间")
    private Long retentionTime;

    private Integer partitionNum;

    private Integer replicaNum;

    private String regions;

    private String brokers;

    @ApiModelProperty(value = "申请人")
    private String applicant;

    @ApiModelProperty(value = "负责人")
    private String principals;

    @ApiModelProperty(value = "备注信息")
    private String description;

    @ApiModelProperty(value = "工单状态, 0:待处理, 1:通过, 2:拒绝, 3:撤销")
    private Integer orderStatus;

    @ApiModelProperty(value = "审批人")
    private String approver;

    @ApiModelProperty(value = "审批意见")
    private String opinion;

    @ApiModelProperty(value = "创建时间")
    private Long gmtCreate;

    @ApiModelProperty(value = "修改时间")
    private Long gmtModify;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

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

    public Long getPeakBytesIn() {
        return peakBytesIn;
    }

    public void setPeakBytesIn(Long peakBytesIn) {
        this.peakBytesIn = peakBytesIn;
    }

    public Long getRetentionTime() {
        return retentionTime;
    }

    public void setRetentionTime(Long retentionTime) {
        this.retentionTime = retentionTime;
    }

    public Integer getPartitionNum() {
        return partitionNum;
    }

    public void setPartitionNum(Integer partitionNum) {
        this.partitionNum = partitionNum;
    }

    public Integer getReplicaNum() {
        return replicaNum;
    }

    public void setReplicaNum(Integer replicaNum) {
        this.replicaNum = replicaNum;
    }

    public String getRegions() {
        return regions;
    }

    public void setRegions(String regions) {
        this.regions = regions;
    }

    public String getBrokers() {
        return brokers;
    }

    public void setBrokers(String brokers) {
        this.brokers = brokers;
    }

    public String getApplicant() {
        return applicant;
    }

    public void setApplicant(String applicant) {
        this.applicant = applicant;
    }

    public String getPrincipals() {
        return principals;
    }

    public void setPrincipals(String principals) {
        this.principals = principals;
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

    public Long getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Long gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Long getGmtModify() {
        return gmtModify;
    }

    public void setGmtModify(Long gmtModify) {
        this.gmtModify = gmtModify;
    }

    @Override
    public String toString() {
        return "OrderTopicVO{" +
                "orderId=" + orderId +
                ", clusterId=" + clusterId +
                ", clusterName='" + clusterName + '\'' +
                ", topicName='" + topicName + '\'' +
                ", peakBytesIn=" + peakBytesIn +
                ", retentionTime=" + retentionTime +
                ", partitionNum=" + partitionNum +
                ", replicaNum=" + replicaNum +
                ", regions='" + regions + '\'' +
                ", brokers='" + brokers + '\'' +
                ", applicant='" + applicant + '\'' +
                ", principals='" + principals + '\'' +
                ", description='" + description + '\'' +
                ", orderStatus=" + orderStatus +
                ", approver='" + approver + '\'' +
                ", opinion='" + opinion + '\'' +
                ", gmtCreate=" + gmtCreate +
                ", gmtModify=" + gmtModify +
                '}';
    }
}