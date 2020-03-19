package com.xiaojukeji.kafka.manager.web.vo.topic;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author arthur
 * @date 2017/6/6.
 */
@ApiModel(value = "分区信息")
public class TopicPartitionVO {
    @ApiModelProperty(value = "分区Id")
    private Integer partitionId;

    @ApiModelProperty(value = "offset偏移")
    private Long offset;

    @ApiModelProperty(value = "分区leader所在Broker")
    private Integer leaderBrokerId;

    @ApiModelProperty(value = "首选leader的Broker")
    private Integer preferredBrokerId;

    @ApiModelProperty(value = "leaderEpoch")
    private Integer leaderEpoch;

    @ApiModelProperty(value = "replica")
    private List<Integer> replicaBrokerIdList;

    @ApiModelProperty(value = "ISR")
    private List<Integer> isrBrokerIdList;

    @ApiModelProperty(value = "True:未同步, False:已同步")
    private Boolean underReplicated;

    public Integer getPartitionId() {
        return partitionId;
    }

    public void setPartitionId(Integer partitionId) {
        this.partitionId = partitionId;
    }

    public Long getOffset() {
        return offset;
    }

    public void setOffset(Long offset) {
        this.offset = offset;
    }

    public Integer getLeaderBrokerId() {
        return leaderBrokerId;
    }

    public void setLeaderBrokerId(Integer leaderBrokerId) {
        this.leaderBrokerId = leaderBrokerId;
    }

    public Integer getPreferredBrokerId() {
        return preferredBrokerId;
    }

    public void setPreferredBrokerId(Integer preferredBrokerId) {
        this.preferredBrokerId = preferredBrokerId;
    }

    public Integer getLeaderEpoch() {
        return leaderEpoch;
    }

    public void setLeaderEpoch(Integer leaderEpoch) {
        this.leaderEpoch = leaderEpoch;
    }

    public List<Integer> getReplicaBrokerIdList() {
        return replicaBrokerIdList;
    }

    public void setReplicaBrokerIdList(List<Integer> replicaBrokerIdList) {
        this.replicaBrokerIdList = replicaBrokerIdList;
    }

    public List<Integer> getIsrBrokerIdList() {
        return isrBrokerIdList;
    }

    public void setIsrBrokerIdList(List<Integer> isrBrokerIdList) {
        this.isrBrokerIdList = isrBrokerIdList;
    }

    public boolean isUnderReplicated() {
        return underReplicated;
    }

    public void setUnderReplicated(boolean underReplicated) {
        this.underReplicated = underReplicated;
    }

    @Override
    public String toString() {
        return "TopicPartitionVO{" +
                "partitionId=" + partitionId +
                ", offset=" + offset +
                ", leaderBrokerId=" + leaderBrokerId +
                ", preferredBrokerId=" + preferredBrokerId +
                ", leaderEpoch=" + leaderEpoch +
                ", replicaBrokerIdList=" + replicaBrokerIdList +
                ", isrBrokerIdList=" + isrBrokerIdList +
                ", underReplicated=" + underReplicated +
                '}';
    }
}
