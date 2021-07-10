package com.xiaojukeji.kafka.manager.common.entity.vo.rd.broker;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author zengqiao
 * @date 19/4/22
 */
@ApiModel(description = "Broker分区信息")
public class BrokerPartitionVO {
    @ApiModelProperty(value = "Topic名称")
    private String topicName;

    @ApiModelProperty(value = "Leader分区")
    private List<Integer> leaderPartitionList;

    @ApiModelProperty(value = "Follower分区")
    private List<Integer> followerPartitionIdList;

    @ApiModelProperty(value = "是否未同步完成")
    private Boolean underReplicated;

    @ApiModelProperty(value = "未同步分区列表")
    private List<Integer> notUnderReplicatedPartitionIdList;

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public List<Integer> getLeaderPartitionList() {
        return leaderPartitionList;
    }

    public void setLeaderPartitionList(List<Integer> leaderPartitionList) {
        this.leaderPartitionList = leaderPartitionList;
    }

    public List<Integer> getFollowerPartitionIdList() {
        return followerPartitionIdList;
    }

    public void setFollowerPartitionIdList(List<Integer> followerPartitionIdList) {
        this.followerPartitionIdList = followerPartitionIdList;
    }

    public Boolean getUnderReplicated() {
        return underReplicated;
    }

    public void setUnderReplicated(Boolean underReplicated) {
        this.underReplicated = underReplicated;
    }

    public List<Integer> getNotUnderReplicatedPartitionIdList() {
        return notUnderReplicatedPartitionIdList;
    }

    public void setNotUnderReplicatedPartitionIdList(List<Integer> notUnderReplicatedPartitionIdList) {
        this.notUnderReplicatedPartitionIdList = notUnderReplicatedPartitionIdList;
    }

    @Override
    public String toString() {
        return "BrokerPartitionVO{" +
                "topicName='" + topicName + '\'' +
                ", leaderPartitionList=" + leaderPartitionList +
                ", followerPartitionIdList=" + followerPartitionIdList +
                ", underReplicated=" + underReplicated +
                ", notUnderReplicatedPartitionIdList=" + notUnderReplicatedPartitionIdList +
                '}';
    }
}
