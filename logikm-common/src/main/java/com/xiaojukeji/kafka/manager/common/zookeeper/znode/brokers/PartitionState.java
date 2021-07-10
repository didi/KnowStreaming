package com.xiaojukeji.kafka.manager.common.zookeeper.znode.brokers;

import java.util.ArrayList;
import java.util.List;

/**
 * PartitionState实例
 * 对应zookeeper下的state节点信息以及partition的其它信息
 * @author tukun
 * @date 2015/11/10.
 */
public class PartitionState implements Cloneable {
    /**
     * partition id
     */
    private int partitionId;

    /**
     * kafka集群中的中央控制器选举次数
    */
    private int controller_epoch;

    /**
     * Partition所属的leader broker编号
     */
    private int leader;

    /**
     * partition的版本号
     */
    private int version;

    /**
     * 该partition leader选举次数
     */
    private int leader_epoch;

    /**
     * 同步副本组brokerId列表
     */
    private List<Integer> isr;

    /**
     * 是否处于复制同步状态, true表示未同步, false表示已经同步
     */
    private boolean isUnderReplicated;

    /**
     * Partition的offset
     */
    private long offset;

    /**
     * 被消费的offset
     */
    private long consumeOffset;

    /**
     * 消费者对应的消费group
     */
    private String consumerGroup;

    public int getPartitionId() {
        return partitionId;
    }

    public void setPartitionId(int partitionId) {
        this.partitionId = partitionId;
    }

    public int getControllerEpoch() {
        return controller_epoch;
    }

    public void setControllerEpoch(int controllerEpoch) {
        this.controller_epoch = controllerEpoch;
    }

    public int getLeader() {
        return leader;
    }

    public void setLeader(int leader) {
        this.leader = leader;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getLeaderEpoch() {
        return leader_epoch;
    }

    public void setLeaderEpoch(int leaderEpoch) {
        this.leader_epoch = leaderEpoch;
    }

    public List<Integer> getIsr() {
        return isr;
    }

    public void setIsr(List<Integer> isr) {
        this.isr = isr;
    }

    public boolean isUnderReplicated() {
        return isUnderReplicated;
    }

    public void setUnderReplicated(boolean underReplicated) {
        isUnderReplicated = underReplicated;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public long getConsumeOffset() {
        return consumeOffset;
    }

    public void setConsumeOffset(long consumeOffset) {
        this.consumeOffset = consumeOffset;
    }

    public String getConsumerGroup() {
        return consumerGroup;
    }

    public void setConsumerGroup(String consumerGroup) {
        this.consumerGroup = consumerGroup;
    }

    @Override
    public String toString() {
        return "PartitionState{" +
                "partitionId=" + partitionId +
                ", controller_epoch=" + controller_epoch +
                ", leader=" + leader +
                ", version=" + version +
                ", leader_epoch=" + leader_epoch +
                ", isr=" + isr +
                ", isUnderReplicated=" + isUnderReplicated +
                ", offset=" + offset +
                ", consumeOffset=" + consumeOffset +
                ", consumerGroup='" + consumerGroup + '\'' +
                '}';
    }

    @Override
    public PartitionState clone() {
        try {
            PartitionState partitionState = (PartitionState) super.clone();
            partitionState.setPartitionId(this.partitionId);
            partitionState.setControllerEpoch(this.controller_epoch);
            partitionState.setLeader(this.leader);
            partitionState.setVersion(this.version);
            partitionState.setLeaderEpoch(this.leader_epoch);
            partitionState.setIsr(new ArrayList<>(this.isr));
            partitionState.setOffset(this.offset);
            partitionState.setConsumeOffset(this.consumeOffset);
            partitionState.setConsumerGroup(this.consumerGroup);
            return partitionState;
        } catch (CloneNotSupportedException e) {
        }
        return null;
    }
}
