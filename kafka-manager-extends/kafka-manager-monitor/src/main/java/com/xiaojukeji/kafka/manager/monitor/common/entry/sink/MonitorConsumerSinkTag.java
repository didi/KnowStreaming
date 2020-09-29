package com.xiaojukeji.kafka.manager.monitor.common.entry.sink;

/**
 * @author huangjw
 * @date 17/5/25.
 */
public class MonitorConsumerSinkTag extends MonitorKafkaBaseSinkTag {
    private String topic;

    private String consumerGroup;

    public MonitorConsumerSinkTag(String host, String cluster, String topic, String consumerGroup) {
        super(host, cluster);
        this.topic = topic;
        this.consumerGroup = consumerGroup;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getConsumerGroup() {
        return consumerGroup;
    }

    public void setConsumerGroup(String consumerGroup) {
        this.consumerGroup = consumerGroup;
    }

    @Override
    public String toString() {
        return "MonitorConsumerSinkTag{" +
                "topic='" + topic + '\'' +
                ", consumerGroup='" + consumerGroup + '\'' +
                ", cluster='" + cluster + '\'' +
                ", host='" + host + '\'' +
                '}';
    }
}
