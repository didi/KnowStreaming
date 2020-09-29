package com.xiaojukeji.kafka.manager.monitor.common.entry.sink;

/**
 * @author zengqiao
 * @date 20/09/02
 */
public class MonitorConsumePartitionSinkTag extends MonitorTopicPartitionSinkTag {
    private String consumerGroup;

    public MonitorConsumePartitionSinkTag(String host, String cluster, String topic, Integer partition, String consumerGroup) {
        super(host, cluster, topic, partition);
        this.consumerGroup = consumerGroup;
    }

    public String getConsumerGroup() {
        return consumerGroup;
    }

    public void setConsumerGroup(String consumerGroup) {
        this.consumerGroup = consumerGroup;
    }

    @Override
    public String toString() {
        return "MonitorConsumePartitionSinkTag{" +
                "consumerGroup='" + consumerGroup + '\'' +
                ", topic='" + topic + '\'' +
                ", partition='" + partition + '\'' +
                ", cluster='" + cluster + '\'' +
                ", host='" + host + '\'' +
                '}';
    }
}
