package com.xiaojukeji.kafka.manager.common.entity.pojo;

import java.util.Date;

/**
 * @author zengqiao
 * @date 20/4/29
 */
public class ClusterMetricsDO {
    private Long id;

    private Date gmtCreate;

    private Long clusterId;

    private String metrics;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    public String getMetrics() {
        return metrics;
    }

    public void setMetrics(String metrics) {
        this.metrics = metrics;
    }

    @Override
    public String toString() {
        return "ClusterMetricsDO{" +
                "id=" + id +
                ", gmtCreate=" + gmtCreate +
                ", clusterId=" + clusterId +
                ", metrics='" + metrics + '\'' +
                '}';
    }
}
