package com.xiaojukeji.kafka.manager.common.entity.pojo;

import java.util.Date;

/**
 * @author zengqiao
 * @date 19/4/3
 */
public class BrokerDO {
    private Long id;

    private Integer status;

    private Date gmtCreate;

    private Date gmtModify;

    private Long clusterId;

    private Integer brokerId;

    private String host;

    private Integer port;

    private Long timestamp;

    private Double maxAvgBytesIn;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtModify() {
        return gmtModify;
    }

    public void setGmtModify(Date gmtModify) {
        this.gmtModify = gmtModify;
    }

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

    public Double getMaxAvgBytesIn() {
        return maxAvgBytesIn;
    }

    public void setMaxAvgBytesIn(Double maxAvgBytesIn) {
        this.maxAvgBytesIn = maxAvgBytesIn;
    }

    @Override
    public String toString() {
        return "BrokerDO{" +
                "id=" + id +
                ", status=" + status +
                ", gmtCreate=" + gmtCreate +
                ", gmtModify=" + gmtModify +
                ", clusterId=" + clusterId +
                ", brokerId=" + brokerId +
                ", host='" + host + '\'' +
                ", port=" + port +
                ", timestamp=" + timestamp +
                ", maxAvgBytesIn=" + maxAvgBytesIn +
                '}';
    }
}
