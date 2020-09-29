package com.xiaojukeji.kafka.manager.common.entity.vo.rd.broker;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author huangjw
 * @date 17/6/1.
 */
@ApiModel(description = "Broker基本信息")
public class BrokerBasicVO {
    @ApiModelProperty(value = "主机名")
    private String host;

    @ApiModelProperty(value = "服务端口")
    private Integer port;

    @ApiModelProperty(value = "JMX端口")
    private Integer jmxPort;

    @ApiModelProperty(value = "Topic数")
    private Integer topicNum;

    @ApiModelProperty(value = "分区数")
    private Integer partitionCount;

    @ApiModelProperty(value = "Leader数")
    private Integer leaderCount;

    @ApiModelProperty(value = "启动时间")
    private Long startTime;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Integer getJmxPort() {
        return jmxPort;
    }

    public void setJmxPort(Integer jmxPort) {
        this.jmxPort = jmxPort;
    }

    public Integer getTopicNum() {
        return topicNum;
    }

    public void setTopicNum(Integer topicNum) {
        this.topicNum = topicNum;
    }

    public Integer getPartitionCount() {
        return partitionCount;
    }

    public void setPartitionCount(Integer partitionCount) {
        this.partitionCount = partitionCount;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Integer getLeaderCount() {
        return leaderCount;
    }

    public void setLeaderCount(Integer leaderCount) {
        this.leaderCount = leaderCount;
    }

    @Override
    public String toString() {
        return "BrokerBasicVO{" +
                "host='" + host + '\'' +
                ", port=" + port +
                ", jmxPort=" + jmxPort +
                ", topicNum=" + topicNum +
                ", partitionCount=" + partitionCount +
                ", leaderCount=" + leaderCount +
                ", startTime=" + startTime +
                '}';
    }
}
