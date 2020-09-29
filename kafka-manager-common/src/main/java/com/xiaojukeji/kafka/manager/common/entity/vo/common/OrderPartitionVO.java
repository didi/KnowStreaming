package com.xiaojukeji.kafka.manager.common.entity.vo.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author zengqiao
 * @date 19/6/23
 */
@ApiModel(value = "OrderPartitionVO", description = "分区申请工单")
public class OrderPartitionVO {
    @ApiModelProperty(value = "工单ID")
    private Long orderId;

    @ApiModelProperty(value = "集群Id")
    private Long clusterId;

    @ApiModelProperty(value = "集群名称")
    private String clusterName;

    @ApiModelProperty(value = "Topic名称")
    private String topicName;

    @ApiModelProperty(value = "申请人")
    private String applicant;

    @ApiModelProperty(value = "预计峰值流量(MB/s)")
    private Long predictBytesIn;

    @ApiModelProperty(value = "近24小时峰值流量(MB/s)")
    private Long realBytesIn;

    @ApiModelProperty(value = "当前分区数")
    private Integer partitionNum;

    @ApiModelProperty(value = "当前Topic所处的Region")
    private List<String> regionNameList;

    @ApiModelProperty(value = "Region的brokerId列表")
    private List<Integer> regionBrokerIdList;

    @ApiModelProperty(value = "Topic的brokerId列表")
    private List<Integer> brokerIdList;

    @ApiModelProperty(value = "备注信息")
    private String description;

    @ApiModelProperty(value = "工单状态, 0:待处理, 1:通过, 2:拒绝, 3:撤销")
    private Integer orderStatus;

    @ApiModelProperty(value = "审批人")
    private String approver;

    @ApiModelProperty(value = "审批意见")
    private String approvalOpinions;

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

    public String getApplicant() {
        return applicant;
    }

    public void setApplicant(String applicant) {
        this.applicant = applicant;
    }

    public Long getPredictBytesIn() {
        return predictBytesIn;
    }

    public void setPredictBytesIn(Long predictBytesIn) {
        this.predictBytesIn = predictBytesIn;
    }

    public Long getRealBytesIn() {
        return realBytesIn;
    }

    public void setRealBytesIn(Long realBytesIn) {
        this.realBytesIn = realBytesIn;
    }

    public Integer getPartitionNum() {
        return partitionNum;
    }

    public void setPartitionNum(Integer partitionNum) {
        this.partitionNum = partitionNum;
    }

    public List<String> getRegionNameList() {
        return regionNameList;
    }

    public void setRegionNameList(List<String> regionNameList) {
        this.regionNameList = regionNameList;
    }

    public List<Integer> getRegionBrokerIdList() {
        return regionBrokerIdList;
    }

    public void setRegionBrokerIdList(List<Integer> regionBrokerIdList) {
        this.regionBrokerIdList = regionBrokerIdList;
    }

    public List<Integer> getBrokerIdList() {
        return brokerIdList;
    }

    public void setBrokerIdList(List<Integer> brokerIdList) {
        this.brokerIdList = brokerIdList;
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

    public String getApprovalOpinions() {
        return approvalOpinions;
    }

    public void setApprovalOpinions(String approvalOpinions) {
        this.approvalOpinions = approvalOpinions;
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
        return "OrderPartitionVO{" +
                "orderId=" + orderId +
                ", clusterId=" + clusterId +
                ", clusterName='" + clusterName + '\'' +
                ", topicName='" + topicName + '\'' +
                ", applicant='" + applicant + '\'' +
                ", predictBytesIn=" + predictBytesIn +
                ", realBytesIn=" + realBytesIn +
                ", partitionNum=" + partitionNum +
                ", regionNameList=" + regionNameList +
                ", regionBrokerIdList=" + regionBrokerIdList +
                ", brokerIdList=" + brokerIdList +
                ", description='" + description + '\'' +
                ", orderStatus=" + orderStatus +
                ", approver='" + approver + '\'' +
                ", approvalOpinions='" + approvalOpinions + '\'' +
                ", gmtCreate=" + gmtCreate +
                ", gmtModify=" + gmtModify +
                '}';
    }
}