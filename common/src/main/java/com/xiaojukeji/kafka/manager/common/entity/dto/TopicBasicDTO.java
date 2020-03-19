package com.xiaojukeji.kafka.manager.common.entity.dto;

/**
 * @author arthur
 * @date 2018/09/03
 */
public class TopicBasicDTO {
    private String topicName;

    private Integer partitionNum;

    private Integer replicaNum;

    private Integer brokerNum;

    private String remark;

    private Long modifyTime;

    private Long createTime;

    private String region;

    private Long retentionTime;

    private String principal;

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
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

    public Integer getBrokerNum() {
        return brokerNum;
    }

    public void setBrokerNum(Integer brokerNum) {
        this.brokerNum = brokerNum;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public Long getRetentionTime() {
        return retentionTime;
    }

    public void setRetentionTime(Long retentionTime) {
        this.retentionTime = retentionTime;
    }

    public Long getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Long modifyTime) {
        this.modifyTime = modifyTime;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    @Override
    public String toString() {
        return "TopicBasicInfoDTO{" +
                "topicName='" + topicName + '\'' +
                ", partitionNum=" + partitionNum +
                ", replicaNum=" + replicaNum +
                ", brokerNum=" + brokerNum +
                ", remark='" + remark + '\'' +
                ", modifyTime=" + modifyTime +
                ", createTime=" + createTime +
                ", region='" + region + '\'' +
                ", retentionTime=" + retentionTime +
                ", principal='" + principal + '\'' +
                '}';
    }
}
