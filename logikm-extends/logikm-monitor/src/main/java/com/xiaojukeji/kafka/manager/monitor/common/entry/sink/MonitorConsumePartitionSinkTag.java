package com.xiaojukeji.kafka.manager.monitor.common.entry.sink;

/**
 * @author zengqiao
 * @date 20/09/02
 */
public class MonitorConsumePartitionSinkTag extends MonitorPartitionSinkTag {
    private String consumerGroup;

    public MonitorConsumePartitionSinkTag(String cluster, String topic, Integer partition, String consumerGroup) {
        super(cluster, topic, partition);
        this.consumerGroup = consumerGroup;
    }

    @Override
    public String toString() {
        return "MonitorConsumePartitionSinkTag{" +
                "consumerGroup='" + consumerGroup + '\'' +
                ", topic='" + topic + '\'' +
                ", partition='" + partition + '\'' +
                ", cluster='" + cluster + '\'' +
                '}';
    }

    @Override
    public String convert2Tags() {
        return String.format("cluster=%s,topic=%s,partition=%s,consumerGroup=%s", cluster, topic, partition, consumerGroup);
    }
}
