package com.xiaojukeji.kafka.manager.common.entity.ao.topic;

import java.util.List;

/**
 * @author arthur
 * @date 2017/6/6.
 */
public class TopicPartitionDTO {
    private Integer partitionId;

    private Long beginningOffset;

    private Long endOffset;

    private Long msgNum;

    private Integer leaderBrokerId;

    private Integer preferredBrokerId;

    private Integer leaderEpoch;

    private List<Integer> replicaBrokerIdList;

    private List<Integer> isrBrokerIdList;

    private Boolean underReplicated;

    private Long logSize;

    private String location;

    public Integer getPartitionId() {
        return partitionId;
    }

    public void setPartitionId(Integer partitionId) {
        this.partitionId = partitionId;
    }

    public Long getBeginningOffset() {
        return beginningOffset;
    }

    public void setBeginningOffset(Long beginningOffset) {
        this.beginningOffset = beginningOffset;
    }

    public Long getEndOffset() {
        return endOffset;
    }

    public void setEndOffset(Long endOffset) {
        this.endOffset = endOffset;
    }

    public Long getMsgNum() {
        return msgNum;
    }

    public void setMsgNum(Long msgNum) {
        this.msgNum = msgNum;
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

    public Boolean getUnderReplicated() {
        return underReplicated;
    }

    public void setUnderReplicated(Boolean underReplicated) {
        this.underReplicated = underReplicated;
    }

    public Long getLogSize() {
        return logSize;
    }

    public void setLogSize(Long logSize) {
        this.logSize = logSize;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "TopicPartitionDTO{" +
                "partitionId=" + partitionId +
                ", beginningOffset=" + beginningOffset +
                ", endOffset=" + endOffset +
                ", msgNum=" + msgNum +
                ", leaderBrokerId=" + leaderBrokerId +
                ", preferredBrokerId=" + preferredBrokerId +
                ", leaderEpoch=" + leaderEpoch +
                ", replicaBrokerIdList=" + replicaBrokerIdList +
                ", isrBrokerIdList=" + isrBrokerIdList +
                ", underReplicated=" + underReplicated +
                ", logSize=" + logSize +
                ", location='" + location + '\'' +
                '}';
    }
}
