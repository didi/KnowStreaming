package com.xiaojukeji.kafka.manager.openapi.common.vo;

/**
 * @author zengqiao
 * @date 20/9/14
 */
public class BrokerRegionVO {
    private Long clusterId;

    private Integer brokerId;

    private String hostname;

    private String regionName;

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

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    @Override
    public String toString() {
        return "BrokerRegionVO{" +
                "clusterId=" + clusterId +
                ", brokerId=" + brokerId +
                ", hostname='" + hostname + '\'' +
                ", regionName='" + regionName + '\'' +
                '}';
    }
}