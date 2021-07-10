package com.xiaojukeji.kafka.manager.monitor.common.entry.sink;

/**
 * @author huangjw
 * @date 17/5/24.
 */
public class MonitorTopicSinkTag extends AbstractMonitorKafkaSinkTag {
    protected String topic;

    public MonitorTopicSinkTag(String cluster, String topic) {
        super(cluster);
        this.topic = topic;
    }

    @Override
    public String toString() {
        return "MonitorTopicSinkTag{" +
                "topic='" + topic + '\'' +
                ", cluster='" + cluster + '\'' +
                '}';
    }

    @Override
    public String convert2Tags() {
        return String.format("cluster=%s,topic=%s", cluster, topic);
    }
}
