package com.xiaojukeji.kafka.manager.common.entity.ao;

/**
 * Broker基本信息
 * @author zengqiao_cn@163.com
 * @date 19/4/8
 */
public class BrokerBasicDTO {
    private String host;

    private Integer port;

    private Integer jmxPort;

    private Integer topicNum;

    private Integer partitionCount;

    private Long startTime;

    private Integer leaderCount;

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
        return "BrokerBasicInfoDTO{" +
                "host='" + host + '\'' +
                ", port=" + port +
                ", jmxPort=" + jmxPort +
                ", topicNum=" + topicNum +
                ", partitionCount=" + partitionCount +
                ", startTime=" + startTime +
                ", leaderCount=" + leaderCount +
                '}';
    }
}