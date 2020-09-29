package com.xiaojukeji.kafka.manager.common.entity.ao.topic;

import java.util.List;

/**
 * @author zhongyuankai
 * @date 20/4/17
 */
public class TopicBrokerDTO {
    private Integer brokerId;

    private String host;

    private Integer partitionNum;

    private List<Integer> partitionIdList;

    private List<Integer> leaderPartitionIdList;

    private boolean alive;

    public Integer getBrokerId() {
        return brokerId;
    }

    public void setBrokerId(Integer brokerId) {
        this.brokerId = brokerId;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPartitionNum() {
        return partitionNum;
    }

    public void setPartitionNum(Integer partitionNum) {
        this.partitionNum = partitionNum;
    }

    public List<Integer> getPartitionIdList() {
        return partitionIdList;
    }

    public void setPartitionIdList(List<Integer> partitionIdList) {
        this.partitionIdList = partitionIdList;
    }

    public List<Integer> getLeaderPartitionIdList() {
        return leaderPartitionIdList;
    }

    public void setLeaderPartitionIdList(List<Integer> leaderPartitionIdList) {
        this.leaderPartitionIdList = leaderPartitionIdList;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    @Override
    public String toString() {
        return "TopicBrokerDTO{" +
                "brokerId=" + brokerId +
                ", host='" + host + '\'' +
                ", partitionNum=" + partitionNum +
                ", partitionIdList=" + partitionIdList +
                ", leaderPartitionIdList=" + leaderPartitionIdList +
                ", alive=" + alive +
                '}';
    }
}
