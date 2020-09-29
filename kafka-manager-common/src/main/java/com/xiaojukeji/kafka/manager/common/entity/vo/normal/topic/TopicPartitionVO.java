package com.xiaojukeji.kafka.manager.common.entity.vo.normal.topic;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author arthur
 * @date 2017/6/6.
 */
@ApiModel(value = "分区信息")
public class TopicPartitionVO {
    @ApiModelProperty(value = "分区ID")
    private Integer partitionId;

    @ApiModelProperty(value = "起始偏移")
    private Long beginningOffset;

    @ApiModelProperty(value = "结尾偏移")
    private Long endOffset;

    @ApiModelProperty(value = "消息条数")
    private Long msgNum;

    @ApiModelProperty(value = "Leader副本")
    private Integer leaderBrokerId;

    @ApiModelProperty(value = "首选副本")
    private Integer preferredBrokerId;

    @ApiModelProperty(value = "replicas")
    private List<Integer> replicaBrokerIdList;

    @ApiModelProperty(value = "ISR")
    private List<Integer> isrBrokerIdList;

    @ApiModelProperty(value = "True:未同步, False:已同步")
    private Boolean underReplicated;

    @ApiModelProperty(value = "Leader副本的大小(B)")
    private Long logSize;

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

    @Override
    public String toString() {
        return "TopicPartitionVO{" +
                "partitionId=" + partitionId +
                ", beginningOffset=" + beginningOffset +
                ", endOffset=" + endOffset +
                ", msgNum=" + msgNum +
                ", leaderBrokerId=" + leaderBrokerId +
                ", preferredBrokerId=" + preferredBrokerId +
                ", replicaBrokerIdList=" + replicaBrokerIdList +
                ", isrBrokerIdList=" + isrBrokerIdList +
                ", underReplicated=" + underReplicated +
                ", logSize=" + logSize +
                '}';
    }
}
