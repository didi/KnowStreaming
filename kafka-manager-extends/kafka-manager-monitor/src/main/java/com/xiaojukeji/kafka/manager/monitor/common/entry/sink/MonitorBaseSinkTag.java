package com.xiaojukeji.kafka.manager.monitor.common.entry.sink;

/**
 * @author zengqiao
 * @date 20/5/24
 */
public class MonitorBaseSinkTag {
    /**
     * 主机名
     */
    protected String host;

    public MonitorBaseSinkTag(String host) {
        this.host = host;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    @Override
    public String toString() {
        return "BaseTag{" +
                "host='" + host + '\'' +
                '}';
    }
}