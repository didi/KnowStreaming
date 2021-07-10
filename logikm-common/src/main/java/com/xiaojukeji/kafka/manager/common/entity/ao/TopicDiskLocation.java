package com.xiaojukeji.kafka.manager.common.entity.ao;

import java.util.List;

/**
 * @author zengqiao
 * @date 20/7/8
 */
public class TopicDiskLocation {
    private Long clusterId;

    private String topicName;

    private Integer brokerId;

    private String diskName;

    private List<Integer> leaderPartitions;

    private List<Integer> followerPartitions;

    private Boolean isUnderReplicated;

    private List<Integer> underReplicatedPartitions;

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
        return isUnderReplicated;
    }

    public void setUnderReplicated(Boolean underReplicated) {
        isUnderReplicated = underReplicated;
    }

    public List<Integer> getUnderReplicatedPartitions() {
        return underReplicatedPartitions;
    }

    public void setUnderReplicatedPartitions(List<Integer> underReplicatedPartitions) {
        this.underReplicatedPartitions = underReplicatedPartitions;
    }

    @Override
    public String toString() {
        return "TopicDiskLocation{" +
                "clusterId=" + clusterId +
                ", topicName='" + topicName + '\'' +
                ", brokerId=" + brokerId +
                ", diskName='" + diskName + '\'' +
                ", leaderPartitions=" + leaderPartitions +
                ", followerPartitions=" + followerPartitions +
                ", isUnderReplicated=" + isUnderReplicated +
                ", underReplicatedPartitions=" + underReplicatedPartitions +
                '}';
    }
}