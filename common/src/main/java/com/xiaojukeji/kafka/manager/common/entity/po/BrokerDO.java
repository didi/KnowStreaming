package com.xiaojukeji.kafka.manager.common.entity.po;

/**
 * @author zengqiao
 * @date 19/4/3
 */
public class BrokerDO extends BaseDO {
    private Long clusterId;

    private Integer brokerId;

    private String host;

    private Integer port;

    private Long timestamp;

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

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "BrokerDO{" +
                "clusterId=" + clusterId +
                ", brokerId=" + brokerId +
                ", host='" + host + '\'' +
                ", port=" + port +
                ", timestamp=" + timestamp +
                ", id=" + id +
                ", status=" + status +
                ", gmtCreate=" + gmtCreate +
                ", gmtModify=" + gmtModify +
                '}';
    }
}
