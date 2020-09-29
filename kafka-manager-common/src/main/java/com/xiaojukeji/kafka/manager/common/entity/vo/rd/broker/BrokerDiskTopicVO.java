package com.xiaojukeji.kafka.manager.common.entity.vo.rd.broker;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author zengqiao
 * @date 20/4/30
 */
@ApiModel(description = "Broker磁盘信息")
public class BrokerDiskTopicVO {
    @ApiModelProperty(value = "集群ID")
    private Long clusterId;

    @ApiModelProperty(value = "Topic名称")
    private String topicName;

    @ApiModelProperty(value = "BrokerID")
    private Integer brokerId;

    @ApiModelProperty(value = "磁盘名")
    private String diskName;

    @ApiModelProperty(value = "Leader分区")
    private List<Integer> leaderPartitions;

    @ApiModelProperty(value = "Follow分区")
    private List<Integer> followerPartitions;

    @ApiModelProperty(value = "处于同步状态")
    private Boolean underReplicated;

    @ApiModelProperty(value = "未处于同步状态的分区")
    private List<Integer> notUnderReplicatedPartitions;

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

    public Integer getBrokerId() {
        return brokerId;
    }

    public void setBrokerId(Integer brokerId) {
        this.brokerId = brokerId;
    }

    public String getDiskName() {
        return diskName;
    }

    public void setDiskName(String diskName) {
        this.diskName = diskName;
    }

    public List<Integer> getLeaderPartitions() {
        return leaderPartitions;
    }

    public void setLeaderPartitions(List<Integer> leaderPartitions) {
        this.leaderPartitions = leaderPartitions;
    }

    public List<Integer> getFollowerPartitions() {
        return followerPartitions;
    }

    public void setFollowerPartitions(List<Integer> followerPartitions) {
        this.followerPartitions = followerPartitions;
    }

    public Boolean getUnderReplicated() {
        return underReplicated;
    }

    public void setUnderReplicated(Boolean underReplicated) {
        this.underReplicated = underReplicated;
    }

    public List<Integer> getNotUnderReplicatedPartitions() {
        return notUnderReplicatedPartitions;
    }

    public void setNotUnderReplicatedPartitions(List<Integer> notUnderReplicatedPartitions) {
        this.notUnderReplicatedPartitions = notUnderReplicatedPartitions;
    }

    @Override
    public String toString() {
        return "BrokerDiskTopicVO{" +
                "clusterId=" + clusterId +
                ", topicName='" + topicName + '\'' +
                ", brokerId=" + brokerId +
                ", diskName='" + diskName + '\'' +
                ", leaderPartitions=" + leaderPartitions +
                ", followerPartitions=" + followerPartitions +
                ", underReplicated=" + underReplicated +
                ", notUnderReplicatedPartitions=" + notUnderReplicatedPartitions +
                '}';
    }
}