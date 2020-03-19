package com.xiaojukeji.kafka.manager.common.entity.dto;

import com.xiaojukeji.kafka.manager.common.entity.metrics.BrokerMetrics;
import com.xiaojukeji.kafka.manager.common.entity.zookeeper.BrokerMetadata;
import com.xiaojukeji.kafka.manager.common.entity.bizenum.DBStatusEnum;

/**
 * @author zengqiao_cn@163.com
 * @date 19/4/21
 */
public class BrokerOverviewDTO {
    private Integer brokerId;

    private String host;

    private Integer port;

    private Integer jmxPort;

    private Long startTime;

    private Double byteIn;

    private Double byteOut;

    private Integer status;

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

    public Integer getJmxPort() {
        return jmxPort;
    }

    public void setJmxPort(Integer jmxPort) {
        this.jmxPort = jmxPort;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Double getByteIn() {
        return byteIn;
    }

    public void setByteIn(Double byteIn) {
        this.byteIn = byteIn;
    }

    public Double getByteOut() {
        return byteOut;
    }

    public void setByteOut(Double byteOut) {
        this.byteOut = byteOut;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "BrokerInfoDTO{" +
                "brokerId=" + brokerId +
                ", host='" + host + '\'' +
                ", port=" + port +
                ", jmxPort=" + jmxPort +
                ", startTime=" + startTime +
                ", byteIn=" + byteIn +
                ", byteOut=" + byteOut +
                ", status=" + status +
                '}';
    }

    public static BrokerOverviewDTO newInstance(BrokerMetadata brokerMetadata, BrokerMetrics brokerMetrics) {
        BrokerOverviewDTO brokerOverviewDTO = new BrokerOverviewDTO();
        brokerOverviewDTO.setBrokerId(brokerMetadata.getBrokerId());
        brokerOverviewDTO.setHost(brokerMetadata.getHost());
        brokerOverviewDTO.setPort(brokerMetadata.getPort());
        brokerOverviewDTO.setJmxPort(brokerMetadata.getJmxPort());
        brokerOverviewDTO.setStartTime(brokerMetadata.getTimestamp());
        brokerOverviewDTO.setStatus(DBStatusEnum.NORMAL.getStatus());
        if (brokerMetrics == null) {
            return brokerOverviewDTO;
        }
        brokerOverviewDTO.setByteIn(brokerMetrics.getBytesInPerSec());
        brokerOverviewDTO.setByteOut(brokerMetrics.getBytesOutPerSec());
        return brokerOverviewDTO;
    }
}