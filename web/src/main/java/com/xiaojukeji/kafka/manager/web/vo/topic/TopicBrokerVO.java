package com.xiaojukeji.kafka.manager.web.vo.topic;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * Topic所在Broker的信息
 * @author zengqiao
 * @date 19/4/3
 */
@ApiModel(value = "TopicBroker信息")
public class TopicBrokerVO {
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

    @Override
    public String toString() {
        return "TopicBrokerVO{" +
                "brokerId=" + brokerId +
                ", host='" + host + '\'' +
                ", partitionNum=" + partitionNum +
                ", partitionIdList=" + partitionIdList +
                ", leaderPartitionIdList=" + leaderPartitionIdList +
                '}';
    }
}
