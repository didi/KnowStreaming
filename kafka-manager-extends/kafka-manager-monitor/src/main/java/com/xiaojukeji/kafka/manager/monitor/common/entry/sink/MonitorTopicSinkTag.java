package com.xiaojukeji.kafka.manager.monitor.common.entry.sink;

/**
 * @author huangjw
 * @date 17/5/24.
 */
public class MonitorTopicSinkTag extends MonitorKafkaBaseSinkTag {

    private String topic;

    public MonitorTopicSinkTag(String host, String cluster, String topic) {
        super(host, cluster);
        this.topic = topic;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    @Override
    public String toString() {
        return "MonitorTopicSinkTag{" +
                "topic='" + topic + '\'' +
                ", cluster='" + cluster + '\'' +
                ", host='" + host + '\'' +
                '}';
    }
}
