package com.xiaojukeji.kafka.manager.common.zookeeper.znode.brokers;

import java.util.Set;

/**
 * 存储Topic的元信息, 元信息对应的ZK节点是/brokers/topics/${topicName}
 * @author zengqiao
 * @date 19/4/3
 */
public class TopicMetadata implements Cloneable {
    private String topic; //topic名称

    private PartitionMap partitionMap; //partition所在的Broker

    private Set<Integer> brokerIdSet; //topic所在的broker, 由partitionMap获取得到

    private int replicaNum; //副本数

    private int partitionNum; //分区数

    private long modifyTime; //修改节点的时间

    private long createTime; //创建节点的时间

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public int getReplicaNum() {
        return replicaNum;
    }

    public void setReplicaNum(int replicaNum) {
        this.replicaNum = replicaNum;
    }

    public PartitionMap getPartitionMap() {
        return partitionMap;
    }

    public void setPartitionMap(PartitionMap partitionMap) {
        this.partitionMap = partitionMap;
    }

    public Set<Integer> getBrokerIdSet() {
        return brokerIdSet;
    }

    public void setBrokerIdSet(Set<Integer> brokerIdSet) {
        this.brokerIdSet = brokerIdSet;
    }

    public int getPartitionNum() {
        return partitionNum;
    }

    public void setPartitionNum(int partitionNum) {
        this.partitionNum = partitionNum;
    }

    public long getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(long modifyTime) {
        this.modifyTime = modifyTime;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "TopicMetadata{" +
                "topic='" + topic + '\'' +
                ", partitionMap=" + partitionMap +
                ", brokerIdSet=" + brokerIdSet +
                ", replicaNum=" + replicaNum +
                ", partitionNum=" + partitionNum +
                ", modifyTime=" + modifyTime +
                ", createTime=" + createTime +
                '}';
    }
}
