package com.xiaojukeji.kafka.manager.common.entity.vo.rd.broker;

/**
 * @author zengqiao
 * @date 19/7/12
 */
public class BrokerMetadataVO {
    private Integer brokerId;

    private String host;

    public BrokerMetadataVO(int brokerId, String host) {
        this.brokerId = brokerId;
        this.host = host;
    }

    public int getBrokerId() {
        return brokerId;
    }

    public void setBrokerId(int brokerId) {
        this.brokerId = brokerId;
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

    @Override
    public String toString() {
        return "BrokerMetadataVO{" +
                "brokerId=" + brokerId +
                ", host='" + host + '\'' +
                '}';
    }
}