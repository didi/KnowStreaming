package com.xiaojukeji.kafka.manager.common.entity.vo.rd;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * Topic所在Broker的信息
 * @author zengqiao
 * @date 19/4/3
 */
public class TopicBrokerVO {
    @ApiModelProperty(value = "物理集群ID")
    private Long clusterId;

    @ApiModelProperty(value = "brokerId")
    private Integer brokerId;

    @ApiModelProperty(value = "broker主机名")
    private String host;

    @ApiModelProperty(value = "分区数")
    private Integer partitionNum;

    @ApiModelProperty(value = "分区的Id")
    private List<Integer> partitionIdList;

    @ApiModelProperty(value = "leader分区的Id")
    private List<Integer> leaderPartitionIdList;

    @ApiModelProperty(value = "是否存活")
    private boolean alive;

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

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
        return "TopicBrokerVO{" +
                "clusterId=" + clusterId +
                ", brokerId=" + brokerId +
                ", host='" + host + '\'' +
                ", partitionNum=" + partitionNum +
                ", partitionIdList=" + partitionIdList +
                ", leaderPartitionIdList=" + leaderPartitionIdList +
                ", alive=" + alive +
                '}';
    }
}
