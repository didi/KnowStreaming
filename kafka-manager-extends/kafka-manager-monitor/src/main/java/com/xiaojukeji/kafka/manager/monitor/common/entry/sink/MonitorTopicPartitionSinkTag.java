package com.xiaojukeji.kafka.manager.monitor.common.entry.sink;

/**
 * @author zengqiao
 * @date 20/9/3
 */
public class MonitorTopicPartitionSinkTag extends MonitorKafkaBaseSinkTag {
    protected String topic;

    protected String partition;

    public MonitorTopicPartitionSinkTag(String host, String cluster, String topic, Integer partition) {
        super(host, cluster);
        this.topic = topic;
        this.partition = String.valueOf(partition);
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getPartition() {
        return partition;
    }

    public void setPartition(String partition) {
        this.partition = partition;
    }

    @Override
    public String toString() {
        return "MonitorTopicPartitionSinkTag{" +
                "topic='" + topic + '\'' +
                ", partition='" + partition + '\'' +
                ", cluster='" + cluster + '\'' +
                ", host='" + host + '\'' +
                '}';
    }
}
