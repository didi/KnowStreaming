package com.xiaojukeji.kafka.manager.common.entity.dto;

import java.io.Serializable;
import java.util.List;

/**
 * @author arthur
 * @date 2017/6/6.
 */
public class TopicPartitionDTO implements Serializable {

    private Integer partitionId;

    private Long  offset;

    private Integer leaderBrokerId;

    private Integer preferredBrokerId;

    private Integer leaderEpoch;

    private List<Integer> replicasBroker;

    private List<Integer> isr;

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

    public List<Integer> getReplicasBroker() {
        return replicasBroker;
    }

    public void setReplicasBroker(List<Integer> replicasBroker) {
        this.replicasBroker = replicasBroker;
    }

    public List<Integer> getIsr() {
        return isr;
    }

    public void setIsr(List<Integer> isr) {
        this.isr = isr;
    }

    public boolean isUnderReplicated() {
        return underReplicated;
    }

    public void setUnderReplicated(boolean underReplicated) {
        this.underReplicated = underReplicated;
    }

    @Override
    public String toString() {
        return "TopicPartitionDTO{" +
                "partitionId=" + partitionId +
                ", offset=" + offset +
                ", leaderBrokerId=" + leaderBrokerId +
                ", preferredBrokerId=" + preferredBrokerId +
                ", leaderEpoch=" + leaderEpoch +
                ", replicasBroker=" + replicasBroker +
                ", isr=" + isr +
                ", underReplicated=" + underReplicated +
                '}';
    }
}
