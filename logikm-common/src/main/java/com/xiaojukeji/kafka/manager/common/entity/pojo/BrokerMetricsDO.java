package com.xiaojukeji.kafka.manager.common.entity.pojo;

import java.util.Date;

/**
 * @author zengqiao
 * @date 20/6/17
 */
public class BrokerMetricsDO {
    private Long id;

    private Long clusterId;

    private Integer brokerId;

    private String metrics;

    private Date gmtCreate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getMetrics() {
        return metrics;
    }

    public void setMetrics(String metrics) {
        this.metrics = metrics;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    @Override
    public String toString() {
        return "BrokerMetricsDO{" +
                "id=" + id +
                ", clusterId=" + clusterId +
                ", brokerId=" + brokerId +
                ", metrics='" + metrics + '\'' +
                ", gmtCreate=" + gmtCreate +
                '}';
    }
}