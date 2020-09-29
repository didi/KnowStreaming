package com.xiaojukeji.kafka.manager.monitor.common.entry.sink;

/**
 * @author zengqiao
 * @date 20/5/24
 */
public class MonitorKafkaBaseSinkTag extends MonitorBaseSinkTag {
    protected String cluster;

    public MonitorKafkaBaseSinkTag(String host, String cluster) {
        super(host);
        this.cluster = cluster;
    }

    public String getCluster() {
        return cluster;
    }

    public void setCluster(String cluster) {
        this.cluster = cluster;
    }

    @Override
    public String toString() {
        return "MonitorKafkaBaseSinkTag{" +
                "cluster='" + cluster + '\'' +
                ", host='" + host + '\'' +
                '}';
    }
}